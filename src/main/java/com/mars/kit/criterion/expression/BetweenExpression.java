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
 *@FileName  IbsBetweenExpression.java
 *@Date  16-5-20 上午11:18
 *@author Colley
 *@version 1.0
 */
public class BetweenExpression implements Criterion {
    private static final long serialVersionUID = -2136550490838064365L;
    private String property;
    private final Object lo;
    private final Object hi;
    private final ExprOper exprOper;

    public BetweenExpression(String property, Object lo, Object hi) {
        this(property, lo, hi, ExprOper.between);
    }

    public BetweenExpression(String property, Object lo, Object hi, ExprOper exprOper) {
        this.property = property;
        this.lo = lo;
        this.hi = hi;
        this.exprOper = exprOper;
    }

    public String getOp() {
        return this.exprOper.getOp();
    }

    public String getProperty() {
        return StringUtils.trim(property);
    }

    public Object getLo() {
        return lo;
    }

    public Object getHi() {
        return hi;
    }

    @Override
    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
    	String paramterNamelo = criterionQuery.addParameter(property+"lo", lo);
		String paramterNamehi = criterionQuery.addParameter(property + "hi", hi);
    	StringBuffer buffer = new StringBuffer();
    	buffer.append("(");
    	buffer.append(getProperty());
    	buffer.append(getOp());
    	buffer.append(IbsStringHelper.repeatParamFormat(paramterNamelo));
    	buffer.append(" AND ");
    	buffer.append(IbsStringHelper.repeatParamFormat(paramterNamehi));
    	buffer.append(")");
        return buffer.toString();
    }
    
    
    public String toString(){
    	return getProperty() +getOp()+lo+" AND "+hi;
    }

    @Override
    public String getOpType() {
        return this.exprOper.name();
    }
}
