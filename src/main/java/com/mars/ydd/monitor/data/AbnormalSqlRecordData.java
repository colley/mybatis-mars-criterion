package com.mars.ydd.monitor.data;

import java.util.Date;

public class AbnormalSqlRecordData extends AbstractStatisticData {
    private static final long serialVersionUID = -339140317732246122L;

    private final String text;
    private final String parameter;
    private final String abnormalType; // Slow | Error
    private final Date executeTime;
    private final long elapseTime;
    private final String errorMsg;

    private int sqlId;
    
    public AbnormalSqlRecordData(DataSourceIdentityData dataSourceIdentityData, Date startTime, Date endTime,
            String text, String abnormalType, Date executeTime, long elapseTime) {
        super(dataSourceIdentityData, startTime, endTime);
        this.text = text;
        this.parameter = null;
        this.abnormalType = abnormalType;
        this.executeTime = executeTime;
        this.elapseTime = elapseTime;
        this.errorMsg = null;
    }

    public AbnormalSqlRecordData(DataSourceIdentityData dataSourceIdentityData, Date startTime, Date endTime,
            String text, String abnormalType, Date executeTime, String errorMsg) {
        super(dataSourceIdentityData, startTime, endTime);
        this.text = text;
        this.parameter = null;
        this.abnormalType = abnormalType;
        this.executeTime = executeTime;
        this.elapseTime = 0;
        this.errorMsg = errorMsg;
    }

    public AbnormalSqlRecordData(DataSourceIdentityData dataSourceIdentityData, Date startTime, Date endTime,
            String text, String parameter, String abnormalType, Date executeTime, long elapseTime) {
        super(dataSourceIdentityData, startTime, endTime);
        this.text = text;
        this.parameter = parameter;
        this.abnormalType = abnormalType;
        this.executeTime = executeTime;
        this.elapseTime = elapseTime;
        this.errorMsg = null;
    }

    public AbnormalSqlRecordData(DataSourceIdentityData dataSourceIdentityData, Date startTime, Date endTime,
            String text, String parameter, String abnormalType, Date executeTime, String errorMsg) {
        super(dataSourceIdentityData, startTime, endTime);
        this.text = text;
        this.parameter = parameter;
        this.abnormalType = abnormalType;
        this.executeTime = executeTime;
        this.elapseTime = 0;
        this.errorMsg = errorMsg;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((abnormalType == null) ? 0 : abnormalType.hashCode());
        result = prime * result + (int) (elapseTime ^ (elapseTime >>> 32));
        result = prime * result + ((errorMsg == null) ? 0 : errorMsg.hashCode());
        result = prime * result + ((executeTime == null) ? 0 : executeTime.hashCode());
        result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbnormalSqlRecordData other = (AbnormalSqlRecordData) obj;
        if (abnormalType == null) {
            if (other.abnormalType != null)
                return false;
        } else if (!abnormalType.equals(other.abnormalType))
            return false;
        if (elapseTime != other.elapseTime)
            return false;
        if (errorMsg == null) {
            if (other.errorMsg != null)
                return false;
        } else if (!errorMsg.equals(other.errorMsg))
            return false;
        if (executeTime == null) {
            if (other.executeTime != null)
                return false;
        } else if (!executeTime.equals(other.executeTime))
            return false;
        if (parameter == null) {
            if (other.parameter != null)
                return false;
        } else if (!parameter.equals(other.parameter))
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        return true;
    }

    public String getText() {
        return text;
    }

    public String getParameter() {
        return parameter;
    }

    public String getAbnormalType() {
        return abnormalType;
    }

    public Date getExecuteTime() {
        return executeTime;
    }

    public long getElapseTime() {
        return elapseTime;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

	public int getSqlId() {
		return sqlId;
	}

	public void setSqlId(int sqlId) {
		this.sqlId = sqlId;
	}
    
    
    
}
