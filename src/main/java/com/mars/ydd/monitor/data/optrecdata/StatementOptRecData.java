package com.mars.ydd.monitor.data.optrecdata;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mars.ydd.monitor.data.AbnormalSqlRecordData;
import com.mars.ydd.monitor.data.OperationRecordData;
import com.mars.ydd.monitor.data.SqlStatisticData;
import com.mars.ydd.monitor.processor.MonitorDataProcessor;
import com.mars.ydd.monitor.processor.MonitorDataStructure;
import com.mars.ydd.monitor.util.SqlUtils;

public class StatementOptRecData {
    public static enum StmtErrType {
        EXCEPTION, TIMEOUT
    }

    public static abstract class CommonStmtOpt extends OperationRecordData {
        private final String preparedSql;

        protected CommonStmtOpt(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql) {
            super(monitorDataProcessor, recordTime);
            this.preparedSql = preparedSql;
        }

        protected SqlStatisticData getSsd(MonitorDataStructure mds) {
            Map<String, SqlStatisticData> ssdMap = getSsdMap(mds);
            SqlStatisticData ssd = ssdMap.get(preparedSql);
            if (ssd == null) {
                final long archiveIdleTimeMillis = getMonitorDataProcessor().getArchiveIdleTimeMillis();
                final long keyTime = getRecordTime() - (getRecordTime() % archiveIdleTimeMillis);
                ssd = new SqlStatisticData(getMonitorDataProcessor().getDataSourceIdentityData(), new Date(keyTime),
                        new Date(keyTime + archiveIdleTimeMillis), preparedSql);
                ssdMap.put(preparedSql, ssd);
            }
            return ssd;
        }

        protected String getPreparedSql() {
            return preparedSql;
        }
    }

    public static abstract class StmtOptAbnormal extends CommonStmtOpt {
        private final Throwable ex;

        protected StmtOptAbnormal(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                Throwable ex) {
            super(monitorDataProcessor, recordTime, preparedSql);
            this.ex = StmtOptAbnormal.processEx(ex);
        }

        protected static String getExStr(Throwable ex) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(baos);
            ex.printStackTrace(pw);
            pw.flush();
            pw.close();
            return baos.toString();
        }

        protected static Throwable processEx(Throwable ex) {
            Throwable rootcCause = ex;
            while (rootcCause.getCause() != null) {
                rootcCause = rootcCause.getCause();
            }
            StackTraceElement[] stackTrace = rootcCause.getStackTrace();
            int idx = stackTrace.length;
            do {
                StackTraceElement ste = stackTrace[--idx];
                if (ste.getClassName().indexOf("com.yihaodian") == 0
                        || ste.getClassName().indexOf("com.newheight") == 0
                        || ste.getClassName().indexOf("com.yhd") == 0) {
                    break;
                }
            } while (idx > 0);
            if (idx > 0) {
                StackTraceElement[] newStackTrace = new StackTraceElement[idx + 1];
                System.arraycopy(stackTrace, 0, newStackTrace, 0, idx + 1);
                Throwable newEx = new Throwable(rootcCause.getMessage());
                newEx.setStackTrace(newStackTrace);
                return newEx;
            } else {
                return ex;
            }
        }

