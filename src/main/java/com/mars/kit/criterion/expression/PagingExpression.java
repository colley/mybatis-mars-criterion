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
public class PagingExpression implements Criterion {
	 private static final long serialVersionUID = 5882222749135309304L;

	    //private String property;
	    private final int startPos;
	    private final int pageSize;
	    private final ExprOper exprOper;

	    public PagingExpression(int startPos, int pageSize, ExprOper exprOper) {
	        this.startPos = startPos;
	        this.pageSize = pageSize;
	        this.exprOper = exprOper;
	    }

	    @Override
	    public void setProperty(String property) {
	        //
	    }

	    public int getStartPos() {
	        return startPos;
	    }

	    public int getPageSize() {
	        return pageSize;
	    }

	    @Override
	    public String getProperty() {
	        return "NONE";
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
	    	String paramterNamePos = criterionQuery.addParameter("limitStartPos", startPos);
	    	String paramterNamepSize=  criterionQuery.addParameter("limitPageSize", pageSize);
	        StringBuffer buffer = new StringBuffer();
	        if ("limit".equals(getOpType())) {
	            buffer.append(getOp());
	            buffer.append(IbsStringHelper.repeatParamFormat(paramterNamePos));
	            buffer.append(",");
	            buffer.append(IbsStringHelper.repeatParamFormat(paramterNamepSize));
	        }

	        return buffer.toString();
	    }
}
