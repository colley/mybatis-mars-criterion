/**
 * Copyright NewHeight Co.,Ltd. (C)2016
 * File Name: CaseExpression.java
 * Encoding: UTF-8
 * Date: 16-11-23 下午1:58
 * History:
 */
package com.mars.kit.criterion.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.common.IbsStringHelper;


/**
 * @author mayuanchao
 * @version 1.0  Date: 16-11-23 下午1:58
 */
public class CaseExpression implements Criterion {
    private static final long serialVersionUID = -2416480424807523198L;
    private final List<WhenExpression> criteria = new ArrayList<WhenExpression>();
    private final ExprOper exprOper;
    private final Object value;

    public CaseExpression(WhenExpression[] criteria, Object value, ExprOper exprOper) {
        if (criteria != null) {
            this.criteria.addAll(Arrays.asList(criteria));
        }

        this.value = value;
        this.exprOper = exprOper;
    }

    public CaseExpression add(WhenExpression criterion) {
        if (criterion != null) {
            criteria.add(criterion);
        }

        return this;
    }

    public Criterion[] getCriteria() {
        if (CollectionUtils.isNotEmpty(criteria)) {
            return criteria.toArray(new Criterion[criteria.size()]);
        }

        return new Criterion[0];
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

    /**
     *(case when a=1 then a when a=2 then b end)='值'
     */
    @Override
    public String getSqlString(CriterionQuery criterionQuery) {
        String paramterName = criterionQuery.addParameter("caseExpress", value);
        StringBuffer fragment = new StringBuffer("(CASE ");

        //设置when then
        Criterion[] criterias = getCriteria();

        for (Criterion criteria : criterias) {
            fragment.append(criteria.getSqlString(criterionQuery));
        }

        //设置end
        fragment.append(" END)");
        fragment.append(getOp());
        fragment.append(IbsStringHelper.repeatParamFormat(paramterName));

        return fragment.toString();
    }

    public static class WhenExpression implements Criterion {
        private static final long serialVersionUID = -2416480424807523198L;
        private final List<Criterion> criteria = new ArrayList<Criterion>();
        private final ExprOper exprOper;
        private final Object value;
        private final String propertyName;

        public WhenExpression(Criterion[] criteria, Object value, boolean isProperty) {
            if (criteria != null) {
                this.criteria.addAll(Arrays.asList(criteria));
            }

            if (isProperty) {
                this.propertyName = value.toString();
                this.value = null;
                this.exprOper = ExprOper.WHEN_PROPER;
            } else {
                this.propertyName = null;
                this.value = value;
                this.exprOper = ExprOper.WHEN;
            }
        }

        public WhenExpression(Criterion criteria, Object value, boolean isProperty) {
            if (criteria != null) {
                this.criteria.add(criteria);
            }

            if (isProperty) {
                this.propertyName = value.toString();
                this.value = null;
                this.exprOper = ExprOper.WHEN_PROPER;
            } else {
                this.propertyName = null;
                this.value = value;
                this.exprOper = ExprOper.WHEN;
            }
        }

        public static WhenExpression whenProperty(Criterion[] criteria, Object value) {
            return new WhenExpression(criteria, value, true);
        }

        public static WhenExpression whenProperty(Criterion criteria, Object value) {
            return new WhenExpression(criteria, value, true);
        }

        public static WhenExpression whenValue(Criterion[] criteria, Object value) {
            return new WhenExpression(criteria, value, false);
        }

        public static WhenExpression whenValue(Criterion criteria, Object value) {
            return new WhenExpression(criteria, value, false);
        }

        public Criterion[] getCriteria() {
            if (CollectionUtils.isNotEmpty(criteria)) {
                return criteria.toArray(new Criterion[criteria.size()]);
            }

            return new Criterion[0];
        }

        public WhenExpression add(Criterion criterion) {
            if (criterion != null) {
                criteria.add(criterion);
            }

            return this;
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

        @Override
        public String getSqlString(CriterionQuery criterionQuery) {
            StringBuffer fragment = new StringBuffer();
            fragment.append(getOp());

            Criterion[] criterias = getCriteria();

            if (criterias.length > 1) {
                fragment.append("(");
            }
            Iterator<Criterion> iter = criteria.iterator();
            while ( iter.hasNext() ) {
                fragment.append(iter.next().getSqlString(criterionQuery) );
                if ( iter.hasNext() ) fragment.append(" AND ");
            }

            if (criterias.length > 1) {
                fragment.append(")");
            }

            fragment.append(" THEN ");

            if (ExprOper.WHEN_PROPER.equals(this.exprOper)) {
                fragment.append(propertyName);
            } else {
                String paramterName = criterionQuery.addParameter("wexpress", value);
                fragment.append(IbsStringHelper.repeatParamFormat(paramterName));
            }

            return fragment.toString();
        }
    }
}
