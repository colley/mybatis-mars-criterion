/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-9 下午1:56
 * History:
 */
package com.mars.kit.criterion.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.common.GetterHelper;


/**
 * DeleteCriteria.java
 *
 * @author ColleyMa
 * @version 19-5-9 下午1:56
*/
public class DeleteCriteria extends BaseIbtsCriteria {
    private static final long serialVersionUID = -205683972361209369L;
    private List<IbsOrder> orderBys = new ArrayList<IbsOrder>();
    private String aliasTableName;
    private final ExprOper exprOper;

    public DeleteCriteria() {
        this.exprOper = ExprOper.DELETE;
    }

    @Override
    public List<IbsOrder> getOrderBys() {
        return orderBys;
    }

    public DeleteCriteria addOrder(IbsOrder order) {
        if (order != null) {
            this.orderBys.add(order);
        }

        return this;
    }

    public BaseIbtsCriteria addFromClause(HsCriteria fromClause, String aliasTableName) {
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
        buf.append(getOp()).append(" from ").append(fromClauseSqlString(criterionQuery));

        if (CollectionUtils.isNotEmpty(super.whereClause)) {
            buf.append(" where ");
            buf.append(whereClauseSqlString(criterionQuery));
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
        return exprOper.getOp();
    }

    @Override
    public String getAliasTableName() {
        return GetterHelper.getString(aliasTableName);
    }

    @Override
    public void setAliasTableName(String aliasTableName) {
        this.aliasTableName = aliasTableName;
    }

    @Override
    public HsCriteria addGroupBy(GroupCriteria groupByClause) {
        //ignore
        return this;
    }
}
