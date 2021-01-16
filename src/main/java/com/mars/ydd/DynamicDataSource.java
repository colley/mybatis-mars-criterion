package com.mars.ydd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;

import com.mars.ydd.monitor.proxy.driver.MySQLDriverCjMonitorProxy;
import com.mars.ydd.monitor.proxy.driver.MySQLDriverMonitorProxy;
import com.mars.ydd.util.Encrypter;
import com.mars.ydd.util.config.YddAppConfig;

public class DynamicDataSource implements DataSource, InitializingBean, BeanFactoryAware, BeanNameAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSource.class);
    
	private String poolId;
	private String groupId;
	private String dataId;
	private String envOverride = "true";
	private String beanName;
	private BeanFactory beanFactory;
     
	public final Properties ddsProperties = new Properties();
	private final Properties ydd2Properties = new Properties();
   
	private final static String DRIVER_CLASS_NAME = "driverClassName";
	private final static String DATA_SOURCE_CLASS_NAME_STR = "dataSourceClassName";
	private final static String PROP_CONNECTIONPROPERTIES = "connectionProperties";
	private final static String PROP_CONNECTTIMEOUT = "connectTimeout";
	private final static String PROP_SOCKETTIMEOUT = "socketTimeout";
	private String connectTimeout = "5000";
	private String socketTimeout = "300000";
    
	public volatile AbstractDataSource currentAds;
	private volatile int currentAdsIdx = 0;
	public volatile Properties currentDbProperties;
	private final List<Properties> dbPropertiesList = new ArrayList<Properties>(2);
	private final List<AbstractDataSource> adsList = new ArrayList<AbstractDataSource>(2);
	public final ReadWriteLock failoverLock = new ReentrantReadWriteLock(true);
    
	private volatile AdsFailoverCheckThread adsFailoverCheckThread = new AdsFailoverCheckThread();
