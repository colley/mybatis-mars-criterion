/**
 * Copyright (C), 2011-2017
 * File Name: UpdateItem.java
 * Encoding: UTF-8
 * Date: 17-8-22 下午3:12
 * History:
 */
package com.mars.kit.archiver.conf;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import com.mars.kit.common.IMarsTemplateExecutor;


/**
 * ArchiveItem.java
 *
 * @author ColleyMa
 * @version 19-5-7 下午5:21
*/
public class ArchiveItem implements Serializable {
    private static final long serialVersionUID = 4977207034726930627L;
    private IMarsTemplateExecutor executor;
    private String[] cols;
    private ArchiveConfig config;
    private Long maxId;
    
    //需要归档的数据量
    private Integer dataCount = 0;
    
    protected AtomicInteger progress;
    
    protected AtomicInteger delNum;
    
    /**重复数据**/
    protected AtomicInteger repeatNum;
    
    protected AtomicInteger retries;
    
    protected long startTime;
    
    //next limit maxId
    protected Long nextMaxId;

    public ArchiveItem(ArchiveConfig config) {
        this.config = config;
        this.progress= new AtomicInteger(0);
        this.retries = new AtomicInteger(0);
        this.delNum = new AtomicInteger(0);
        this.repeatNum = new AtomicInteger(0);
        this.startTime = System.currentTimeMillis(); 
    }

    public AtomicInteger getProgress() {
		return progress;
	}


	public String[] getCols() {
        return cols;
    }

    public void setCols(String[] cols) {
        this.cols = cols;
    }

    public ArchiveConfig getConfig() {
        return config;
    }

    public void setConfig(ArchiveConfig config) {
        this.config = config;
    }

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public IMarsTemplateExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(IMarsTemplateExecutor executor) {
        this.executor = executor;
    }

	public AtomicInteger getRetries() {
		return retries;
	}

	public void setRetries(AtomicInteger retries) {
		this.retries = retries;
	}

	public long getStartTime() {
		return startTime;
	}

	public AtomicInteger getDelNum() {
		return delNum;
	}

	public Long getNextMaxId() {
		return nextMaxId;
	}

	public void setNextMaxId(Long nextMaxId) {
		this.nextMaxId = nextMaxId;
	}

	public AtomicInteger getRepeatNum() {
		return repeatNum;
	}

	public Integer getDataCount() {
		return dataCount;
	}

	public void setDataCount(Integer dataCount) {
		this.dataCount = dataCount;
	}

}
