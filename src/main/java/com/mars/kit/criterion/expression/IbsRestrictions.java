/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.expression;

import java.util.Collection;
import java.util.List;

import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.common.IbsMatchMode;
import com.mars.kit.criterion.expression.BetweenExpression;
import com.mars.kit.criterion.expression.CaseExpression;
import com.mars.kit.criterion.expression.Criterion;
import com.mars.kit.criterion.expression.FunctionExpression;
import com.mars.kit.criterion.expression.FuzzyExpression;
import com.mars.kit.criterion.expression.InExpression;
import com.mars.kit.criterion.expression.JunctionExpression;
import com.mars.kit.criterion.expression.LikeExpression;
import com.mars.kit.criterion.expression.NotExpression;
import com.mars.kit.criterion.expression.NullExpression;
import com.mars.kit.criterion.expression.PagingExpression;
import com.mars.kit.criterion.expression.PropertyExpression;
import com.mars.kit.criterion.expression.SimpleExpression;
import com.mars.kit.criterion.expression.SingleFuncExpression;
import com.mars.kit.criterion.expression.SubSelectExpression;
import com.mars.kit.criterion.expression.CaseExpression.WhenExpression;
import com.mars.kit.criterion.expression.FunctionExpression.FunctionBody;
import com.mars.kit.criterion.sql.HsCriteria;


/**
 *@FileName  IbsRestrictions.java
 *@Date  16-5-20 上午11:21
 *@author Colley
 *@version 1.0
 */
public final class IbsRestrictions {
    private IbsRestrictions() {
        //cannot be instantiated
    }
    

    public static Criterion leftFunc(String funcName,String property,Object value){
        return new SingleFuncExpression(funcName, property, value, true);
    }
    
    public static Criterion rightFunc(String funcName,String property,Object value){
        return new SingleFuncExpression(funcName, property, value, false);
    }
    
