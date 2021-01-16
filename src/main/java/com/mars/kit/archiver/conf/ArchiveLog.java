/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-27 下午8:59
 * History:
 */
package com.mars.kit.archiver.conf;

import java.util.Date;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * ArchiveLog.java
 * @author ColleyMa
 * @version 19-5-27 下午8:59
 */
public class ArchiveLog {
	/** 是否成功*/
	private boolean succ;

	/** 当前归档表*/
	private String srcTableName;

	/** 目标表*/
	private String desTableName;

	/** 最大max(id)*/
	private Long maxId;
	
	/**剩余归档数**/
	private Integer remainCount;

	/** Limit数*/
	private Integer limit;

	/** 归档总数*/
	private Integer progressSize;

	/** 删除总数*/
	private Integer delTotalNum;
	
	/**归档重复总数*/
	private Integer repeatNum;

	/** 重试总次数*/
	private Integer retries;

	/** 是否删除归档表数据*/
	private boolean purge;

	/** 总花费时间*/
	private String costTime;

	/** 异常信息*/
	private String traceInfo;

	/**开始时间**/
	private Date startTime;

	/**结束时间**/
	private Date endTime;

	private boolean sendEmail;

	private Set<String> recipients;

	public boolean isSucc() {
		return succ;
	}

	public void setSucc(boolean succ) {
		this.succ = succ;
	}

	public String getSrcTableName() {
		return srcTableName;
	}

	public void setSrcTableName(String srcTableName) {
		this.srcTableName = srcTableName;
	}

	public String getDesTableName() {
		return desTableName;
	}

	public void setDesTableName(String desTableName) {
		this.desTableName = desTableName;
	}

	public Long getMaxId() {
		return maxId;
	}

	public void setMaxId(Long maxId) {
		this.maxId = maxId;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getProgressSize() {
		return progressSize;
	}

	public void setProgressSize(Integer progressSize) {
		this.progressSize = progressSize;
	}

	public Integer getDelTotalNum() {
		return delTotalNum;
	}

	public void setDelTotalNum(Integer delTotalNum) {
		this.delTotalNum = delTotalNum;
	}

	public Integer getRetries() {
		return retries;
	}

	public void setRetries(Integer retries) {
		this.retries = retries;
	}

	public boolean isPurge() {
		return purge;
	}

	public void setPurge(boolean purge) {
		this.purge = purge;
	}

	public String getCostTime() {
		return costTime;
	}

	public void setCostTime(String costTime) {
		this.costTime = costTime;
	}

	public String getTraceInfo() {
		return traceInfo;
	}

	public void setTraceInfo(String traceInfo) {
		this.traceInfo = traceInfo;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public boolean isSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

	public Set<String> getRecipients() {
		if(recipients==null) {
			recipients = Sets.newHashSet();
		}
		return recipients;
	}

	public void setRecipients(Set<String> recipients) {
		this.recipients = recipients;
	}
	
	

	public Integer getRepeatNum() {
		return repeatNum;
	}

	public void setRepeatNum(Integer repeatNum) {
		this.repeatNum = repeatNum;
	}



	public Integer getRemainCount() {
		return remainCount;
	}

	public void setRemainCount(Integer remainCount) {
		this.remainCount = remainCount;
	}




	public static class Builder {
		private boolean succ;
		private String srcTableName;
		private String desTableName;
		private Long maxId;
		/**需要归档数据量**/
		private Integer remainCount;
		
		private Integer limit;
		private Integer progressSize;
		private Integer delTotalNum;
		private Integer retries;
		private boolean purge;
		private String costTime;
		private String traceInfo;
		private Date startTime;
		private Date endTime;
		private boolean sendEmail;
		private Set<String> recipients;
		
		/**归档重复总数*/
		private Integer repeatNum;

		public Builder remainCount(Integer remainCount) {
			this.remainCount = remainCount;
			return this;
		}
		
		public Builder succ(boolean succ) {
			this.succ = succ;
			return this;
		}
		
		public Builder repeatNum(Integer repeatNum) {
			this.repeatNum = repeatNum;
			return this;
		}

		public Builder srcTableName(String srcTableName) {
			this.srcTableName = srcTableName;
			return this;
		}

		public Builder desTableName(String desTableName) {
			this.desTableName = desTableName;
			return this;
		}

		public Builder maxId(Long maxId) {
			this.maxId = maxId;
			return this;
		}

		public Builder limit(Integer limit) {
			this.limit = limit;
			return this;
		}

		public Builder progressSize(Integer progressSize) {
			this.progressSize = progressSize;
			return this;
		}

		public Builder delTotalNum(Integer delTotalNum) {
			this.delTotalNum = delTotalNum;
			return this;
		}

		public Builder retries(Integer retries) {
			this.retries = retries;
			return this;
		}

		public Builder purge(boolean purge) {
			this.purge = purge;
			return this;
		}

		public Builder costTime(String costTime) {
			this.costTime = costTime;
			return this;
		}

		public Builder traceInfo(String traceInfo) {
			this.traceInfo = traceInfo;
			return this;
		}

		public Builder startTime(Date startTime) {
			this.startTime = startTime;
			return this;
		}

		public Builder endTime(Date endTime) {
			this.endTime = endTime;
			return this;
		}

		public Builder sendEmail(boolean sendEmail) {
			this.sendEmail = sendEmail;
			return this;
		}

		public Builder recipients(Set<String> recipients) {
			this.recipients = recipients;
			return this;
		}

		public ArchiveLog build() {
			return new ArchiveLog(this);
		}
	}

	private ArchiveLog(Builder builder) {
		this.succ = builder.succ;
		this.srcTableName = builder.srcTableName;
		this.desTableName = builder.desTableName;
		this.maxId = builder.maxId;
		this.limit = builder.limit;
		this.progressSize = builder.progressSize;
		this.delTotalNum = builder.delTotalNum;
		this.retries = builder.retries;
		this.purge = builder.purge;
		this.costTime = builder.costTime;
		this.traceInfo = builder.traceInfo;
		this.startTime = builder.startTime;
		this.endTime = builder.endTime;
		this.sendEmail = builder.sendEmail;
		this.recipients = builder.recipients;
		this.repeatNum = builder.repeatNum;
		this.remainCount = builder.remainCount;
	}
}
