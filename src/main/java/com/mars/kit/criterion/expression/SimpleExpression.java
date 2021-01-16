/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.expression;

import org.apache.commons.lang.StringUtils;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.common.IbsStringHelper;


/**
 *@FileName  IbatisExpression.java
 *@Date  16-5-20 上午11:03
 *@author Colley
 *@version 1.0
 */
public class SimpleExpression implements Criterion {
    private static final long serialVersionUID = -2835703300683541644L;
    private String property;
    private final Object value;
    private final boolean ignoreCase;
    private final ExprOper exprOper;

    public SimpleExpression(String property, ExprOper op) {
        this(property, null, op, false);
    }

    public SimpleExpression(String property, Object value, ExprOper exprOper) {
        this(property, value, exprOper, false);
    }

    public SimpleExpression(String property, Object value, ExprOper exprOper, boolean ignoreCase) {
        this.property = property;
        this.value = value;
        this.ignoreCase = ignoreCase;
        this.exprOper = exprOper;
    }

    public String getProperty() {
        return StringUtils.trim(property);
    }

    public Object getValue() {
        return value;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public String getOp() {
        return exprOper.getOp();
    }

    @Override
    public void setProperty(String property) {
        this.property = property;
    }

    public String getOpType() {
        return exprOper.name();
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
        String paramterName = criterionQuery.addParameter(property, value);
        String[] columns = new String[] { property };
        StringBuffer fragment = new StringBuffer();

        if (columns.length > 1) {
            fragment.append('(');
        }

        for (int i = 0; i < columns.length; i++) {
            fragment.append(columns[i]);
            fragment.append(getOp()).append(IbsStringHelper.repeatParamFormat(paramterName));

            if (i < (columns.length - 1)) {
                fragment.append(" AND ");
            }
        }

        if (columns.length > 1) {
            fragment.append(')');
        }

        return fragment.toString();
    }

    public String toString() {
        return property + getOp() + value.toString();
    }
}
