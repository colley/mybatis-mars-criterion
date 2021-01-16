/**
 * Copyright (C), 2011-2017
 * File Name: ExecutionMarsServiceImpl.java
 * Encoding: UTF-8
 * Date: 17-8-23 下午5:21
 * History:
 */
package com.mars.kit.common;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.mars.kit.criterion.DetachedHsCriteria;
import com.mars.kit.criterion.DetachedIUCriteria;
import com.mars.kit.criterion.common.EmptyObject;
import com.mars.kit.criterion.common.HsSqlText;
import com.mars.kit.criterion.parsing.HBoundSqlBuilder;
import com.mars.kit.criterion.parsing.HsBoundSql;

import org.apache.commons.lang.ArrayUtils;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * ExecutionMarsServiceImpl.java
 *
 * @author ColleyMa
 * @version 19-5-7 下午3:59
*/
public class MarsTemplateExecutor extends MarsTemplate implements IMarsTemplateExecutor {
    /** spring事务*/
    private TransactionTemplate transactionTemplate;

    /** 标记是否读库  读库不需要增加事务*/
    private boolean readDatasource;

    @Override
    public List<Map<String, Object>> findExecutionByCriteria(HsSqlText hsSqlTxt) {
        List<Map<String, Object>> result = super.queryForCriteria(hsSqlTxt.getNewSql(), hsSqlTxt.getParameter());

        if (result == null) {
            result = EmptyObject.emptyList();
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> findDataByCriteria(DetachedHsCriteria detachedHsCriteria) {
        HsSqlText hsSqlTxt = detachedHsCriteria.getHsSqlText();

        return findExecutionByCriteria(hsSqlTxt);
    }

    @Override
    public <T> List<T> findDataByCriteria(DetachedHsCriteria detachedHsCriteria, Class<T> clazz) {
        HsSqlText hsSqlTxt = detachedHsCriteria.getHsSqlText();
        List<T> result = super.queryForCriteria(hsSqlTxt.getNewSql(), hsSqlTxt.getParameter(), clazz);

        if (result == null) {
            result = EmptyObject.emptyList();
        }

        return result;
    }

    @Override
    public <T> List<T> findDataByCriteria(DetachedHsCriteria detachedHsCriteria, RowMapper<T> rowMapper) {
        HsSqlText hsSqlTxt = detachedHsCriteria.getHsSqlText();

        return super.queryForCriteria(hsSqlTxt.getNewSql(), hsSqlTxt.getParameter(), rowMapper);
    }

    @Override
    public <T> List<T> findDataByCriteria(DetachedHsCriteria detachedHsCriteria, ResultSetExtractor<List<T>> resultSetExtractor) {
        HsSqlText hsSqlTxt = detachedHsCriteria.getHsSqlText();
        String sql = hsSqlTxt.getNewSql();
        Object parameterObject = hsSqlTxt.getParameter();

        long startTime = System.currentTimeMillis();

        HsBoundSql hsBoundSql = HBoundSqlBuilder.parseSql(sql, parameterObject);
        String newSql = hsBoundSql.getSql();
        final Object[] paramterObjectValues = hsBoundSql.getParamObjectValues();

        logger.info("finaly Execution sql==> {}", newSql);
        logger.info("finaly parameter==> {}", JSON.toJSONString(paramterObjectValues));

        List<T> result = null;

        try {
            if (ArrayUtils.isEmpty(paramterObjectValues)) {
                result = super.query(newSql, resultSetExtractor);
            } else {
                result = super.query(newSql, paramterObjectValues, resultSetExtractor);
            }
        } finally {
            long endTime = System.currentTimeMillis();
            super.logRunTime(endTime - startTime);
        }

        if (result == null) {
            result = EmptyObject.emptyList();
        }

        return result;
    }

    @Override
    public int updateByCriteria(DetachedIUCriteria detachedIUCriteria) {
        HsSqlText hsSqlTxt = detachedIUCriteria.getHsSqlText();

        return super.updateForCriteria(hsSqlTxt.getNewSql(), hsSqlTxt.getParameter());
    }

    @Override
    public int insertByCriteria(DetachedIUCriteria detachedIUCriteria) {
        HsSqlText hsSqlTxt = detachedIUCriteria.getHsSqlText();

        return super.insertForCriteria(hsSqlTxt.getNewSql(), hsSqlTxt.getParameter());
    }

    @Override
    public int[] batchUpdate(final String[] sql) {
        return super.batchUpdate(sql);
    }

    @Override
    public <T> T queryByCriteriaForObject(DetachedHsCriteria detachedHsCriteria, Class<T> requiredType) {
        HsSqlText hsSqlTxt = detachedHsCriteria.getHsSqlText();

        return super.queryByCriteriaForObject(hsSqlTxt.getNewSql(), hsSqlTxt.getParameter(), requiredType);
    }

    @Override
    public Integer findByCriteriaForInt(DetachedHsCriteria detachedHsCriteria) {
        HsSqlText hsSqlTxt = detachedHsCriteria.getHsSqlText();
        Integer count = super.queryByCriteriaForObject(hsSqlTxt.getNewSql(), hsSqlTxt.getParameter(), Integer.class);

        if (count == null) {
            return new Integer(0);
        }

        return count;
    }

    public void setReadDatasource(boolean readDatasource) {
        this.readDatasource = readDatasource;
    }

    @Override
    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        if (!this.readDatasource) {
            if (getTransactionTemplate() == null) {
                throw new IllegalArgumentException("Property 'transactionTemplate' is required");
            }
        }
    }

    @Override
    public int deleteByCriteria(DetachedHsCriteria detachedIUCriteria) {
        HsSqlText hsSqlTxt = detachedIUCriteria.getHsSqlText();

        return super.insertForCriteria(hsSqlTxt.getNewSql(), hsSqlTxt.getParameter());
    }
}
