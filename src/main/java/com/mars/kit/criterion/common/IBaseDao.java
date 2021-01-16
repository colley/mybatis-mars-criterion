/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.common;

import java.io.Serializable;
import java.util.List;

import com.mars.kit.criterion.DetachedHsCriteria;

/**
 * The Interface ICommonBaseDao.
 * 
 * @FileName ICommonBaseDao.java
 * @param <V>
 *            the value type
 * @author Colley
 * @version 1.0
 * @Date 16-6-6 上午11:53
 */
public interface IBaseDao<V extends Serializable> {

	/**
	 * 动态查询 参加 Ibatis_QueryByCriteriaSelect
	 * queryByCriteriaSelect
	 * @param criteria
	 * @return
	 * @return List<T>   
	 * @author mayuanchao
	 * @date 2016年11月28日
	 */
	<T> List<T> queryByCriteriaSelect(DetachedHsCriteria criteria);

	/**
	 * Find by criteria.
	 * 
	 * @param detachedCriteria
	 *            the detached criteria
	 * @return the list
	 * @author colley(mayc@yihaodian.com)
	 * @version Revision: 1.00
	 * @title: findByCriteria
	 * @date: 2016-6-20
	 */
	List<V> findByCriteria(DetachedHsCriteria detachedCriteria);

	/**
	 * Update.
	 * 
	 * @param entity
	 *            the entity
	 * @author colley(mayc@yihaodian.com)
	 * @version Revision: 1.00
	 * @title: update
	 * @date: 2016-6-20
	 */
	V update(V entity);

	/**
	 * Save.
	 * 
	 * @param entity
	 *            the entity
	 * @author colley(mayc@yihaodian.com)
	 * @version Revision: 1.00
	 * @title: save
	 * @date: 2016-6-20
	 */
	V save(V entity);

	/**
	 * Save or update.
	 * 
	 * @param entity
	 *            the entity
	 * @author colley(mayc@yihaodian.com)
	 * @version Revision: 1.00
	 * @title: saveOrUpdate
	 * @date: 2016-6-20
	 */
	V saveOrUpdate(V entity);

}
