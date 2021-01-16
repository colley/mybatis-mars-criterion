package com.mars.ydd;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import com.mars.ydd.util.DynamicDbContext;

/**
 * author: hikin yao
 * version: 1.0
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
    private volatile List<Object> dataSourceLookupKeyList = new CopyOnWriteArrayList<Object>();
    private AtomicInteger sets = new AtomicInteger(0);
    private ThreadLocal<Object> currentLookupKey = new ThreadLocal<Object>();

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        for (Object lookupKey : super.resolvedDataSources.keySet()) {
            dataSourceLookupKeyList.add(lookupKey);
        }
        //后台维护线程
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        Long delayTime = 60 * 3L;//3分钟
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                checkDataSourceForRecover();
            }
        }, delayTime, delayTime, TimeUnit.SECONDS);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.getConnection(null, null);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        boolean needAuth = false;
        if (username != null && password != null) {
            needAuth = true;
        }
        Integer retryTimes = new Integer(dataSourceLookupKeyList.size());
        Connection conn = null;
        //当前线程数据源是主库类型或者当前强制主库开关开启，则直接切换到主库
        if (DynamicDbContext.isMasterDB() == true || isForceMasterDB() == true) {
            conn = null;
        } else {
            conn = determineTargetConnection(needAuth, username, password, retryTimes);
        }
        if (conn == null) {
            try {
                if (needAuth == true) {
                    conn = resolvedDefaultDataSource.getConnection(username, password);
                } else {
                    conn = resolvedDefaultDataSource.getConnection();
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("第 [defaultDataSource] 数据源连接成功!");
                }
            } catch (Exception e) {
                logger.error("第 [defaultDataSource] 数据源连接错误!", e);
                sendDBErrorMsg("数据库异常", "---第 [defaultDataSource] 数据源连接错误!", e);
                throw new SQLException(e.getMessage());
            }
        }
        return conn;
    }

    private Connection determineTargetConnection(boolean needAuth, String username, String password, int h) {
        Connection conn = null;
        if (h > 0) {
            try {
                DataSource targetDataSource = determineTargetDataSource();
                if (needAuth == true) {
                    conn = targetDataSource.getConnection(username, password);
                } else {
                    conn = targetDataSource.getConnection();
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("\n第  [" + currentLookupKey.get() + "] 数据源连接成功!");
                }
            } catch (Exception e) {
                //剔除当前无效数据源
                if (dataSourceLookupKeyList.remove(currentLookupKey.get())) {
                    logger.error("---[!!!重要!!!]第 [" + currentLookupKey.get() + "] 数据源连接失败,已被剔除!", e);
                    sendDBErrorMsg("数据库异常", "---第 [" + currentLookupKey.get() + "] 数据源连接失败,已被剔除!", e);
                } else {
                    logger.error("第  [" + currentLookupKey.get() + "] 数据源连接失败!", e);
                }
                conn = determineTargetConnection(needAuth, username, password, --h);
            }
        }
        return conn;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        int i = sets.incrementAndGet();
        if (i < 0) {
            sets = new AtomicInteger(0);
            i = 0;
        }
        Object lookupKey = null;
        if (dataSourceLookupKeyList != null && dataSourceLookupKeyList.size() > 0) {
            int index = i % dataSourceLookupKeyList.size();
            lookupKey = dataSourceLookupKeyList.get(index);
            currentLookupKey.set(lookupKey);
        }
        return lookupKey;
    }

    /**
     * 是否强制主库运行
     *
     * @return
     */
    protected boolean isForceMasterDB() {
        return false;
    }

    /**
     * 发送异常信息报警
     *
     * @param tag
     * @param title
     * @param content
     */
    protected void sendDBErrorMsg(String tag, String title, String content) {
        return;
    }

    protected void sendDBErrorMsg(String tag, String title, Throwable t) {
        return;
    }

    /**
     * 检查之前踢出的数据源是否已经复活,如果已经复活重新放进环中
     */
    private void checkDataSourceForRecover() {
        try {
            //当前有备库数据源并且非强制主库(即允许备库查询)
            if (resolvedDataSources != null && resolvedDataSources.size() > 0 && isForceMasterDB() == false) {
                for (Iterator it = resolvedDataSources.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) it.next();
                    Object lookupKey = entry.getKey();
                    DataSource dataSource = (DataSource) entry.getValue();
                    //如果当前数据源已经踢出了,检查一下当前是否已经存活
                    if (dataSourceLookupKeyList.contains(lookupKey) == false) {
                        Connection conn = null;
                        try {
                            conn = dataSource.getConnection();
                            if (conn != null) {
                                dataSourceLookupKeyList.add(lookupKey);
                                logger.error("第[" + lookupKey + "] 数据源已连接,成功复活!");
                                sendDBErrorMsg("数据库恢复", "---第 [" + lookupKey + "] 数据源连接成功,已复活!", "---[!!!重要!!!]第 [" + lookupKey + "] 数据源连接成功,已复活!");
                            }
                        } catch (Exception e) {
                            logger.error("第[" + lookupKey + "] 数据源依然连接错误,未复活!", e);
                            sendDBErrorMsg("数据库异常", "---第[" + lookupKey + "] 数据源连接错误,未复活!", e);
                        } finally {
                            if (conn != null) {
                                conn.close();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
