/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.sql;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.expression.Criterion;


/**
 *@FileName  GroupCriteria.java
 *@Date  16-5-24 上午11:28
 *@author Colley
 *@version 1.0
 */
public interface GroupCriteria {
    Criterion[] getHaving();

    GroupCriteria add(Criterion criterion);

    GroupCriteria addColumn(String columnName);

    String[] getGroupByColumn();
    
    public String getSqlString(CriterionQuery criterionQuery);
}
