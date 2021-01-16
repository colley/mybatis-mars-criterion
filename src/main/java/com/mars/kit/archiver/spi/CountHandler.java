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
 * 计算是否有数据需要归档，count<=0 不归档
 * CountHandler.java
 * @author ColleyMa
 * @version 19-5-7 下午4:20
 */
public class CountHandler implements ArchiveHandler {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handle(ArchiveItem item) {
		ArchiveConfig config = item.getConfig();
        try {
			/**
			* SELECT COUNT(1) FROM xxx  where gmt_create<"2020-08-10";
			*/
			DetachedHsCriteria qryCriteria = DetachedHsCriteria.forInstance();
			qryCriteria.addColumnName(AliasColumn.neAs("COUNT(1)"));
			qryCriteria.addFromClause(TableFromCriteria.setTableName(config.getSrcTblName()));
			qryCriteria.add(IbsRestrictions.asConst(config.getWhere()));
			Integer dataCount = item.getExecutor().findByCriteriaForInt(qryCriteria);
			//设置归档数量
			item.setDataCount(dataCount);
		} catch (Exception e) {
			logger.error("获取归档表maxId报错  - 归档表 ：{} - where条件: {} - 异常信息： {}", config.getSrcTblName(),config.getWhere(), e.getMessage());
			ExceptionUtil.throwException(e);
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
