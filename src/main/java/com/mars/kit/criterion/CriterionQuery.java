/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion;

import java.io.Serializable;

/**
 *@FileName  CriterionQuery.java
 *@Date  16-5-26 下午5:38
 *@author Colley
 *@version 1.0
 */
public interface CriterionQuery extends Serializable{
    public Object getParameter();

    public String addParameter(String parameterName, Object parameterValue);

    public boolean containParameter(String parameterName);
    
    public int getMaxIndex();
}
