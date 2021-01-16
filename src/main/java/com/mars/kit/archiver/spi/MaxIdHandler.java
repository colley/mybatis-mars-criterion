/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-7 下午4:20
 * History:
 */
package com.mars.kit.archiver.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mars.kit.archiver.ArchiveHandler;
import com.mars.kit.archiver.conf.ArchiveConfig;
import com.mars.kit.archiver.conf.ArchiveItem;
import com.mars.kit.criterion.DetachedHsCriteria;
import com.mars.kit.criterion.common.GetterHelper;
import com.mars.kit.criterion.expression.IbsRestrictions;
import com.mars.kit.criterion.sql.AliasColumn;
import com.mars.kit.criterion.sql.TableFromCriteria;
import com.mars.kit.exception.ExceptionUtil;


/**
 * 获取原表需要归档的maxId
 * MaxIdHandler.java
 * @author ColleyMa
 * @version 19-5-7 下午4:20
 */
public class MaxIdHandler implements ArchiveHandler {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handle(ArchiveItem item) {
		ArchiveConfig config = item.getConfig();
        try {
        	//dataCount<=0  没有数据需要归档
    		if(item.getDataCount()<=0) {
    			logger.warn("归档数获取 - 归档表: {} - 需要归档数为0 - where条件: {}",
    					new Object[] { config.getSrcTblName(),config.getWhere() });
    			return;
    		}
    		
			/**
			* SELECT MAX(id) FROM xxx  where gmt_create<"2020-08-10";
			*/
			DetachedHsCriteria qryCriteria = DetachedHsCriteria.forInstance();
			qryCriteria.addColumnName(AliasColumn.neAs("MAX(id)"));
			qryCriteria.addFromClause(TableFromCriteria.setTableName(config.getSrcTblName()));
			qryCriteria.add(IbsRestrictions.asConst(config.getWhere()));
			Long maxId = item.getExecutor().queryByCriteriaForObject(qryCriteria, Long.class);
			logger.warn("maxId获取 - 归档表: {} - 最大max(id) :{} - where条件: {}",
					new Object[] { config.getSrcTblName(), maxId, config.getWhere() });
			//设置最大的MaxId
			item.setMaxId(maxId);
		} catch (Exception e) {
			logger.error("获取归档表maxId报错  - 归档表 ：{} - where条件: {} - 异常信息： {}", config.getSrcTblName(),config.getWhere(), e.getMessage());
			ExceptionUtil.throwException(e);
		}
        //不删除原表数据，需要获取nextmaxId
        if(!config.isPurge()) {
         	this.handlerNextMaxId(item);
         }
    }
    
    /**
     * 获取归档目标表的最大maxId
     * @param item
     */
    protected void handlerNextMaxId(ArchiveItem item) {
		ArchiveConfig config = item.getConfig();
        try {
			/**
			* SELECT MAX(id) FROM xxx_archive;
			*/
			DetachedHsCriteria qryCriteria = DetachedHsCriteria.forInstance();
			qryCriteria.addColumnName(AliasColumn.neAs("MAX(id)"));
			qryCriteria.addFromClause(TableFromCriteria.setTableName(config.getDesTblName()));
			Long maxId = item.getExecutor().queryByCriteriaForObject(qryCriteria, Long.class);
			logger.warn("目标表nextMaxId获取 - 目标表: {} - nextMaxId(id) :{}",
					new Object[] { config.getDesTblName(), maxId});
			//设置目标表的最大值
			item.setNextMaxId(GetterHelper.getLong(maxId, 0L));
		} catch (Exception e) {
			logger.error("目标表nextMaxId获取 - 目标表: {}  - 异常信息： {}", config.getDesTblName(), e.getMessage());
			ExceptionUtil.throwException(e);
		}
    }
}
