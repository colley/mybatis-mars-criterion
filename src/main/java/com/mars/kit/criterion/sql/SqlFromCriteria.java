/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;


/**
 *@FileName  SubSelFromCriteria.java
 *@Date  16-5-24 上午11:07
 *@author Colley
 *@version 1.0
 */
public class SqlFromCriteria extends BaseIbtsCriteria implements HsCriteria {
    private static final long serialVersionUID = -4587630586604350464L;
    private GroupCriteria groupByClause = null;
    private List<IbsOrder> orderBys = new ArrayList<IbsOrder>();
    private final String op;
    public String aliasTableName;

    public SqlFromCriteria() {
        this.op = ExprOper.subSel.name();
    }

    public SqlFromCriteria(TableFromCriteria tableNameCriteria, GroupCriteria groupByClause) {
        this.fromClause.add(tableNameCriteria);
        this.op = ExprOper.subSel.name();
        this.groupByClause = groupByClause;
    }

    public String getOp() {
        return op;
    }

    public SqlFromCriteria addGroupBy(GroupCriteria groupByClause) {
        this.groupByClause = groupByClause;
        return this;
    }

    public GroupCriteria getGroupBy() {
        return this.groupByClause;
    }

    @Override
    public List<IbsOrder> getOrderBys() {
        return orderBys;
    }

    public BaseIbtsCriteria addOrder(IbsOrder order) {
        if (order != null) {
            this.orderBys.add(order);
        }

        return this;
    }

    public BaseIbtsCriteria addFromClause(HsCriteria fromClause,String aliasTableName) {
        if (fromClause != null) {
        	fromClause.setAliasTableName(aliasTableName);
            this.fromClause.add(fromClause);
        }

        return this;
    }

    public BaseIbtsCriteria addFromJoins(JoinCriteria joinCriteria) {
        if (joinCriteria != null) {
            this.fromJoins.add(joinCriteria);
        }

        return this;
    }
    
    public void setAliasTableName(String aliasTableName) {
		this.aliasTableName = aliasTableName;
	}

	public String getOrderByStr() {
        if (CollectionUtils.isNotEmpty(orderBys)) {
            StringBuffer orderBy = new StringBuffer();
            String ascending = orderBys.get(0).isAscending() ? "ASC" : "DESC";
            for (int i = 0; i < orderBys.size(); i++) {
                IbsOrder order = orderBys.get(i);
                orderBy.append(order.getPropertyName());
                if (i < (orderBys.size() - 1)) {
                    orderBy.append(",");
                }
            }

            orderBy.append(" ").append(ascending);
            return orderBy.toString();
        }

        return null;
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
        StringBuffer buffer = new StringBuffer();
        String sql = super.getSqlString(criterionQuery);
        buffer.append(sql);
        //增加gropBy和order
        if (groupByClause != null) {
            buffer.append(groupByClause.getSqlString(criterionQuery));
        }

        if (CollectionUtils.isNotEmpty(orderBys)) {
            buffer.append(" order by ").append(getOrderByStr());
        }
        return buffer.toString();
    }

  

	@Override
	public String getAliasTableName() {
		if(aliasTableName ==null) return StringUtils.EMPTY;
		return aliasTableName;
	}
}
