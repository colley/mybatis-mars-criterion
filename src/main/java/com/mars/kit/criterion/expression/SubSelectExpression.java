/**
 * Copyright NewHeight Co.,Ltd. (C)2016
 * File Name: SubSelectExpression.java
 * Encoding: UTF-8
 * Date: 16-12-1 下午5:31
 * History:
 */
package com.mars.kit.criterion.expression;

import org.apache.commons.lang.StringUtils;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.sql.HsCriteria;


/**
 * @author mayuanchao
 * @version 1.0  Date: 16-12-1 下午5:31
 */
public class SubSelectExpression implements Criterion {
    private static final long serialVersionUID = 4386501295732383619L;
    private String property;
    private final HsCriteria subCriteria;
    private final ExprOper exprOper;

    public SubSelectExpression(String property, HsCriteria subCriteria, ExprOper exprOper) {
        this.property = property;
        this.subCriteria = subCriteria;
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
        StringBuffer sql = new StringBuffer();
        sql.append(getProperty()).append(getOp()).append(" (");
        sql.append(subCriteria.getSqlString(criterionQuery));
        sql.append(")");

        return sql.toString();
    }

    public String toString() {
        return property + getOp() + subCriteria.toString();
    }
}
