/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.IbsStringHelper;
import com.mars.kit.criterion.expression.Criterion;


/**
 *@FileName  BaseCriteria.java
 *@Date  16-5-20 下午5:49
 *@author Colley
 *@version 1.0
 */
public abstract class BaseIbtsCriteria implements HsCriteria {
    private static final long serialVersionUID = -6819935861456680579L;
    protected final Log logger = LogFactory.getLog(this.getClass());
    private List<AliasColumn> selectClause = new ArrayList<AliasColumn>();
    protected List<HsCriteria> fromClause = new ArrayList<HsCriteria>();
    protected List<JoinCriteria> fromJoins = new ArrayList<JoinCriteria>();
    protected List<Criterion> whereClause = new ArrayList<Criterion>();
    protected Criterion pagingClause = null;

    @Override
    public Criterion[] allCriteria() {
        if (CollectionUtils.isNotEmpty(whereClause)) {
            return whereClause.toArray(new Criterion[0]);
        }

        return null;
    }
    
    @Override
	public HsCriteria addFromClause(HsCriteria fromClause) {
		 this.fromClause.add(fromClause);
		 
		 return this;
	}

    @Override
    public Criterion[] getCriteria() {
        if (CollectionUtils.isNotEmpty(whereClause)) {
            return whereClause.toArray(new Criterion[0]);
        }

        return null;
    }

    public BaseIbtsCriteria add(Criterion criterion) {
        if (criterion != null) {
            whereClause.add(criterion);
        }

        return this;
    }

    public HsCriteria addPagingLimit(Criterion criterion) {
        if (criterion != null) {
            pagingClause = criterion;
        }

        return this;
    }

    public Criterion getPagingLimit() {
        return pagingClause;
    }

    public BaseIbtsCriteria addColumn(AliasColumn columnName) {
        if (columnName != null) {
            selectClause.add(columnName);
        }

        return this;
    }

    public BaseIbtsCriteria addColumn(String columnName) {
        if (columnName != null) {
            AliasColumn aliasColumn = AliasColumn.neAs(columnName);
            selectClause.add(aliasColumn);
        }

        return this;
    }

    public BaseIbtsCriteria addColumn(String[] columnNames) {
        if (ArrayUtils.isNotEmpty(columnNames)) {
            for (String columnName : columnNames) {
                if (StringUtils.isNotEmpty(columnName)) {
                    AliasColumn aliasColumn = AliasColumn.neAs(columnName);
                    selectClause.add(aliasColumn);
                }
            }
        }

        return this;
    }

    public BaseIbtsCriteria addColumn(List<String> columnNames) {
        if (CollectionUtils.isNotEmpty(columnNames)) {
            for (String columnName : columnNames) {
                AliasColumn aliasColumn = AliasColumn.neAs(columnName);
                selectClause.add(aliasColumn);
            }
        }

        return this;
    }

    public AliasColumn[] getColumnNames() {
        if (CollectionUtils.isNotEmpty(selectClause)) {
            return selectClause.toArray(new AliasColumn[0]);
        }

        return null;
    }

    @Override
    public HsCriteria[] getFromClause() {
        if (CollectionUtils.isNotEmpty(fromClause)) {
            return this.fromClause.toArray(new HsCriteria[0]);
        }

        return null;
    }

    @Override
    public JoinCriteria[] getFromJoins() {
        if (CollectionUtils.isNotEmpty(fromJoins)) {
            return fromJoins.toArray(new JoinCriteria[0]);
        }

        return null;
    }

    protected String toColumnSqlString(CriterionQuery criterionQuery) {
        StringBuffer buf = new StringBuffer();
        AliasColumn[] allColumn = getColumnNames();
        int colSize = allColumn.length;
        for (int i = 0; i < (colSize - 1); i++) {
            AliasColumn col = allColumn[i];
            buf.append(col.getColumnName()).append(",");
        }

        buf.append(allColumn[colSize - 1].getColumnName());
        return buf.toString();
    }

    protected String fromClauseSqlString(CriterionQuery criterionQuery) {
    	HsCriteria[] fromClauses = getFromClause();
        if (ArrayUtils.isNotEmpty(fromClauses)) {
            String[] fromClausesql = new String[fromClauses.length];
            for (int i = 0; i < fromClauses.length; i++) {
            	if("tName".equals(fromClauses[i].getOp())){
            		 fromClausesql[i] = fromClauses[i].getSqlString(criterionQuery);
            	}else{
            		fromClausesql[i] =new StringBuffer()
                    		.append("(")
                    		.append(fromClauses[i].getSqlString(criterionQuery))
                    		.append(") ")
                    		.append(fromClauses[i].getAliasTableName())
                    		.toString();
            	}
            }
            return IbsStringHelper.join(",", fromClausesql);
        }

        return "";
    }

    protected String fromJoinsSqlString(CriterionQuery criterionQuery) {
        JoinCriteria[] fromJoins = getFromJoins();
        if (fromJoins == null) {
            return "";
        }

        String[] fromClausesql = new String[fromJoins.length];
        for (int i = 0; i < fromJoins.length; i++) {
            fromClausesql[i] = fromJoins[i].getSqlString(criterionQuery);
        }

        return IbsStringHelper.join(" ", fromClausesql);
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
    public String getSqlString(CriterionQuery criterionQuery) {
        StringBuffer buf = new StringBuffer();
        buf.append("select ").append(toColumnSqlString(criterionQuery)).append(" from ")
           .append(fromClauseSqlString(criterionQuery));

        String outerJoinsAfterFrom = fromJoinsSqlString(criterionQuery);
        if (IbsStringHelper.isNotEmpty(outerJoinsAfterFrom)) {
            buf.append(outerJoinsAfterFrom);
        }

        if (CollectionUtils.isNotEmpty(whereClause)) {
            buf.append(" where ");
            buf.append(whereClauseSqlString(criterionQuery));
        }

        return buf.toString();
    }
}
