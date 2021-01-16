package com.mars.ydd.monitor.data;

import java.util.Date;

public class SqlStatisticData extends AbstractStatisticData {
    private static final long serialVersionUID = -7752174556085043139L;

    private final String text;
    private int executeCount;
    private int finishCount;
    private int timeoutCount;
    private int errorCount;
    private long finishTimeSum;
    private long timeoutTimeSum;
    private int effectRowSum;
    private int queryRowSum;
    private int resultSetOpenCount;
    private int resultSetCloseCount;

    private int sqlId;
    
    
    public SqlStatisticData(DataSourceIdentityData dataSourceIdentityData, Date startTime, Date endTime, String text) {
        super(dataSourceIdentityData, startTime, endTime);
        this.text = text;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + effectRowSum;
        result = prime * result + errorCount;
        result = prime * result + executeCount;
        result = prime * result + finishCount;
        result = prime * result + (int) (finishTimeSum ^ (finishTimeSum >>> 32));
        result = prime * result + queryRowSum;
        result = prime * result + resultSetCloseCount;
        result = prime * result + resultSetOpenCount;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        result = prime * result + timeoutCount;
        result = prime * result + (int) (timeoutTimeSum ^ (timeoutTimeSum >>> 32));
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
        SqlStatisticData other = (SqlStatisticData) obj;
        if (effectRowSum != other.effectRowSum)
            return false;
        if (errorCount != other.errorCount)
            return false;
        if (executeCount != other.executeCount)
            return false;
        if (finishCount != other.finishCount)
            return false;
        if (finishTimeSum != other.finishTimeSum)
            return false;
        if (queryRowSum != other.queryRowSum)
            return false;
        if (resultSetCloseCount != other.resultSetCloseCount)
            return false;
        if (resultSetOpenCount != other.resultSetOpenCount)
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        if (timeoutCount != other.timeoutCount)
            return false;
        if (timeoutTimeSum != other.timeoutTimeSum)
            return false;
        return true;
    }

    public String getText() {
        return text;
    }

    public void increaseExecuteCount(int executeCount) {
        this.executeCount += executeCount;
    }

    public void increaseFinishCount(int finishCount) {
        this.finishCount += finishCount;
    }

    public void increaseErrorCount(int errorCount) {
        this.errorCount += errorCount;
    }

    public void increaseTimeoutCount(int timeoutCount) {
        this.timeoutCount += timeoutCount;
    }

    public void increaseFinishTimeSum(long finishTimeSum) {
        this.finishTimeSum += finishTimeSum;
    }

    public void increaseTimeoutTimeSum(long timeoutTimeSum) {
        this.timeoutTimeSum += timeoutTimeSum;
    }

    public void increaseEffectRowSum(int effectRowSum) {
        this.effectRowSum += effectRowSum;
    }

    public void increaseQueryRowSum(int queryRowSum) {
        this.queryRowSum += queryRowSum;
    }

    public void increaseResultSetOpenCount(int resultSetOpenCount) {
        this.resultSetOpenCount += resultSetOpenCount;
    }

    public void increaseResultSetCloseCount(int resultSetCloseCount) {
        this.resultSetCloseCount += resultSetCloseCount;
    }

    public int getExecuteCount() {
        return executeCount;
    }

    public void setExecuteCount(int executeCount) {
        this.executeCount = executeCount;
    }

    public int getFinishCount() {
        return finishCount;
    }

    public void setFinishCount(int finishCount) {
        this.finishCount = finishCount;
    }

    public int getTimeoutCount() {
        return timeoutCount;
    }

    public void setTimeoutCount(int timeoutCount) {
        this.timeoutCount = timeoutCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public long getFinishTimeSum() {
        return finishTimeSum;
    }

    public void setFinishTimeSum(long finishTimeSum) {
        this.finishTimeSum = finishTimeSum;
    }

    public long getTimeoutTimeSum() {
        return timeoutTimeSum;
    }

    public void setTimeoutTimeSum(long timeoutTimeSum) {
        this.timeoutTimeSum = timeoutTimeSum;
    }

    public int getEffectRowSum() {
        return effectRowSum;
    }

    public void setEffectRowSum(int effectRowSum) {
        this.effectRowSum = effectRowSum;
    }

    public int getQueryRowSum() {
        return queryRowSum;
    }

    public void setQueryRowSum(int queryRowSum) {
        this.queryRowSum = queryRowSum;
    }

    public int getResultSetOpenCount() {
        return resultSetOpenCount;
    }

    public void setResultSetOpenCount(int resultSetOpenCount) {
        this.resultSetOpenCount = resultSetOpenCount;
    }

    public int getResultSetCloseCount() {
        return resultSetCloseCount;
    }

    public void setResultSetCloseCount(int resultSetCloseCount) {
        this.resultSetCloseCount = resultSetCloseCount;
    }

	public int getSqlId() {
		return sqlId;
	}

	public void setSqlId(int sqlId) {
		this.sqlId = sqlId;
	}
    
    
}