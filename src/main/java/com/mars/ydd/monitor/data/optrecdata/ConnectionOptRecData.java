package com.mars.ydd.monitor.data.optrecdata;

import org.apache.tomcat.jdbc.pool.ConnectionPool;

import com.mars.ydd.monitor.data.DataSourceStatisticData;
import com.mars.ydd.monitor.data.OperationRecordData;
import com.mars.ydd.monitor.processor.MonitorDataProcessor;
import com.mars.ydd.monitor.processor.MonitorDataStructure;

public final class ConnectionOptRecData {

    public static class PhyCreateConn extends OperationRecordData {

        public PhyCreateConn(MonitorDataProcessor monitorDataProcessor) {
            super(monitorDataProcessor, System.currentTimeMillis());
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            DataSourceStatisticData dssd = getDssd(mds);
            dssd.increasePhyCreateConnCount(1);
            ConnectionPool connPool = getMonitorDataProcessor().getConnPool();
            if (connPool != null) {
                dssd.setCurrentAlivePhyConnCount(connPool.getSize());
            }
        }

    }

    public static class PhyCreateConnSuc extends OperationRecordData {

        public PhyCreateConnSuc(MonitorDataProcessor monitorDataProcessor) {
            super(monitorDataProcessor, System.currentTimeMillis());
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            DataSourceStatisticData dssd = getDssd(mds);
            dssd.increasePhyCreateConnSucCount(1);
            ConnectionPool connPool = getMonitorDataProcessor().getConnPool();
            if (connPool != null) {
                dssd.setCurrentAlivePhyConnCount(connPool.getSize());
            }
        }

    }

    public static class PhyCreateConnErr extends OperationRecordData {

        public PhyCreateConnErr(MonitorDataProcessor monitorDataProcessor) {
            super(monitorDataProcessor, System.currentTimeMillis());
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            DataSourceStatisticData dssd = getDssd(mds);
            dssd.increasePhyCreateConnErrCount(1);
            ConnectionPool connPool = getMonitorDataProcessor().getConnPool();
            if (connPool != null) {
                dssd.setCurrentAlivePhyConnCount(connPool.getSize());
            }
        }

    }

    public static class PhyCloseConn extends OperationRecordData {

        public PhyCloseConn(MonitorDataProcessor monitorDataProcessor) {
            super(monitorDataProcessor, System.currentTimeMillis());
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            DataSourceStatisticData dssd = getDssd(mds);
            dssd.increasePhyCloseConnCount(1);
            ConnectionPool connPool = getMonitorDataProcessor().getConnPool();
            if (connPool != null) {
                dssd.setCurrentAlivePhyConnCount(connPool.getSize());
            }
        }

    }

    public static class GetConn extends OperationRecordData {
        public GetConn(MonitorDataProcessor monitorDataProcessor, long recordTime) {
            super(monitorDataProcessor, recordTime);
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            DataSourceStatisticData dssd = getDssd(mds);
            dssd.increaseGetConnCount(1);
            ConnectionPool connPool = getMonitorDataProcessor().getConnPool();
            if (connPool != null) {
                dssd.setCurrentAlivePhyConnCount(connPool.getSize());
            }
        }

    }

    public static class GetConnSuc extends OperationRecordData {

        private final long waitTime;

        public GetConnSuc(MonitorDataProcessor monitorDataProcessor, long recordTime, long waitTime) {
            super(monitorDataProcessor, recordTime);
            this.waitTime = waitTime;
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            DataSourceStatisticData dssd = getDssd(mds);
            dssd.increaseGetConnSucCount(1);
            dssd.increaseGetConnWaitTimeSum(waitTime);
            if (waitTime > dssd.getGetConnMaxWaitTime()) {
                dssd.setGetConnMaxWaitTime(waitTime);
            }
            ConnectionPool connPool = getMonitorDataProcessor().getConnPool();
            if (connPool != null) {
                dssd.setCurrentAlivePhyConnCount(connPool.getSize());
            }
        }

    }

    public static class GetConnErr extends OperationRecordData {
        public GetConnErr(MonitorDataProcessor monitorDataProcessor, long recordTime) {
            super(monitorDataProcessor, recordTime);
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            DataSourceStatisticData dssd = getDssd(mds);
            dssd.increaseGetConnErrCount(1);
            ConnectionPool connPool = getMonitorDataProcessor().getConnPool();
            if (connPool != null) {
                dssd.setCurrentAlivePhyConnCount(connPool.getSize());
            }
        }

    }

    public static class CloseConn extends OperationRecordData {

        public CloseConn(MonitorDataProcessor monitorDataProcessor) {
            super(monitorDataProcessor, System.currentTimeMillis());
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            DataSourceStatisticData dssd = getDssd(mds);
            dssd.increaseCloseConnCount(1);
            ConnectionPool connPool = getMonitorDataProcessor().getConnPool();
            if (connPool != null) {
                dssd.setCurrentAlivePhyConnCount(connPool.getSize());
            }
        }
    }

    public static class CreateTrans extends OperationRecordData {

        public CreateTrans(MonitorDataProcessor monitorDataProcessor) {
            super(monitorDataProcessor, System.currentTimeMillis());
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            getDssd(mds).increaseCreateTransCount(1);
        }

    }

    public static class CommitTrans extends OperationRecordData {

        public CommitTrans(MonitorDataProcessor monitorDataProcessor) {
            super(monitorDataProcessor, System.currentTimeMillis());
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            getDssd(mds).increaseCommitTransCount(1);
        }

    }

    public static class RollbackTrans extends OperationRecordData {

        public RollbackTrans(MonitorDataProcessor monitorDataProcessor) {
            super(monitorDataProcessor, System.currentTimeMillis());
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            getDssd(mds ).increaseRollbackTransCount(1);
        }

    }

    public static class ErrTrans extends OperationRecordData {

        public ErrTrans(MonitorDataProcessor monitorDataProcessor) {
            super(monitorDataProcessor, System.currentTimeMillis());
        }

        @Override
        public void transfer(MonitorDataStructure mds) {
            getDssd(mds).increaseErrTransCount(1);
        }

    }

}