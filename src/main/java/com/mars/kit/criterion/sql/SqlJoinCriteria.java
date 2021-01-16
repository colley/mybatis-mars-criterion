/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.expression.Criterion;
import com.mars.kit.criterion.expression.IbsRestrictions;


/**
 *@FileName  SqlJoinCriteria.java
 *@Date  16-5-26 下午5:13
 *@author Colley
 *@version 1.0
 */
public class SqlJoinCriteria implements JoinCriteria {
    private final String tableName;
    private HsCriteria sqlCriteria;
    private final Criterion onClause;
    private List<Criterion> whereScope = new ArrayList<Criterion>();
    private final ExprOper exprOper;

    public SqlJoinCriteria(ExprOper exprOper, String aliasTabelName, HsCriteria sqlCriteria,String[] onClasuse) {
        this.exprOper = exprOper;
        this.tableName = aliasTabelName;
        this.sqlCriteria = sqlCriteria;
        this.onClause = IbsRestrictions.eqProperty(onClasuse[0], onClasuse[1]);
    }
    
    
    public static JoinCriteria leftJoinOn(String aliasTabelName, HsCriteria sqlCriteria, String[] onClasuse) {
        return new SqlJoinCriteria(ExprOper.LFET_JOIN, aliasTabelName,sqlCriteria, onClasuse);
    }

    public static JoinCriteria rightJoinOn(String aliasTabelName, HsCriteria sqlCriteria,String[] onClasuse) {
        return new SqlJoinCriteria(ExprOper.RIGHT_JOIN, aliasTabelName,sqlCriteria, onClasuse);
    }

    public static JoinCriteria joinOn(String aliasTabelName, HsCriteria sqlCriteria,String[] onClasuse) {
        return new SqlJoinCriteria(ExprOper.JOIN, aliasTabelName,sqlCriteria,onClasuse);
    }
    public static JoinCriteria innerJoinOn(String aliasTabelName, HsCriteria sqlCriteria,String[] onClasuse) {
        return new SqlJoinCriteria(ExprOper.INNER_JOIN, aliasTabelName,sqlCriteria,onClasuse);
    }

    public HsCriteria getSqlCriteria() {
        return sqlCriteria;
    }

    @Override
    public String getOpType() {
        return exprOper.name();
    }

	@Override
    public String getOp() {
        return exprOper.getOp();
    }

    @Override
    public String getTableName() {
        return this.tableName;
    }

    @Override
    public Criterion[] getCriteria() {
        if (CollectionUtils.isNotEmpty(whereScope)) {
            return whereScope.toArray(new Criterion[0]);
        }

        return null;
    }

    @Override
    public void add(Criterion criterion) {
        if (criterion != null) {
            whereScope.add(criterion);
        }
    }

    @Override
    public Criterion getOnCriteria() {
        return onClause;
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
        StringBuffer sql = new StringBuffer(" ");
        sql.append(getOp());
        sql.append("(");
        sql.append(sqlCriteria.getSqlString(criterionQuery));
        sql.append(") ");
        sql.append(tableName);
        sql.append(" ON ").append(onClause.getSqlString(criterionQuery));
        if (ArrayUtils.isNotEmpty(getCriteria())) {
            Criterion[] jwhereScope = getCriteria();
            int criSize = jwhereScope.length;
            for (int i = 0; i < criSize; i++) {
                sql.append(" AND ").append(jwhereScope[i].getSqlString(criterionQuery));
            }
        }

        return sql.toString();
    }
}
