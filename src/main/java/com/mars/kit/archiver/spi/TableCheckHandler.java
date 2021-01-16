/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-7 下午4:20
 * History:
 */
package com.mars.kit.archiver.spi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mars.kit.archiver.ArchiveHandler;
import com.mars.kit.archiver.conf.ArchiveConfig;
import com.mars.kit.archiver.conf.ArchiveItem;
import com.mars.kit.criterion.DetachedHsCriteria;
import com.mars.kit.criterion.expression.IbsRestrictions;
import com.mars.kit.criterion.parsing.HBoundSqlBuilder;
import com.mars.kit.criterion.parsing.HsBoundSql;
import com.mars.kit.criterion.sql.TableFromCriteria;
import com.mars.kit.exception.ExceptionUtil;


/**
 * 检查原表和目标表字段是否一致
 * TableCheckHandler.java
 * @author ColleyMa
 * @version 19-5-7 下午4:20
 */
public class TableCheckHandler implements ArchiveHandler {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handle(ArchiveItem item) {
        ArchiveConfig config = item.getConfig();
        List<String> srcCols = getTableCols(item, config.getSrcTblName());
        List<String> desCols = getTableCols(item, config.getDesTblName());
        if(!ArrayUtils.toString(Sets.newTreeSet(srcCols))
        		.equals(ArrayUtils.toString(Sets.newTreeSet(desCols)))) {
        	//归档的表结构不一致
        	ExceptionUtil.throwException("原始表：{},与目标表:{} 字段不一致",config.getSrcTblName(),config.getDesTblName());
        }
        //设置归档表字段
        item.setCols(srcCols.toArray(new String[srcCols.size()]));
    }
    
    protected List<String> getTableCols(ArchiveItem item,String tblName) {
 		List<String> colsName  = new ArrayList<String>();
    	try {
			DetachedHsCriteria qryCriteria = DetachedHsCriteria.forInstance();
			qryCriteria.addColumnName(Lists.newArrayList("*"));
			qryCriteria.addFromClause(TableFromCriteria.setTableName(tblName));
			qryCriteria.addLimit(IbsRestrictions.limit(0, 0));
			HsBoundSql hsBoundSql = HBoundSqlBuilder.parseSql(qryCriteria.getHsSqlText());
			SqlRowSet rowSet = item.getExecutor().queryForRowSet(hsBoundSql.getSql(),
					hsBoundSql.getParamObjectValues());
			SqlRowSetMetaData metaData = rowSet.getMetaData();
			int columnCount = metaData.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				colsName.add(metaData.getColumnName(i));
			} 
		} catch (DataAccessException e) {
			logger.error("获取表字段报错  tableName：{}", tblName, e);
			ExceptionUtil.throwException(e);
		}
		return colsName;
    }
}
