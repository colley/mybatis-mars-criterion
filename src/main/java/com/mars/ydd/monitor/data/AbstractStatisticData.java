package com.mars.ydd.monitor.data;

import java.io.Serializable;
import java.util.Date;

public abstract class AbstractStatisticData implements Serializable {
    private static final long serialVersionUID = 7738288473170281114L;

    private int hash = 0;

    private final DataSourceIdentityData dataSourceIdentityData;
    private final Date startTime;
    private final Date endTime;

    public AbstractStatisticData(DataSourceIdentityData dataSourceIdentityData, Date startTime, Date endTime) {
        super();
        this.dataSourceIdentityData = dataSourceIdentityData;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DataSourceIdentityData getDataSourceIdentityData() {
        return dataSourceIdentityData;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    @Override
    public int hashCode() {
        int result = hash;
        if (result == 0) {
            final int prime = 31;
            result = 1;
            result = prime * result + ((dataSourceIdentityData == null) ? 0 : dataSourceIdentityData.hashCode());
            result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
            result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
            hash = result;
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractStatisticData other = (AbstractStatisticData) obj;
        if (dataSourceIdentityData == null) {
            if (other.dataSourceIdentityData != null)
                return false;
        } else if (!dataSourceIdentityData.equals(other.dataSourceIdentityData))
            return false;
        if (endTime == null) {
            if (other.endTime != null)
                return false;
        } else if (!endTime.equals(other.endTime))
            return false;
        if (startTime == null) {
            if (other.startTime != null)
                return false;
        } else if (!startTime.equals(other.startTime))
            return false;
        return true;
    }
}
