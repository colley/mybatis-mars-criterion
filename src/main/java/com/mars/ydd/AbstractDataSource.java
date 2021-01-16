package com.mars.ydd;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.sql.DataSource;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mars.ydd.monitor.data.DataSourceIdentityData;
import com.mars.ydd.monitor.processor.MonitorDataProcessor;
import com.mars.ydd.monitor.proxy.driver.JdbcDriverMonitorProxy;
import com.mars.ydd.monitor.proxy.driver.JdbcDriverMonitorProxy.PhyGetConnException;
import com.mars.ydd.util.DNSUtil;
import com.mars.ydd.util.ResetableCountDownLatch;

public abstract class AbstractDataSource implements DataSource {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataSource.class);

	public final static int DB_ERR_MAX_COUNT = 20;
	private final ResetableCountDownLatch dbErrCount = new ResetableCountDownLatch(DB_ERR_MAX_COUNT);

	private Semaphore activeCount;

	private final static String CON_RETRY_TIME_STR = "conRetryTime";
	private int conRetryTime;

	private final static String MONITOR_SUPPORT_STR = "monitorSupport";

	private final static String SLOW_SQL_THRESHOLD_STR = "slowSqlThreshold";
	private final static long SLOW_SQL_THRESHOLD_DEFAULT = 1000;// millisecond

	private final static String ARCHIVE_IDLE_TIME_STR = "archiveIdleTime";
	private final static int ARCHIVE_IDLE_TIME_DEFAULT = 300;// second

	private final static String ERR_MSG_LIMIT_STR = "errMsgLimit";

	private final static String SSD_SWITCH = "ssdSwitch";

	private final static String DSSD_SWITCH = "dssdSwitch";

	private final static String ASRD_SWITCH = "asrdSwitch";

	private final static int ERR_MSG_LIMIT_DEFAULT = 3;

	private final static int DEFAULT_TEST_CONNECTION_COUNT = 2;

	private String dsName;

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	private DynamicDataSource dynamicDataSource;
	private final AtomicReference<MonitorDataProcessor> mdpRef = new AtomicReference<MonitorDataProcessor>();

	public AtomicReference<MonitorDataProcessor> getMdpRef() {
		return mdpRef;
	}

	protected abstract long getMaxWait();

	protected abstract int getMaxActive();

	protected abstract ConnectionPool getTomcatConnPool();

	protected void setProperties(Properties properties) {
		String tmpValue = properties.getProperty(CON_RETRY_TIME_STR);
		if (StringUtils.isNotEmpty(tmpValue) && NumberUtils.isDigits(tmpValue)) {
			conRetryTime = Integer.parseInt(tmpValue);
			if (conRetryTime < 1) {
				conRetryTime = 0;
			}
		}

		activeCount = new Semaphore(DEFAULT_TEST_CONNECTION_COUNT, true);
	}

	protected void close() {
		MonitorDataProcessor mdp = mdpRef.get();
		if (mdp != null) {
			mdp.stopWork();
		}
	}

	protected int flushMonitorData(boolean isTest) {
		int result = 0;
		MonitorDataProcessor mdp = mdpRef.get();
		if (mdp != null) {
			result = mdp.flushMonitorData(isTest);
		}
		return result;
	}

	protected abstract DataSource getRealDataSource();

	protected Connection getConnectionWithLock(boolean needParams, String username, String password, String transactionIsolation)
			throws SQLException {
		Connection connection = null;
		int retryTime = this.conRetryTime;
		long startTime = 0, usedTime = 0, tmpUsedTime = 0, sleepTime = 0;

		boolean needActiveCountRelease = false;
		try {
			startTime = System.currentTimeMillis();
			if (dbErrCount.getCount() <= 0) {
				if (!activeCount.tryAcquire(getMaxWait(), TimeUnit.MILLISECONDS)) {
					throw new SQLException("Get connection fail due to DB error," + "pool id:" + dynamicDataSource.getPoolId()
							+ ",data id:" + dynamicDataSource.getDataId() + " ==#" + dynamicDataSource.getDsProperties().getProperty("url")
							+ "#== ");
				} else {
					if (dbErrCount.getCount() <= 0) {
						LOGGER.warn("[Try to recover DB Connection.]");
						needActiveCountRelease = true;
					} else {
						activeCount.release();
					}
				}
			}
			tmpUsedTime = System.currentTimeMillis() - startTime;
			usedTime += tmpUsedTime;

			MonitorDataProcessor tmpMdp = mdpRef.get();
			JdbcDriverMonitorProxy.curThreadMDP.set(tmpMdp);
			if (getTomcatConnPool() != null && tmpMdp != null) {
				tmpMdp.setConnPool(getTomcatConnPool());
			}

			while (retryTime >= 0) {
				startTime = System.currentTimeMillis();
				try {
					if (needParams) {
						connection = getConnection(username, password);
					} else {
						connection = getConnection();
					}
					if (dbErrCount.getCount() < AbstractDataSource.DB_ERR_MAX_COUNT) {
						LOGGER.info("[Recover DB Connection success.]");
						dbErrCount.resetCount();
					}

					if (StringUtils.isNotBlank(transactionIsolation)) {
						int leave = 0;
						if (transactionIsolation.equals("TRANSACTION_READ_UNCOMMITTED"))
							leave = 1;
						else if (transactionIsolation.equals("TRANSACTION_READ_COMMITTED"))
							leave = 2;
						else if (transactionIsolation.equals("TRANSACTION_REPEATABLE_READ"))
							leave = 4;
						else if (transactionIsolation.equals("TRANSACTION_SERIALIZABLE"))
							leave = 8;

						if (leave != 0)
							connection.setTransactionIsolation(leave);
					}
					return connection;
				} catch (SQLException e) {
					tmpUsedTime = System.currentTimeMillis() - startTime;
					usedTime += tmpUsedTime;

					// remove DNS cache.added by zhaijingtao
					DNSUtil.removeDNSCache(dynamicDataSource.getDsProperties().getProperty("url"));
					Throwable rootCause = e;
					boolean isDBErr = false;
					while (rootCause != null) {
						if (e instanceof PhyGetConnException) {
							isDBErr = true;
							PhyGetConnException phyGetConnException = (PhyGetConnException) e;
							if (phyGetConnException.isExceptionFatal()) {
								dbErrCount.clearCount();
							} else {
								dbErrCount.countDown();
							}
							break;
						} else {
							rootCause = rootCause.getCause();
						}
					}

					if (usedTime < getMaxWait() && tmpUsedTime < getMaxWait()) {
						sleepTime = usedTime + getMaxWait() >> 2 <= getMaxWait() ? getMaxWait() >> 2 : getMaxWait() - usedTime;
						try {
							Thread.sleep(sleepTime);
							usedTime += sleepTime;
						} catch (InterruptedException e1) {
							throw new SQLException("Thread has been canceled,pool id:" + dynamicDataSource.getPoolId() + ",data id:"
									+ dynamicDataSource.getDataId() + " ==#" + dynamicDataSource.getDsProperties().getProperty("url")
									+ "#== ", e1);
						}
					} else {// Get connection timeout error or other error.
						retryTime--;
					}
					if (retryTime < 0) {
						if (isDBErr) {
							throw new SQLException("Get connection with DB error,pool id:" + dynamicDataSource.getPoolId() + ",data id:"
									+ dynamicDataSource.getDataId() + " ==#" + dynamicDataSource.getDsProperties().getProperty("url")
									+ "#==", e);
						} else {
							throw new SQLException("Get connection timeout,pool id:" + dynamicDataSource.getPoolId() + ",data id:"
									+ dynamicDataSource.getDataId() + " ==#" + dynamicDataSource.getDsProperties().getProperty("url")
									+ "#== ", e);
						}
					}
				}
			}
		} catch (InterruptedException e) {
			throw new SQLException("Thread has been canceled,pool id:" + dynamicDataSource.getPoolId() + ",data id:"
					+ dynamicDataSource.getDataId() + " ==#" + dynamicDataSource.getDsProperties().getProperty("url") + "#== ", e);
		} finally {
			if (needActiveCountRelease) {
				activeCount.release();
			}
		}
		throw new SQLException("Unknow error.");
	}

	public DynamicDataSource getDynamicDataSource() {
		return dynamicDataSource;
	}

	public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
		this.dynamicDataSource = dynamicDataSource;
	}

	public MonitorDataProcessor getMonitorDataProcessor() {
		return mdpRef.get();
	}

	public boolean needChange(long waitTime) throws InterruptedException {
		return dbErrCount.await(waitTime, TimeUnit.MILLISECONDS);
	}

	protected boolean setMonitorProperties(Properties properties) {
		boolean result = false;
		String tmpValue = properties.getProperty(MONITOR_SUPPORT_STR);
		if (StringUtils.isNotEmpty(tmpValue) && BooleanUtils.toBoolean(tmpValue)) {

			StringBuilder localIp = new StringBuilder();
			try {
				Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
				while (networkInterfaces.hasMoreElements()) {
					NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
					if (networkInterface.isUp() && !networkInterface.isLoopback() && !networkInterface.isVirtual()) {
						Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
						while (inetAddresses.hasMoreElements()) {
							InetAddress inetAddress = (InetAddress) inetAddresses.nextElement();
							if (inetAddress instanceof Inet4Address
									&& !(inetAddress.isAnyLocalAddress() || inetAddress.isLinkLocalAddress() || inetAddress
											.isLoopbackAddress())) {
								localIp.append(inetAddress.getHostAddress()).append(',');
							}
						}
					}
				}
			} catch (SocketException e) {
			}
			if (localIp.length() > 0) {
				localIp.deleteCharAt(localIp.length() - 1);
			}
			DataSourceIdentityData dataSourceIdentityData = new DataSourceIdentityData(getDynamicDataSource().getPoolId(),
					getDynamicDataSource().getGroupId(), getDynamicDataSource().getDataId(), properties.getProperty("url"),
					properties.getProperty("username"), localIp.toString());

			tmpValue = properties.getProperty(ARCHIVE_IDLE_TIME_STR);
			long archiveIdleTimeMillis = NumberUtils.toInt(tmpValue, ARCHIVE_IDLE_TIME_DEFAULT) * 1000;

			tmpValue = properties.getProperty(SLOW_SQL_THRESHOLD_STR);
			long slowSqlThreshold = NumberUtils.toLong(tmpValue, SLOW_SQL_THRESHOLD_DEFAULT);

			tmpValue = properties.getProperty(ERR_MSG_LIMIT_STR);
			int errMsgLimit = NumberUtils.toInt(tmpValue, ERR_MSG_LIMIT_DEFAULT);

			Properties switchPro = new Properties();
			tmpValue = properties.getProperty(SSD_SWITCH);
			Boolean ssdSwitch = BooleanUtils.toBoolean(tmpValue);
			tmpValue = properties.getProperty(DSSD_SWITCH);
			Boolean dssdSwitch = BooleanUtils.toBoolean(tmpValue);
			tmpValue = properties.getProperty(ASRD_SWITCH);
			Boolean asrdSwitch = BooleanUtils.toBoolean(tmpValue);
			switchPro.put(SSD_SWITCH, ssdSwitch);
			switchPro.put(DSSD_SWITCH, dssdSwitch);
			switchPro.put(ASRD_SWITCH, asrdSwitch);

			MonitorDataProcessor tmpMDP = mdpRef.get();
			if (tmpMDP != null) {
				tmpMDP.stopWork();
			}

			mdpRef.set(new MonitorDataProcessor(archiveIdleTimeMillis, slowSqlThreshold, errMsgLimit, NumberUtils.toLong(
					properties.getProperty("maxWait"), slowSqlThreshold), NumberUtils.toInt(properties.getProperty("maxActive"), 50),
					dataSourceIdentityData, this.dsName, switchPro));

			mdpRef.get().startWork();

			result = true;
		} else {
			MonitorDataProcessor tmpMDP = mdpRef.get();
			if (tmpMDP != null) {
				mdpRef.compareAndSet(tmpMDP, null);
				tmpMDP.stopWork();
			}
		}

		return result;
	}
}