package com.mars.ydd.monitor.processor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.mars.ydd.monitor.data.DataSourceIdentityData;
import com.mars.ydd.monitor.data.OperationRecordData;
import com.mars.ydd.monitor.data.OperationRecordData.OrdContainer;

public class MonitorDataProcessor {

    public enum DbType {
        ORACLE, MYSQL, H2, UNKNOW
    }

    private final static Logger LOGGER = LoggerFactory.getLogger(MonitorDataProcessor.class);

    private final static long marginTime = 1000;
    private final static int cacheRatio = 256;
    private volatile boolean continueRun = false;
    private final long archiveIdleTimeMillis;
    private final long slowSqlThreshold;
    private final long connMaxWait;
    private final int errMsgLimit;
    private final DbType dbtype;
    private final String dsName;
    private final Object sendWaitObj = new Object();
    private final Object flushWaitObj = new Object();
    private final Object cacheDataWaitObj = new Object();

    private volatile ConnectionPool connPool;
    
    private final DataSourceIdentityData dataSourceIdentityData;

    private final RingBuffer<OrdContainer> recordContainer;

    //For test only
    public static class MdsContainer {
        private MonitorDataStructure mds;

        public MonitorDataStructure getMds() {
            return mds;
        }

        public void setMds(MonitorDataStructure mds) {
            this.mds = mds;
        }

        public final static EventFactory<MdsContainer> MDS_FACTORY = new EventFactory<MdsContainer>() {
            public MdsContainer newInstance() {
                return new MdsContainer();
            }
        };
    }

    //For test only
    protected static RingBuffer<MdsContainer> testMDSRingBuffer;

    private volatile MonitorDataStructure monitorDataStructure;

    private volatile Properties switchPro;
    
    public Properties getSwitchPro() {
		return switchPro;
	}

	private final AtomicInteger sendCount = new AtomicInteger(0);

    private class DataSendThread extends Thread {

