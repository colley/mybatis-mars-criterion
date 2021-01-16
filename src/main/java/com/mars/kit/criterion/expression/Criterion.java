/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.expression;

import java.io.Serializable;

import com.mars.kit.criterion.CriterionQuery;


/**
 *@FileName  Criterion.java
 *@Date  16-5-20 下午1:48
 *@author Colley
 *@version 1.0
 */
public interface Criterion extends Serializable {
    public void setProperty(String property);
    
    public String getProperty();
    
    public String getOp();
    
    public String getOpType();
    
    public String getSqlString(CriterionQuery criterionQuery);
}
