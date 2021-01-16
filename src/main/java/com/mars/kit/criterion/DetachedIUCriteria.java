/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mars.kit.criterion.common.ExprOper;
import com.mars.kit.criterion.common.HsSqlText;
import com.mars.kit.criterion.common.UIType;
import com.mars.kit.criterion.expression.Criterion;
import com.mars.kit.criterion.sql.HsCriteria;
import com.mars.kit.criterion.sql.IUColumn;
import com.mars.kit.criterion.sql.IUCriteria;
import com.mars.kit.criterion.sql.InsertCriteria;
import com.mars.kit.criterion.sql.UpdateCriteria;


/**
 *@FileName  DetachedIUCriteria.java
 *@Date  16-5-20 下午5:09
 *@author Colley
 *@version 1.0
 */
public class DetachedIUCriteria implements Serializable {
    private static final long serialVersionUID = 2860555967858668492L;
    protected final static Log logger = LogFactory.getLog(DetachedIUCriteria.class);
    private final IUCriteria criteria;
    private CriterionQuery criterionQuery;

    protected DetachedIUCriteria(boolean isInsert,boolean isReplace) {
    	if(!isInsert) {
    		this.criteria = new UpdateCriteria();
    	}else if(isReplace) {
    		this.criteria = new InsertCriteria(ExprOper.REPLACE);
    	}else {
    		this.criteria = new InsertCriteria(ExprOper.INSERT);
    	}
        this.criterionQuery = new CriterionQueryTranslator();
    }
    
    public static DetachedIUCriteria udpateInstance() {
        return new DetachedIUCriteria(false,false);
    }
    
    
    public static DetachedIUCriteria replaceInstance() {
    	 return new DetachedIUCriteria(true,true);
    }
    
    public static DetachedIUCriteria insertInstance() {
    	return new DetachedIUCriteria(true,false);
   }

    public static DetachedIUCriteria forInstance(UIType type) {
        if (UIType.INSERT.equals(type)) {
            return insertInstance();
        }
        
        if (UIType.REPLACE.equals(type)) {
            return replaceInstance();
        }
        
        if (UIType.UDAPTE.equals(type)) {
            return udpateInstance();
        }
        
        return null;
    }

    public DetachedIUCriteria setTableName(String tableName) {
        criteria.setTableName(tableName);
        return this;
    }

    public DetachedIUCriteria addColumnName(IUColumn iuColumn) {
        criteria.addIUColumn(iuColumn);
        return this;
    }

    public DetachedIUCriteria addColumnName(IUColumn[] iuColumns) {
        if (ArrayUtils.isNotEmpty(iuColumns)) {
            for (IUColumn alias : iuColumns) {
                criteria.addIUColumn(alias);
            }
        }

        return this;
    }

    public DetachedIUCriteria addColumnName(String[] columnNames) {
        criteria.addColumn(columnNames);
        return this;
    }

    public DetachedIUCriteria addColumnName(List<String> columnNames) {
        criteria.addColumn(columnNames);
        return this;
    }

    public DetachedIUCriteria addFromClause(HsCriteria fromCriteria) {
        criteria.addFromClause(fromCriteria);
        return this;
    }

    public DetachedIUCriteria addFromClause(DetachedHsCriteria fromCriteria) {
        criteria.addFromClause(fromCriteria.getCriteria());
        return this;
    }

    public DetachedIUCriteria add(Criterion criterion) {
        criteria.add(criterion);
        return this;
    }

    public DetachedIUCriteria add(List<Criterion> criterions) {
        if (CollectionUtils.isNotEmpty(criterions)) {
            for (Criterion criterion : criterions) {
                if (criterion != null) {
                    criteria.add(criterion);
                }
            }
        }

        return this;
    }

    public HsSqlText getHsSqlText() {
        String newSql = criteria.getSqlString(criterionQuery);
        Object parameter = criterionQuery.getParameter();
        return new HsSqlText(newSql, parameter);
    }

    public IUCriteria getCriteria() {
        return criteria;
    }
    
    
    public String getDynamicUpdateStatementName() {
        return "criteria.Ibatis_UpdateByCriteriaUpdate";
    }
    
    public String getDynamicInsertStatementName() {
        return "criteria.Ibatis_InsertByCriteriaInsert";
    }
    
}
