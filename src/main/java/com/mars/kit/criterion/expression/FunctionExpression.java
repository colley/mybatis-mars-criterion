/**
 * Copyright NewHeight Co.,Ltd. (C)2016
 * File Name: FunctionExpression.java
 * Encoding: UTF-8
 * Date: 16-11-23 下午12:43
 * History:
 */
package com.mars.kit.criterion.expression;

import java.text.MessageFormat;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.common.IbsStringHelper;


/**
 * @author mayuanchao
 * @version 1.0  Date: 16-11-23 下午12:43
 */
public class FunctionExpression implements Criterion {
    private static final long serialVersionUID = 4136372599913962873L;
    private final FunctionBody funcEntry;
    private final ExprOper exprOper;
    private final Object value;
    private final FunctionBody secondFuncEntry;

    public FunctionExpression(FunctionBody funcEntry, Object value, ExprOper exprOper) {
        this(funcEntry, value, null, exprOper);
    }

    public FunctionExpression(FunctionBody funcEntry, Object value, FunctionBody secondFuncEntry, ExprOper exprOper) {
        this.funcEntry = funcEntry;
        this.value = value;
        this.secondFuncEntry = secondFuncEntry;
        this.exprOper = exprOper;
    }

    public void setProperty(String property) {
        throw new UnsupportedOperationException("don't support");
    }

    public String getProperty() {
        throw new UnsupportedOperationException("don't support");
    }

    @Override
    public String getOp() {
        return exprOper.getOp();
    }

    @Override
    public String getOpType() {
        return exprOper.name();
    }

    /**
     *特殊情况 函数中带参数，用?代替
     */
    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
        StringBuffer fragment = new StringBuffer();
        fragment.append(funcEntry.getSqlString(criterionQuery));
        fragment.append(getOp());

        if (secondFuncEntry == null) {
            String paramterName = criterionQuery.addParameter("functionMode", value);
            fragment.append(IbsStringHelper.repeatParamFormat(paramterName));
        } else {
            fragment.append(secondFuncEntry.getSqlString(criterionQuery));
        }

        return fragment.toString();
    }

    public String toString() {
        StringBuffer fragment = new StringBuffer();
        fragment.append(funcEntry.toString());
        fragment.append(getOp());

        if (secondFuncEntry == null) {
            fragment.append(value);
        } else {
            fragment.append(secondFuncEntry.toString());
        }

        return fragment.toString();
    }

    /**
     * 单个函数实现
     *
     * @author mayuanchao
     * @version 1.0  Date: 2016年11月23日
     */
    public static class FunctionBody implements Criterion {
        private static final long serialVersionUID = 4136372599913962873L;
        private final String functionName;
        private final String functionValue;
        private final Object[] values;
        private final ExprOper exprOper;

        public FunctionBody(String functionName, String functionValue) {
            this(functionName, functionValue, new Object[0]);
        }

        public FunctionBody(String functionName, String functionValue, Object[] values) {
            this.functionName = functionName;
            this.functionValue = functionValue;
            this.values = values;
            this.exprOper = ExprOper.FUNC;
        }
        
        
        public static FunctionBody neFunc(String functionName, String functionValue, Object[] values) {
            return new FunctionBody(functionName, functionValue, values);
        }

        public static FunctionBody neFunc(String functionName, String functionValue) {
            return new FunctionBody(functionName, functionValue);
        }

        public void setProperty(String property) {
            throw new UnsupportedOperationException("don't support");
        }

        public String getProperty() {
            throw new UnsupportedOperationException("don't support");
        }

        @Override
        public String getOp() {
            return exprOper.getOp();
        }

        @Override
        public String getOpType() {
            return exprOper.name();
        }

        /**
         *特殊情况 函数中带参数，用?代替
         */
        @Override
        public String getSqlString(CriterionQuery criterionQuery) {
            String[] propertyNames = new String[values.length];

            for (int i = 0; i < values.length; i++) {
                String paramterName = criterionQuery.addParameter("funcParam", values[i]);
                propertyNames[i] = IbsStringHelper.repeatParamFormat(paramterName);
            }

            StringBuffer fragment = new StringBuffer();
            fragment.append(functionName).append("(");
            fragment.append(formatFunctionValue(propertyNames));
            fragment.append(")");

            return fragment.toString();
        }

        public String toString() {
            return functionName + "(" + functionValue + ")";
        }

        private String formatFunctionValue(Object[] propertyNames) {
            StringBuffer newPropertyStr = new StringBuffer(functionValue.length());
            int index = 0;

            for (int i = 0; i < functionValue.length(); i++) {
                char ch = functionValue.charAt(i);

                if (functionValue.charAt(i) == '?') {
                    newPropertyStr.append("{").append(index).append("}");
                    index++;
                } else {
                    newPropertyStr.append(ch);
                }
            }

            String nwFuncValue = MessageFormat.format(newPropertyStr.toString(), propertyNames);

            return nwFuncValue;
        }
    }
}
