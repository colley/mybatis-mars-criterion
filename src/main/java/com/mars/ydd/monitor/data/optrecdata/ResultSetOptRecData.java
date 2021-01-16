package com.mars.ydd.monitor.data.optrecdata;

import com.mars.ydd.monitor.data.SqlStatisticData;
import com.mars.ydd.monitor.data.optrecdata.StatementOptRecData.CommonStmtOpt;
import com.mars.ydd.monitor.processor.MonitorDataProcessor;
import com.mars.ydd.monitor.processor.MonitorDataStructure;

public class ResultSetOptRecData {

    public static class OpenResultSet extends CommonStmtOpt {

        public OpenResultSet(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql) {
            super(monitorDataProcessor, recordTime, preparedSql);
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            getSsd(mds).increaseResultSetOpenCount(1);
        }

    }

    public static class CloseResultSet extends CommonStmtOpt {
        private final int queryRowCount;

        public CloseResultSet(MonitorDataProcessor monitorDataProcessor, long recordTime, String preparedSql,
                int queryRowCount) {
            super(monitorDataProcessor, recordTime, preparedSql);
            this.queryRowCount = queryRowCount;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            SqlStatisticData ssd = getSsd(mds );
            ssd.increaseResultSetCloseCount(1);
            ssd.increaseQueryRowSum(queryRowCount);
        }

    }

}