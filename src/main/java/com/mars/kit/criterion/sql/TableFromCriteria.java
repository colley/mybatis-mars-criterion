/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.sql;

import java.util.List;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.common.GetterHelper;
import com.mars.kit.criterion.expression.Criterion;


/**
 *@FileName  FromCriteria.java
 *@Date  16-5-24 上午10:38
 *@author Colley
 *@version 1.0
 */
public class TableFromCriteria implements HsCriteria {
    private static final long serialVersionUID = 4982984239694001166L;
    private final String tableName;
    private final String op;
    public String aliasTableName;

    public TableFromCriteria(String tableName) {
        this.tableName = tableName;
        this.op = ExprOper.tName.getOp();
        this.aliasTableName = "";
    }

    public TableFromCriteria(String tableName, String aliasTableName) {
        this.tableName = tableName;
        this.op = ExprOper.tName.getOp();
        this.aliasTableName = aliasTableName;
    }

    @Override
    public Criterion[] getCriteria() {
        return null;
    }

    @Override
    public List<IbsOrder> getOrderBys() {
        return null;
    }

    @Override
    public HsCriteria addOrder(IbsOrder order) {
        throw new UnsupportedOperationException("from TableName don't support");
    }

    @Override
    public HsCriteria add(Criterion criterion) {
        throw new UnsupportedOperationException("from TableName don't support");
    }

    @Override
    public HsCriteria addColumn(String columnName) {
        throw new UnsupportedOperationException("from TableName don't support");
    }

    public String getFromCriteria() {
        return tableName;
    }

    public String getOp() {
        return op;
    }

    public String getOpType() {
        return ExprOper.tName.name();
    }

    @Override
    public Object getFromClause() {
        return null;
    }

    @Override
    public JoinCriteria[] getFromJoins() {
        return null;
    }

    public static TableFromCriteria setTableName(String tableName) {
        return new TableFromCriteria(tableName);
    }

    public static TableFromCriteria setTableName(String tableName, String aliasName) {
        return new TableFromCriteria(tableName, aliasName);
    }

    @Override
    public Criterion[] allCriteria() {
        return null;
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" ").append(getFromCriteria()).append(" ");
        buffer.append(getAliasTableName());
        return buffer.toString();
    }

    @Override
    public HsCriteria addColumn(String[] columnNames) {
        throw new UnsupportedOperationException("from TableName don't support");
    }

    @Override
    public HsCriteria addColumn(List<String> columnNames) {
        throw new UnsupportedOperationException("from TableName don't support");
    }

    @Override
    public HsCriteria addFromClause(HsCriteria fromCriteria, String aliasTableName) {
        throw new UnsupportedOperationException("from TableName don't support");
    }

    @Override
    public HsCriteria addFromClause(HsCriteria fromClause) {
        throw new UnsupportedOperationException("from TableName don't support");
    }

    @Override
    public HsCriteria addGroupBy(GroupCriteria groupByClause) {
        throw new UnsupportedOperationException("from TableName don't support");
    }

    @Override
    public HsCriteria addFromJoins(JoinCriteria leftJoinOn) {
        throw new UnsupportedOperationException("from TableName don't support");
    }


    @Override
    public AliasColumn[] getColumnNames() {
        return new AliasColumn[0];
    }

    @Override
    public HsCriteria addPagingLimit(Criterion criterion) {
        throw new UnsupportedOperationException("from TableName don't support");
    }

    @Override
    public String getAliasTableName() {
        return GetterHelper.getString(aliasTableName);
    }

    @Override
    public void setAliasTableName(String aliasTableName) {
        this.aliasTableName = aliasTableName;
    }
}
