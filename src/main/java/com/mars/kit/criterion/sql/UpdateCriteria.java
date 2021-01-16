/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.common.IbsStringHelper;
import com.mars.kit.criterion.expression.Criterion;


/**
 *@FileName  UpdateCriteria.java
 *@Date  16-6-12 下午12:25
 *@author Colley
 *@version 1.0
 */
public class UpdateCriteria implements IUCriteria {
    private static final long serialVersionUID = 8296238627354076991L;
    protected final Log logger = LogFactory.getLog(this.getClass());
    private List<IUColumn> selectClause = new ArrayList<IUColumn>();
    protected HsCriteria fromClause = null;
    private List<Criterion> whereClause = new ArrayList<Criterion>();
    private String tableName;

    @Override
    public Criterion[] getCriteria() {
        if (CollectionUtils.isNotEmpty(whereClause)) {
            return whereClause.toArray(new Criterion[0]);
        }

        return null;
    }

    @Override
    public IUCriteria add(Criterion criterion) {
        if (criterion != null) {
            whereClause.add(criterion);
        }

        return this;
    }

    @Override
    public IUCriteria addIUColumn(IUColumn column) {
        if (column != null) {
            selectClause.add(column);
        }

        return this;
    }

    @Override
    public IUCriteria addFromClause(HsCriteria fromClause) {
        throw new UnsupportedOperationException("update op don't support");
    }

    @Override
    public HsCriteria getFromClause() {
        return this.fromClause;
    }

    @Override
    public IUColumn[] getColumnNames() {
        if (CollectionUtils.isNotEmpty(selectClause)) {
            return selectClause.toArray(new IUColumn[0]);
        }

        return null;
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
        IUColumn[] columns = getColumnNames();
        if ((columns == null) || (columns.length <= 0) || StringUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("IUColumn or tableName don't empty");
        }

        StringBuffer sql = new StringBuffer();
        sql.append(getOp()).append(getTableName()).append(" SET ");
        String[] columnVal = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            IUColumn column = columns[i];
            if (column.isConst()) {
                columnVal[i] = new StringBuffer().append(column.getColumnName()).append("=")
                                                 .append(column.getColumnValue()).toString();
            } else {
                String newParam = criterionQuery.addParameter(column.getColumnName(), column.getColumnValue());
                columnVal[i] = new StringBuffer().append(column.getColumnName()).append("=")
                                                 .append(IbsStringHelper.repeatParamFormat(newParam)).toString();
            }
        }

        sql.append(IbsStringHelper.join(",", columnVal));
        if (getCriteria() != null) {
            sql.append(" WHERE ").append(whereClauseSqlString(criterionQuery));
        }

        return sql.toString();
    }

    protected String whereClauseSqlString(CriterionQuery criterionQuery) {
        StringBuffer buffer = new StringBuffer();
        if (CollectionUtils.isNotEmpty(whereClause)) {
            Iterator<Criterion> iter = whereClause.iterator();
            while (iter.hasNext()) {
                buffer.append(((Criterion) iter.next()).getSqlString(criterionQuery));
                if (iter.hasNext()) {
                    buffer.append(' ').append(" AND ").append(' ');
                }
            }
        }

        return buffer.toString();
    }

    @Override
    public String getOp() {
        return ExprOper.UPDATE.getOp();
    }

    @Override
    public String getOpType() {
        return ExprOper.UPDATE.name();
    }

    @Override
    public String getTableName() {
        return this.tableName;
    }

    @Override
    public IUCriteria setTableName(String tableName) {
        if (StringUtils.isNotEmpty(tableName)) {
            this.tableName = tableName;
        }

        return this;
    }

    @Override
    public IUCriteria addColumn(String[] columnNames) {
        throw new UnsupportedOperationException("update op don't support");
    }

    @Override
    public IUCriteria addColumn(List<String> columnNames) {
        throw new UnsupportedOperationException("update op don't support");
    }
}
