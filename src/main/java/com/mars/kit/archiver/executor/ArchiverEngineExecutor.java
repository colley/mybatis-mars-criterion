/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-13 下午2:35
 * History:
 */
package com.mars.kit.archiver.executor;

import java.util.Date;
import java.util.Iterator;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.mars.kit.archiver.ArchiveHandler;
import com.mars.kit.archiver.conf.ArchiveConfig;
import com.mars.kit.archiver.conf.ArchiveItem;
import com.mars.kit.archiver.conf.ArchiveLog;
import com.mars.kit.archiver.notify.ArchiveLogMonitor;
import com.mars.kit.archiver.notify.ArchiveMonitor;
import com.mars.kit.common.IMarsTemplateExecutor;
import com.mars.kit.criterion.common.GetterHelper;
import com.mars.kit.criterion.common.IbsStringHelper;
import com.mars.kit.exception.ArchiveException;


/**
 * ArchiverEngineExecutor.java
 *
 * @author ColleyMa
 * @version 19-5-13 下午2:35
*/
public class ArchiverEngineExecutor implements ArchiverDataEngine {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /** spring事务*/
    private IMarsTemplateExecutor executor;
    private ArchiveMonitor monitor;

    public ArchiverEngineExecutor(IMarsTemplateExecutor executor, ArchiveMonitor monitor) {
        this.executor = executor;
        this.monitor = monitor;
    }

    @Override
    public void archive(ArchiveConfig config) {
        ArchiveItem item = new ArchiveItem(config);
        item.setExecutor(executor);
        try {
            init(); //初始化
            ServiceLoader<ArchiveHandler> filters = ServiceLoader.load(ArchiveHandler.class);
            Iterator<ArchiveHandler> iterator = filters.iterator();

            while (iterator.hasNext()) {
                ArchiveHandler archiveHandler = iterator.next();
                archiveHandler.handle(item);
            }
            done(item, null);
        } catch (Exception e) {
            logger.error("数据归档失败！", e);
            done(item, e);
        }
    }

    public void init() throws ArchiveException {
        if (executor == null) {
            throw new ArchiveException("Property 'marsTemplateExecutor' is required");
        }
    }

    public void done(ArchiveItem item, Exception ex) {
        if (monitor == null) {
            //默认日志打印，可以自己实现发邮件等
            monitor = new ArchiveLogMonitor();
        }
        ArchiveLog.Builder builder = new ArchiveLog.Builder();
        if (ex == null) {
            builder.succ(true);
        } else {
            builder.succ(false);
        }
        //计算剩余数
        Integer remainCount = GetterHelper.getInteger(item.getDataCount())-item.getProgress().get();
        
        builder.srcTableName(item.getConfig().getSrcTblName())
        		.desTableName(item.getConfig().getDesTblName())
        		.maxId(item.getMaxId())
                .limit(item.getConfig().getLimit())
                .progressSize(item.getProgress().get())
                .delTotalNum(item.getDelNum().get())
                .repeatNum(item.getRepeatNum().get())
                .retries(item.getRetries().get())
                .purge(item.getConfig().isPurge())
                .startTime(new Date(item.getStartTime()))
                .endTime(new Date())
                .remainCount(remainCount>0?remainCount:0)//剩余数量
                .costTime(IbsStringHelper.msTimeformat2String((System.currentTimeMillis() - item.getStartTime())));
        if (ex != null) {
            builder.traceInfo(ex.getMessage());
        }
        
        //设置是否发邮件
        builder.sendEmail(item.getConfig().isSendEmail());
        
        //设置接收人的邮件地址
        builder.recipients(Sets.newHashSet(GetterHelper.getSplit2List(item.getConfig().getRecipients())));
        
        monitor.monitor(builder.build());
    }
}
