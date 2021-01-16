/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.sql;

import java.util.List;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.expression.Criterion;


/**
 *@FileName  IUCriteria.java
 *@Date  16-6-12 上午10:29
 *@author Colley
 *@version 1.0
 */
public interface IUCriteria extends java.io.Serializable {
    public Criterion[] getCriteria();

    public IUCriteria add(Criterion criterion);

    public IUCriteria addIUColumn(IUColumn column);

    public IUCriteria addFromClause(HsCriteria fromClause);

    public HsCriteria getFromClause();

    public IUColumn[] getColumnNames();
    
    public IUCriteria addColumn(String[] columnNames);

    public IUCriteria addColumn(List<String> columnNames);

    public String getSqlString(CriterionQuery criterionQuery);

    public String getOp();
    
    public String getOpType();
    
    public String getTableName();
    
    public IUCriteria setTableName(String tableName);
}
