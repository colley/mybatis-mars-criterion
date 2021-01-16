package com.mars.ydd.monitor.data.optrecdata;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mars.ydd.monitor.data.AbnormalSqlRecordData;
import com.mars.ydd.monitor.data.SqlStatisticData;
import com.mars.ydd.monitor.data.optrecdata.StatementOptRecData.CommonStmtOpt;
import com.mars.ydd.monitor.data.optrecdata.StatementOptRecData.ExecuteStmt;
import com.mars.ydd.monitor.data.optrecdata.StatementOptRecData.FinishQueryStmt;
import com.mars.ydd.monitor.data.optrecdata.StatementOptRecData.FinishStmt;
import com.mars.ydd.monitor.data.optrecdata.StatementOptRecData.FinishUpdateStmt;
import com.mars.ydd.monitor.data.optrecdata.StatementOptRecData.StmtErrType;
import com.mars.ydd.monitor.data.optrecdata.StatementOptRecData.StmtOptAbnormal;
import com.mars.ydd.monitor.processor.MonitorDataProcessor;
import com.mars.ydd.monitor.processor.MonitorDataStructure;

public class PreparedStatementOptRecData {

    public static class ExecutePreparedStmt extends ExecuteStmt {

        public ExecutePreparedStmt(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql) {
            super(monitorDataProcessor, recordTime, preparedSql);
        }
    }

    public static class PreparedStmtUpdateCount extends CommonStmtOpt {
        private final int effectRowCount;

        public PreparedStmtUpdateCount(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                int effectRowCount) {
            super(monitorDataProcessor, recordTime, preparedSql);
            this.effectRowCount = effectRowCount;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            if (effectRowCount > 0) {
                getSsd(mds).increaseEffectRowSum(effectRowCount);
            }
        }

    }

    public static class FinishUpdatePreparedStmt extends FinishUpdateStmt {

