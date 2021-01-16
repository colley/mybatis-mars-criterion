/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-6 下午1:55
 * History:
 */
package com.mars.kit.archiver.conf;

/**
 * ArchiveConfig.java
 * @author ColleyMa
 * @version 19-5-6 下午1:55
 */
public class ArchiveConfig {
	
    /** where条件*/
    private String where="1=1";

    /** 每次limit取行数据归档处理）*/
    private Integer limit;

    /** 设置一个事务提交一次的数量. 单条插入和单条删除*/
    private Integer txnSize;

    /** 是否删除source数据库的相关匹配记录*/
    private boolean purge = Boolean.TRUE;

    /** progressSize每次归档限制总条数）*/
    private Integer progressSize = Integer.MAX_VALUE;
    
    /**尝试次数**/
    private Integer retries = 2;

    /** 每次归档了limit个行记录后的休眠1秒（单位为毫秒）*/
    private Long sleep = 5*1000L;
    
    /**归档的表名***/
    private String srcTblName;
    
    /**归档目标表名**/
    private String desTblName;
    
    /**是否批次执行**/
    private boolean bulk = true;
    
    /**是否发送邮件**/
    private boolean sendEmail;
    
    /**邮件接收人,逗号分隔**/
    private String recipients;
    

    public String getSrcTblName() {
		return srcTblName;
	}

	public void setSrcTblName(String srcTblName) {
		this.srcTblName = srcTblName;
	}

	public String getDesTblName() {
		return desTblName;
	}

	public void setDesTblName(String desTblName) {
		this.desTblName = desTblName;
	}

	public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getTxnSize() {
        return txnSize;
    }

    public void setTxnSize(Integer txnSize) {
        this.txnSize = txnSize;
    }

    public boolean isPurge() {
        return purge;
    }

    public void setPurge(boolean purge) {
        this.purge = purge;
    }

    public Integer getProgressSize() {
        return progressSize;
    }

    public void setProgressSize(Integer progressSize) {
        this.progressSize = progressSize;
    }

    public Long getSleep() {
        return sleep;
    }

    public void setSleep(Long sleep) {
        this.sleep = sleep;
    }

	public Integer getRetries() {
		return retries;
	}

	public void setRetries(Integer retries) {
		this.retries = retries;
	}

	public boolean isBulk() {
		return bulk;
	}

	public void setBulk(boolean bulk) {
		this.bulk = bulk;
	}

	public boolean isSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

	public String getRecipients() {
		return recipients;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}
	
}
