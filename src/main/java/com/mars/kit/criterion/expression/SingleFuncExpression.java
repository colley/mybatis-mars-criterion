/**
 * Copyright NewHeight Co.,Ltd. (C)2016
 * File Name: SingleFuncExpression.java
 * Encoding: UTF-8
 * Date: 16-12-7 上午9:33
 * History:
 */
package com.mars.kit.criterion.expression;

import org.apache.commons.lang.StringUtils;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.CriterionQueryTranslator;
import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.common.IbsStringHelper;


/**
 * @author mayuanchao
 * @version 1.0  Date: 16-12-7 上午9:33
 */
public class SingleFuncExpression implements Criterion {
    private static final long serialVersionUID = 4961884669048774333L;
    private String property;
    private final Object value;
    private final String funcName;
    private final ExprOper exprOper;
    private final boolean left;

    public SingleFuncExpression(String funcName,String property,Object value,boolean isLeft){
        this.left = isLeft;
        this.funcName = funcName;
        this.property = property;
        this.value = value;
        this.exprOper = ExprOper.S_FUNC;
    }
    
    public String getProperty() {
        return StringUtils.trim(property);
    }

    public Object getValue() {
        return value;
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

    public String getFuncName() {
        return funcName;
    }

    public ExprOper getExprOper() {
        return exprOper;
    }

    public boolean isLeft() {
        return left;
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
        String paramterName = criterionQuery.addParameter(property, value);
        StringBuffer fragment = new StringBuffer();
        fragment.append(funcName).append("(");
        if(left){
            fragment.append(getProperty()).append(",");
            fragment.append(IbsStringHelper.repeatParamFormat(paramterName)).append(")");
        }else{
            fragment.append(IbsStringHelper.repeatParamFormat(paramterName)).append(",");
            fragment.append(getProperty()).append(")");
        }
        return fragment.toString();
    }

    public String toString() {
        if(left){
            return funcName + "("+property +",'"+ value.toString()+"')";
        }else{
            return funcName + "('"+value.toString() +"',"+property+")";
        }
    }
    
    public static void main(String[] args) {
        
        CriterionQuery criterionQuery = new  CriterionQueryTranslator();
        SingleFuncExpression func = new SingleFuncExpression("FIND_IN_SET", "task_list", "2", true);
        System.out.println(func.getSqlString(criterionQuery));
    }
}
