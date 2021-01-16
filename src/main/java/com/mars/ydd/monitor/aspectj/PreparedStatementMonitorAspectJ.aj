package com.yihaodian.ydd.monitor.aspectj;

import java.sql.BatchUpdateException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.lang.Boolean;

import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.FinishQueryStmtTimeoutErr;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.FinishUpdateStmtTimeoutErr;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.StmtErrErr;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.FinishBatchStmtTimeoutErr;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.BatchStmtPartialSucErr;
import com.yihaodian.ydd.monitor.data.optrecdata.StatementOptRecData.BatchStmt;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.PreparedStmt;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.FinishUpdatePreparedStmtTimeoutErr;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.FinishQueryPreparedStmtTimeoutErr;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.FinishBatchPreparedStmtTimeoutErr;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.BatchPreparedStmtPartialSucErr;

import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.FinishQueryPreparedStmtTimeoutErr;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.BatchPreparedStmtErr;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.BatchPreparedStmtPartialSuc;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.ExecuteBatchPreparedStmt;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.ExecutePreparedStmt;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.FinishBatchPreparedStmt;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.FinishBatchPreparedStmtTimeout;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.FinishQueryPreparedStmt;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.FinishQueryPreparedStmtTimeout;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.FinishUpdatePreparedStmt;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.FinishUpdatePreparedStmtTimeout;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.PreparedStmtErr;
import com.yihaodian.ydd.monitor.data.optrecdata.PreparedStatementOptRecData.PreparedStmtUpdateCount;
import com.yihaodian.ydd.monitor.data.optrecdata.ResultSetOptRecData.OpenResultSet;
import com.yihaodian.ydd.monitor.processor.MonitorDataProcessor;
import com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy;
import com.yihaodian.ydd.monitor.proxy.ResultSetMonitorProxy;

