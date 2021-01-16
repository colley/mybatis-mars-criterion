/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-7 下午7:55
 * History:
 */
package com.mars.kit.criterion.expression;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;


/**
 * ConstExpression.java
 *
 * @author ColleyMa
 * @version 19-5-7 下午7:55
*/
public class ConstExpression implements Criterion {
    private static final long serialVersionUID = -7513648992339912341L;
    private String constValue;

    public ConstExpression(String constValue) {
        this.constValue = constValue;
    }

    public void setProperty(String property) {
        throw new UnsupportedOperationException("don't support");
    }

    public String getProperty() {
        throw new UnsupportedOperationException("don't support");
    }

    @Override
    public String getOp() {
        return ExprOper.CONST.getOp();
    }

    @Override
    public String getOpType() {
        return ExprOper.CONST.name();
    }

    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
        return constValue;
    }
}