//    private YddHealthCheck yddHealthCheck = null;
	public String transactionIsolation = null;

	//Below added for jd
	/**
	 * 相应数据源配置文件的文件名或者数据源配置文件的相对路径。
	 * 
	 * 如果配置了该属性，则yddAppConfig中的configFolderFullPath属性必须配置。
	 */
	private String configFileName;

	private YddAppConfig yddAppConfig;

	/**
	 * 配置文件的位置。
	 *
	 * 支持如下格式：
	 * 	1. classpath:XXXX/XXXX.properties
	 * 	2. file:C:/XXXXX.properties
	 * 	3. C:/XXXXX.properties
	 *
	 */
	private String configFileLocation;

	public String getConfigFileName() {
		return configFileName;
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

	public YddAppConfig getYddAppConfig() {
		return yddAppConfig;
	}

	public void setYddAppConfig(YddAppConfig yddAppConfig) {
		this.yddAppConfig = yddAppConfig;
	}

	public String getConfigFileLocation() {
		return configFileLocation;
	}

	public void setConfigFileLocation(String configFileLocation) {
		this.configFileLocation = configFileLocation;
	}

	private class AdsFailoverCheckThread extends Thread {
		private static final int WAIT_TIME_DEFAULT = 3000;
		private volatile boolean isContinue = true;

		public void stopCheck() {
			this.isContinue = false;
		}

		@Override
		public void run() {
			if (adsList.size() <= 1) {
				return;
			}

			Thread.currentThread().setName("-" + beanName + "-Ads Failover Check Thread-");

			long waitTime = NumberUtils.toLong(dbPropertiesList.get(0).getProperty("maxWait"), WAIT_TIME_DEFAULT);

			Set<Integer> failAdsSet = new TreeSet<Integer>();
			while (isContinue) {
				try {
					if (currentAds.needChange(waitTime)) {
						int currentFailIdx = currentAdsIdx;

						for (int i = 0; i < adsList.size(); i++) {
							if (i != currentAdsIdx) {
								AbstractDataSource tmpAds = adsList.get(i);
								try {
									tmpAds.getConnectionWithLock(false, null, null ,transactionIsolation).close();
									failoverLock.writeLock().lock();
									try {
										currentAds = tmpAds;
										currentDbProperties = dbPropertiesList.get(i);
										currentAdsIdx = i;
										LOGGER.warn("\n-------------Failover to DB 【" + currentDbProperties.getProperty("url") + "】");
									} finally {
										failoverLock.writeLock().unlock();
									}
									adsList.get(currentFailIdx).flushMonitorData(false);
									break;
								} catch (Exception e) {
									LOGGER.error("\n-------------Failover test error", e);
								}
							}
						}
						failAdsSet.add(Integer.valueOf(currentFailIdx));

					} else if (failAdsSet.size() > 0) {
						boolean needChange = false;
						int changeIdx = currentAdsIdx;
						int beforeChangeIdx = currentAdsIdx;
						Set<Integer> sucessAdsSet = new HashSet<Integer>();
						for (Integer i : failAdsSet) {
							AbstractDataSource tmpAds = adsList.get(i);
							try {
								tmpAds.getConnectionWithLock(false, null, null,transactionIsolation).close();
								if (i.intValue() < changeIdx) {
									needChange = true;
									changeIdx = i.intValue();
								}
								sucessAdsSet.add(i);
								LOGGER.info("\n-------------Datasource 【" + dbPropertiesList.get(i).getProperty("url") + "】 test Success");
							} catch (Exception e) {
								LOGGER.error("\n-------------Datasource 【" + dbPropertiesList.get(i).getProperty("url") + "】 test fail", e);
							}
						}
						if(sucessAdsSet.size() > 0) {
							failAdsSet.removeAll(sucessAdsSet);
						}
						if (needChange) {
							failoverLock.writeLock().lock();
							try {
								currentAds = adsList.get(changeIdx);
								currentDbProperties = dbPropertiesList.get(changeIdx);
								currentAdsIdx = changeIdx;
								LOGGER.info("Datasource 【\n-------------" + currentDbProperties.getProperty("url") + "】rework");
							} finally {
								failoverLock.writeLock().unlock();
							}
							adsList.get(beforeChangeIdx).flushMonitorData(false);
						}
					}
				} catch (InterruptedException e) {
					isContinue = false;
				}
			}
		}
	}

	public static Hashtable<String, String> loadPropertiesByFile(File propFile) {
		if (!propFile.exists()) {
			throw new RuntimeException("Config file <" + propFile.getAbsolutePath() + "> doesn't exists.");
		}

		Properties prop = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(propFile);
			prop.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch(InterruptedIOException ignore) {
					Thread.currentThread().interrupt();
				} catch(IOException ignore) {
				} catch(RuntimeException ignore) {
				}
			}
		}

		Hashtable<String, String> result = getHashtableByProperties(prop);
		return result;
	}

	public static Hashtable<String, String> loadPropertiesByUrl(URL configURL) {
		if (null==configURL) {
			throw new RuntimeException("configURL should not be null.");
		}

		Properties prop = new Properties();
		InputStream inputStream = null;
		URLConnection urlConnection = null;
		try {
			urlConnection = configURL.openConnection();
			urlConnection.setUseCaches(false);
			inputStream = urlConnection.getInputStream();
			prop.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch(InterruptedIOException ignore) {
					Thread.currentThread().interrupt();
				} catch(IOException ignore) {
				} catch(RuntimeException ignore) {
				}
			}
		}

		Hashtable<String, String> result = getHashtableByProperties(prop);
		return result;
	}

	public static Hashtable<String, String> getHashtableByProperties(Properties prop) {
		if (null==prop) {
			throw new RuntimeException("prop should not be null.");
		}
		Hashtable<String, String> result = new Hashtable<String, String>();
		for (Object okey : prop.keySet()) {
			String key = (String) okey;
			String value = prop.getProperty(key);
			result.put(key, value);
		}
		return result;
	}

	public String getPoolId() {
		return poolId;
	}

	public void setPoolId(String poolId) {
		this.poolId = poolId;
	}

	public String getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(String connectTimeout) {
		if(StringUtils.isNotBlank(connectTimeout))
		this.connectTimeout = connectTimeout.trim();
	}

	public String getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(String socketTimeout) {
		if(StringUtils.isNotBlank(socketTimeout))
		this.socketTimeout = socketTimeout.trim();
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public String getEnvOverride() {
		return envOverride;
	}

	public void setEnvOverride(String envOverride) {
		this.envOverride = envOverride;
	}

	protected String getGroupId() {
		return groupId;
	}

	public Properties getDdsProperties() {
		return ddsProperties;
	}

	public Properties getYdd2Properties() {
		return ydd2Properties;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return currentAds.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		currentAds.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		currentAds.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return currentAds.getLoginTimeout();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return currentAds.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return currentAds.isWrapperFor(iface);
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public DynamicDataSource() {
		super();
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		if(null==this.yddAppConfig) {
			throw new RuntimeException("You should config com.yihaodian.ydd.util.config.YddAppConfig first.");
		}
		String appId = this.yddAppConfig.getAppId();
		this.poolId = appId;
		Hashtable<String, String> propsHashtable = null;
		if(StringUtils.isNotBlank(this.configFileName)) {
			//Ydd for jd
			String configFolderFullPath = this.yddAppConfig.getConfigFolderFullPath();
			if(StringUtils.isBlank(configFolderFullPath)) {
				throw new RuntimeException("configFolderFullPath should be configured in com.yihaodian.ydd.util.config.YddAppConfig when configFileName is not blank .");
			}
			this.configFileName = SystemPropertyUtils.resolvePlaceholders(this.configFileName);
			this.dataId = getFileName(this.configFileName);
			File propFile = new File(configFolderFullPath, this.configFileName);
			propsHashtable = loadPropertiesByFile(propFile);
		} else if(StringUtils.isNotBlank(this.configFileLocation)) {
			this.configFileLocation = SystemPropertyUtils.resolvePlaceholders(this.configFileLocation);
			this.dataId = getFileName(this.configFileLocation);
			URL url = ResourceUtils.getURL(this.configFileLocation);
			propsHashtable = loadPropertiesByUrl(url);
		}
		else {
			throw new RuntimeException(beanName + " defined error, please check spring config.");
		}

		if(StringUtils.isNotBlank(this.dataId)) {
			if(!this.dataId.toLowerCase().endsWith(".properties")) {
				throw new RuntimeException("The name of config file should be end with .properties.");
			}
		} else {
			throw new RuntimeException("The name of config file should not be blank.");
		}

		if(null!=propsHashtable) {
			for (Entry<String, String> kvsEntry : propsHashtable.entrySet()) {
				if (kvsEntry.getKey().toString().startsWith("jdbc.")) {
					ddsProperties.put(kvsEntry.getKey().substring(5), kvsEntry.getValue().trim());
				}
			}
		} else {
			throw new RuntimeException("No config found for app id:" + this.poolId + ",configFileName=" + configFileName + ",configFileLocation=" + configFileLocation);
		}

		startDataSource();

	}

	private static String getFileName(String path) {
		String returnValue = null;
		if(StringUtils.isNotBlank(path)) {
			path = path.trim();
			if (path.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
				path = path.substring(ResourceUtils.CLASSPATH_URL_PREFIX.length());
			} else if (path.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
				path = path.substring(ResourceUtils.FILE_URL_PREFIX.length());
			}
			int position1 = path.lastIndexOf("/");
			int position2 = path.lastIndexOf("\\");
			int position = Math.max(position1, position2);
			if(position<0) {
				returnValue = path;
			} else {
				if(path.length()>1) {
					returnValue = path.substring(position+1);
				} else if(path.length()==1) {
					returnValue = "";
				}
			}
		}
		return returnValue;
	}

	private void startDataSource() {
		ydd2Properties.setProperty("monitorSupport", "false");
		ydd2Properties.setProperty("archiveIdleTime", "300");
		ydd2Properties.setProperty("slowSqlThreshold", "1000");


		setDataSourceProperties();
	}
	
	private void setDefaultDataSourceProperties(Properties properties) {
		if(!properties.containsKey("initialSize")) {
			properties.setProperty("initialSize", "0");
		}
		if(!properties.containsKey("maxWait")) {
			properties.setProperty("maxWait", "5000");
		}
		if(!properties.containsKey("removeAbandoned")) {
			properties.setProperty("removeAbandoned", "true");
		}
		if(!properties.containsKey("removeAbandonedTimeout")) {
			properties.setProperty("removeAbandonedTimeout", "600");
		}
		if(!properties.containsKey("testOnBorrow")) {
			properties.setProperty("testOnBorrow", "true");
		}
		if(!properties.containsKey("testOnReturn")) {
			properties.setProperty("testOnReturn", "false");
		}
		if(!properties.containsKey("testWhileIdle")) {
			properties.setProperty("testWhileIdle", "true");
		}
		if(!properties.containsKey("validationQuery")) {
			properties.setProperty("validationQuery", "select 1 from dual");
		}
		if(!properties.containsKey("minEvictableIdleTimeMillis")) {
			properties.setProperty("minEvictableIdleTimeMillis", "1800000");
		}
		if(!properties.containsKey("timeBetweenEvictionRunsMillis")) {
			properties.setProperty("timeBetweenEvictionRunsMillis", "300000");
		}
		
	}

	public void setDataSourceProperties() {
		if (ddsProperties.isEmpty()) {
			throw new RuntimeException(dataId + " has some problem. Please check.");
		}

		Properties properties = new Properties();

		for (String spn : ddsProperties.stringPropertyNames()) {
			properties.setProperty(spn, ddsProperties.getProperty(spn));
		}

		setDefaultDataSourceProperties(properties);
		
		for (String spn : ydd2Properties.stringPropertyNames()) {
			properties.setProperty(spn, ydd2Properties.getProperty(spn));
		}

		Set<String> slaveHeadSet = new TreeSet<String>();
		Set<String> propertiesSet = new HashSet<String>();
		Pattern pattern = Pattern.compile("(slave\\.\\d+\\.)?(\\w+(\\.encrypt)?)");
		for (Object keyObj : properties.keySet()) {
			Matcher matcher = pattern.matcher((String) keyObj);
			if (matcher.find()) {
				final String group1 = matcher.group(1);
				if (group1 != null) {
					slaveHeadSet.add(group1);
				}
				propertiesSet.add(matcher.group(2));
			}
		}

		int slaveCount = slaveHeadSet.size();
		List<Properties> dbPropertiesList = new ArrayList<Properties>(slaveCount + 1);
		for (int i = 0; i <= slaveCount; i++) {
			dbPropertiesList.add(new Properties());
		}

		String property, slaveProperty;
		String[] slaveHeads = slaveHeadSet.toArray(new String[slaveCount]);
		for (String propKey : propertiesSet) {
			property = properties.getProperty(propKey);
			if (property != null) {
				dbPropertiesList.get(0).put(propKey, property);
			}
			for (int i = 0; i < slaveHeads.length; i++) {
				final Properties tmpProperties = dbPropertiesList.get(i + 1);
				if ("initialSize".equals(propKey) || "minIdle".equals(propKey)) {
					tmpProperties.put(propKey, "0");
				} else {
					String slaveHead = slaveHeads[i];
					slaveProperty = properties.getProperty(slaveHead.concat(propKey));
					if (slaveProperty != null) {
						tmpProperties.put(propKey, slaveProperty);
					} else {
						tmpProperties.put(propKey, property);
					}
				}
			}
		}

		boolean setMonitorProperties = false;
		List<AbstractDataSource> adsList = new ArrayList<AbstractDataSource>(dbPropertiesList.size());
		for (int i = 0; i < dbPropertiesList.size(); i++) {
			if (i == 0) {
				adsList.add(generateAds(dbPropertiesList.get(i), dataId.substring(0, dataId.length() - 11).concat("-Master")));
				setMonitorProperties = adsList.get(i).getMdpRef().get() != null;
			} else {
				adsList.add(generateAds(dbPropertiesList.get(i), dataId.substring(0, dataId.length() - 11).concat("-").concat(slaveHeads[i - 1])));
			}
		}

		AbstractDataSource tmpAds = adsList.get(0);
		StringBuilder builder = new StringBuilder();
		builder.append("\n-------------动态数据源【").append(beanName).append("】代理模块启动-------------").append("\n-------------动态数据源【").append(beanName).append("】配置文件为").append(dataId)
				.append("-------------");
		if (setMonitorProperties) {
			builder.append("\n-------------动态数据源【").append(beanName).append("】性能监控模块启动-------------\n");
		}
		LOGGER.info(builder.toString());
		builder.delete(0, builder.length() - 1);

		if (adsFailoverCheckThread != null && adsFailoverCheckThread.isAlive()) {
			adsFailoverCheckThread.stopCheck();
			try {
				adsFailoverCheckThread.join();
			} catch (InterruptedException e) {
			}
			adsFailoverCheckThread = new AdsFailoverCheckThread();
		}

		currentAds = tmpAds;
		currentAdsIdx = 0;
		currentDbProperties = dbPropertiesList.get(currentAdsIdx);

		if (this.adsList.size() > 0) {
			for (AbstractDataSource ads : this.adsList) {
				ads.close();
			}
			this.adsList.clear();
			for (Properties dbProperties : this.dbPropertiesList) {
				dbProperties.clear();
			}
			this.dbPropertiesList.clear();
		}

		this.adsList.addAll(adsList);
		this.dbPropertiesList.addAll(dbPropertiesList);

		try {
			currentAds.getConnectionWithLock(false, null, null ,transactionIsolation).close();
			if (this.adsList.size() > 1) {
				adsFailoverCheckThread.start();
			}
		} catch (SQLException e) {
			if (this.adsList.size() > 1) {
				LOGGER.warn("\n-------------动态数据源【" + beanName + "】配置文件" + dataId + "调用不带用户名密码的getConnection()方法出错，自动Failover功能启动失败-------------", e);
			} else {
				LOGGER.warn("\n-------------动态数据源【" + beanName + "】配置文件" + dataId + "调用不带用户名密码的getConnection()方法出错-------------", e);
			}
		}
	}

	private AbstractDataSource generateAds(Properties properties, String dsName) {
		String key = "password";
		if (properties.containsKey(key)) {
			String pw = properties.remove(key).toString();
			final String decryptPw = Encrypter.decrypt(pw);
			if (StringUtils.isEmpty(decryptPw)) {
				properties.put(key, pw);
			} else {
				properties.put(key, decryptPw);
			}
		} else if (properties.containsKey("password.encrypt")) {
			properties.put(key, Encrypter.decrypt(properties.remove("password.encrypt").toString()));
		}
		
		String PropConnectTimeout = properties.getProperty(PROP_CONNECTTIMEOUT);
        if(StringUtils.isNotBlank(PropConnectTimeout))
        {
        	connectTimeout = PropConnectTimeout;
        }
        String PropSocketTimeout = properties.getProperty(PROP_SOCKETTIMEOUT);
        if(StringUtils.isNotBlank(PropSocketTimeout))
        {
        	socketTimeout = PropSocketTimeout;
        }
		String driverClassName = properties.getProperty(DRIVER_CLASS_NAME);
		if (StringUtils.isNotBlank(driverClassName)) {
			if("com.mysql.cj.jdbc.Driver".equals(driverClassName)) {
				properties.setProperty(DRIVER_CLASS_NAME, MySQLDriverCjMonitorProxy.class.getName());
				properties.setProperty(PROP_CONNECTIONPROPERTIES, "connectTimeout="+connectTimeout+";socketTimeout="+socketTimeout);
			}else {
				// com.mysql.jdbc.Driver
				properties.setProperty(DRIVER_CLASS_NAME, MySQLDriverMonitorProxy.class.getName());
				properties.setProperty(PROP_CONNECTIONPROPERTIES, "connectTimeout="+connectTimeout+";socketTimeout="+socketTimeout);
			}
		}
		String dataSourceClassName = properties.getProperty(DATA_SOURCE_CLASS_NAME_STR);
		if (StringUtils.isEmpty(dataSourceClassName)) {
			dataSourceClassName = "com.mars.ydd.dbcp.DbcpDataSource";
		}
		
		AbstractDataSource tmpDS;
		driverClassName = properties.getProperty(DRIVER_CLASS_NAME);
		if(driverClassName != null) {
			try {
				Class.forName(driverClassName, true, this.getClass().getClassLoader()).newInstance();
			} catch (Exception e) {
			}
		}
		try {
			tmpDS = (AbstractDataSource) Thread.currentThread().getContextClassLoader().loadClass(dataSourceClassName).newInstance();
			tmpDS.setDynamicDataSource(this);
			tmpDS.setDsName(dsName);
			tmpDS.setMonitorProperties(properties);
			tmpDS.setProperties(properties);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Wrong value in datasource property dataSourceClassName [" + dataSourceClassName + "].");
		} catch (Exception e) {
			throw new RuntimeException("Can't init datasource class.", e);
		}
		return tmpDS;
	}

	@Override
	public Connection getConnection() throws SQLException {
		failoverLock.readLock().lock();
		try {
			return currentAds.getConnectionWithLock(false, null, null ,transactionIsolation);
		} finally {
			failoverLock.readLock().unlock();
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		failoverLock.readLock().lock();
		try {
			return currentAds.getConnectionWithLock(true, username, password ,transactionIsolation);
		} finally {
			failoverLock.readLock().unlock();
		}
	}

	public void close() {
		for (AbstractDataSource ads : adsList) {
			ads.close();
		}
	}

	protected int flushMonitorData(boolean isTest) {
		int result = 0;
		for (AbstractDataSource ads : adsList) {
			result += ads.flushMonitorData(isTest);
		}
		return result;
	}

	protected int flushMonitorData() {
		return flushMonitorData(false);
	}

	protected AbstractDataSource getAds() {
		return currentAds;
	}

	protected Properties getDsProperties() {
		return currentDbProperties;
	}

	public String getTransactionIsolation() {
		return transactionIsolation;
	}

	public void setTransactionIsolation(String transactionIsolation) {
		this.transactionIsolation = transactionIsolation;
	}

	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}


	
}