        @Override
        public void run() {
            Thread.currentThread().setName("-" + dsName + "-YDD Data Send Thread-");
            long sleepEscape = archiveIdleTimeMillis + Math.max(connMaxWait, slowSqlThreshold) + marginTime;
            MonitorDataStructure currentMDS = null;

            boolean isTestRunning = testMDSRingBuffer != null;

            while (true) {

                if (!continueRun && monitorDataStructure.isEmpty()) {
                    LOGGER.info("-" + dsName + "-YDD Data Send Thread Finish-");
                    return;
                }

                if (continueRun) {
                    Random random = new Random(System.currentTimeMillis());
                    long randomAdjust = random.nextInt(1000);
                    try {
                        synchronized (sendWaitObj) {
                            sendWaitObj.wait(sleepEscape - System.currentTimeMillis() % archiveIdleTimeMillis
                                    + randomAdjust);
                        }
                    } catch (InterruptedException e) {
                    }
                }

                if (!monitorDataStructure.isEmpty()) {
                    synchronized (cacheDataWaitObj) {
                        currentMDS = monitorDataStructure;
                        monitorDataStructure = new MonitorDataStructure(dataSourceIdentityData);
                    }
                }
                
                if(switchPro.isEmpty()){
                	switchPro.put("asrdSwitch", true);
                	switchPro.put("dssdSwitch", true);
                	switchPro.put("ssdSwitch", true);
                 }

                if (currentMDS != null) {
                    if (isTestRunning) {
                        long next = testMDSRingBuffer.next();
                        MdsContainer mdsContainer = testMDSRingBuffer.get(next);
                        mdsContainer.setMds(currentMDS);
                        testMDSRingBuffer.publish(next);
                    } else {
//                        MonitorJmsSendUtil.sendMessageAwait(currentMDS, JUMPER_TOPIC_NAME);
                    }
//                    if (LOGGER.isDebugEnabled()) {
//                        LOGGER.debug("\n\nSend message < "
//                                + JSON.toJSONString(currentMDS, SerializerFeature.WriteDateUseDateFormat,
//                                        SerializerFeature.SkipTransientField, SerializerFeature.SortField,
//                                        SerializerFeature.DisableCircularReferenceDetect) + " > Sent.\n\n");
//                    }
                    sendCount.incrementAndGet();
                    currentMDS = null;
                }
                synchronized (flushWaitObj) {
                    flushWaitObj.notifyAll();
                    try {
                        flushWaitObj.wait(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    private final EventHandler<OrdContainer> ordEventHandler = new EventHandler<OrdContainer>() {

        private final AtomicInteger errorOutputCount = new AtomicInteger();

        @Override
        public void onEvent(OrdContainer event, long sequence, boolean endOfBatch) throws Exception {
            synchronized (cacheDataWaitObj) {
                try {
                    event.getOrd().transfer(monitorDataStructure);
                    errorOutputCount.set(0);
                } catch (Throwable e) {
                    long tmp = errorOutputCount.getAndIncrement();
                    if (tmp % 10 == 0 && tmp <= 100) {
                        LOGGER.error("OperationRecordData process error.", e);
                    }
                }
            }
        }

    };

    private volatile Thread dataSendThread = new DataSendThread();
    private final Disruptor<OrdContainer> disruptor;
    private final List<Thread> ordProcessThreadList = Collections.synchronizedList(new ArrayList<Thread>(8));
    private final ExecutorService execService = Executors.newCachedThreadPool(new ThreadFactory() {
        private final AtomicInteger threadCount = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "-" + dsName + "-ORD Process Thread-" + threadCount.incrementAndGet() + "-");
            thread.setDaemon(true);
            ordProcessThreadList.add(thread);
            return thread;
        }
    });

    @SuppressWarnings("unchecked")
    public MonitorDataProcessor(long archiveIdleTimeMillis, long slowSqlThreshold, int errMsgLimit,
            long createConnMaxWait, int maxActive, DataSourceIdentityData dataSourceIdentityData, String dsName, Properties switchPro) {
        this.archiveIdleTimeMillis = archiveIdleTimeMillis;
        this.slowSqlThreshold = slowSqlThreshold;
        this.connMaxWait = createConnMaxWait;
        this.errMsgLimit = errMsgLimit;
        this.dataSourceIdentityData = dataSourceIdentityData;
        this.monitorDataStructure = new MonitorDataStructure(dataSourceIdentityData);
        this.switchPro = switchPro;
        this.disruptor = new Disruptor<OrdContainer>(
                OrdContainer.ORD_FACTORY,
                BigDecimal.valueOf(2).pow(
                        BigDecimal.valueOf(Math.log(maxActive) / Math.log(2)).setScale(0, RoundingMode.CEILING).intValue()).intValue()
                        * cacheRatio, execService);
        this.disruptor.handleEventsWith(ordEventHandler);
        this.recordContainer = disruptor.getRingBuffer();

        String jdbcUrl = dataSourceIdentityData.getJdbcUrl();
        this.dsName = dsName;
        if (jdbcUrl.indexOf("jdbc:oracle") == 0) {
            dbtype = DbType.ORACLE;
        } else if (jdbcUrl.indexOf("jdbc:mysql") == 0) {
            dbtype = DbType.MYSQL;
        } else if (jdbcUrl.indexOf("jdbc:h2") == 0) {
            dbtype = DbType.H2;
        } else {
            dbtype = DbType.UNKNOW;
        }
    }

    public void putOperationRecord(OperationRecordData operationRecordData) {
        if (continueRun) {
            try {
                long next = recordContainer.next();
                OrdContainer ordContainer = recordContainer.get(next);
                ordContainer.setOrd(operationRecordData);
                recordContainer.publish(next);
            } catch (Throwable e) {
            }
        }
    }

    public void startWork() {
        continueRun = true;
        disruptor.start();
        dataSendThread.setDaemon(true);
        dataSendThread.start();
    }

    public int stopWork() {
        disruptor.shutdown();
        continueRun = false;
        if (dataSendThread.isAlive()) {
            try {
                synchronized (sendWaitObj) {
                    sendWaitObj.notifyAll();
                }
                dataSendThread.join();
            } catch (InterruptedException e) {
            }
        }
        int result = sendCount.intValue();
        sendCount.set(0);
        dataSendThread = new DataSendThread();
        return result;
    }

    public int flushMonitorData(boolean isTest) {
        int result = 0;
        synchronized (sendWaitObj) {
            sendWaitObj.notifyAll();
        }
        synchronized (flushWaitObj) {
            try {
                flushWaitObj.wait(60000);
            } catch (InterruptedException e) {
            }
            if (isTest) {
                result = sendCount.getAndSet(0);
            } else {
                result = sendCount.get();
            }
            flushWaitObj.notifyAll();
        }
        return result;
    }

    public DbType getDbtype() {
        return dbtype;
    }

    public long getArchiveIdleTimeMillis() {
        return archiveIdleTimeMillis;
    }

    public DataSourceIdentityData getDataSourceIdentityData() {
        return dataSourceIdentityData;
    }

    public long getSlowSqlThreshold() {
        return slowSqlThreshold;
    }

    public int getErrMsgLimit() {
        return errMsgLimit;
    }

    public ConnectionPool getConnPool() {
        return connPool;
    }

    public void setConnPool(ConnectionPool connPool) {
        this.connPool = connPool;
    }
}