/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;


/**
 *@FileName  JunctionExpression.java
 *@Date  16-5-24 下午4:21
 *@author Colley
 *@version 1.0
 */
public class JunctionExpression implements Criterion {
    private static final long serialVersionUID = -7645645903059822294L;
    private final List<Criterion> criteria = new ArrayList<Criterion>();
    private final ExprOper exprOper;

    public Criterion[] getCriteria() {
        if (CollectionUtils.isNotEmpty(criteria)) {
            return criteria.toArray(new Criterion[criteria.size()]);
        }

        return null;
    }

    public JunctionExpression(ExprOper exprOper) {
       this.exprOper = exprOper;
    }

    public JunctionExpression add(Criterion criterion) {
        if (criterion != null) {
            criteria.add(criterion);
        }

        return this;
    }
    
    public JunctionExpression add(Criterion[] criterion) {
        if (criterion != null) {
            criteria.addAll(Arrays.asList(criterion));
        }

        return this;
    }

    public void setProperty(String property) {
        throw new UnsupportedOperationException("don't support");
    }

    public String getProperty() {
        throw new UnsupportedOperationException("don't support");
    }

    public String getOp() {
        return exprOper.getOp();
    }

    public String getOpType() {
        return exprOper.name();
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery){
    	if ( criteria.size()==0 ) return "1=1";
		StringBuffer buffer = new StringBuffer()
			.append('(');
		Iterator<Criterion> iter = criteria.iterator();
		while ( iter.hasNext() ) {
			buffer.append( ( (Criterion) iter.next() ).getSqlString(criterionQuery) );
			if ( iter.hasNext() ) buffer.append(' ').append(getOp()).append(' ');
		}
		return buffer.append(')').toString();
    }
}