public aspect PreparedStatementMonitorAspectJ {

    private static final String UNKNOW_STR = "<>";
    private static final String NULL_STR = "NULL";
    private static final String SET_NULL = "setNull";

    pointcut recordExecute(PreparedStatementMonitorProxy psmp) : execution(boolean com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy.execute(..) throws SQLException)
	&& target(psmp) && within(com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy);

    boolean around(PreparedStatementMonitorProxy psmp) throws SQLException : recordExecute(psmp) {
        boolean result = false;
        long currentTimeMillis = System.currentTimeMillis();
        psmp.setLastExecuteSqlTime(currentTimeMillis);
        final String preparedSql = psmp.getPreparedSql();
        final MonitorDataProcessor processor = psmp.getMonitorDataProcessor();
        if (processor != null) {
        Properties switchPro = processor.getSwitchPro(); 
        Boolean ssdSwitch = false;
        Boolean asrdSwitch = false;
        if(switchPro!=null){
         ssdSwitch = (Boolean)switchPro.get("ssdSwitch");
         asrdSwitch = (Boolean)switchPro.get("asrdSwitch");
        }
        final List<Object[][]> parameterValueList = psmp.getParameterValueList();
        if (parameterValueList.isEmpty()) {
            parameterValueList.add(new Object[0][]);
        } else if (parameterValueList.size() == 1) {
            Object[][] parameterValue = parameterValueList.get(0);
            int i = 0;
            for (; i < parameterValue.length && parameterValue[i] != null; i++) {
            }
            parameterValueList.remove(0);
            parameterValueList.add(0, Arrays.copyOf(parameterValue, i));
        }
        if(ssdSwitch){
        processor.putOperationRecord(new ExecutePreparedStmt(processor, currentTimeMillis, preparedSql));
        }
        long startTime = System.currentTimeMillis();
        try {
            result = proceed(psmp);
            long elapseTime = System.currentTimeMillis() - startTime;
            if (elapseTime <= processor.getSlowSqlThreshold()) {
                if (result) {
                    if(ssdSwitch){
                    processor.putOperationRecord(new FinishQueryPreparedStmt(processor, currentTimeMillis, preparedSql,
                            elapseTime)); //Query无法直接得到真正的查询的行数，必须通过ResultSet实际查询的行数来获得。
                            }
                } else {
                    if(ssdSwitch){
                    processor.putOperationRecord(new FinishUpdatePreparedStmt(processor, currentTimeMillis,
                            preparedSql, elapseTime)); //无法直接获得更新数量，需要等待用户调用Statement.getUpdateCount。
                            }
                }
            } else {
                if (result) {
                    if(ssdSwitch){
                    processor.putOperationRecord(new FinishQueryPreparedStmtTimeout(processor, currentTimeMillis,
                            preparedSql, elapseTime)); //Query无法直接得到真正的查询的行数，必须通过ResultSet实际查询的行数来获得。
                             }
                    if(asrdSwitch){       
                      processor.putOperationRecord(new FinishQueryPreparedStmtTimeoutErr(processor, currentTimeMillis,
                            preparedSql, parameterValueList.get(0), elapseTime)); 
                      }     
                    
                } else {
                if(ssdSwitch){
                    processor.putOperationRecord(new FinishUpdatePreparedStmtTimeout(processor, currentTimeMillis,
                            preparedSql, elapseTime)); //无法直接获得更新数量，需要等待用户调用Statement.getUpdateCount。
                  }
                  if(asrdSwitch){  
                    processor.putOperationRecord(new FinishUpdatePreparedStmtTimeoutErr(processor, currentTimeMillis,
                            preparedSql, parameterValueList.get(0), elapseTime));
                  }
                }
            }
        } catch (SQLException e) {
        if(ssdSwitch){
            processor.putOperationRecord(new PreparedStmt(processor, currentTimeMillis, preparedSql, e ));
                    }
       if(asrdSwitch){
            processor.putOperationRecord(new PreparedStmtErr(processor, currentTimeMillis, preparedSql, e,
                    parameterValueList.get(0))); 
                    }       
            throw e;
        } finally {
            parameterValueList.clear();
        }
        }
        return result;
     
    }

    pointcut recordExecuteUpdate(PreparedStatementMonitorProxy psmp) : execution(int com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy.executeUpdate(..) throws SQLException)
	&& target(psmp) && within(com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy);

    int around(PreparedStatementMonitorProxy psmp) throws SQLException : recordExecuteUpdate(psmp) {
//        return proceed(psmp);
        int result = -1;
        long currentTimeMillis = System.currentTimeMillis();
        final String preparedSql = psmp.getPreparedSql();
        final MonitorDataProcessor processor = psmp.getMonitorDataProcessor();
        if (processor != null) {
        Properties switchPro = processor.getSwitchPro(); 
        Boolean ssdSwitch = false;
        Boolean asrdSwitch = false;
        if(switchPro!=null){
         ssdSwitch = (Boolean)switchPro.get("ssdSwitch");
         asrdSwitch = (Boolean)switchPro.get("asrdSwitch");
        }
        final List<Object[][]> parameterValueList = psmp.getParameterValueList();
        if (parameterValueList.isEmpty()) {
            parameterValueList.add(new Object[0][]);
        } else if (parameterValueList.size() == 1) {
            Object[][] parameterValue = parameterValueList.get(0);
            int i = 0;
            for (; i < parameterValue.length && parameterValue[i] != null; i++) {
            }
            parameterValueList.remove(0);
            parameterValueList.add(0, Arrays.copyOf(parameterValue, i));
        }
        if(ssdSwitch){
        processor.putOperationRecord(new ExecutePreparedStmt(processor, currentTimeMillis, preparedSql));
        }
        long startTime = System.currentTimeMillis();
        try {
            result = proceed(psmp);
            long elapseTime = System.currentTimeMillis() - startTime;
            if (elapseTime <= processor.getSlowSqlThreshold()) {
                if(ssdSwitch){
                processor.putOperationRecord(new FinishUpdatePreparedStmt(processor, currentTimeMillis, preparedSql,
                        elapseTime, result));
                        }
            } else {
            if(ssdSwitch){
                processor.putOperationRecord(new FinishUpdatePreparedStmtTimeout(processor, currentTimeMillis,
                        preparedSql, elapseTime, result));
                        }
            if(asrdSwitch) {
             processor.putOperationRecord(new FinishUpdatePreparedStmtTimeoutErr(processor, currentTimeMillis,
                        preparedSql, parameterValueList.get(0), elapseTime, result));          
            }
            }
        } catch (SQLException e) {
        if(ssdSwitch){
            processor.putOperationRecord(new PreparedStmt(processor, currentTimeMillis, preparedSql, e));
                    }
        if(asrdSwitch){         
            processor.putOperationRecord(new PreparedStmtErr(processor, currentTimeMillis, preparedSql, e,
                    parameterValueList.get(0)));
                    }
            throw e;
        } finally {
            parameterValueList.clear();
        }
        }
        return result;

    }

    pointcut recordExecuteQuery(PreparedStatementMonitorProxy psmp) : execution(java.sql.ResultSet com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy.executeQuery(..) throws SQLException)
	&& target(psmp) && within(com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy);

    ResultSet around(PreparedStatementMonitorProxy psmp) throws SQLException : recordExecuteQuery(psmp) {
//        return proceed(psmp);
        ResultSet result = null;
        long currentTimeMillis = System.currentTimeMillis();
        final String preparedSql = psmp.getPreparedSql();
        final MonitorDataProcessor processor = psmp.getMonitorDataProcessor();
        if (processor != null) {
        Properties switchPro = processor.getSwitchPro(); 
        Boolean ssdSwitch = false;
        Boolean asrdSwitch = false;
        if(switchPro!=null){
         ssdSwitch = (Boolean)switchPro.get("ssdSwitch");
         asrdSwitch = (Boolean)switchPro.get("asrdSwitch");
        }
        final List<Object[][]> parameterValueList = psmp.getParameterValueList();
        if (parameterValueList.isEmpty()) {
            parameterValueList.add(new Object[0][]);
        } else if (parameterValueList.size() == 1) {
            Object[][] parameterValue = parameterValueList.get(0);
            int i = 0;
            for (; i < parameterValue.length && parameterValue[i] != null; i++) {
            }
            parameterValueList.remove(0);
            parameterValueList.add(0, Arrays.copyOf(parameterValue, i));
        }
        if(ssdSwitch){
        processor.putOperationRecord(new ExecutePreparedStmt(processor, currentTimeMillis, preparedSql));
        }
        long startTime = System.currentTimeMillis();
        try {
            result = new ResultSetMonitorProxy(proceed(psmp), preparedSql, currentTimeMillis, processor);
            long elapseTime = System.currentTimeMillis() - startTime;
            if (elapseTime <= processor.getSlowSqlThreshold()) {
            if(ssdSwitch){
                processor.putOperationRecord(new FinishQueryPreparedStmt(processor, currentTimeMillis, preparedSql,
                        elapseTime)); //Query无法直接得到真正的查询的行数，必须通过ResultSet实际查询的行数来获得。
                        }
            } else {
                if(ssdSwitch){
                    processor.putOperationRecord(new FinishQueryPreparedStmtTimeout(processor, currentTimeMillis,
                        preparedSql, elapseTime)); //Query无法直接得到真正的查询的行数，必须通过ResultSet实际查询的行数来获得。
                             }
                    if(asrdSwitch){       
                      processor.putOperationRecord(new FinishQueryPreparedStmtTimeoutErr(processor, currentTimeMillis,
                        preparedSql, parameterValueList.get(0), elapseTime)); //Query无法直接得到真正的查询的行数，必须通过ResultSet实际查询的行数来获得。
                      }
            }
            if(ssdSwitch){
            processor.putOperationRecord(new OpenResultSet(processor, currentTimeMillis, preparedSql));
            }
        } catch (SQLException e) {
            if(ssdSwitch){
            processor.putOperationRecord(new PreparedStmt(processor, currentTimeMillis, preparedSql, e ));
                    }
            if(asrdSwitch){         
            processor.putOperationRecord(new PreparedStmtErr(processor, currentTimeMillis, preparedSql, e,
                    parameterValueList.get(0)));
                    }
            throw e;
        } finally {
            parameterValueList.clear();
        }
        }
        return result;

    }

    ResultSet around(PreparedStatementMonitorProxy psmp) throws SQLException : execution(java.sql.ResultSet com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy.getResultSet() throws SQLException) && target(psmp) {
        ResultSet result = null;
        final String preparedSql = psmp.getPreparedSql();
        final MonitorDataProcessor processor = psmp.getMonitorDataProcessor();
        if (processor != null) {
        Properties switchPro = processor.getSwitchPro(); 
        Boolean ssdSwitch = false;
        if(switchPro!=null){
         ssdSwitch = (Boolean)switchPro.get("ssdSwitch");
        }
        result = new ResultSetMonitorProxy(proceed(psmp), preparedSql, psmp.getLastExecuteSqlTime(), processor);
        if(ssdSwitch){
        processor.putOperationRecord(new OpenResultSet(processor, psmp.getLastExecuteSqlTime(), preparedSql));
        }
        }
        return result;
    }

    pointcut recordExecuteBatch(PreparedStatementMonitorProxy psmp) : execution(int[] com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy.executeBatch(..) throws SQLException)
	&& target(psmp) && within(com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy);

    int[] around(PreparedStatementMonitorProxy psmp) throws SQLException : recordExecuteBatch(psmp) {
        int[] result = null;
        long currentTimeMillis = System.currentTimeMillis();
        final String preparedSql = psmp.getPreparedSql();
        final MonitorDataProcessor processor = psmp.getMonitorDataProcessor();
        if (processor != null) {
        Properties switchPro = processor.getSwitchPro(); 
        Boolean ssdSwitch = false;
        Boolean asrdSwitch = false;
        if(switchPro!=null){
         ssdSwitch = (Boolean)switchPro.get("ssdSwitch");
         asrdSwitch = (Boolean)switchPro.get("asrdSwitch");
        }
        final List<Object[][]> parameterValueList = psmp.getParameterValueList();
        if (parameterValueList.isEmpty()) {
            parameterValueList.add(new Object[0][]);
        } else if (parameterValueList.size() == 1) {
            Object[][] parameterValue = parameterValueList.get(0);
            int i = 0;
            for (; i < parameterValue.length && parameterValue[i] != null; i++) {
            }
            parameterValueList.remove(0);
            parameterValueList.add(0, Arrays.copyOf(parameterValue, i));
        } else {
            parameterValueList.remove(parameterValueList.size() - 1);
        }
        final int size = parameterValueList.size();
        if(ssdSwitch){
        processor.putOperationRecord(new ExecuteBatchPreparedStmt(processor, currentTimeMillis, preparedSql, size));
        }
        long startTime = System.currentTimeMillis();
        try {
            result = proceed(psmp);
            long elapseTime = System.currentTimeMillis() - startTime;
            if (elapseTime <= processor.getSlowSqlThreshold() * size) {
             if(ssdSwitch){
                processor.putOperationRecord(new FinishBatchPreparedStmt(processor, currentTimeMillis, preparedSql,
                        elapseTime, result));
                        }
            } else {
                if(ssdSwitch){
                processor.putOperationRecord(new FinishBatchPreparedStmtTimeout(processor, currentTimeMillis,
                        preparedSql , elapseTime, result));
                        }
                if(asrdSwitch){        
                processor.putOperationRecord(new FinishBatchPreparedStmtTimeoutErr(processor, currentTimeMillis,
                        preparedSql, parameterValueList, elapseTime, result));   
                        }    
            }
        } catch (SQLException e) {
            final long elapseTime = System.currentTimeMillis() - startTime;
            if (e instanceof BatchUpdateException) {
                BatchUpdateException bue = (BatchUpdateException) e;
                result = bue.getUpdateCounts();
                 if(ssdSwitch){
                processor.putOperationRecord(new BatchPreparedStmtPartialSuc(processor, currentTimeMillis, preparedSql,
                        elapseTime, result , e));
                        }
                if(asrdSwitch){
                processor.putOperationRecord(new BatchPreparedStmtPartialSucErr(processor, currentTimeMillis, preparedSql,
                        elapseTime, result, parameterValueList, e));     
                        }
            } else {
                if(asrdSwitch){
                processor.putOperationRecord(new BatchPreparedStmtErr(processor, currentTimeMillis, preparedSql,
                        elapseTime, parameterValueList, e));
                        }
            }
            throw e;
        } finally {
            parameterValueList.clear();
        }
        }
        return result;
    }

    after(PreparedStatementMonitorProxy psmp, int idx) : execution( void com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy.set*(int,..)) && target(psmp) && args(idx,..) {
        Object[] as = thisJoinPoint.getArgs();
        List<Object[][]> parameterValueList = psmp.getParameterValueList();
        int newArrSize = (idx + 4) / 5 * 5;
        if (parameterValueList.isEmpty()) {
            parameterValueList.add(new Object[newArrSize][]);
        }
        Object[][] parameterValueArr = parameterValueList.get(parameterValueList.size() - 1);
        if (idx > parameterValueArr.length) {
            parameterValueList.remove(parameterValueList.size() - 1);
            parameterValueArr = Arrays.copyOf(parameterValueArr, newArrSize);
            parameterValueList.add(parameterValueArr);
        }
        Object para = as[1];
        if (thisJoinPointStaticPart.getSignature().getName().contains(SET_NULL)) {
            parameterValueArr[idx - 1] = new Object[] { NULL_STR };
        } else if (para instanceof Number || para instanceof String || para instanceof Date || para instanceof Boolean) {
            parameterValueArr[idx - 1] = as;
        } else {
            parameterValueArr[idx - 1] = new Object[] { UNKNOW_STR };
        }
    }

    after(PreparedStatementMonitorProxy psmp) : execution( void com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy.addBatch()) && target(psmp) {
        List<Object[][]> parameterValueList = psmp.getParameterValueList();
        if (parameterValueList.isEmpty()) {
            parameterValueList.add(new Object[5][]);
        } else {
            if (parameterValueList.size() == 1) {
                Object[][] parameterValue = parameterValueList.get(0);
                int i = 0;
                for (; i < parameterValue.length && parameterValue[i] != null; i++) {
                }
                parameterValueList.remove(0);
                parameterValueList.add(0, Arrays.copyOf(parameterValue, i));
            }
            parameterValueList.add(new Object[parameterValueList.get(0).length][]);
        }

    }

    after(PreparedStatementMonitorProxy psmp) : execution( void com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy.clearParameters()) && target(psmp) {
        List<Object[][]> parameterValueList = psmp.getParameterValueList();
        Object[][] objss = parameterValueList.get(parameterValueList.size() - 1);
        for (int i = 0; i < objss.length; i++) {
            objss[i] = null;
        }
    }

    after(PreparedStatementMonitorProxy psmp) : execution( void com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy.clearBatch()) && target(psmp) {
        List<Object[][]> parameterValueList = psmp.getParameterValueList();
        for (Object[][] objss : parameterValueList) {
            for (int i = 0; i < objss.length; i++) {
                objss[i] = null;
            }
        }
        parameterValueList.clear();
    }

    after(PreparedStatementMonitorProxy psmp) returning(int effectRowCount) : execution(int com.yihaodian.ydd.monitor.proxy.PreparedStatementMonitorProxy.getUpdateCount() throws SQLException) && target(psmp) {
        final MonitorDataProcessor processor = psmp.getMonitorDataProcessor();
        if (processor != null) {
        Properties switchPro = processor.getSwitchPro(); 
        Boolean ssdSwitch = false;
        if(switchPro!=null){
         ssdSwitch = (Boolean)switchPro.get("ssdSwitch");
        }
        if(ssdSwitch){
        processor.putOperationRecord(new PreparedStmtUpdateCount(processor, psmp.getLastExecuteSqlTime(),
                psmp.getPreparedSql(), effectRowCount));
                }
                }
    }
}
