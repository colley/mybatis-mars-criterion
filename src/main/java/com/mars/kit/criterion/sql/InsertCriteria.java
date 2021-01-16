/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.sql;

import java.util.ArrayList;
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
 *@FileName  InsertCriteria.java
 *@Date  16-6-12 上午10:32
 *@author Colley
 *@version 1.0
 */
public class InsertCriteria implements IUCriteria {
    private static final long serialVersionUID = 8296238627354076991L;
    protected final Log logger = LogFactory.getLog(this.getClass());
    private List<IUColumn> selectClause = new ArrayList<IUColumn>();
    protected HsCriteria fromClause = null;
    private List<Criterion> whereClause = new ArrayList<Criterion>();
    private String tableName;
    
    private final ExprOper exprOper;

    
    public InsertCriteria(ExprOper exprOper) {
    	this.exprOper = exprOper;
    }

    @Override
    public Criterion[] getCriteria() {
        if (CollectionUtils.isNotEmpty(whereClause)) {
            return whereClause.toArray(new Criterion[0]);
        }

        return null;
    }

    @Override
    public IUCriteria add(Criterion criterion) {
        throw new UnsupportedOperationException("Insert Criteria don't support");
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
        if (fromClause != null) {
            this.fromClause = fromClause;
        }

        return this;
    }

    @Override
    public HsCriteria getFromClause() {
        return this.fromClause;
    }

    @Override
    public IUColumn[] getColumnNames() {
        if (CollectionUtils.isNotEmpty(selectClause)) {
            return selectClause.toArray(new IUColumn[selectClause.size()]);
        }

        return null;
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
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
        if (getFromClause() == null) {
            //属于insert into table () values ()
            sql.append(" VALUES(");
            String[] columnVals = new String[columns.length];
            for (int i = 0; i < columns.length; i++) {
                IUColumn column = columns[i];
                if (column.getColumnValue() == null) {
                    columnVals[i] = null;
                } else {
                    if (column.isConst()) {
                        columnVals[i] = column.getColumnValue().toString();
                    } else {
                        String newParam = criterionQuery.addParameter(column.getColumnName(), column.getColumnValue());
                        columnVals[i] = IbsStringHelper.repeatParamFormat(newParam);
                    }
                }
            }

            sql.append(IbsStringHelper.join(",", columnVals)).append(")");
        } else {
            //属于 insert into table() select * from...
            sql.append(" ").append(getFromClause().getSqlString(criterionQuery));
        }

        return sql.toString();
    }

    @Override
    public String getOp() {
        return exprOper.getOp();
    }

    @Override
    public String getOpType() {
        return exprOper.name();
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
        for (String column : columnNames) {
            selectClause.add(IUColumn.select(column));
        }

        return this;
    }

    @Override
    public IUCriteria addColumn(List<String> columnNames) {
        for (String column : columnNames) {
            selectClause.add(IUColumn.select(column));
        }

        return this;
    }
}
