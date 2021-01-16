package com.mars.ydd.monitor.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.commons.lang.BooleanUtils;

import com.lmax.disruptor.EventFactory;
import com.mars.ydd.monitor.data.optrecdata.StatementOptRecData;
import com.mars.ydd.monitor.data.optrecdata.StatementOptRecData.StmtErrType;
import com.mars.ydd.monitor.processor.MonitorDataProcessor;
import com.mars.ydd.monitor.processor.MonitorDataStructure;

public abstract class OperationRecordData {
    public final static class OrdContainer {
        private OperationRecordData ord;

        public OperationRecordData getOrd() {
            return ord;
        }

        public void setOrd(OperationRecordData ord) {
            this.ord = ord;
        }

        public final static EventFactory<OrdContainer> ORD_FACTORY = new EventFactory<OrdContainer>() {
            public OrdContainer newInstance() {
                return new OrdContainer();
            }
        };
    }

    public abstract void transfer(MonitorDataStructure mds);

    private final MonitorDataProcessor monitorDataProcessor;
    private final long recordTime;

    protected OperationRecordData(MonitorDataProcessor monitorDataProcessor, long recordTime) {
        this.monitorDataProcessor = monitorDataProcessor;
        this.recordTime = recordTime;
    }

    protected DataSourceStatisticData getDssd(MonitorDataStructure mds) {
        final long archiveIdleTimeMillis = monitorDataProcessor.getArchiveIdleTimeMillis();
        final long keyTime = recordTime - (recordTime % archiveIdleTimeMillis);
        DataSourceStatisticData dssd = mds.processedDSSD.get(keyTime);
        if (dssd == null) {
            dssd = new DataSourceStatisticData(monitorDataProcessor.getDataSourceIdentityData(), new Date(keyTime),
                    new Date(keyTime + archiveIdleTimeMillis));
            mds.processedDSSD.put(Long.valueOf(keyTime), dssd);
        }
        return dssd;
    }

    protected Map<String, SqlStatisticData> getSsdMap(MonitorDataStructure mds) {
        final long archiveIdleTimeMillis = monitorDataProcessor.getArchiveIdleTimeMillis();
        final long keyTime = recordTime - (recordTime % archiveIdleTimeMillis);
        Map<String, SqlStatisticData> ssdMap = mds.processedSSDMap.get(keyTime);
        if (ssdMap == null) {
            ssdMap = new HashMap<String, SqlStatisticData>();
            mds.processedSSDMap.put(Long.valueOf(keyTime), ssdMap);
        }
        return ssdMap;
    }

    protected Map<String, Map<StmtErrType, List<AbnormalSqlRecordData>>> getAsrdMap(MonitorDataStructure mds ) {
        final long archiveIdleTimeMillis = monitorDataProcessor.getArchiveIdleTimeMillis();
        final long keyTime = recordTime - (recordTime % archiveIdleTimeMillis);
        Map<String, Map<StmtErrType, List<AbnormalSqlRecordData>>> asrdMap = mds.processedASRDMap.get(keyTime);
        if (asrdMap == null) {
            asrdMap = new HashMap<String, Map<StmtErrType, List<AbnormalSqlRecordData>>>();
            mds.processedASRDMap.put(Long.valueOf(keyTime), asrdMap);           
        }
        return asrdMap;
    }

    protected List<AbnormalSqlRecordData> getASRDList(MonitorDataStructure mds, String preparedSql, StmtErrType set ) {
        final Map<String, Map<StmtErrType, List<AbnormalSqlRecordData>>> asrdMap = getAsrdMap(mds);
        Map<StmtErrType, List<AbnormalSqlRecordData>> asrdListMap = asrdMap.get(preparedSql);
        if (asrdListMap == null) {
            asrdListMap = new HashMap<StatementOptRecData.StmtErrType, List<AbnormalSqlRecordData>>();
            asrdMap.put(preparedSql, asrdListMap);
        }
        List<AbnormalSqlRecordData> asrdList = asrdListMap.get(set);
        if (asrdList == null) {
            asrdList = new ArrayList<AbnormalSqlRecordData>();
            asrdListMap.put(set, asrdList);
        }
        return asrdList;
    }

    protected MonitorDataProcessor getMonitorDataProcessor() {
        return monitorDataProcessor;
    }

    protected long getRecordTime() {
        return recordTime;
    }
}
