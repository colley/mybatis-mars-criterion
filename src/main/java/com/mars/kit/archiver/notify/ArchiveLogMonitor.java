/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-28 下午8:34
 * History:
 */
package com.mars.kit.archiver.notify;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanMap;

import com.google.common.collect.Maps;
import com.mars.kit.archiver.conf.ArchiveLog;
import com.mars.kit.archiver.conf.JavaMailSource;


/**
 * ArchiveLogMonitor.java
 *
 * @author ColleyMa
 * @version 19-5-28 下午8:34
*/
public class ArchiveLogMonitor implements ArchiveMonitor {
    protected Logger monitor = LoggerFactory.getLogger("archiverdataLog");
    private JavaEmailClientFactory emailClient;
    private String messageTemplateFile;
    private String recipients; //接收人，多个逗号分隔

    @Override
    public void monitor(ArchiveLog archiveLog) {
        StringBuilder stringBuilder = new StringBuilder();
        if (archiveLog.isSucc()) {
            stringBuilder.append("归档数据成功汇总   ");
        } else {
            stringBuilder.append("归档数据失败   ");
        }
        stringBuilder.append(" - 当前归档表 : ").append(archiveLog.getSrcTableName());
        stringBuilder.append(" - 目标表 : ").append(archiveLog.getDesTableName());
        stringBuilder.append(" - 未归档数 : ").append(archiveLog.getRemainCount());
        stringBuilder.append(" - 最大max(id) : ").append(archiveLog.getMaxId());
        stringBuilder.append(" - Limit数 : ").append(archiveLog.getLimit());
        stringBuilder.append(" - 归档总数 : ").append(archiveLog.getProgressSize());
        stringBuilder.append(" - 归档重复总数 : ").append(archiveLog.getRepeatNum());
        stringBuilder.append(" - 删除总数: ").append(archiveLog.getDelTotalNum());
        stringBuilder.append(" - 重试总次数 : ").append(archiveLog.getRetries());
        stringBuilder.append(" - 是否删除归档表数据 : ").append(archiveLog.isPurge());
        stringBuilder.append(" - 总花费时间: ").append(archiveLog.getCostTime());

        if (!archiveLog.isSucc()) {
            stringBuilder.append(" - 异常信息: ").append(archiveLog.getTraceInfo());
        }
        monitor.error(stringBuilder.toString());
        
        if(!archiveLog.isSendEmail() || emailClient==null) {
        	return;
        }
        
      	//发邮件
    	JavaMailSource.Builder builder = new JavaMailSource.Builder();
    	
    	builder.recipients(archiveLog.getRecipients())
    	.messageTemplateFile(messageTemplateFile)
    	.subject("【"+archiveLog.getSrcTableName()+"】表数据归档完成")
    	.recipients(recipients);
    	//设置内容
    	Map<String, Object> contents = Maps.newHashMap();
    	BeanMap beanMap = BeanMap.create(archiveLog);
    	for (Object key : beanMap.keySet()) {
    		contents.put(Objects.toString(key), beanMap.get(key));
        }
    	builder.contents(contents);
    	emailClient.sendMessage(builder.build());
    	
    }

    public void setEmailClient(JavaEmailClientFactory emailClient) {
        this.emailClient = emailClient;
    }

    public void setMessageTemplateFile(String messageTemplateFile) {
        this.messageTemplateFile = messageTemplateFile;
    }

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}
    
}
