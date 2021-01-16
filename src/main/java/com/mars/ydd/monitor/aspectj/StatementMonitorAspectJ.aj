package com.yihaodian.ydd.monitor.aspectj;

import java.sql.BatchUpdateException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.lang.Boolean;

import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.FinishQueryStmtTimeoutErr;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.FinishUpdateStmtTimeoutErr;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.StmtErrErr;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.FinishBatchStmtTimeoutErr;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.BatchStmtPartialSucErr;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.BatchStmt;


import com.yihaodian.ydd.monitor.data.optrecdata.ResultSetOptRecData.OpenResultSet;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.BatchStmtErr;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.BatchStmtPartialSuc;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.ExecuteBatchStmt;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.ExecuteStmt;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.FinishBatchStmt;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.FinishBatchStmtTimeout;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.FinishQueryStmt;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.FinishQueryStmtTimeout;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.FinishUpdateStmt;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.FinishUpdateStmtTimeout;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.StmtErr;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.StmtUpdateCount;
import com.yihaodian.ydd.monitor.processor.MonitorDataProcessor;
import com.yihaodian.ydd.monitor.proxy.ResultSetMonitorProxy;
import com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy;
import com.yihaodian.ydd.monitor.util.SqlUtils;

public aspect StatementMonitorAspectJ {
    pointcut recordExecute(StatementMonitorProxy smp, String sql) : execution(boolean com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy.execute(String,..) throws SQLException)
		&& args(sql,..) && target(smp) && within(com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy);

    boolean around(StatementMonitorProxy smp, String sql) throws SQLException : recordExecute(smp, sql) {
        boolean result = false;
        long currentTimeMillis = System.currentTimeMillis();
        final MonitorDataProcessor processor = smp.getMonitorDataProcessor();
        if (processor != null) {
        final String preparedSql = SqlUtils.generatePreparedStatement(sql, processor);
        smp.setLastPreparedSql(preparedSql);
        smp.setLastExecuteSqlTime(currentTimeMillis);
        Properties switchPro = processor.getSwitchPro(); 
        Boolean ssdSwitch = false;
        Boolean asrdSwitch = false;
        if(switchPro!=null){
         ssdSwitch = (Boolean)switchPro.get("ssdSwitch");
         asrdSwitch = (Boolean)switchPro.get("asrdSwitch");
        }
        if(ssdSwitch){
        processor.putOperationRecord(new ExecuteStmt(processor, currentTimeMillis, preparedSql));
        }
        long startTime = System.currentTimeMillis();
        try {
            result = proceed(smp, sql);
            long elapseTime = System.currentTimeMillis() - startTime;
            if (elapseTime <= processor.getSlowSqlThreshold()) {
                if (result) {
                 if(ssdSwitch){
                    processor.putOperationRecord(new FinishQueryStmt(processor, currentTimeMillis, preparedSql,
                            elapseTime)); //Query无法直接得到真正的查询的行数，必须通过ResultSet实际查询的行数来获得。
                            }
                } else {
                 if(ssdSwitch){
                    processor.putOperationRecord(new FinishUpdateStmt(processor, currentTimeMillis, preparedSql,
                            elapseTime)); //无法直接获得更新数量，需要等待用户调用Statement.getUpdateCount。
                            }
                }
            } else {
                if (result) {
                    if(ssdSwitch){
                    processor.putOperationRecord(new FinishQueryStmtTimeout(processor, currentTimeMillis, preparedSql,
                             elapseTime)); 
                    }
                    if(asrdSwitch){
                    processor.putOperationRecord(new FinishQueryStmtTimeoutErr(processor, currentTimeMillis, preparedSql,
                            sql, elapseTime)); //Query无法直接得到真正的查询的行数，必须通过ResultSet实际查询的行数来获得。
                    }        
                } else {
                 if(ssdSwitch){
                    processor.putOperationRecord(new FinishUpdateStmtTimeout(processor, currentTimeMillis, preparedSql,
                           elapseTime)); 
                            }
                 if(asrdSwitch){        
                    processor.putOperationRecord(new FinishUpdateStmtTimeoutErr(processor, currentTimeMillis, preparedSql,
                            sql, elapseTime));      
                            }   
                    }
                }
            } catch (SQLException e) {
        if(ssdSwitch){
            processor.putOperationRecord(new StmtErr(processor, currentTimeMillis, preparedSql, e ));
            }
         if(asrdSwitch){
            processor.putOperationRecord(new StmtErrErr(processor, currentTimeMillis, preparedSql, e , sql));
         }   
            throw e;
        }
        }
        return result;
    }

    pointcut recordExecuteUpdate(StatementMonitorProxy smp, String sql) : execution(int com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy.executeUpdate(String,..) throws SQLException)
		&& args(sql,..) && target(smp) && within(com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy);

    int around(StatementMonitorProxy smp, String sql) throws SQLException : recordExecuteUpdate(smp, sql) {
        int result = -1;
        long currentTimeMillis = System.currentTimeMillis();
        final MonitorDataProcessor processor = smp.getMonitorDataProcessor();
        if (processor != null) {
         Properties switchPro = processor.getSwitchPro(); 
        Boolean ssdSwitch = false;
        Boolean asrdSwitch = false;
        if(switchPro!=null){
         ssdSwitch = (Boolean)switchPro.get("ssdSwitch");
         asrdSwitch = (Boolean)switchPro.get("asrdSwitch");
        }
        final String preparedSql = SqlUtils.generatePreparedStatement(sql, processor);
        if(ssdSwitch){
        processor.putOperationRecord(new ExecuteStmt(processor, currentTimeMillis, preparedSql));
        }
        long startTime = System.currentTimeMillis();
        try {
            result = proceed(smp, sql);
            long elapseTime = System.currentTimeMillis() - startTime;
            if (elapseTime <= processor.getSlowSqlThreshold()) {
            if(ssdSwitch){
                processor.putOperationRecord(new FinishUpdateStmt(processor, currentTimeMillis, preparedSql,
                        elapseTime, result));
                        }
            } else {
             if(ssdSwitch){
                processor.putOperationRecord(new FinishUpdateStmtTimeout(processor, currentTimeMillis, preparedSql,
                        elapseTime, result));
                        }
             if(asrdSwitch){      
                processor.putOperationRecord(new FinishUpdateStmtTimeoutErr(processor, currentTimeMillis, preparedSql,
                        sql, elapseTime ));        
                        }
            }
        } catch (SQLException e) {
         if(ssdSwitch){
            processor.putOperationRecord(new StmtErr(processor, currentTimeMillis, preparedSql, e ));
            }
          if(asrdSwitch){        
            processor.putOperationRecord(new StmtErrErr(processor, currentTimeMillis, preparedSql, e , sql));
            }
            throw e;
        }
        }
        return result;
    }

    pointcut recordExecuteQuery(StatementMonitorProxy smp, String sql) : execution(java.sql.ResultSet com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy.executeQuery(String,..) throws SQLException)
        && args(sql,..) && target(smp) && within(com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy);

    ResultSet around(StatementMonitorProxy smp, String sql) throws SQLException : recordExecuteQuery(smp, sql) {
        ResultSet result = null;
        long currentTimeMillis = System.currentTimeMillis();
        final MonitorDataProcessor processor = smp.getMonitorDataProcessor();
        if (processor != null) {
        final String preparedSql = SqlUtils.generatePreparedStatement(sql, processor);
        Properties switchPro = processor.getSwitchPro(); 
        Boolean ssdSwitch = false;
        Boolean asrdSwitch = false;
        if(switchPro!=null){
         ssdSwitch = (Boolean)switchPro.get("ssdSwitch");
         asrdSwitch = (Boolean)switchPro.get("asrdSwitch");
        }
        if(ssdSwitch){
        processor.putOperationRecord(new ExecuteStmt(processor, currentTimeMillis, preparedSql));
        }
        long startTime = System.currentTimeMillis();
        try {
            result = new ResultSetMonitorProxy(proceed(smp, sql), preparedSql, currentTimeMillis, processor);
            long elapseTime = System.currentTimeMillis() - startTime;
            if (elapseTime <= processor.getSlowSqlThreshold()) {
               if(ssdSwitch){
                processor.putOperationRecord(new FinishQueryStmt(processor, currentTimeMillis, preparedSql, elapseTime)); //Query无法直接得到真正的查询的行数，必须通过ResultSet实际查询的行数来获得。
                }
            } else {
            if(ssdSwitch){
                     processor.putOperationRecord(new FinishQueryStmtTimeout(processor, currentTimeMillis, preparedSql, 
                        elapseTime));
                    }
                    if(asrdSwitch){
                     processor.putOperationRecord(new FinishQueryStmtTimeoutErr(processor, currentTimeMillis, preparedSql, sql,
                        elapseTime));
                    } 
            }
            if(ssdSwitch){
            processor.putOperationRecord(new OpenResultSet(processor, currentTimeMillis, preparedSql));
            }
        } catch (SQLException e) {
          if(ssdSwitch){
            processor.putOperationRecord(new StmtErr(processor, currentTimeMillis, preparedSql, e ));
            }
          if(asrdSwitch){        
            processor.putOperationRecord(new StmtErrErr(processor, currentTimeMillis, preparedSql, e, sql));
            }
            throw e;
        }
        }
        return result;
    }

    ResultSet around(StatementMonitorProxy smp) throws SQLException : execution(java.sql.ResultSet com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy.getResultSet() throws SQLException)
        && target(smp) && within(com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy) {
        ResultSet result = null;
        final String preparedSql = smp.getLastPreparedSql();
        final MonitorDataProcessor processor = smp.getMonitorDataProcessor();
        if (processor != null) {
        Properties switchPro = processor.getSwitchPro(); 
        Boolean ssdSwitch = false;
        if(switchPro!=null){
         ssdSwitch = (Boolean)switchPro.get("ssdSwitch");
        }
        result = new ResultSetMonitorProxy(proceed(smp), preparedSql, smp.getLastExecuteSqlTime(), processor);
        if(ssdSwitch){
        processor.putOperationRecord(new OpenResultSet(processor, smp.getLastExecuteSqlTime(), preparedSql));
        }
        }
        return result;
    }

    before(StatementMonitorProxy smp, String sql) : execution(void com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy.addBatch(String) throws SQLException)
        && args(sql,..) && target(smp) && within(com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy) {
        smp.getBatchSqlList().add(sql);
    }

    before(StatementMonitorProxy smp) : execution(void com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy.clearBatch() throws SQLException)
        && target(smp) && within(com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy) {
        smp.getBatchSqlList().clear();
    }

    pointcut recordExecuteBatch(StatementMonitorProxy smp) : execution(int[] com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy.executeBatch() throws SQLException)
		&& target(smp) && within(com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy);

    int[] around(StatementMonitorProxy smp) throws SQLException : recordExecuteBatch(smp) {
        int[] result = null;
        long currentTimeMillis = System.currentTimeMillis();
        final MonitorDataProcessor processor = smp.getMonitorDataProcessor();
        if (processor != null) {
        Properties switchPro = processor.getSwitchPro(); 
        Boolean ssdSwitch = false;
        Boolean asrdSwitch = false;
        if(switchPro!=null){
         ssdSwitch = (Boolean)switchPro.get("ssdSwitch");
         asrdSwitch = (Boolean)switchPro.get("asrdSwitch");
        }
        final List<String> batchSqlList = smp.getBatchSqlList();
        if(ssdSwitch){
        processor.putOperationRecord(new ExecuteBatchStmt(processor, currentTimeMillis, batchSqlList));
        }
        long startTime = System.currentTimeMillis();
        try {
            result = proceed(smp);
            long elapseTime = System.currentTimeMillis() - startTime;
            if (elapseTime <= processor.getSlowSqlThreshold() * batchSqlList.size()) {
            if(ssdSwitch){
                processor.putOperationRecord(new FinishBatchStmt(processor, currentTimeMillis, batchSqlList,
                        elapseTime, result));
                        }
            } else {
            if(ssdSwitch){
                processor.putOperationRecord(new FinishBatchStmtTimeout(processor, currentTimeMillis, batchSqlList,
                        elapseTime, result));
                }
            if(asrdSwitch){           
                processor.putOperationRecord(new FinishBatchStmtTimeoutErr(processor, currentTimeMillis, batchSqlList,
                        elapseTime, result));   
                        }     
            }
        } catch (SQLException e) {
            long elapseTime = System.currentTimeMillis() - startTime;
            if (e instanceof BatchUpdateException) {
                BatchUpdateException bue = (BatchUpdateException) e;
                result = bue.getUpdateCounts();
                if(ssdSwitch){
                processor.putOperationRecord(new BatchStmtPartialSuc(processor, currentTimeMillis, batchSqlList,
                        elapseTime, e, result));
                        }
                if(asrdSwitch){
                processor.putOperationRecord(new BatchStmtPartialSucErr(processor, currentTimeMillis, batchSqlList,
                        elapseTime, e, result));
                }        
            } else {
                if(ssdSwitch){
                processor.putOperationRecord(new BatchStmt(processor, currentTimeMillis, batchSqlList, elapseTime, e));
                }
                if(asrdSwitch){
                processor.putOperationRecord(new BatchStmtErr(processor, currentTimeMillis, batchSqlList, elapseTime, e));
                }
            }
            throw e;
        } finally {
            batchSqlList.clear();
        }
        }
        return result;
    }

    after(StatementMonitorProxy smp) returning(int effectRowCount) : execution(int com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy.getUpdateCount() throws SQLException)
        && target(smp) && within(com.yihaodian.ydd.monitor.proxy.StatementMonitorProxy) {
        final MonitorDataProcessor processor = smp.getMonitorDataProcessor();
        if (processor != null) {
        Properties switchPro = processor.getSwitchPro(); 
        Boolean ssdSwitch = false;
        if(switchPro!=null){
         ssdSwitch = (Boolean)switchPro.get("ssdSwitch");
        }
        if(ssdSwitch){        
        processor.putOperationRecord(new StmtUpdateCount(processor, smp.getLastExecuteSqlTime(),
                smp.getLastPreparedSql(), effectRowCount));
                }
                }
    }
}