    public static FuzzyExpression fuzzy(String propertyName, Object value,String op) {
        return new FuzzyExpression(propertyName, value,op);
    }
    
    
    public static SimpleExpression eq(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, ExprOper.eq);
    }
    
    /**
     * (case when then end)=value
     * eq
     * @param when
     * @param value
     * @return
     * @return CaseExpression   
     * @author mayuanchao
     * @date 2016年11月23日
     */
    public static CaseExpression eq(WhenExpression[] when, Object value) {
        return new CaseExpression(when, value, ExprOper.eq);
    }
    
    /**
     * (case when then end)=value
     * eq
     * @param when
     * @param value
     * @return
     * @return CaseExpression   
     * @author mayuanchao
     * @date 2016年11月23日
     */
    public static CaseExpression eq(List<WhenExpression> when, Object value) {
        return new CaseExpression(when.toArray(new WhenExpression[when.size()]), value, ExprOper.eq);
    }
   
    /**
     * 函数表达式
     * eq
     * @param func
     * @param value
     * @return
     * @return FunctionExpression   
     * @author mayuanchao
     * @date 2016年11月23日
     */
    public static FunctionExpression eq(FunctionBody func, Object value) {
        return new FunctionExpression(func, value, ExprOper.eq);
    }
    
    /**
     * 函数表达式
     * eq
     * @param oneFunc
     * @param senFunc
     * @return
     * @return FunctionExpression   
     * @author mayuanchao
     * @date 2016年11月23日
     */
    public static FunctionExpression eq(FunctionBody oneFunc, FunctionBody senFunc) {
        return new FunctionExpression(oneFunc, null, senFunc, ExprOper.eq);
    }

    /**
      * Apply a "not equal" constraint to the named property
      * @param propertyName
      * @param value
      * @return Criterion
      */
    public static SimpleExpression ne(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, ExprOper.ne);
    }

    /**
      * Apply a "like" constraint to the named property
      * @param propertyName
      * @param value
      * @return Criterion
      */
    public static Criterion like(String propertyName, Object value) {
        return new LikeExpression(propertyName, value);
    }

    /**
      * Apply a "like" constraint to the named property
      * @param propertyName
      * @param value
      * @return Criterion
      */
    public static Criterion like(String propertyName, String value, IbsMatchMode matchMode) {
        return new LikeExpression(propertyName, value, matchMode);
    }
    
    public static Criterion notLike(String propertyName, String value, IbsMatchMode matchMode) {
        return new LikeExpression(propertyName, value,ExprOper.notLike,matchMode);
    }

    /**
      * A case-insensitive "like", similar to Postgres <tt>ilike</tt>
      * operator
      *
      * @param propertyName
      * @param value
      * @return Criterion
      */
    public static Criterion ilike(String propertyName, String value, IbsMatchMode matchMode) {
        return new LikeExpression(propertyName, value, true, matchMode);
    }

    /**
      * A case-insensitive "like", similar to Postgres <tt>ilike</tt>
      * operator
      *
      * @param propertyName
      * @param value
      * @return Criterion
      */
    public static Criterion ilike(String propertyName, Object value) {
        return new LikeExpression(propertyName, value, true, IbsMatchMode.EXACT);
    }

    /**
      * Apply a "greater than" constraint to the named property
      * @param propertyName
      * @param value
      * @return Criterion
      */
    public static SimpleExpression gt(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, ExprOper.gt);
    }

    /**
      * Apply a "less than" constraint to the named property
      * @param propertyName
      * @param value
      * @return Criterion
      */
    public static SimpleExpression lt(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, ExprOper.lt);
    }

    /**
      * Apply a "less than or equal" constraint to the named property
      * @param propertyName
      * @param value
      * @return Criterion
      */
    public static SimpleExpression le(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, ExprOper.le);
    }

    /**
      * Apply a "greater than or equal" constraint to the named property
      * @param propertyName
      * @param value
      * @return Criterion
      */
    public static SimpleExpression ge(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, ExprOper.ge);
    }

    /**
      * Apply a "between" constraint to the named property
      * @param propertyName
      * @param lo value
      * @param hi value
      * @return Criterion
      */
    public static Criterion between(String propertyName, Object lo, Object hi) {
        return new BetweenExpression(propertyName, lo, hi);
    }
    
    public static Criterion notBetween(String propertyName, Object lo, Object hi) {
        return new BetweenExpression(propertyName, lo, hi,ExprOper.notBetween);
    }

    /**
      * Apply an "in" constraint to the named property
      * @param propertyName
      * @param values
      * @return Criterion
      */
    public static Criterion in(String propertyName, Object[] values) {
        return new InExpression(propertyName, values, ExprOper.in);
    }
    
    
    /**
     * 子查询 a in (select Id from XXX)
     * in
     * @param propertyName
     * @param subcriteria
     * @return
     * @return Criterion   
     * @author mayuanchao
     * @date 2016年12月1日
     */
    public static Criterion in(String propertyName, HsCriteria subcriteria) {
        return new SubSelectExpression(propertyName, subcriteria, ExprOper.in);
    }
    
    public static Criterion notIn(String propertyName, HsCriteria subcriteria) {
        return new SubSelectExpression(propertyName, subcriteria, ExprOper.notin);
    }
    

    /**
      * Apply an "in" constraint to the named property
      * @param propertyName
      * @param values
      * @return Criterion
      */
    @SuppressWarnings("rawtypes")
    public static Criterion in(String propertyName, Collection values) {
        return new InExpression(propertyName, values.toArray(), ExprOper.in);
    }
    
    /**
     * Apply an "not In" constraint to the named property
     * @param propertyName
     * @param values
     * @return Criterion
     */
   public static Criterion notIn(String propertyName, Object[] values) {
       return new InExpression(propertyName, values, ExprOper.notin);
   }

   /**
     * Apply an "not in" constraint to the named property
     * @param propertyName
     * @param values
     * @return Criterion
     */
   @SuppressWarnings("rawtypes")
   public static Criterion notIn(String propertyName, Collection values) {
       return new InExpression(propertyName, values.toArray(), ExprOper.notin);
   }

    /**
      * Apply an "is null" constraint to the named property
      * @return Criterion
      */
    public static Criterion isNull(String propertyName) {
        return new NullExpression(propertyName, ExprOper.isNull);
    }

    /**
      * Apply an "is not null" constraint to the named property
      * @return Criterion
      */
    public static Criterion isNotNull(String propertyName) {
        return new NullExpression(propertyName, ExprOper.isNotNull);
    }
    
    
    /**
	 * Apply an "equal" constraint to two properties
	 */
	public static PropertyExpression eqProperty(String propertyName, String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName, ExprOper.eq);
	}
	/**
	 * Apply a "not equal" constraint to two properties
	 */
	public static PropertyExpression neProperty(String propertyName, String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName, ExprOper.ne);
	}
	/**
	 * Apply a "less than" constraint to two properties
	 */
	public static PropertyExpression ltProperty(String propertyName, String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName,ExprOper.lt);
	}
	/**
	 * Apply a "less than or equal" constraint to two properties
	 */
	public static PropertyExpression leProperty(String propertyName, String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName, ExprOper.le);
	}
	/**
	 * Apply a "greater than" constraint to two properties
	 */
	public static PropertyExpression gtProperty(String propertyName, String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName, ExprOper.gt);
	}
	/**
	 * Apply a "greater than or equal" constraint to two properties
	 */
	public static PropertyExpression geProperty(String propertyName, String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName, ExprOper.ge);
	}
	
	public static PropertyExpression betweenProperty(String propertyName, String firstPropertyName,String secondParopertyName) {
		return new PropertyExpression(propertyName, firstPropertyName,secondParopertyName, ExprOper.between);
	}
	
	
	
	public static Criterion asConst(String constValue) {
		return new ConstExpression(constValue);
	}
	
	/**
	 * Group expressions together in a single conjunction (A and B and C...)
	 *
	 * @return JunctionExpression
	 */
	public static JunctionExpression conjunction() {
		return new JunctionExpression(ExprOper.AND_JUNC);
	}

	/**
	 * Group expressions together in a single disjunction (A or B or C...)
	 *
	 * @return JunctionExpression
	 */
	public static JunctionExpression disjunction() {
		return new JunctionExpression(ExprOper.OR_JUNC);
	}
	
	
	public static Criterion not(Criterion expression) {
		return new NotExpression(expression);
	}
	
	public static PagingExpression limit(int startPos, int pageSize) {
		return new PagingExpression(startPos, pageSize, ExprOper.limit);
	}
}
