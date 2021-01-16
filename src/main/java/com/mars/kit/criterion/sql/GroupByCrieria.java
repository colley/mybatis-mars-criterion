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

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.expression.Criterion;


/**
 *@FileName  GroupByCrieria.java
 *@Date  16-5-24 上午11:26
 *@author Colley
 *@version 1.0
 */
public class GroupByCrieria implements GroupCriteria {
    private List<String> groupByColumn = new ArrayList<String>();
    private List<Criterion> having = new ArrayList<Criterion>();
    private final ExprOper exprOper;

    public GroupByCrieria(String[] groupcolumn) {
        if (ArrayUtils.isNotEmpty(groupcolumn)) {
            for (String columnName : groupcolumn) {
                if (StringUtils.isNotEmpty(columnName)) {
                    this.groupByColumn.add(columnName);
                }
            }
        }

        this.exprOper = ExprOper.groupBy;
    }

    @Override
    public GroupByCrieria add(Criterion criterion) {
        if (criterion != null) {
            this.having.add(criterion);
        }

        return this;
    }

    @Override
    public GroupByCrieria addColumn(String columnName) {
        if (StringUtils.isNotEmpty(columnName)) {
            this.groupByColumn.add(columnName);
        }

        return this;
    }

    @Override
    public Criterion[] getHaving() {
        if (CollectionUtils.isNotEmpty(having)) {
            return having.toArray(new Criterion[0]);
        }

        return null;
    }

    @Override
    public String[] getGroupByColumn() {
        if (CollectionUtils.isNotEmpty(groupByColumn)) {
            return groupByColumn.toArray(new String[0]);
        }

        return null;
    }

    public static GroupByCrieria groupBy(String[] groupcolumn) {
        return new GroupByCrieria(groupcolumn);
    }
    
    public static GroupByCrieria groupBy(String groupcolumn) {
        String[] groupcolumns = new String[]{groupcolumn};
        return new GroupByCrieria(groupcolumns);
    }

    public String getOp() {
        return exprOper.getOp();
    }
    
    public String getOpType() {
        return exprOper.name();
    }

    public String getGroupByColumnStr() {
        if (CollectionUtils.isNotEmpty(groupByColumn)) {
            return StringUtils.join(groupByColumn, ",");
        }

        return null;
    }

    public String toString() {
        return getOp() + " " + StringUtils.join(groupByColumn, ",") + " HAVING " + having.iterator();
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getOp()).append(StringUtils.join(groupByColumn, ","));
        if (CollectionUtils.isNotEmpty(having)) {
        	buffer.append(" HAVING ");
            Iterator<Criterion> iter = having.iterator();
            while (iter.hasNext()) {
                buffer.append(((Criterion) iter.next()).getSqlString(criterionQuery));
                if (iter.hasNext()) {
                    buffer.append(' ').append(" AND ").append(' ');
                }
            }
        }

        return buffer.toString();
    }
}
