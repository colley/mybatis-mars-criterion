/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.expression;

import org.apache.commons.lang.StringUtils;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.common.IbsMatchMode;
import com.mars.kit.criterion.common.IbsStringHelper;


/**
 *@FileName  IlikeExpression.java
 *@Date  16-5-20 上午11:31
 *@author Colley
 *@version 1.0
 */
public class LikeExpression implements Criterion {
    private static final long serialVersionUID = 6691136444709699737L;
    private String property;
    private final Object value;
    private final boolean ignoreCase;
    private final String matchMode;
    private final ExprOper exprOper;

    public LikeExpression(String property, Object value) {
        this(property, value, false, IbsMatchMode.EXACT);
    }

    public LikeExpression(String property, Object value, IbsMatchMode matchMode) {
        this(property, value, false, matchMode);
    }

    public LikeExpression(String property, Object value, ExprOper exprOper, IbsMatchMode matchMode) {
        this.property = property;
        this.value = value;
        this.matchMode = matchMode.toString();
        this.ignoreCase = false;
        this.exprOper = exprOper;
    }

    public LikeExpression(String property, Object value, boolean ignoreCase, IbsMatchMode matchMode) {
        this.property = property;
        this.value = value;
        this.matchMode = matchMode.toString();
        this.ignoreCase = ignoreCase;

        if (ignoreCase) {
            this.exprOper = ExprOper.ilike;
        } else {
            this.exprOper = ExprOper.like;
        }
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
        return this.exprOper.getOp();
    }

    public String getMatchMode() {
        return matchMode;
    }

    public String getOpType() {
        return exprOper.name();
    }

    @Override
    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
        String paramterName = criterionQuery.addParameter(property, value);
        StringBuffer fragment = new StringBuffer();

        if (ExprOper.ilike.name().equals(this.getOpType())) {
            fragment.append("lower(").append(getProperty()).append(")");
        } else if (ExprOper.like.name().equals(this.getOpType())) {
            fragment.append(getProperty());
        }

        fragment.append(getOp());

        if (IbsMatchMode.EXACT.name().equals(matchMode)) {
            fragment.append(IbsStringHelper.repeatParamFormat(property));
        } else if (IbsMatchMode.START.name().equals(matchMode)) {
            fragment.append("CONCAT('%',").append(IbsStringHelper.repeatParamFormat(paramterName)).append(")");
        } else if (IbsMatchMode.END.name().equals(matchMode)) {
            fragment.append("CONCAT(").append(IbsStringHelper.repeatParamFormat(paramterName)).append(",'%')");
        } else if (IbsMatchMode.ANYWHERE.name().equals(matchMode)) {
            fragment.append("CONCAT('%',").append(IbsStringHelper.repeatParamFormat(paramterName)).append(",'%')");
        }

        return fragment.toString();
    }

    public String toString() {
        return property + getOp() + value.toString();
    }
}
