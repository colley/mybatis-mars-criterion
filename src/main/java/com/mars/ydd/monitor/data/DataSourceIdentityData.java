package com.mars.ydd.monitor.data;

import java.io.Serializable;
import java.util.Date;

public class DataSourceIdentityData implements Serializable {
    private static final long serialVersionUID = 2918300159472957333L;
    
    private final String poolId;
    private final String groupId;
    private final String dataId;
    private final String jdbcUrl;
    private final String username;
    private final String serverIp;
    private final Date datasourceCreateTime;

    public DataSourceIdentityData(String poolId, String groupId, String dataId, String jdbcUrl, String username,
            String serverIp) {
        super();
        this.poolId = poolId;
        this.groupId = groupId;
        this.dataId = dataId;
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.serverIp = serverIp;
        this.datasourceCreateTime = new Date();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dataId == null) ? 0 : dataId.hashCode());
        result = prime * result + ((datasourceCreateTime == null) ? 0 : datasourceCreateTime.hashCode());
        result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
        result = prime * result + ((jdbcUrl == null) ? 0 : jdbcUrl.hashCode());
        result = prime * result + ((poolId == null) ? 0 : poolId.hashCode());
        result = prime * result + ((serverIp == null) ? 0 : serverIp.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
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
        DataSourceIdentityData other = (DataSourceIdentityData) obj;
        if (dataId == null) {
            if (other.dataId != null)
                return false;
        } else if (!dataId.equals(other.dataId))
            return false;
        if (datasourceCreateTime == null) {
            if (other.datasourceCreateTime != null)
                return false;
        } else if (!datasourceCreateTime.equals(other.datasourceCreateTime))
            return false;
        if (groupId == null) {
            if (other.groupId != null)
                return false;
        } else if (!groupId.equals(other.groupId))
            return false;
        if (jdbcUrl == null) {
            if (other.jdbcUrl != null)
                return false;
        } else if (!jdbcUrl.equals(other.jdbcUrl))
            return false;
        if (poolId == null) {
            if (other.poolId != null)
                return false;
        } else if (!poolId.equals(other.poolId))
            return false;
        if (serverIp == null) {
            if (other.serverIp != null)
                return false;
        } else if (!serverIp.equals(other.serverIp))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    public String getPoolId() {
        return poolId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getDataId() {
        return dataId;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getServerIp() {
        return serverIp;
    }

    public Date getDatasourceCreateTime() {
        return datasourceCreateTime;
    }

}
