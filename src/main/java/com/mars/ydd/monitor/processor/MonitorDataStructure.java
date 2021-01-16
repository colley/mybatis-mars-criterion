package com.mars.ydd.monitor.processor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mars.ydd.monitor.data.AbnormalSqlRecordData;
import com.mars.ydd.monitor.data.DataSourceIdentityData;
import com.mars.ydd.monitor.data.DataSourceStatisticData;
import com.mars.ydd.monitor.data.SqlStatisticData;
import com.mars.ydd.monitor.data.optrecdata.StatementOptRecData.StmtErrType;

public class MonitorDataStructure implements Serializable {
    private static final long serialVersionUID = -7078827184160336005L;

    public final DataSourceIdentityData dataSourceIdentityData;
    public final Map<Long, DataSourceStatisticData> processedDSSD = new HashMap<Long, DataSourceStatisticData>();
    public final Map<Long, Map<String, SqlStatisticData>> processedSSDMap = new HashMap<Long, Map<String, SqlStatisticData>>();
    public final Map<Long, Map<String, Map<StmtErrType, List<AbnormalSqlRecordData>>>> processedASRDMap = new HashMap<Long, Map<String, Map<StmtErrType, List<AbnormalSqlRecordData>>>>();

    public MonitorDataStructure(DataSourceIdentityData dataSourceIdentityData) {
        this.dataSourceIdentityData = dataSourceIdentityData;
    }

    protected boolean isEmpty() {
        return processedDSSD.isEmpty() && processedSSDMap.isEmpty() && processedASRDMap.isEmpty();
    }

    protected void clear() {
        processedDSSD.clear();
        for (Map<String, SqlStatisticData> value : processedSSDMap.values()) {
            value.clear();
        }
        processedSSDMap.clear();
        for (Map<String, Map<StmtErrType, List<AbnormalSqlRecordData>>> value : processedASRDMap.values()) {
            for (Map<StmtErrType, List<AbnormalSqlRecordData>> subValue : value.values()) {
                for (List<AbnormalSqlRecordData> asrdList : subValue.values()) {
                    asrdList.clear();
                }
                subValue.clear();
            }
            value.clear();
        }
        processedASRDMap.clear();
    }
}