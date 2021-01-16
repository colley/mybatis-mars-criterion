/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-10 上午11:14
 * History:
 */
package com.mars.kit.archiver.executor.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.mars.kit.archiver.conf.ArchiveConfig;
import com.mars.kit.archiver.conf.ArchiveItem;
import com.mars.kit.archiver.executor.ArchiveExecutor;
import com.mars.kit.criterion.DetachedHsCriteria;
import com.mars.kit.criterion.common.IbsStringHelper;
import com.mars.kit.criterion.sql.AliasColumn;
import com.mars.kit.criterion.sql.TableFromCriteria;
import com.mars.kit.exception.ExceptionUtil;


/**
 * AbsArchiveExecutor.java
 *
 * @author ColleyMa
 * @version 19-5-10 上午11:14
*/
public abstract class AbsArchiveExecutor implements ArchiveExecutor {
    protected Logger monitor = LoggerFactory.getLogger("monitorArchiveLog");
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取归档目标表的最大maxId
     *
     * @param item
     */
    protected void handlerNextMaxId(ArchiveItem item) {
        ArchiveConfig config = item.getConfig();

        try {
            /** SELECT MAX(id) FROM xxx_archive;*/
            DetachedHsCriteria qryCriteria = DetachedHsCriteria.forInstance();
            qryCriteria.addColumnName(AliasColumn.neAs("MAX(id)"));
            qryCriteria.addFromClause(TableFromCriteria.setTableName(config.getDesTblName()));

            Long maxId = item.getExecutor().queryByCriteriaForObject(qryCriteria, Long.class);
            logger.warn("目标表nextMaxId获取 - 目标表: {} - nextMaxId(id) :{}", new Object[] { config.getDesTblName(), maxId });
            //设置最大的MaxId
            item.setNextMaxId(maxId);
        } catch (Exception e) {
            logger.error("目标表nextMaxId获取 - 目标表: {}  - 异常信息： {}", config.getDesTblName(), e.getMessage());
            ExceptionUtil.throwException(e);
        }
    }

    public void commitLog(ArchiveItem item, int insertNum, int deleteNum, boolean isSucc) {
        StringBuffer strBuff = new StringBuffer();

        if (isSucc) {
            strBuff.append("commit成功  ");
        } else {
            strBuff.append("执行失败回滚   ");
        }

        strBuff.append(" - 当前归档表 : ").append(item.getConfig().getSrcTblName());
        strBuff.append(" - 目标表 : ").append(item.getConfig().getDesTblName());
        strBuff.append(" - 最大max(id) : ").append(item.getMaxId());
        strBuff.append(" - Limit数 : ").append(item.getConfig().getLimit());
        strBuff.append(" - txn-size : ").append(item.getConfig().getTxnSize());
        strBuff.append(" - 归档行数 : ").append(insertNum);
        strBuff.append(" - 归档重复行数 : ").append(item.getRepeatNum().get());
        strBuff.append(" - 删除行数: ").append(deleteNum);
        strBuff.append(" - 重试总次数 : ").append(item.getRetries().get());
        strBuff.append(" - 花费时间: ").append(IbsStringHelper.msTimeformat2String((System.currentTimeMillis() - item.getStartTime())));
        monitor.error(strBuff.toString());
    }

    public class MarsTransactionCallback implements TransactionCallback<Integer> {
        private ArchiveItem item;
        private List<Map<String, Object>> archiveData;

        public MarsTransactionCallback(ArchiveItem item, List<Map<String, Object>> archiveData) {
            this.item = item;
            this.archiveData = archiveData;
        }

        @Override
        public Integer doInTransaction(TransactionStatus status) {
            int insertNum = 0;
            int deleteNum = 0;
            ArchiveConfig config = item.getConfig();

            try {
                // 先归档
                insertNum = archiveItem(item, archiveData);

                // 归档成功才能删除
                if (insertNum > 0) {
                    // 执行删除操作
                    if (config.isPurge()) {
                        deleteNum = deleteItem(item, archiveData);
                        item.getDelNum().addAndGet(deleteNum);
                    }
                    
                    if(insertNum>item.getConfig().getLimit()) {
                    	// 设置插入的条数
                        item.getProgress().addAndGet(item.getConfig().getLimit());
                    }else {
                    	// 设置插入的条数
                        item.getProgress().addAndGet(insertNum);
                    }
                    
                  //重复条数
        			item.getRepeatNum().addAndGet(insertNum-deleteNum);
                }
            } catch (Exception e) {
                logger.error("===> 批次归档失败，执行回滚操作", e);
                commitLog(item, insertNum, deleteNum, false);
                //设置回滚
                status.setRollbackOnly();
                throw new RuntimeException("批次归档失败，执行回滚操作", e);
            }

            commitLog(item, insertNum, deleteNum, true);

            return insertNum;
        }
    }
}
