/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.common;

import java.util.List;

import com.mars.kit.criterion.DetachedHsCriteria;


/**
 * The Interface IBaseService.
 *
 * @FileName IBaseService.java
 * @param <T>
 *            the generic type
 * @author Colley
 * @version 1.0
 * @Date 16-6-6 下午6:13
 */
public interface IBaseService<T> {
    
    /**
	 * Find by criteria.
	 *
	 * @param detachedCriteria
	 *            the detached criteria
	 * @return the list
	 * @author colley(mayc@yihaodian.com)
	 * @version Revision: 1.00
	 * @title: findByCriteria
	 * @date: 2016-6-21
	 */
    List<T> findByCriteria(DetachedHsCriteria detachedCriteria);
}
