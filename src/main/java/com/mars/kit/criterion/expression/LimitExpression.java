/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.expression;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.common.IbsStringHelper;


/**
 *@FileName  PagingExpression.java
 *@Date  16-5-26 下午1:22
 *@author Colley
 *@version 1.0
 */
public class LimitExpression implements Criterion {
    private static final long serialVersionUID = 5882222749135309304L;

    private int limit;
    private final ExprOper exprOper;

    protected LimitExpression(int limit) {
        this.limit = limit;
        this.exprOper = ExprOper.limit;
    }
    
    public static LimitExpression limit(int limit) {
    	return new LimitExpression(limit);
    }

    @Override
    public void setProperty(String property) {
        //ignore
    }

    public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
    public String getProperty() {
        return "limit";
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
    	String paramterNamePos = criterionQuery.addParameter("limit", limit);
        StringBuffer buffer = new StringBuffer();
        buffer.append(getOp());
        buffer.append(IbsStringHelper.repeatParamFormat(paramterNamePos));
        return buffer.toString();
    }
}
