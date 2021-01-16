/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;

import com.mars.kit.criterion.common.IbsStringHelper;


/**
 *@FileName  CriterionQueryTranslator.java
 *@Date  16-5-25 上午11:51
 *@author Colley
 *@version 1.0
 */
public class CriterionQueryTranslator implements CriterionQuery {
	private static final long serialVersionUID = -4425146886703374522L;
	private ReentrantLock lock = new ReentrantLock();
    private Map<String, Object> parameterMap = new HashMap<String, Object>();

    @Override
    public Object getParameter() {
        return parameterMap;
    }

    @Override
    public String addParameter(String parameterName, Object parameterValue) {
        lock.lock();
        try {
        	if(StringUtils.isNotEmpty(parameterName)){
        		String parameterN = IbsStringHelper.replace(parameterName);
        		if(containParameter(parameterN)){
        			parameterN = parameterN + getMaxIndex();
        		}
        		parameterMap.put(parameterN, parameterValue);
        		return parameterN;
        	}
        } finally {
            lock.unlock();
        }
        return parameterName;
    }


	@Override
	public boolean containParameter(String parameterName) {
		if(StringUtils.isNotEmpty(parameterName)){
    		parameterName = IbsStringHelper.replace(parameterName);
    		return parameterMap.containsKey(parameterName);
    	}
		return false;
	}

	@Override
	public int getMaxIndex() {
		return parameterMap.size()+1;
	}
}