        public FinishUpdatePreparedStmt(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                long elapseTime, int effectRowCount) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime, effectRowCount);
        }

        public FinishUpdatePreparedStmt(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                long elapseTime) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime);
        }
    }

    public static class FinishUpdatePreparedStmtTimeout extends FinishUpdateStmt {

        public FinishUpdatePreparedStmtTimeout(MonitorDataProcessor monitorDataProcessor, long recordTime,
                String preparedSql, long elapseTime, int effectRowCount) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime, effectRowCount);
        }

        public FinishUpdatePreparedStmtTimeout(MonitorDataProcessor monitorDataProcessor, long recordTime,
                String preparedSql, long elapseTime) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime);
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            SqlStatisticData ssd = getSsd(mds);
            ssd.increaseTimeoutCount(1);
            ssd.increaseTimeoutTimeSum(getElapseTime());
            if (getEffectRowCount() > 0) {
                ssd.increaseEffectRowSum(getEffectRowCount());
            }
        }
    }

    
    public static class FinishUpdatePreparedStmtTimeoutErr extends FinishUpdateStmt {
        private final Object[][] parameterValue;

        public FinishUpdatePreparedStmtTimeoutErr(MonitorDataProcessor monitorDataProcessor, long recordTime,
                String preparedSql, Object[][] parameterValue, long elapseTime, int effectRowCount) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime, effectRowCount);
            this.parameterValue = parameterValue;
        }

        public FinishUpdatePreparedStmtTimeoutErr(MonitorDataProcessor monitorDataProcessor, long recordTime,
                String preparedSql, Object[][] parameterValue, long elapseTime) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime);
            this.parameterValue = new Object[parameterValue.length][];
            for (int i = 0; i < parameterValue.length; i++) {
                this.parameterValue[i] = new Object[parameterValue[i].length];
                for (int j = 0; j < parameterValue[i].length; j++) {
                    this.parameterValue[i][j] = parameterValue[i][j];
                }
            }
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            
            final MonitorDataProcessor processor = getMonitorDataProcessor();
            final long archiveIdleTimeMillis = processor.getArchiveIdleTimeMillis();
            final long recordTime = getRecordTime();
            final long keyTime = recordTime - (recordTime % archiveIdleTimeMillis);
            final String preparedSql = getPreparedSql();
            final List<AbnormalSqlRecordData> asrdList = getASRDList(mds, preparedSql, StmtErrType.TIMEOUT);
            if (asrdList.size() < processor.getErrMsgLimit()) {
                asrdList.add(new AbnormalSqlRecordData(processor.getDataSourceIdentityData(), new Date(keyTime),
                        new Date(keyTime + archiveIdleTimeMillis), preparedSql,
                        PreparedStmtErr.generateParameterValueStr(parameterValue), StmtErrType.TIMEOUT.toString(),
                        new Date(recordTime), getElapseTime()));
            }
            for (int i = 0; i < this.parameterValue.length; i++) {
                for (int j = 0; j < this.parameterValue[i].length; j++) {
                    this.parameterValue[i][j] = null;
                }
                this.parameterValue[i] = null;
            }
        }
    }
    
    
    public static class FinishQueryPreparedStmt extends FinishQueryStmt {

        public FinishQueryPreparedStmt(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                long elapseTime) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime);
        }
    }

    public static class FinishQueryPreparedStmtTimeout extends FinishQueryStmt {

        public FinishQueryPreparedStmtTimeout(MonitorDataProcessor monitorDataProcessor, long recordTime,
                String preparedSql, long elapseTime) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime);
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            SqlStatisticData ssd = getSsd(mds);
            ssd.increaseTimeoutCount(1);
            ssd.increaseTimeoutTimeSum(getElapseTime());
        }
    }

    
    public static class FinishQueryPreparedStmtTimeoutErr extends FinishQueryStmt {
        private final Object[][] parameterValue;

        public FinishQueryPreparedStmtTimeoutErr(MonitorDataProcessor monitorDataProcessor, long recordTime,
                String preparedSql, Object[][] parameterValue, long elapseTime) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime);
            this.parameterValue = new Object[parameterValue.length][];
            for (int i = 0; i < parameterValue.length; i++) {
                this.parameterValue[i] = new Object[parameterValue[i].length];
                for (int j = 0; j < parameterValue[i].length; j++) {
                    this.parameterValue[i][j] = parameterValue[i][j];
                }
            }
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            final MonitorDataProcessor processor = getMonitorDataProcessor();
            final long archiveIdleTimeMillis = processor.getArchiveIdleTimeMillis();
            final long recordTime = getRecordTime();
            final long keyTime = recordTime - (recordTime % archiveIdleTimeMillis);
            final String preparedSql = getPreparedSql();
            final List<AbnormalSqlRecordData> asrdList = getASRDList(mds, preparedSql, StmtErrType.TIMEOUT);
            if (asrdList.size() < processor.getErrMsgLimit()) {
                asrdList.add(new AbnormalSqlRecordData(processor.getDataSourceIdentityData(), new Date(keyTime),
                        new Date(keyTime + archiveIdleTimeMillis), preparedSql,
                        PreparedStmtErr.generateParameterValueStr(parameterValue), StmtErrType.TIMEOUT.toString(),
                        new Date(recordTime), getElapseTime()));
            }
            for (int i = 0; i < this.parameterValue.length; i++) {
                for (int j = 0; j < this.parameterValue[i].length; j++) {
                    this.parameterValue[i][j] = null;
                }
                this.parameterValue[i] = null;
            }
        }
    }
    
    
    
    public static class ExecuteBatchPreparedStmt extends ExecuteStmt {
        private final int batchCount;

        public ExecuteBatchPreparedStmt(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                int batchCount) {
            super(monitorDataProcessor, recordTime, preparedSql);
            this.batchCount = batchCount;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            getSsd(mds).increaseExecuteCount(batchCount);
        }
    }

    public static class FinishBatchPreparedStmt extends FinishStmt {
        private final int[] batchResults;

        public FinishBatchPreparedStmt(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                long elapseTime, int[] batchResults) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime);
            this.batchResults = batchResults;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            SqlStatisticData ssd = getSsd(mds);
            ssd.increaseFinishCount(batchResults.length);
            ssd.increaseFinishTimeSum(getElapseTime());
            for (int br : batchResults) {
                if (br > 0) {
                    ssd.increaseEffectRowSum(br);
                }
            }
        }

        protected int[] getBatchResults() {
            return batchResults;
        }
    }

    public static class FinishBatchPreparedStmtTimeout extends FinishBatchPreparedStmt {

        public FinishBatchPreparedStmtTimeout(MonitorDataProcessor monitorDataProcessor, long recordTime,
                String preparedSql, long elapseTime, int[] batchResults) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime, batchResults);
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            SqlStatisticData ssd = getSsd(mds);
            final int[] batchResults = getBatchResults();
            final long elapseTime = getElapseTime();
            final int length = batchResults.length;
            ssd.increaseTimeoutCount(length);
            ssd.increaseTimeoutTimeSum(elapseTime);

            for (int br : batchResults) {
                if (br > 0) {
                    ssd.increaseEffectRowSum(br);
                }
            }
        }
    }
    
    
    public static class FinishBatchPreparedStmtTimeoutErr extends FinishBatchPreparedStmt {
        private final List<Object[][]> parameterValueList;

        public FinishBatchPreparedStmtTimeoutErr(MonitorDataProcessor monitorDataProcessor, long recordTime,
                String preparedSql, List<Object[][]> parameterValueList, long elapseTime, int[] batchResults) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime, batchResults);
            this.parameterValueList = new ArrayList<Object[][]>(parameterValueList.size());
            for (Object[][] tmpobjss : parameterValueList) {
                Object[][] objss = new Object[tmpobjss.length][];
                for (int i = 0; i < tmpobjss.length; i++) {
                    objss[i] = new Object[tmpobjss[i].length];
                    for (int j = 0; j < tmpobjss[i].length; j++) {
                        objss[i][j] = tmpobjss[i][j];
                    }
                }
                this.parameterValueList.add(objss);
            }
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            final int[] batchResults = getBatchResults();
            final long elapseTime = getElapseTime();
            final int length = batchResults.length;
            long everyElapseTime = BigDecimal.valueOf(elapseTime / length).setScale(0, RoundingMode.HALF_UP).longValue();
           
            final MonitorDataProcessor processor = getMonitorDataProcessor();
            final long archiveIdleTimeMillis = processor.getArchiveIdleTimeMillis();
            final long recordTime = getRecordTime();
            final long keyTime = recordTime - (recordTime % archiveIdleTimeMillis);
            final String preparedSql = getPreparedSql();
            final List<AbnormalSqlRecordData> asrdList = getASRDList(mds, preparedSql, StmtErrType.TIMEOUT);
            if (asrdList.size() < processor.getErrMsgLimit()) {
                asrdList.add(new AbnormalSqlRecordData(processor.getDataSourceIdentityData(), new Date(keyTime),
                        new Date(keyTime + archiveIdleTimeMillis), preparedSql,
                        PreparedStmtErr.generateParameterValueStr(parameterValueList.get(0)),
                        StmtErrType.TIMEOUT.toString(), new Date(recordTime), everyElapseTime));
            }

            for (Object[][] objss : this.parameterValueList) {
                for (int i = 0; i < objss.length; i++) {
                    for (int j = 0; j < objss[i].length; j++) {
                        objss[i][j] = null;
                    }
                    objss[i] = null;
                }
            }
            this.parameterValueList.clear();
        }
    }
    
    

    public static class PreparedStmt extends StmtOptAbnormal {
        public final static SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");
        public final static SimpleDateFormat SDF_TIME = new SimpleDateFormat("HH:mm:ss");
        public final static SimpleDateFormat SDF_TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        public PreparedStmt(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                Throwable ex) {
            super(monitorDataProcessor, recordTime, preparedSql, ex);
        }

        protected static String generateParameterValueStr(Object[][] parameterValue) {
            final DecimalFormat df = new DecimalFormat("0.########");
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < parameterValue.length; i++) {
                Object[] parameter = parameterValue[i];
                if (parameter != null) {
                    if (parameter.length > 1) {
                        Object para = parameter[1];
                        if (para instanceof Number && parameter.length == 2) {
                            builder.append(df.format(parameter[1]));
                        } else if (para instanceof String) {
                            builder.append('「').append(para).append('」');
                        } else if (para instanceof java.sql.Date) { //针对带有Calendar参数的方法暂时不做处理
                            synchronized (SDF_DATE) {
                                builder.append(SDF_DATE.format((java.sql.Date) para));
                            }
                        } else if (para instanceof java.sql.Time) {
                            synchronized (SDF_TIME) {
                                builder.append(SDF_TIME.format((java.sql.Time) para));
                            }
                        } else if (para instanceof java.sql.Timestamp) {
                            synchronized (SDF_TIMESTAMP) {
                                builder.append(SDF_TIMESTAMP.format((java.sql.Timestamp) para));
                            }
                        } /*else if (para instanceof Integer && parameter.length == 3 && parameter[2] instanceof String) {
                            builder.append("NULL");
                          } */
                        else if (para instanceof Boolean) {
                            builder.append((Boolean) para);
                        } else if (para instanceof Character) {
                            builder.append('「').append(para).append('」');
                        }
                    } else if (parameter.length == 1) {
                        builder.append(parameter[0].toString());
                    } else {
                        continue;
                    }
                    builder.append('‖');
                }
            }
            if (builder.length() > 1) {
                builder.deleteCharAt(builder.length() - 1);
            }
            return builder.toString();
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            getSsd(mds).increaseErrorCount(1);
         }
    }

    
    public static class PreparedStmtErr extends StmtOptAbnormal {
        public final static SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");
        public final static SimpleDateFormat SDF_TIME = new SimpleDateFormat("HH:mm:ss");
        public final static SimpleDateFormat SDF_TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        private final Object[][] parameterValue;

        public PreparedStmtErr(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                Throwable ex, Object[][] parameterValue) {
            super(monitorDataProcessor, recordTime, preparedSql, ex);
            this.parameterValue = new Object[parameterValue.length][];
            for (int i = 0; i < parameterValue.length; i++) {
                this.parameterValue[i] = new Object[parameterValue[i].length];
                for (int j = 0; j < parameterValue[i].length; j++) {
                    this.parameterValue[i][j] = parameterValue[i][j];
                }
            }
        }

        protected static String generateParameterValueStr(Object[][] parameterValue) {
            final DecimalFormat df = new DecimalFormat("0.########");
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < parameterValue.length; i++) {
                Object[] parameter = parameterValue[i];
                if (parameter != null) {
                    if (parameter.length > 1) {
                        Object para = parameter[1];
                        if (para instanceof Number && parameter.length == 2) {
                            builder.append(df.format(parameter[1]));
                        } else if (para instanceof String) {
                            builder.append('「').append(para).append('」');
                        } else if (para instanceof java.sql.Date) { //针对带有Calendar参数的方法暂时不做处理
                            synchronized (SDF_DATE) {
                                builder.append(SDF_DATE.format((java.sql.Date) para));
                            }
                        } else if (para instanceof java.sql.Time) {
                            synchronized (SDF_TIME) {
                                builder.append(SDF_TIME.format((java.sql.Time) para));
                            }
                        } else if (para instanceof java.sql.Timestamp) {
                            synchronized (SDF_TIMESTAMP) {
                                builder.append(SDF_TIMESTAMP.format((java.sql.Timestamp) para));
                            }
                        } /*else if (para instanceof Integer && parameter.length == 3 && parameter[2] instanceof String) {
                            builder.append("NULL");
                          } */
                        else if (para instanceof Boolean) {
                            builder.append((Boolean) para);
                        } else if (para instanceof Character) {
                            builder.append('「').append(para).append('」');
                        }
                    } else if (parameter.length == 1) {
                        builder.append(parameter[0].toString());
                    } else {
                        continue;
                    }
                    builder.append('‖');
                }
            }
            if (builder.length() > 1) {
                builder.deleteCharAt(builder.length() - 1);
            }
            return builder.toString();
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            final MonitorDataProcessor processor = getMonitorDataProcessor();
            final long archiveIdleTimeMillis = processor.getArchiveIdleTimeMillis();
            final long recordTime = getRecordTime();
            final long keyTime = recordTime - (recordTime % archiveIdleTimeMillis);
            final String preparedSql = getPreparedSql();

            final List<AbnormalSqlRecordData> asrdList = getASRDList(mds, preparedSql, StmtErrType.EXCEPTION);
            if (asrdList.size() < processor.getErrMsgLimit()) {
                asrdList.add(new AbnormalSqlRecordData(processor.getDataSourceIdentityData(), new Date(keyTime),
                        new Date(keyTime + archiveIdleTimeMillis), preparedSql,
                        generateParameterValueStr(parameterValue), StmtErrType.EXCEPTION.toString(), new Date(
                                recordTime), StmtOptAbnormal.getExStr(getEx())));
            }
            for (int i = 0; i < this.parameterValue.length; i++) {
                for (int j = 0; j < this.parameterValue[i].length; j++) {
                    this.parameterValue[i][j] = null;
                }
                this.parameterValue[i] = null;
            }
        }
    }
    
    
    public static class BatchPreparedStmtPartialSuc extends FinishBatchPreparedStmt {
        private final Throwable ex;

        public BatchPreparedStmtPartialSuc(MonitorDataProcessor monitorDataProcessor, long recordTime,
                String preparedSql, long elapseTime, int[] batchResults ,
                Throwable ex) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime, batchResults);
            this.ex = ex;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            final int[] batchResults = getBatchResults();
            final int length = batchResults.length;
            if(length>0) {
            final long everyElapseTime = BigDecimal.valueOf(getElapseTime() / length).setScale(0, RoundingMode.HALF_UP).longValue();
            final SqlStatisticData ssd = getSsd(mds);
            int idx = 0;
            int br;
            for (; idx < length; idx++) {
                br = batchResults[idx];
                if (br >= 0) {
                    ssd.increaseEffectRowSum(br);
                    ssd.increaseFinishCount(1);
                    ssd.increaseFinishTimeSum(everyElapseTime);
                } else if (br == Statement.SUCCESS_NO_INFO) {
                    ssd.increaseFinishCount(1);
                    ssd.increaseFinishTimeSum(everyElapseTime);
                } else {
                    ssd.increaseErrorCount(1);
                }
            }
        }
        }

        protected Throwable getEx() {
            return ex;
        }
    }
    
    public static class BatchPreparedStmtPartialSucErr extends FinishBatchPreparedStmt {
        private final List<Object[][]> parameterValueList;
        private final Throwable ex;

        public BatchPreparedStmtPartialSucErr(MonitorDataProcessor monitorDataProcessor, long recordTime,
                String preparedSql, long elapseTime, int[] batchResults, List<Object[][]> parameterValueList,
                Throwable ex) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime, batchResults);
            this.parameterValueList = new ArrayList<Object[][]>(parameterValueList.size());
            for (Object[][] tmpobjss : parameterValueList) {
                Object[][] objss = new Object[tmpobjss.length][];
                for (int i = 0; i < tmpobjss.length && tmpobjss[i] != null; i++) {
                    objss[i] = new Object[tmpobjss[i].length];
                    for (int j = 0; j < tmpobjss[i].length; j++) {
                        objss[i][j] = tmpobjss[i][j];
                    }
                }
                this.parameterValueList.add(objss);
            }
            this.ex = ex;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            final int[] batchResults = getBatchResults();
            final int length = batchResults.length;
            if(length>0) {
            final MonitorDataProcessor processor = getMonitorDataProcessor();
            final long archiveIdleTimeMillis = processor.getArchiveIdleTimeMillis();
            final long recordTime = getRecordTime();
            final long keyTime = recordTime - (recordTime % archiveIdleTimeMillis);
            final String preparedSql = getPreparedSql();
            final List<AbnormalSqlRecordData> asrdList = getASRDList(mds, preparedSql, StmtErrType.EXCEPTION);
            int idx = 0;
            int br;
            for (; idx < length; idx++) {
                br = batchResults[idx];
                if (br < 0 && br != Statement.SUCCESS_NO_INFO) {
                    if (asrdList.size() < processor.getErrMsgLimit()) {
                        asrdList.add(new AbnormalSqlRecordData(processor.getDataSourceIdentityData(),
                                new Date(keyTime), new Date(keyTime + archiveIdleTimeMillis), preparedSql,
                                PreparedStmtErr.generateParameterValueStr(parameterValueList.get(idx)),
                                StmtErrType.EXCEPTION.toString(), new Date(getRecordTime()),
                                StmtOptAbnormal.getExStr(ex)));
                    }
                }
            }
            for (; idx < parameterValueList.size(); idx++) {
                if (asrdList.size() < processor.getErrMsgLimit()) {
                    asrdList.add(new AbnormalSqlRecordData(processor.getDataSourceIdentityData(), new Date(keyTime),
                            new Date(keyTime + archiveIdleTimeMillis), preparedSql,
                            PreparedStmtErr.generateParameterValueStr(parameterValueList.get(idx)),
                            StmtErrType.EXCEPTION.toString(), new Date(getRecordTime()), StmtOptAbnormal.getExStr(ex)));
                }
            }

            for (Object[][] objss : this.parameterValueList) {
                for (int i = 0; i < objss.length && objss[i] != null; i++) {
                    for (int j = 0; j < objss[i].length; j++) {
                        objss[i][j] = null;
                    }
                    objss[i] = null;
                }
            }
            this.parameterValueList.clear();
        }
        }

        protected List<Object[][]> getParameterValueList() {
            return parameterValueList;
        }

        protected Throwable getEx() {
            return ex;
        }
    }
    
    

    public static class BatchPreparedStmtErr extends BatchPreparedStmtPartialSucErr {

        public BatchPreparedStmtErr(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                long elapseTime, List<Object[][]> parameterValueList, Throwable ex) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime, null, parameterValueList, ex);
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            final List<Object[][]> parameterValueList = getParameterValueList();
            final int length = parameterValueList.size();
            final MonitorDataProcessor processor = getMonitorDataProcessor();
            final long archiveIdleTimeMillis = processor.getArchiveIdleTimeMillis();
            final long keyTime = getRecordTime() - (getRecordTime() % archiveIdleTimeMillis);
            final String preparedSql = getPreparedSql();
            final List<AbnormalSqlRecordData> asrdList = getASRDList(mds, preparedSql, StmtErrType.EXCEPTION );
            final SqlStatisticData ssd = getSsd(mds);
            int idx = 0;
            for (; idx < length; idx++) {
                ssd.increaseErrorCount(1);
                if (asrdList.size() < processor.getErrMsgLimit()) {
                    asrdList.add(new AbnormalSqlRecordData(processor.getDataSourceIdentityData(), new Date(keyTime),
                            new Date(keyTime + archiveIdleTimeMillis), preparedSql,
                            PreparedStmtErr.generateParameterValueStr(parameterValueList.get(idx)),
                            StmtErrType.EXCEPTION.toString(), new Date(getRecordTime()),
                            StmtOptAbnormal.getExStr(getEx())));
                }
            }
        }
    }
}
