/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.sql;

import com.mars.kit.criterion.CriterionQuery;
import com.mars.kit.criterion.expression.Criterion;


/**
 *@FileName  JoinCriteria.java
 *@Date  16-5-24 上午10:07
 *@author Colley
 *@version 1.0
 */
public interface JoinCriteria {
    public String getOp();

    public String getTableName();

    public Criterion[] getCriteria();

    public Criterion getOnCriteria();

    public void add(Criterion criterion);
    
    public String getSqlString(CriterionQuery criterionQuery);
    
    public String getOpType();


}
