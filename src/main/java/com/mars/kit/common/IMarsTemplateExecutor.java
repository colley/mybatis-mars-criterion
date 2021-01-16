/**
 * Copyright (C), 2011-2017
 * File Name: ExecutionMarsService.java
 * Encoding: UTF-8
 * Date: 17-8-23 下午5:19
 * History:
 */
package com.mars.kit.common;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;

import com.mars.kit.criterion.DetachedHsCriteria;
import com.mars.kit.criterion.DetachedIUCriteria;
import com.mars.kit.criterion.common.HsSqlText;

/**
 * @author mayuanchao
 * @version 1.0  Date: 17-8-23 下午5:19
 */
public interface IMarsTemplateExecutor extends JdbcOperations{
    List<Map<String, Object>> findExecutionByCriteria(HsSqlText hsSqlTxt);

    List<Map<String, Object>> findDataByCriteria(DetachedHsCriteria detachedHsCriteria);

    <T> List<T> findDataByCriteria(DetachedHsCriteria detachedHsCriteria, RowMapper<T> rowMapper);

    <T> List<T> findDataByCriteria(DetachedHsCriteria detachedHsCriteria, ResultSetExtractor<List<T>> resultSetExtractor);

    <T> T queryByCriteriaForObject(DetachedHsCriteria detachedHsCriteria, Class<T> requiredType);

    Integer findByCriteriaForInt(DetachedHsCriteria detachedHsCriteria);

    <T> List<T> findDataByCriteria(DetachedHsCriteria detachedHsCriteria, Class<T> clazz);

    int updateByCriteria(DetachedIUCriteria detachedIUCriteria);

    int insertByCriteria(DetachedIUCriteria detachedIUCriteria);
    
    int deleteByCriteria(DetachedHsCriteria detachedIUCriteria);
    
    //配置事务
	public TransactionTemplate getTransactionTemplate();

}
