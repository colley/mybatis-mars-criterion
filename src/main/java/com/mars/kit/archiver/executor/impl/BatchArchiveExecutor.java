package com.mars.kit.archiver.executor.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.transaction.support.TransactionTemplate;

import com.mars.kit.archiver.conf.ArchiveConfig;
import com.mars.kit.archiver.conf.ArchiveItem;
import com.mars.kit.criterion.DetachedHsCriteria;
import com.mars.kit.criterion.DetachedIUCriteria;
import com.mars.kit.criterion.expression.IbsRestrictions;
import com.mars.kit.criterion.expression.LimitExpression;
import com.mars.kit.criterion.sql.IbsOrder;
import com.mars.kit.criterion.sql.TableFromCriteria;

public class BatchArchiveExecutor extends AbsArchiveExecutor {

	@Override
	public void execute(ArchiveItem item) {
		TransactionTemplate transactionTemplate = item.getExecutor().getTransactionTemplate();		
		// 需要循环递归，达到最大数 或则归档的条数为0
		while (item.getProgress().get() < item.getConfig().getProgressSize()) {
			try {
				int insertRows = transactionTemplate.execute(new MarsTransactionCallback(item,null));
				if (insertRows <= 0) {
					// 没有执行的记录数了
					break;
				}
				if (!item.getConfig().isPurge()) {
					super.handlerNextMaxId(item);
				}
			} catch (Exception e) {
				int retries = item.getRetries().incrementAndGet();
				logger.error("执行归档报错，重试次数为  retries:{}",retries,e);
				if(item.getRetries().get()>=item.getConfig().getRetries()) {
					//超过重试次数
					break;
				}
				try {
					//500ms后重试
					TimeUnit.MILLISECONDS.sleep(500);
					continue;
				} catch (InterruptedException e1) {
					//igore
					logger.error("{} ms后重试！", 500);
				}
			}
			try {
				TimeUnit.MILLISECONDS.sleep(item.getConfig().getSleep());
				logger.warn("每次归档了limit个行记录后休眠{} 毫秒", item.getConfig().getSleep());
			} catch (Exception e) {
				//igore
				//每次归档了limit个行记录后的休眠120秒（单位为秒）
				logger.error("每次归档了limit个行记录后休眠报错！");
			}
		}
	}

	/**
	 * 根据max(id)和 limit 获取limit行数据处理 批量插入 同一个事务 
	 * replace INTO xxx_archive(`id`,`scene_id`) 
	 * SELECT !40001 SQL_NO_CACHE `id`, `scene_id` FROM xxx FORCE INDEX(`PRIMARY`) WHERE id<=1227 order by id LIMIT 10;
	 */
	@Override
	public int archiveItem(ArchiveItem item,List<Map<String, Object>> archiveData) {
		ArchiveConfig config = item.getConfig();
		DetachedIUCriteria batchCriteria = DetachedIUCriteria.replaceInstance();
		batchCriteria.addColumnName(item.getCols());
		batchCriteria.setTableName(config.getDesTblName());
		DetachedHsCriteria batchSelect = DetachedHsCriteria.forInstance();
		batchSelect.addColumnName(item.getCols());
		batchSelect.addFromClause(TableFromCriteria.setTableName(config.getSrcTblName() + " FORCE INDEX(`PRIMARY`)"));
		batchSelect.add(IbsRestrictions.le("id", item.getMaxId()));
		//不删除原表数据，需要设置nextMaxId
		 if(!config.isPurge()) {
			batchSelect.add(IbsRestrictions.gt("id", item.getNextMaxId()));
		 }
		batchSelect.addOrder(IbsOrder.asc("id"));
		batchSelect.addLimit(LimitExpression.limit(config.getLimit()));
		batchCriteria.addFromClause(batchSelect);

		int rows = item.getExecutor().insertByCriteria(batchCriteria);
		return rows;
	}

	/**
	 * 批量删除 DELETE FROM xxx WHERE id<=1227 order by id LIMIT 10;
	 */
	@Override
	public int deleteItem(ArchiveItem item,List<Map<String, Object>> archiveData) {
		ArchiveConfig config = item.getConfig();
		// 是否需要删除原表数据
		DetachedHsCriteria deleteCriteria = DetachedHsCriteria.deleteInstance();
		deleteCriteria.addFromClause(TableFromCriteria.setTableName(config.getSrcTblName()));
		deleteCriteria.add(IbsRestrictions.le("id", item.getMaxId()));
		deleteCriteria.addOrder(IbsOrder.asc("id"));
		deleteCriteria.addLimit(LimitExpression.limit(config.getLimit()));
		int rows = item.getExecutor().deleteByCriteria(deleteCriteria);
		return rows;
	}


}
