/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.expression;

import org.apache.commons.lang.StringUtils;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;

/**
 *@FileName  IbatisExpression.java
 *@Date  16-5-20 上午11:03
 *@author Colley
 *@version 1.0
 */
public class NullExpression implements Criterion {
    private static final long serialVersionUID = -2835703300683541644L;
    private String property;
    private final ExprOper exprOper;

   

    public NullExpression(String property, ExprOper exprOper) {
        this.property = property;
        this.exprOper = exprOper;
    }

    public String getProperty() {
        return StringUtils.trim(property);
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
		StringBuffer fragment = new StringBuffer();
		fragment.append(getProperty()).append(" ").append(getOp());
		return fragment.toString();
    }
	
	public String toString(){
		return property+getOp();
	}
}
