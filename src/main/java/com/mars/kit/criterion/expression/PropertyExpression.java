/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.expression;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;


/**
 *@FileName  PropertyExpression.java
 *@Date  16-5-24 下午1:17
 *@author Colley
 *@version 1.0
 */
public class PropertyExpression implements Criterion {
    private static final long serialVersionUID = 2471086998662951133L;
    private String propertyName;
    private String firstPropertyName;
    private String secondParopertyName;
    private final ExprOper exprOper;

    public PropertyExpression(String propertyName, String otherPropertyName, ExprOper exprOper) {
        this.propertyName = propertyName;
        this.firstPropertyName = otherPropertyName;
        this.exprOper = exprOper;
    }

    public PropertyExpression(String propertyName, String otherPropertyName, String secondParopertyName,
        ExprOper exprOper) {
        this.propertyName = propertyName;
        this.firstPropertyName = otherPropertyName;
        this.secondParopertyName = secondParopertyName;
        this.exprOper = exprOper;
    }

    public void setProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setProperty(String propertyName, String otherPropertyName) {
        this.propertyName = propertyName;
        this.firstPropertyName = otherPropertyName;
    }

    public String getProperty() {
        return propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getOtherPropertyName() {
        return firstPropertyName;
    }

    public String getOp() {
        return exprOper.getOp();
    }

    public String getOpType() {
        return ExprOper.proExpr.name();
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
        StringBuffer sql = new StringBuffer();
        if (ExprOper.between.equals(exprOper)) {
            sql.append(propertyName).append(" BETWEEN ").append(firstPropertyName).append(" AND ")
               .append(secondParopertyName);
        } else {
            sql.append(propertyName).append(getOp()).append(firstPropertyName);
        }

        return sql.toString();
    }
}
