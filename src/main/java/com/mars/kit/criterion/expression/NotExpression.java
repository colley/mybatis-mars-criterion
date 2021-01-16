/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.expression;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;


/**
 *@FileName  NotExpression.java
 *@Date  16-6-14 上午9:43
 *@author Colley
 *@version 1.0
 */
public class NotExpression implements Criterion {
    private static final long serialVersionUID = 3116540219054115788L;
    private Criterion criterion;
    private final ExprOper exprOper;

    public NotExpression(Criterion criterion) {
        this.criterion = criterion;
        exprOper = ExprOper.NOT;
    }

    @Override
    public void setProperty(String property) {
    }

    @Override
    public String getProperty() {
        return "NOT";
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
    public String getSqlString(CriterionQuery criterionQuery) {
        //if ( criterionQuery.getFactory().getDialect() instanceof MySQLDialect ) {
        return "not (" + criterion.getSqlString(criterionQuery) + ')';
        //}
        //else {
        //	return "not " + criterion.toSqlString(criteria, criteriaQuery);
        //}
    }
}
