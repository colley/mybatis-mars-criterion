/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.common.IbsStringHelper;


/**
 *@FileName  InsertCriteria.java
 *@Date  16-6-12 上午10:32
 *@author Colley
 *@version 1.0
 */
public class ReplaceIntoCriteria implements java.io.Serializable {
    private static final long serialVersionUID = 8296238627354076991L;
    protected final Log logger = LogFactory.getLog(this.getClass());
    private List<IUColumn> selectClause = new ArrayList<IUColumn>();
    private String tableName;
    private List<Object[]> batchParamer = new ArrayList<Object[]>();

    public ReplaceIntoCriteria(String tableName){
    	this.tableName = tableName;
    }
    public ReplaceIntoCriteria addIUColumn(IUColumn column) {
        if (column != null) {
            selectClause.add(column);
        }

        return this;
    }

    public IUColumn[] getColumnNames() {
        if (CollectionUtils.isNotEmpty(selectClause)) {
            return selectClause.toArray(new IUColumn[selectClause.size()]);
        }

        return null;
    }

    public String getSqlString() {
        IUColumn[] columns = getColumnNames();

        if ((columns == null) || (columns.length <= 0)) {
            throw new IllegalArgumentException("IUColumn don't null");
        }

        StringBuffer sql = new StringBuffer();
        sql.append(getOp()).append(" INTO ").append(getTableName()).append("(");

        String[] listcolumn = new String[columns.length];

        for (int i = 0; i < columns.length; i++) {
            listcolumn[i] = columns[i].getColumnName();
        }

        sql.append(IbsStringHelper.join(",", listcolumn));
        sql.append(")");
        sql.append(" VALUES(");

        String[] columnVals = new String[columns.length];

        for (int i = 0; i < columns.length; i++) {
            columnVals[i] = IbsStringHelper.repeatParamFormat();
        }

        sql.append(IbsStringHelper.join(",", columnVals)).append(")");

        return sql.toString();
    }

    public List<Object[]> getBatchParamer() {
        return this.batchParamer;
    }

    public String getOp() {
        return ExprOper.REPLACE.getOp();
    }

    public String getOpType() {
        return ExprOper.REPLACE.name();
    }

    public String getTableName() {
        return this.tableName;
    }

    public ReplaceIntoCriteria setTableName(String tableName) {
        if (StringUtils.isNotEmpty(tableName)) {
            this.tableName = tableName;
        }

        return this;
    }

    public ReplaceIntoCriteria addColumn(String[] columnNames) {
        for (String column : columnNames) {
            selectClause.add(IUColumn.select(column));
        }

        return this;
    }

    public ReplaceIntoCriteria addParamer(Map<String, Object> paramer) {
        IUColumn[] columns = getColumnNames();
        Object[] listparamer = new Object[columns.length];

        for (int i = 0; i < columns.length; i++) {
            IUColumn column = columns[i];
            Object param = paramer.get(column.getColumnName());
            listparamer[i] = param;
        }

        batchParamer.add(listparamer);

        return this;
    }

    public ReplaceIntoCriteria addColumn(String columnName) {
        selectClause.add(IUColumn.select(columnName));

        return this;
    }

    public ReplaceIntoCriteria addColumn(List<String> columnNames) {
        for (String column : columnNames) {
            selectClause.add(IUColumn.select(column));
        }

        return this;
    }
}
