package com.mars.ydd.monitor.data;

import java.util.Date;

public class DataSourceStatisticData extends AbstractStatisticData {
    private static final long serialVersionUID = -6960083382118131437L;

    private int phyCreateConnCount;
    private int phyCloseConnCount;
    private int phyCreateConnSucCount;
    private int phyCreateConnErrCount;
    private int getConnCount;
    private int closeConnCount;
    private int getConnSucCount;
    private int getConnErrCount;
    private long getConnWaitTimeSum;
    private long getConnMaxWaitTime;
    private int createTransCount;
    private int commitTransCount;
    private int rollbackTransCount;
    private int errTransCount;
    private int currentAlivePhyConnCount;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + closeConnCount;
        result = prime * result + commitTransCount;
        result = prime * result + createTransCount;
        result = prime * result + currentAlivePhyConnCount;
        result = prime * result + errTransCount;
        result = prime * result + getConnCount;
        result = prime * result + getConnErrCount;
        result = prime * result + (int) (getConnMaxWaitTime ^ (getConnMaxWaitTime >>> 32));
        result = prime * result + getConnSucCount;
        result = prime * result + (int) (getConnWaitTimeSum ^ (getConnWaitTimeSum >>> 32));
        result = prime * result + phyCloseConnCount;
        result = prime * result + phyCreateConnCount;
        result = prime * result + phyCreateConnErrCount;
        result = prime * result + phyCreateConnSucCount;
        result = prime * result + rollbackTransCount;
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
        DataSourceStatisticData other = (DataSourceStatisticData) obj;
        if (closeConnCount != other.closeConnCount)
            return false;
        if (commitTransCount != other.commitTransCount)
            return false;
        if (createTransCount != other.createTransCount)
            return false;
        if (currentAlivePhyConnCount != other.currentAlivePhyConnCount)
            return false;
        if (errTransCount != other.errTransCount)
            return false;
        if (getConnCount != other.getConnCount)
            return false;
        if (getConnErrCount != other.getConnErrCount)
            return false;
        if (getConnMaxWaitTime != other.getConnMaxWaitTime)
            return false;
        if (getConnSucCount != other.getConnSucCount)
            return false;
        if (getConnWaitTimeSum != other.getConnWaitTimeSum)
            return false;
        if (phyCloseConnCount != other.phyCloseConnCount)
            return false;
        if (phyCreateConnCount != other.phyCreateConnCount)
            return false;
        if (phyCreateConnErrCount != other.phyCreateConnErrCount)
            return false;
        if (phyCreateConnSucCount != other.phyCreateConnSucCount)
            return false;
        if (rollbackTransCount != other.rollbackTransCount)
            return false;
        return true;
    }

    public DataSourceStatisticData(DataSourceIdentityData dataSourceIdentityData, Date startTime, Date endTime) {
        super(dataSourceIdentityData, startTime, endTime);
    }

    public void increasePhyCreateConnCount(int pCreateConnCount) {
        this.phyCreateConnCount += pCreateConnCount;
    }

    public void increasePhyCloseConnCount(int pCloseConnCount) {
        this.phyCloseConnCount += pCloseConnCount;
    }

    public void increasePhyCreateConnSucCount(int pCreateConnSucCount) {
        this.phyCreateConnSucCount += pCreateConnSucCount;
    }

    public void increasePhyCreateConnErrCount(int pCreateConnErrCount) {
        this.phyCreateConnErrCount += pCreateConnErrCount;
    }

    public void increaseGetConnCount(int getConnCount) {
        this.getConnCount += getConnCount;
    }

    public void increaseCloseConnCount(int closeConnCount) {
        this.closeConnCount += closeConnCount;
    }

    public void increaseGetConnSucCount(int getConnSucCount) {
        this.getConnSucCount += getConnSucCount;
    }

    public void increaseGetConnErrCount(int getConnErrCount) {
        this.getConnErrCount += getConnErrCount;
    }

    public void increaseGetConnWaitTimeSum(long getConnWaitTimeSum) {
        this.getConnWaitTimeSum += getConnWaitTimeSum;
    }

    public void increaseCreateTransCount(int createTransCount) {
        this.createTransCount += createTransCount;
    }

    public void increaseCommitTransCount(int commitTransCount) {
        this.commitTransCount += commitTransCount;
    }

    public void increaseRollbackTransCount(int rollbackTransCount) {
        this.rollbackTransCount += rollbackTransCount;
    }

    public void increaseErrTransCount(int errTransCount) {
        this.errTransCount += errTransCount;
    }

    public int getPhyCreateConnCount() {
        return phyCreateConnCount;
    }

    public void setPhyCreateConnCount(int phyCreateConnCount) {
        this.phyCreateConnCount = phyCreateConnCount;
    }

    public int getPhyCloseConnCount() {
        return phyCloseConnCount;
    }

    public void setPhyCloseConnCount(int phyCloseConnCount) {
        this.phyCloseConnCount = phyCloseConnCount;
    }

    public int getPhyCreateConnSucCount() {
        return phyCreateConnSucCount;
    }

    public void setPhyCreateConnSucCount(int phyCreateConnSucCount) {
        this.phyCreateConnSucCount = phyCreateConnSucCount;
    }

    public int getPhyCreateConnErrCount() {
        return phyCreateConnErrCount;
    }

    public void setPhyCreateConnErrCount(int phyCreateConnErrCount) {
        this.phyCreateConnErrCount = phyCreateConnErrCount;
    }

    public int getGetConnCount() {
        return getConnCount;
    }

    public void setGetConnCount(int getConnCount) {
        this.getConnCount = getConnCount;
    }

    public int getCloseConnCount() {
        return closeConnCount;
    }

    public void setCloseConnCount(int closeConnCount) {
        this.closeConnCount = closeConnCount;
    }

    public int getGetConnSucCount() {
        return getConnSucCount;
    }

    public void setGetConnSucCount(int getConnSucCount) {
        this.getConnSucCount = getConnSucCount;
    }

    public int getGetConnErrCount() {
        return getConnErrCount;
    }

    public void setGetConnErrCount(int getConnErrCount) {
        this.getConnErrCount = getConnErrCount;
    }

    public long getGetConnWaitTimeSum() {
        return getConnWaitTimeSum;
    }

    public void setGetConnWaitTimeSum(long getConnWaitTimeSum) {
        this.getConnWaitTimeSum = getConnWaitTimeSum;
    }

    public long getGetConnMaxWaitTime() {
        return getConnMaxWaitTime;
    }

    public void setGetConnMaxWaitTime(long getConnMaxWaitTime) {
        this.getConnMaxWaitTime = getConnMaxWaitTime;
    }

    public int getCreateTransCount() {
        return createTransCount;
    }

    public void setCreateTransCount(int createTransCount) {
        this.createTransCount = createTransCount;
    }

    public int getCommitTransCount() {
        return commitTransCount;
    }

    public void setCommitTransCount(int commitTransCount) {
        this.commitTransCount = commitTransCount;
    }

    public int getRollbackTransCount() {
        return rollbackTransCount;
    }

    public void setRollbackTransCount(int rollbackTransCount) {
        this.rollbackTransCount = rollbackTransCount;
    }

    public int getErrTransCount() {
        return errTransCount;
    }

    public void setErrTransCount(int errTransCount) {
        this.errTransCount = errTransCount;
    }

    public int getCurrentAlivePhyConnCount() {
        return currentAlivePhyConnCount;
    }

    public void setCurrentAlivePhyConnCount(int currentAlivePhyConnCount) {
        this.currentAlivePhyConnCount = currentAlivePhyConnCount;
    }
}
