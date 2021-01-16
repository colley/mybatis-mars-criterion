/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.GetterHelper;


/**
 *@FileName  IbatisSelect.java
 *@Date  16-5-24 上午10:33
 *@author Colley
 *@version 1.0
 */
public class IbatisSelect extends BaseIbtsCriteria {
    private static final long serialVersionUID = -2106831102597415592L;
    private List<IbsOrder> orderBys = new ArrayList<IbsOrder>();
    private GroupCriteria groupByClause = null;
    private  String aliasTableName;

    public IbatisSelect addGroupBy(GroupCriteria groupByClause) {
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

    public IbatisSelect addOrder(IbsOrder order) {
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

    public String getOrderByStr() {
        if (CollectionUtils.isNotEmpty(orderBys)) {
            StringBuffer orderBy = new StringBuffer();
            for (int i = 0; i < orderBys.size(); i++) {
                IbsOrder order = orderBys.get(i);
                String ascending = order.isAscending() ? "ASC" : "DESC";
                orderBy.append(order.getPropertyName()).append(" ").append(ascending);
                if (i < (orderBys.size() - 1)) {
                    orderBy.append(",");
                }
            }
            return orderBy.toString();
        }

        return null;
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
        StringBuffer buf = new StringBuffer();
        String sql = super.getSqlString(criterionQuery);
        buf.append(sql);
        //增加gropBy和order
        if (groupByClause != null) {
            buf.append(groupByClause.getSqlString(criterionQuery));
        }

        if (CollectionUtils.isNotEmpty(orderBys)) {
            buf.append(" order by ").append(getOrderByStr());
        }

        if (getPagingLimit() != null) {
            buf.append(getPagingLimit().getSqlString(criterionQuery));
        }

        return buf.toString();
    }

    @Override
    public String getOp() {
        return "select";
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
