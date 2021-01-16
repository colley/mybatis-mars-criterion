/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.expression;


import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.common.IbsStringHelper;


/**
 *@FileName  InExpression.java
 *@Date  16-5-25 下午1:29
 *@author Colley
 *@version 1.0
 */
public class InExpression implements Criterion {
	private static final long serialVersionUID = -4664014410813000557L;
	private String property;
    private final Object[] values;
    private final ExprOper exprOper;

    public InExpression(String property, Object[] values, ExprOper exprOper) {
        this.property = property;
        this.values = values;
        this.exprOper = exprOper;
    }

    public Object[] getValues() {
            if(values!=null && values.length>0){
                return values;   
            }
	 return null;
	}

	@Override
    public void setProperty(String property) {
    	this.property = property;
    }

    @Override
    public String getProperty() {
        return this.property;
    }

    @Override
    public String getOp() {
        return this.exprOper.getOp();
    }

    public String getOpType() {
		return exprOper.name();
	}

	@Override
    public String getSqlString(CriterionQuery criterionQuery) {
		String paramterName = criterionQuery.addParameter(property, values);
		String[] columns = new String[]{property};
		String params = values.length>0 ?
				IbsStringHelper.repeatParamFormat(paramterName,IbsStringHelper.PARAMETER_TOKEN,",",values.length):
				"";
		String cols = IbsStringHelper.join(", ", columns);
		if ( columns.length>1 ) cols = '(' + cols + ')';
		return cols + getOp()+"(" + params + ')';
    }
}
