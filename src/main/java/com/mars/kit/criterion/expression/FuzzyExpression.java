/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.expression;

import org.apache.commons.lang.StringUtils;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.common.GetterHelper;
import com.mars.kit.criterion.common.IbsStringHelper;


/**
 *@FileName  FuzzyExpression.java
 *@Date  16-5-24 下午3:55
 *@author Colley
 *@version 1.0
 */
public class FuzzyExpression implements Criterion {
    private static final long serialVersionUID = 253106397546100801L;
    private String property;
    private final Object value;
    private final String op;
    private final ExprOper exprOper;

    public FuzzyExpression(String property, Object value, String op) {
        this.property = property;
        this.value = value;
        if(StringUtils.isNotEmpty(op)){
        	if(GetterHelper.isChineseChar(op)){
        		op = "<>";
        	}
        }
        this.op = op;
        this.exprOper = ExprOper.fuzzy;
    }

    public String getProperty() {
        return StringUtils.trim(property);
    }

    public Object getValue() {
        return value;
    }

    public String getOp() {
        return op;
    }
    
    public String getOpType() {
        return this.exprOper.name();
    }

    @Override
    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery){
    	String paramterName = criterionQuery.addParameter(property, value);
		String[] columns = new String[]{property};
		StringBuffer fragment = new StringBuffer();
		if (columns.length>1) fragment.append('(');
		for ( int i=0; i<columns.length; i++ ) {
			fragment.append( columns[i] );
			fragment.append( getOp() ).append(IbsStringHelper.repeatParamFormat(paramterName));
			if ( i<columns.length-1 ) fragment.append(" AND ");
		}
		if (columns.length>1) fragment.append(')');
		return fragment.toString();
    }
    
    public String toString(){
		return property+" "+getOp()+" " + value.toString();
	}
}
