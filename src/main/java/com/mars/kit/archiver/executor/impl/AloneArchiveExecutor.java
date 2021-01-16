package com.mars.kit.archiver.executor.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.support.TransactionTemplate;

import com.mars.kit.archiver.conf.ArchiveConfig;
import com.mars.kit.archiver.conf.ArchiveItem;
import com.mars.kit.criterion.DetachedHsCriteria;
import com.mars.kit.criterion.expression.IbsRestrictions;
import com.mars.kit.criterion.expression.LimitExpression;
import com.mars.kit.criterion.model.ListPager;
import com.mars.kit.criterion.sql.IbsOrder;
import com.mars.kit.criterion.sql.ReplaceIntoCriteria;
import com.mars.kit.criterion.sql.TableFromCriteria;

public class AloneArchiveExecutor extends AbsArchiveExecutor {
	
	@Override
	public void execute(ArchiveItem item) {
		// 需要循环递归，达到最大数 或则归档的条数为0
		while (item.getProgress().get() < item.getConfig().getProgressSize()) {
			try {
				List<Map<String, Object>> archivedata = queryItems(item);
				if (CollectionUtils.isEmpty(archivedata)) {
					return;
				}
				int insertRows = doTransaction(item, archivedata);
				if (insertRows <= 0) {
					// 没有执行的记录数了
					return;
				}
				if (!item.getConfig().isPurge()) {
					super.handlerNextMaxId(item);
				}
			} catch (Exception e) {
				int retries = item.getRetries().incrementAndGet();
				logger.error("execute  ArchiveItem error,retries:{}", retries, e);
				if (item.getRetries().get() >= item.getConfig().getRetries()) {
					// 超过重试次数
					return;
				}
				try {
					// 500ms后重试
					TimeUnit.MILLISECONDS.sleep(500);
					continue;
				} catch (InterruptedException e1) {
					// igore
				}
			}
			try {
				TimeUnit.MILLISECONDS.sleep(item.getConfig().getSleep());
				logger.warn("每次归档了limit个行记录后休眠{} 毫秒", item.getConfig().getSleep());
			} catch (Exception e) {
				logger.error("sleep error", e);
			}
		}

	}
	
	public int doTransaction(ArchiveItem item, List<Map<String, Object>> archivedata) {
		TransactionTemplate transactionTemplate = item.getExecutor().getTransactionTemplate();
		AtomicInteger executeCount = new AtomicInteger(0);
		// 分批执行
		int txnBacth = 1;
		ListPager<Map<String, Object>> listPager = new ListPager<Map<String, Object>>(archivedata,
				item.getConfig().getTxnSize());
		listPager.setPageIndex(txnBacth);
		List<Map<String, Object>> txndata = null;
		while (CollectionUtils.isNotEmpty(txndata = listPager.getPageList())) {
			int insertRows = transactionTemplate.execute(new MarsTransactionCallback(item, txndata));
			if (insertRows <= 0) {
				// 没有执行的记录数了
				return executeCount.get();
			}
			if(insertRows>txndata.size()) {
				executeCount.addAndGet(txndata.size());
			}else {
				executeCount.addAndGet(insertRows);
			}
			
			//重复条数
			item.getRepeatNum().addAndGet(insertRows-txndata.size());
			
			txnBacth++;
			listPager.setPageIndex(txnBacth);
		}
		return executeCount.get();
	}
	
	
	/**
	 * SELECT  `id`, `scene_id` FROM xxx FORCE INDEX(`PRIMARY`) WHERE id <=1227 order by id LIMIT 10;
	 * @param item
	 * @return
	 */
	protected List<Map<String, Object>> queryItems(ArchiveItem item){
		DetachedHsCriteria batchSelect = DetachedHsCriteria.forInstance();
		batchSelect.addColumnName(item.getCols());
		batchSelect.addFromClause(TableFromCriteria.setTableName(item.getConfig().getSrcTblName() + " FORCE INDEX(`PRIMARY`)"));
		batchSelect.add(IbsRestrictions.le("id", item.getMaxId()));
		
		//不删除原表数据，需要设置nextMaxId
		 if(!item.getConfig().isPurge()) {
			batchSelect.add(IbsRestrictions.gt("id", item.getNextMaxId()));
		 }
		 
		batchSelect.addOrder(IbsOrder.asc("id"));
		batchSelect.addLimit(LimitExpression.limit(item.getConfig().getLimit()));
		List<Map<String, Object>> archivedata = item.getExecutor().findDataByCriteria(batchSelect);
		return archivedata;
	}

	/**
	 * replace INTO xxx_archive(`id`, `scene_id`) VALUES(?,?);
	 */
	@Override
	public int archiveItem(ArchiveItem item,List<Map<String, Object>> archivedata) {
		ArchiveConfig config = item.getConfig();
		 if (CollectionUtils.isEmpty(archivedata)) {
			 return 0;
		 }
     	ReplaceIntoCriteria intoCriteria = new ReplaceIntoCriteria(config.getDesTblName());  	
     	intoCriteria.addColumn(item.getCols());
     	for(Map<String, Object> updateItem :archivedata){
     		intoCriteria.addParamer(updateItem);
     	}
     	//获取dump的SQL
         String insertSql = intoCriteria.getSqlString();
         List<Object[]> batchArgs = intoCriteria.getBatchParamer(); 
         int[] rowArrays = item.getExecutor().batchUpdate(insertSql, batchArgs);
		return rowArrays.length;
	}

	/**
	 * 批量删除 
	 * DELETE FROM xxx WHERE id<=1227 AND id=?
	 */
	@Override
	public int deleteItem(ArchiveItem item,List<Map<String, Object>> archivedata) {
		ArchiveConfig config = item.getConfig();
		String deleteSql = "DELETE FROM "+config.getSrcTblName()+" WHERE id<=? AND id=?";
		int deleteNum = 0;
		if(CollectionUtils.isNotEmpty(archivedata)) {
			List<Object[]> batchArgs = new ArrayList<Object[]>(archivedata.size());
			for (Iterator<Map<String, Object>> iterator = archivedata.iterator(); iterator.hasNext();) {
				Map<String, Object> data =  iterator.next();
				Long nextId = (Long) data.get("id");
				if(nextId!=null) {
					Object[] batchParamer = new Object[] {item.getMaxId(),nextId};
					batchArgs.add(batchParamer);
				}
			}
			if(CollectionUtils.isNotEmpty(batchArgs)) {
				int[] rowArrays = item.getExecutor().batchUpdate(deleteSql, batchArgs);
			    return rowArrays.length;
			}
		}
		return deleteNum;
	}
}
