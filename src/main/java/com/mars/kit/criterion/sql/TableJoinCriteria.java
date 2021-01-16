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
 *@FileName  SimpleJoinCriteria.java
 *@Date  16-5-24 下午1:28
 *@author Colley
 *@version 1.0
 */
public class TableJoinCriteria implements JoinCriteria {
    private final String tabelName;
    private final String aliasTabelName;
    private Criterion onClause = null;
    private List<Criterion> whereScope = new ArrayList<Criterion>();
    private final ExprOper exprOper;

    public TableJoinCriteria(ExprOper exprOper, String tabelName, String[] onClasuse) {
        this(exprOper, tabelName, "", onClasuse);
    }

    public TableJoinCriteria(ExprOper exprOper, String tabelName, String aliasTabelName, String[] onClasuse) {
        this.onClause = IbsRestrictions.eqProperty(onClasuse[0], onClasuse[1]);
        this.tabelName = tabelName;
        this.aliasTabelName = aliasTabelName;
        this.exprOper = exprOper;
    }

    @Override
    public String getOp() {
        return exprOper.getOp();
    }

    public String getOpType() {
        return exprOper.name();
    }

    @Override
    public String getTableName() {
        return this.tabelName;
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

    public void add(Criterion[] criterions) {
        if (ArrayUtils.isNotEmpty(criterions)) {
            for (int i = 0; i < criterions.length; i++) {
                add(criterions[i]);
            }
        }
    }

    @Override
    public Criterion getOnCriteria() {
        return onClause;
    }

    public String toString() {
        return this.getOp() + " " + tabelName + " ON " + whereScope.iterator();
    }

    public static TableJoinCriteria leftJoinOn(String tabelName, String[] onClasuse) {
        return new TableJoinCriteria(ExprOper.LFET_JOIN, tabelName, onClasuse);
    }

    public static TableJoinCriteria rightJoinOn(String tabelName, String[] onClasuse) {
        return new TableJoinCriteria(ExprOper.RIGHT_JOIN, tabelName, onClasuse);
    }

    public static TableJoinCriteria joinOn(String tabelName, String[] onClasuse) {
        return new TableJoinCriteria(ExprOper.JOIN, tabelName, onClasuse);
    }

    public static TableJoinCriteria leftJoinOn(String tabelName, String aliasTabelName, String[] onClasuse) {
        return new TableJoinCriteria(ExprOper.LFET_JOIN, tabelName, aliasTabelName, onClasuse);
    }

    public static TableJoinCriteria rightJoinOn(String tabelName, String aliasTabelName, String[] onClasuse) {
        return new TableJoinCriteria(ExprOper.RIGHT_JOIN, tabelName, aliasTabelName, onClasuse);
    }

    public static TableJoinCriteria joinOn(String tabelName, String aliasTabelName, String[] onClasuse) {
        return new TableJoinCriteria(ExprOper.JOIN, tabelName, aliasTabelName, onClasuse);
    }

    public static TableJoinCriteria joinOn(String tabelName, String aliasTabelName, String[] onClasuse, Criterion[] whereScope) {
        TableJoinCriteria tableJoinCriteria = new TableJoinCriteria(ExprOper.JOIN, tabelName, aliasTabelName, onClasuse);

        if (ArrayUtils.isNotEmpty(whereScope)) {
            tableJoinCriteria.add(whereScope);
        }

        return tableJoinCriteria;
    }

    public static TableJoinCriteria leftJoinOn(String tabelName, String aliasTabelName, String[] onClasuse, Criterion[] whereScope) {
        TableJoinCriteria tableJoinCriteria = new TableJoinCriteria(ExprOper.LFET_JOIN, tabelName, aliasTabelName, onClasuse);

        if (ArrayUtils.isNotEmpty(whereScope)) {
            tableJoinCriteria.add(whereScope);
        }

        return tableJoinCriteria;
    }

    public static TableJoinCriteria rightJoinOn(String tabelName, String aliasTabelName, String[] onClasuse, Criterion[] whereScope) {
        TableJoinCriteria tableJoinCriteria = new TableJoinCriteria(ExprOper.RIGHT_JOIN, tabelName, aliasTabelName, onClasuse);

        if (ArrayUtils.isNotEmpty(whereScope)) {
            tableJoinCriteria.add(whereScope);
        }

        return tableJoinCriteria;
    }

    public static TableJoinCriteria innerJoinOn(String tabelName, String aliasTabelName, String[] onClasuse, Criterion[] whereScope) {
        TableJoinCriteria tableJoinCriteria = new TableJoinCriteria(ExprOper.INNER_JOIN, tabelName, aliasTabelName, onClasuse);

        if (ArrayUtils.isNotEmpty(whereScope)) {
            tableJoinCriteria.add(whereScope);
        }

        return tableJoinCriteria;
    }
    
    public static TableJoinCriteria joinOn(String tabelName, String aliasTabelName, String[] onClasuse, Criterion[] whereScope,ExprOper exprOper) {
        TableJoinCriteria tableJoinCriteria = new TableJoinCriteria(exprOper, tabelName, aliasTabelName, onClasuse);

        if (ArrayUtils.isNotEmpty(whereScope)) {
            tableJoinCriteria.add(whereScope);
        }

        return tableJoinCriteria;
    }

    public static TableJoinCriteria innerJoinOn(String tabelName, String aliasTabelName, String[] onClasuse) {
        return new TableJoinCriteria(ExprOper.INNER_JOIN, tabelName, aliasTabelName, onClasuse);
    }

    public static TableJoinCriteria innerJoinOn(String tabelName, String[] onClasuse) {
        return new TableJoinCriteria(ExprOper.INNER_JOIN, tabelName, onClasuse);
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
        StringBuffer sql = new StringBuffer(" ");
        sql.append(getOp()).append(tabelName).append(" ").append(aliasTabelName);
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
