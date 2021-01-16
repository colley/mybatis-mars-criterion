/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.alibaba.fastjson.JSON;
import com.mars.kit.criterion.common.EmptyObject;
import com.mars.kit.criterion.parsing.HBoundSqlBuilder;
import com.mars.kit.criterion.parsing.HsBoundSql;


/**
 *@FileName  BaseJdbcClientDao.java
 *@Date  16-5-25 上午11:08
 *@author Colley
 *@version 1.0
 */
public class MarsTemplate extends JdbcTemplate {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 用于调试SQL语句的执行时间.
     */
    protected void logRunTime(long runTime) {
    	logger.info("Sql  executed, Run time estimated:{} ms",runTime);
    }

    public int updateForCriteria(String sql, Object parameterObject) {
        long startTime = System.currentTimeMillis();
        HsBoundSql hsBoundSql = HBoundSqlBuilder.parseSql(sql, parameterObject);
        String newSql = hsBoundSql.getSql();
        Object[] paramterObjectValues = hsBoundSql.getParamObjectValues();
       
        logger.info("finaly Execution sql==> {}",newSql);
        logger.info("finaly parameter==> {}",JSON.toJSONString(paramterObjectValues));
        
        int result = 0;
        try {
            if (ArrayUtils.isEmpty(paramterObjectValues)) {
                result = super.update(newSql);
            } else {
                result = super.update(newSql, paramterObjectValues);
            }
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(endTime - startTime);
        }

        return result;
    }

    public int insertForCriteria(String sql, Object parameterObject) {
        long startTime = System.currentTimeMillis();
        HsBoundSql hsBoundSql = HBoundSqlBuilder.parseSql(sql, parameterObject);
        String newSql = hsBoundSql.getSql();
        final Object[] paramterObjectValues = hsBoundSql.getParamObjectValues();
        
        logger.info("finaly Execution sql==> {}",newSql);
        logger.info("finaly parameter==> {}",JSON.toJSONString(paramterObjectValues));
        
        try {
        	 return super.update(newSql, paramterObjectValues);
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(endTime - startTime);
        }
    }

    public <T> List<T> queryForCriteria(String sql, Object parameterObject, Class<T> clazzType) {
        long startTime = System.currentTimeMillis();
        HsBoundSql hsBoundSql = HBoundSqlBuilder.parseSql(sql, parameterObject);
        String newSql = hsBoundSql.getSql();
        final Object[] paramterObjectValues = hsBoundSql.getParamObjectValues();
        
        logger.info("finaly Execution sql==> {}",newSql);
        logger.info("finaly parameter==> {}",JSON.toJSONString(paramterObjectValues));
        
        List<T> result = null;

        try {
            if (ArrayUtils.isEmpty(paramterObjectValues)) {
                result = super.queryForList(newSql, clazzType);
            } else {
                result = super.queryForList(newSql, paramterObjectValues, clazzType);
            }
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(endTime - startTime);
        }

        if (result == null) {
            result = EmptyObject.emptyList();
        }

        return result;
    }

    public List<Map<String, Object>> queryForCriteria(String sql, Object parameterObject) {
        long startTime = System.currentTimeMillis();
        HsBoundSql hsBoundSql = HBoundSqlBuilder.parseSql(sql, parameterObject);
        String newSql = hsBoundSql.getSql();
        final Object[] paramterObjectValues = hsBoundSql.getParamObjectValues();
        
        logger.info("finaly Execution sql==> {}",newSql);
        logger.info("finaly parameter==> {}",JSON.toJSONString(paramterObjectValues));

        List<Map<String, Object>> result = null;

        try {
            if (ArrayUtils.isEmpty(paramterObjectValues)) {
                result = super.queryForList(newSql);
            } else {
                result = super.queryForList(newSql, paramterObjectValues);
            }
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(endTime - startTime);
        }

        if (result == null) {
            result = EmptyObject.emptyList();
        }

        return result;
    }

    public <T> T queryByCriteriaForObject(String sql, Object parameterObject, Class<T> requiredType) {
        long startTime = System.currentTimeMillis();
        HsBoundSql hsBoundSql = HBoundSqlBuilder.parseSql(sql, parameterObject);
        String newSql = hsBoundSql.getSql();
        final Object[] paramterObjectValues = hsBoundSql.getParamObjectValues();
      
        logger.info("finaly Execution sql==> {}",newSql);
        logger.info("finaly parameter==> {}",JSON.toJSONString(paramterObjectValues));
        
        T result = null;

        try {
            if (ArrayUtils.isEmpty(paramterObjectValues)) {
                result = (T) super.queryForObject(newSql, requiredType);
            } else {
                result = (T) super.queryForObject(newSql, paramterObjectValues, requiredType);
            }
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(endTime - startTime);
        }

        return result;
    }

    public <T> List<T> queryForCriteria(String sql, Object parameterObject, RowMapper<T> rowMapper) {
        long startTime = System.currentTimeMillis();
        HsBoundSql hsBoundSql = HBoundSqlBuilder.parseSql(sql, parameterObject);
        String newSql = hsBoundSql.getSql();
        final Object[] paramterObjectValues = hsBoundSql.getParamObjectValues();
       
        logger.info("finaly Execution sql==> {}",newSql);
        logger.info("finaly parameter==> {}",JSON.toJSONString(paramterObjectValues));

        List<T> result = null;

        try {
            if (ArrayUtils.isEmpty(paramterObjectValues)) {
                result = super.query(newSql, rowMapper);
            } else {
                result = super.query(newSql, paramterObjectValues, rowMapper);
            }
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(endTime - startTime);
        }

        if (result == null) {
            result = EmptyObject.emptyList();
        }

        return result;
    }
}