        protected Throwable getEx() {
            return ex;
        }
    }

    public static class ExecuteStmt extends CommonStmtOpt {

        public ExecuteStmt(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql) {
            super(monitorDataProcessor, recordTime, preparedSql);
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            getSsd(mds ).increaseExecuteCount(1);
        }
    }

    public static abstract class FinishStmt extends CommonStmtOpt {
        private final long elapseTime;

        public FinishStmt(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                long elapseTime) {
            super(monitorDataProcessor, recordTime, preparedSql);
            this.elapseTime = elapseTime;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            SqlStatisticData ssd = getSsd(mds );
            ssd.increaseFinishCount(1);
            ssd.increaseFinishTimeSum(elapseTime);
        }

        protected long getElapseTime() {
            return elapseTime;
        }
    }

    public static class FinishUpdateStmt extends FinishStmt {
        private final int effectRowCount;

        public FinishUpdateStmt(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                long elapseTime, int effectRowCount) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime);
            this.effectRowCount = effectRowCount;
        }

        public FinishUpdateStmt(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                long elapseTime) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime);
            this.effectRowCount = 0;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            super.transfer(mds );
            if (effectRowCount > 0) {
                getSsd(mds ).increaseEffectRowSum(effectRowCount);
            }
        }

        protected int getEffectRowCount() {
            return effectRowCount;
        }
    }

    public static class FinishUpdateStmtTimeout extends FinishStmt {
        private final int effectRowCount;

        public FinishUpdateStmtTimeout(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql, long elapseTime, int effectRowCount) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime);
            this.effectRowCount = effectRowCount;
        }

        public FinishUpdateStmtTimeout(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql, long elapseTime) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime);
            this.effectRowCount = 0;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            SqlStatisticData ssd = getSsd(mds );
            ssd.increaseTimeoutCount(1);
            ssd.increaseTimeoutTimeSum(getElapseTime());
            if (effectRowCount > 0) {
                ssd.increaseEffectRowSum(effectRowCount);
            }
           
        }
    }

    public static class FinishUpdateStmtTimeoutErr extends FinishStmt {
        private final String orginalSql;

        public FinishUpdateStmtTimeoutErr(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                String orginalSql, long elapseTime) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime);
            this.orginalSql = orginalSql;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            final MonitorDataProcessor processor = getMonitorDataProcessor();
            final long archiveIdleTimeMillis = processor.getArchiveIdleTimeMillis();
            final long recordTime = getRecordTime();
            final long keyTime = recordTime - (recordTime % archiveIdleTimeMillis);
            final List<AbnormalSqlRecordData> asrdList = getASRDList(mds,
                    SqlUtils.generatePreparedStatement(orginalSql, processor), StmtErrType.TIMEOUT );
            if (asrdList.size() < processor.getErrMsgLimit()) {
                asrdList.add(new AbnormalSqlRecordData(processor.getDataSourceIdentityData(), new Date(keyTime),
                        new Date(keyTime + archiveIdleTimeMillis), orginalSql, StmtErrType.TIMEOUT.toString(),
                        new Date(recordTime), getElapseTime()));
            }
        }
    }
    
    
    
    public static class FinishQueryStmt extends FinishStmt {

        public FinishQueryStmt(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                long elapseTime) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime);
        }
    }

    public static class FinishQueryStmtTimeout extends FinishStmt {

        public FinishQueryStmtTimeout(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                 long elapseTime) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime);
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            SqlStatisticData ssd = getSsd(mds );
            ssd.increaseTimeoutCount(1);
            ssd.increaseTimeoutTimeSum(getElapseTime());
        }
    }

    public static class FinishQueryStmtTimeoutErr extends FinishStmt {
        private final String orginalSql;

        public FinishQueryStmtTimeoutErr(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                String orginalSql, long elapseTime) {
            super(monitorDataProcessor, recordTime, preparedSql, elapseTime);
            this.orginalSql = orginalSql;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            
            final MonitorDataProcessor processor = getMonitorDataProcessor();
            final long archiveIdleTimeMillis = processor.getArchiveIdleTimeMillis();
            final long recordTime = getRecordTime();
            final long keyTime = recordTime - (recordTime % archiveIdleTimeMillis);
            final List<AbnormalSqlRecordData> asrdList = getASRDList(mds,
                    SqlUtils.generatePreparedStatement(orginalSql, processor), StmtErrType.TIMEOUT);
            if (asrdList.size() < processor.getErrMsgLimit()) {
                asrdList.add(new AbnormalSqlRecordData(processor.getDataSourceIdentityData(), new Date(keyTime),
                        new Date(keyTime + archiveIdleTimeMillis), orginalSql, StmtErrType.TIMEOUT.toString(),
                        new Date(recordTime), getElapseTime()));
            }
        }
    }
    
    
    public static class StmtUpdateCount extends CommonStmtOpt {
        private final int effectRowCount;

        public StmtUpdateCount(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                int effectRowCount) {
            super(monitorDataProcessor, recordTime, preparedSql);
            this.effectRowCount = effectRowCount;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            if (effectRowCount > 0) {
                getSsd(mds ).increaseEffectRowSum(effectRowCount);
            }
        }

    }

    public static class ExecuteBatchStmt extends OperationRecordData {
        private final List<String> batchSqlList;

        public ExecuteBatchStmt(MonitorDataProcessor monitorDataProcessor, long recordTime, List<String> batchSqlList) {
            super(monitorDataProcessor, recordTime);
            this.batchSqlList = batchSqlList;
        }

        protected SqlStatisticData getSsd(MonitorDataStructure mds, String preparedSql ) {
            Map<String, SqlStatisticData> ssdMap = getSsdMap(mds );
            SqlStatisticData ssd = ssdMap.get(preparedSql);
            if (ssd == null) {
                final long archiveIdleTimeMillis = getMonitorDataProcessor().getArchiveIdleTimeMillis();
                final long keyTime = getRecordTime() - (getRecordTime() % archiveIdleTimeMillis);
                ssd = new SqlStatisticData(getMonitorDataProcessor().getDataSourceIdentityData(), new Date(keyTime),
                        new Date(keyTime + archiveIdleTimeMillis), preparedSql);
                ssdMap.put(preparedSql, ssd);
            }
            return ssd;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            for (String orginalSql : batchSqlList) {
                String preparedSql = SqlUtils.generatePreparedStatement(orginalSql, getMonitorDataProcessor());
                SqlStatisticData ssd = getSsd(mds, preparedSql );
                ssd.increaseFinishCount(1);
            }
        }

        protected List<String> getBatchSqlList() {
            return batchSqlList;
        }

    }

    public static class FinishBatchStmt extends ExecuteBatchStmt {
        private final long elapseTime;
        private final int[] batchResults;

        public FinishBatchStmt(MonitorDataProcessor monitorDataProcessor, long recordTime, List<String> batchSqlList,
                long elapseTime, int[] batchResults) {
            super(monitorDataProcessor, recordTime, batchSqlList);
            this.elapseTime = elapseTime;
            this.batchResults = batchResults;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            int length = batchResults.length;
            long everyElapseTime = BigDecimal.valueOf(elapseTime / length).setScale(0, RoundingMode.HALF_UP).longValue();
            for (int i = 0; i < length; i++) {
                String preparedSql = SqlUtils.generatePreparedStatement(getBatchSqlList().get(i),
                        getMonitorDataProcessor());
                SqlStatisticData ssd = getSsd(mds, preparedSql );
                ssd.increaseFinishCount(1);
                ssd.increaseFinishTimeSum(everyElapseTime);
                if (batchResults[i] > 0) {
                    ssd.increaseEffectRowSum(batchResults[i]);
                }
            }
        }

        protected long getElapseTime() {
            return elapseTime;
        }

        protected int[] getBatchResults() {
            return batchResults;
        }

    }

    public static class FinishBatchStmtTimeout extends ExecuteBatchStmt {
        private final long elapseTime;
        private final int[] batchResults;

        public FinishBatchStmtTimeout(MonitorDataProcessor monitorDataProcessor, long recordTime,
                List<String> batchSqlList, long elapseTime, int[] batchResults) {
            super(monitorDataProcessor, recordTime, batchSqlList);
            this.elapseTime = elapseTime;
            this.batchResults = batchResults;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            int length = batchResults.length;
            final MonitorDataProcessor processor = getMonitorDataProcessor();
            for (int i = 0; i < length; i++) {
                final String orginalSql = getBatchSqlList().get(i);
                String preparedSql = SqlUtils.generatePreparedStatement(orginalSql, processor);
                SqlStatisticData ssd = getSsd(mds, preparedSql );
                ssd.increaseTimeoutCount(1);
                ssd.increaseTimeoutTimeSum(getElapseTime());
            }
        }

        protected long getElapseTime() {
            return elapseTime;
        }

        protected int[] getBatchResults() {
            return batchResults;
        }
    }
    
    public static class FinishBatchStmtTimeoutErr extends ExecuteBatchStmt {
        private final long elapseTime;
        private final int[] batchResults;

        public FinishBatchStmtTimeoutErr(MonitorDataProcessor monitorDataProcessor, long recordTime,
                List<String> batchSqlList, long elapseTime, int[] batchResults) {
            super(monitorDataProcessor, recordTime, batchSqlList);
            this.elapseTime = elapseTime;
            this.batchResults = batchResults;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            int length = batchResults.length;
            long everyElapseTime = BigDecimal.valueOf(elapseTime / length).setScale(0, RoundingMode.HALF_UP).longValue();
            final MonitorDataProcessor processor = getMonitorDataProcessor();
            for (int i = 0; i < length; i++) {
                final String orginalSql = getBatchSqlList().get(i);
                String preparedSql = SqlUtils.generatePreparedStatement(orginalSql, processor);

                final long archiveIdleTimeMillis = processor.getArchiveIdleTimeMillis();
                final long recordTime = getRecordTime();
                final long keyTime = recordTime - (recordTime % archiveIdleTimeMillis);
                final List<AbnormalSqlRecordData> asrdList = getASRDList(mds, preparedSql, StmtErrType.TIMEOUT);
                if (asrdList.size() < processor.getErrMsgLimit()) {
                    asrdList.add(new AbnormalSqlRecordData(processor.getDataSourceIdentityData(), new Date(keyTime),
                            new Date(keyTime + archiveIdleTimeMillis), orginalSql, StmtErrType.TIMEOUT.toString(),
                            new Date(recordTime), everyElapseTime));
                }
            }
        }

        protected long getElapseTime() {
            return elapseTime;
        }

        protected int[] getBatchResults() {
            return batchResults;
        }
    }
    

    public static class StmtErr extends StmtOptAbnormal {

        public StmtErr(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql, Throwable ex) {
            super(monitorDataProcessor, recordTime, preparedSql, ex);
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            getSsd(mds ).increaseErrorCount(1);
            }
    }

    
    public static class StmtErrErr extends StmtOptAbnormal {
        private final String orginalSql;

        public StmtErrErr(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql, Throwable ex,
                String orginalSql) {
            super(monitorDataProcessor, recordTime, preparedSql, ex);
            this.orginalSql = orginalSql;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            final MonitorDataProcessor processor = getMonitorDataProcessor();
            final long archiveIdleTimeMillis = processor.getArchiveIdleTimeMillis();
            final long recordTime = getRecordTime();
            final long keyTime = recordTime - (recordTime % archiveIdleTimeMillis);
            final List<AbnormalSqlRecordData> asrdList = getASRDList(mds,
                    SqlUtils.generatePreparedStatement(orginalSql, processor), StmtErrType.EXCEPTION);
            if (asrdList.size() < processor.getErrMsgLimit()) {
                asrdList.add(new AbnormalSqlRecordData(processor.getDataSourceIdentityData(), new Date(keyTime),
                        new Date(keyTime + archiveIdleTimeMillis), orginalSql, StmtErrType.EXCEPTION.toString(),
                        new Date(recordTime), StmtOptAbnormal.getExStr(getEx())));
            }
        }
    }
    
    public static class BatchStmtPartialSuc extends FinishBatchStmt {
        private final Throwable ex;

        public BatchStmtPartialSuc(MonitorDataProcessor monitorDataProcessor, long recordTime,
                List<String> batchSqlList, long elapseTime, Throwable ex, int[] batchResults) {
            super(monitorDataProcessor, recordTime, batchSqlList, elapseTime, batchResults);
            this.ex = StmtOptAbnormal.processEx(ex);
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            final int[] batchResults = getBatchResults();
            final List<String> batchSqlList = getBatchSqlList();
            final MonitorDataProcessor processor = getMonitorDataProcessor();
            final int length = batchResults.length;
            final long everyElapseTime = BigDecimal.valueOf(getElapseTime() / length).setScale(0, RoundingMode.HALF_UP).longValue();
            final Set<String> errPreparedSql = new HashSet<String>();
            int idx = 0;
            String preparedSql;
            SqlStatisticData ssd;
            String orginalSql;
            int br;
            for (; idx < length; idx++) {
                orginalSql = batchSqlList.get(idx);
                preparedSql = SqlUtils.generatePreparedStatement(orginalSql, processor);
                ssd = getSsd(mds, preparedSql );
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
            for (; idx < batchSqlList.size(); idx++) {
                orginalSql = batchSqlList.get(idx);
                preparedSql = SqlUtils.generatePreparedStatement(orginalSql, processor);
                ssd = getSsd(mds, preparedSql );
                ssd.increaseErrorCount(1);
            }
            errPreparedSql.clear();
        }

        protected Throwable getEx() {
            return ex;
        }
    }

    
    public static class BatchStmtPartialSucErr extends FinishBatchStmt {
        private final Throwable ex;

        public BatchStmtPartialSucErr(MonitorDataProcessor monitorDataProcessor, long recordTime,
                List<String> batchSqlList, long elapseTime, Throwable ex, int[] batchResults) {
            super(monitorDataProcessor, recordTime, batchSqlList, elapseTime, batchResults);
            this.ex = StmtOptAbnormal.processEx(ex);
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            final int[] batchResults = getBatchResults();
            final List<String> batchSqlList = getBatchSqlList();
            final MonitorDataProcessor processor = getMonitorDataProcessor();
            final int length = batchResults.length;
            final Set<String> errPreparedSql = new HashSet<String>();
            final long archiveIdleTimeMillis = getMonitorDataProcessor().getArchiveIdleTimeMillis();
            final long keyTime = getRecordTime() - (getRecordTime() % archiveIdleTimeMillis);
            int idx = 0;
            String preparedSql;
            String orginalSql;
            int br;
            for (; idx < length; idx++) {
                orginalSql = batchSqlList.get(idx);
                preparedSql = SqlUtils.generatePreparedStatement(orginalSql, processor);
                br = batchResults[idx];
                if (br < 0 && br != Statement.SUCCESS_NO_INFO) {
                    if (errPreparedSql.add(preparedSql)) {
                        final List<AbnormalSqlRecordData> asrdList = getASRDList(mds, preparedSql,
                                StmtErrType.EXCEPTION);
                        if (asrdList.size() < processor.getErrMsgLimit()) {
                            asrdList.add(new AbnormalSqlRecordData(
                                    getMonitorDataProcessor().getDataSourceIdentityData(), new Date(keyTime), new Date(
                                            keyTime + archiveIdleTimeMillis), orginalSql,
                                    StmtErrType.EXCEPTION.toString(), new Date(getRecordTime()),
                                    StmtOptAbnormal.getExStr(ex)));
                        }
                    }
                }
            }
            for (; idx < batchSqlList.size(); idx++) {
                orginalSql = batchSqlList.get(idx);
                preparedSql = SqlUtils.generatePreparedStatement(orginalSql, processor);
                if (errPreparedSql.add(preparedSql)) {
                    final List<AbnormalSqlRecordData> asrdList = getASRDList(mds, preparedSql, StmtErrType.EXCEPTION);
                    if (asrdList.size() < processor.getErrMsgLimit()) {
                        asrdList.add(new AbnormalSqlRecordData(getMonitorDataProcessor().getDataSourceIdentityData(),
                                new Date(keyTime), new Date(keyTime + archiveIdleTimeMillis), orginalSql,
                                StmtErrType.EXCEPTION.toString(), new Date(getRecordTime()),
                                StmtOptAbnormal.getExStr(ex)));
                    }
                }
            }
            errPreparedSql.clear();
        }

        protected Throwable getEx() {
            return ex;
        }
    }
    
    public static class BatchStmt extends BatchStmtPartialSuc {

        public BatchStmt(MonitorDataProcessor monitorDataProcessor, long recordTime, List<String> batchSqlList,
                long elapseTime, Throwable ex) {
            super(monitorDataProcessor, recordTime, batchSqlList, elapseTime, ex, null);
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            final MonitorDataProcessor processor = getMonitorDataProcessor();
            final List<String> batchSqlList = getBatchSqlList();
            final Set<String> errPreparedSql = new HashSet<String>();
            String preparedSql;
            SqlStatisticData ssd;
            for (String orginalSql : batchSqlList) {
                preparedSql = SqlUtils.generatePreparedStatement(orginalSql, processor);
                ssd = getSsd(mds, preparedSql );
                ssd.increaseErrorCount(1);
            }
            errPreparedSql.clear();
        }
    }
    
    public static class BatchStmtErr extends BatchStmtPartialSuc {

        public BatchStmtErr(MonitorDataProcessor monitorDataProcessor, long recordTime, List<String> batchSqlList,
                long elapseTime, Throwable ex) {
            super(monitorDataProcessor, recordTime, batchSqlList, elapseTime, ex, null);
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            final MonitorDataProcessor processor = getMonitorDataProcessor();
            final List<String> batchSqlList = getBatchSqlList();
            final Set<String> errPreparedSql = new HashSet<String>();
            final long archiveIdleTimeMillis = getMonitorDataProcessor().getArchiveIdleTimeMillis();
            final long keyTime = getRecordTime() - (getRecordTime() % archiveIdleTimeMillis);
            String preparedSql;
            for (String orginalSql : batchSqlList) {
                preparedSql = SqlUtils.generatePreparedStatement(orginalSql, processor);
                if (errPreparedSql.add(preparedSql)) {
                    final List<AbnormalSqlRecordData> asrdList = getASRDList(mds, preparedSql, StmtErrType.EXCEPTION);
                    if (asrdList.size() < processor.getErrMsgLimit()) {
                        asrdList.add(new AbnormalSqlRecordData(getMonitorDataProcessor().getDataSourceIdentityData(),
                                new Date(keyTime), new Date(keyTime + archiveIdleTimeMillis), orginalSql,
                                StmtErrType.EXCEPTION.toString(), new Date(getRecordTime()),
                                StmtOptAbnormal.getExStr(getEx())));
                    }
                }
            }
            errPreparedSql.clear();
        }
    }
    
    
    
}