/**
 * Copyright (C), 2011-2017  yhd.com
 * File Name: BaseCommonDao.java
 * Encoding: UTF-8
 * Date: 17-3-1 下午1:35
 * History:
 */
package com.mars.kit.criterion.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import com.mars.kit.criterion.DetachedHsCriteria;
import com.mars.kit.criterion.DetachedIUCriteria;
import com.mars.kit.criterion.model.Pagination;
import com.mars.kit.criterion.sql.HsCriteria;
import com.mars.kit.exception.DaoException;


/**
 * @FileName  AbsBaseDaoImpl.java
 * @author colley
 * @version 1.0  Date: 17-9-11 上午11:35
 */
public abstract class AbsBaseDaoImpl extends SqlSessionDaoSupport {
    private static Integer DEFAULT_MAX_BATCH_SIZE = 1000; // 批量更新 插入和删掉
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
    * 用于调试SQL语句的执行时间.
    */
    private void logRunTime(String statementName, long runTime) {
        if (logger.isWarnEnabled()) {
        	logger.warn("Sql：{} executed, Run time estimated:{} ms",statementName,runTime);
        }
    }

    /**
     * 查找单个对象，可以统计记录的个数
     *
     * @param statementName
     * @param parameterObject
     * @param qualification
     *
     * @return
     *
     * @throws DaoException
     */
    protected <T> T queryForObject(String statementName, Object parameterObject)
                            throws DaoException {
        long startTime = System.currentTimeMillis();
        statementName = getFullStatementName(statementName);

        T returnObject = null;

        try {
            returnObject = this.getSqlSession().selectOne(statementName, parameterObject);
        } catch (DataAccessException e) {
            throw new DaoException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(statementName, endTime - startTime);
        }

        return returnObject;
    }

    protected <T> T queryForObject(String statementName)
                            throws DaoException {
        long startTime = System.currentTimeMillis();
        statementName = getFullStatementName(statementName);

        T returnObject = null;

        try {
            returnObject = this.getSqlSession().selectOne(statementName);
        } catch (DataAccessException e) {
            throw new DaoException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(statementName, endTime - startTime);
        }

        return returnObject;
    }

    /**
     * 返回查询列表
     * @param <T>
     *
     * @param statementName
     * @param parameterObject
     *
     * @return
     *
     * @throws DaoException
     */
    protected <T> List<T> queryForList(String statementName, Object parameterObject)
                                throws DaoException {
        long startTime = System.currentTimeMillis();
        statementName = getFullStatementName(statementName);

        List<T> list = null;

        try {
            list = this.getSqlSession().selectList(statementName, parameterObject);
        } catch (DataAccessException e) {
            throw new DaoException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(statementName, endTime - startTime);
        }

        if (list == null) {
            list = EmptyObject.emptyList();
        }

        return list;
    }

    protected <T> List<T> queryForList(String statementName)
                                throws DaoException {
        long startTime = System.currentTimeMillis();
        statementName = getFullStatementName(statementName);

        List<T> list = new ArrayList<T>();

        try {
            list = this.getSqlSession().selectList(statementName, null);
        } catch (DataAccessException e) {
            throw new DaoException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(statementName, endTime - startTime);
        }

        if (list == null) {
            list = EmptyObject.emptyList();
        }

        return list;
    }

    /**
     *分页查询数据
     * @param countStatementName 查询总条数的StatementName
     * @param qryStatementName  查询数据的StatementName
     * @param params            参数
     * @param total            总条数，这个参数用于查询总条数只查询一次，点击下一页的时候，传递过来就必要查询总条数了
     * @return Pagination
     * @throws DaoException
     * @author mayuanchao
     * @date 2016年5月13日
     */
    protected <T> Pagination<T> queryForPagination(String countStatementName, String qryStatementName, Object parameterObject, int total)
                                            throws DaoException {
        countStatementName = getFullStatementName(countStatementName);
        qryStatementName = getFullStatementName(qryStatementName);

        Pagination<T> pagination = new Pagination<T>();
        int count = total;

        if (total <= 0) {
            count = queryForInteger(countStatementName, parameterObject);
        }

        pagination.setTotalRows(count);

        if (count > 0) {
            List<T> result = queryForList(qryStatementName, parameterObject);

            if (CollectionUtils.isNotEmpty(result)) {
                pagination.setRows(result);
            }
        }

        return pagination;
    }

    /**
     * 分页查询数据
     * @param countStatementName
     * @param qryStatementName
     * @param parameterObject
     * @return
     * @throws DaoException
     * @author mayuanchao
     * @date 2016年5月13日
     */
    protected <T> Pagination<T> queryForPagination(String countStatementName, String qryStatementName, Object parameterObject)
                                            throws DaoException {
        return queryForPagination(countStatementName, qryStatementName, parameterObject, 0);
    }

    protected <T> List<T> executeQueryForList(String statementName, int offset, int limit)
                                       throws DaoException {
        return queryForList(statementName, null, offset, limit);
    }

    protected <T> List<T> queryForList(String statementName, Object parameterObject, int offset, int limit)
                                throws DaoException {
        long startTime = System.currentTimeMillis();
        statementName = getFullStatementName(statementName);

        List<T> list = new ArrayList<T>();

        try {
            list = getSqlSession().selectList(statementName, parameterObject, new RowBounds(offset, limit));
        } catch (DataAccessException e) {
            throw new DaoException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(statementName, endTime - startTime);
        }

        if (list == null) {
            list = EmptyObject.emptyList();
        }

        return list;
    }

    /**
     * 返回一个MAP结果集，KEY是返回DO中的一个字段名称
     * @param <K>
     *
     * @param statementName
     * @param parameterObject
     * @param dr
     * @param key
     *
     * @return
     *
     * @throws DaoException
     */
    protected <K, V> Map<K, V> queryForMap(String statementName, Object parameterObject, String key)
                                    throws DaoException {
        long startTime = System.currentTimeMillis();
        statementName = getFullStatementName(statementName);

        Map<K, V> resultMap = new HashMap<K, V>();

        try {
            resultMap = this.getSqlSession().selectMap(statementName, parameterObject, key);
        } catch (DataAccessException e) {
            throw new DaoException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(statementName, endTime - startTime);
        }

        if (resultMap == null) {
            resultMap = EmptyObject.emptyMap();
        }

        return resultMap;
    }

    /**
     * 更新数据库，可以插入一条记录，也可以删除一条记录 返回受影响的条数
     *
     * @param statementName
     * @param parameterObject
     * @param dr
     *
     * @return 被更新的记录数
     *
     * @throws DaoException
     */
    protected int update(String statementName, Object parameterObject)
                  throws DaoException {
        int updateRows = 0;
        long startTime = System.currentTimeMillis();
        statementName = getFullStatementName(statementName);

        try {
            updateRows = this.getSqlSession().update(statementName, parameterObject);
        } catch (DataAccessException e) {
            throw new DaoException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(statementName, endTime - startTime);
        }

        return updateRows;
    }

    /**
     * 插入一条记录
     *
     * @param statementName
     * @param parameterObject
     * @param dr
     *
     * @return 新增加的记录主键
     *
     * @throws DaoException
     */
    protected <T> T insert(String statementName, Object parameterObject)
                    throws DaoException {
        T back = null;
        long startTime = System.currentTimeMillis();
        statementName = getFullStatementName(statementName);

        try {
            getSqlSession().insert(statementName, parameterObject);
        } catch (DataAccessException e) {
            throw new DaoException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(statementName, endTime - startTime);
        }

        return back;
    }

    protected void batchInsert(String statementName, List<?> parameterList)
                        throws DaoException {
        batchInsert(statementName, parameterList, DEFAULT_MAX_BATCH_SIZE);
    }

    /**
     *批量插入记录
     * @param statementName                        sqlMap操作的id
     * @param parameterList                        需要插入的记录数
     * @param num                                  多少次插入一次
     * @return                                插入的主键列表，理论上size和parameterList是相等
     * @throws DaoException
     */
    protected void batchInsert(String statementName, List<?> parameterList, int num)
                        throws DaoException {
        long startTime = System.currentTimeMillis();
        SqlSession batchSqlSession = null;

        try {
            if ((parameterList == null) || parameterList.isEmpty()) {
                logger.warn("event=[BaseCommonDao#batchInsert] parameterList is null or empty, statementName:{}"+statementName);

                return;
            }

            batchSqlSession = getSqlSessionForBatch();

            for (int i = 0; i < parameterList.size(); i++) {
                int count = i + 1;

                batchSqlSession.insert(statementName, parameterList.get(i));

                if (((count % num) == 0) || (count == parameterList.size())) {
                    batchSqlSession.commit();
                    batchSqlSession.clearCache();
                }
            }
        } catch (Exception e) {
            if (batchSqlSession != null) {
                batchSqlSession.rollback();
            }

            throw new DaoException(e);
        } finally {
            if (batchSqlSession != null) {
                batchSqlSession.close();
            }

            long endTime = System.currentTimeMillis();
            logRunTime(statementName, endTime - startTime);
        }
    }

    protected int batchUpdate(String statementName, List<?> parameterList, int num)
                       throws DaoException {
        int total = 0;
        long startTime = System.currentTimeMillis();

        SqlSession batchSqlSession = null;

        try {
            if ((parameterList == null) || parameterList.isEmpty()) {
            	logger.warn("event=[BaseDaoImpl#batchUpdate] parameterList is null or empty, statementName = {}",statementName);

                return 0;
            }

            batchSqlSession = getSqlSessionForBatch();

            for (int i = 0; i < parameterList.size(); i++) {
                total = i;

                int count = i + 1;

                batchSqlSession.update(statementName, parameterList.get(i));

                if (((count % num) == 0) || (count == parameterList.size())) {
                    batchSqlSession.commit();
                    batchSqlSession.clearCache();
                }
            }
        } catch (Exception e) {
            if (batchSqlSession != null) {
                batchSqlSession.rollback();
            }

            throw new DaoException(e);
        } finally {
            if (batchSqlSession != null) {
                batchSqlSession.close();
            }

            long endTime = System.currentTimeMillis();
            logRunTime(statementName, endTime - startTime);
        }

        return (total == parameterList.size()) ? total : 0;
    }

    /**
     *批量更新记录
     * @param statementName                        sqlMap操作的id
     * @param parameterList                        需要更新的记录
     * @param num                                  多少次更新一次
     * @return                                插入的主键列表，理论上size和parameterList是相等
     * @throws DaoException
     */
    protected int batchUpdate(String statementName, List<?> parameterList)
                       throws DaoException {
        return batchUpdate(statementName, parameterList, DEFAULT_MAX_BATCH_SIZE);
    }

    /**
     * 插入或更新一条记录，处理逻辑：先根据参数获取是否存在，存在就执行更新操作，不存在，执行插入操作
     * @param countStatementName
     * @param insertStatementName
     * @param updateStatementName
     * @param parameterObject
     * @param dr
     * @return Object
     * @throws DaoException
     */
    protected Object insertOrUpdate(String countStatementName, String insertStatementName, String updateStatementName, Object parameterObject)
                             throws DaoException {
        Integer count = this.queryForObject(countStatementName, parameterObject);

        if ((null != count) && (count.intValue() > 0)) {
            int u = this.update(updateStatementName, parameterObject);

            return new Integer(u);
        }

        return this.insert(insertStatementName, parameterObject);
    }

    protected Object insertOrUpdate(String insertStatementName, String updateStatementName, Object parameterObject, boolean isUpdate)
                             throws DaoException {
        if (isUpdate) {
            int u = this.update(updateStatementName, parameterObject);

            return new Integer(u);
        }

        return this.insert(insertStatementName, parameterObject);
    }

    /**
     * @param statementName
     * @return
     * @author mayuanchao
     * @date 2016年5月13日
     */
    private String getFullStatementName(String statementName) {
        //String fullStatementName = namespace + "." + statementName;
        String fullStatementName = statementName;
        logger.info("the full statementName is :{}", fullStatementName);

        return fullStatementName;
    }

    /**
     * 返回Inteter，用于获取总数或者返回一个Integer的sql
     * @param statement
     * @param parameter
     * @return
     * @throws DaoException
     * @author mayuanchao
     * @date 2016年5月13日
     */
    protected int queryForInteger(String statement, Object parameter)
                           throws DaoException {
        Integer rtn = queryForObject(statement, parameter);

        if (rtn == null) {
            return 0;
        }

        return rtn.intValue();
    }

    /**
     * 删除操作
     * @param statement
     * @param parameter
     * @return
     * @throws DaoException
     * @author mayuanchao
     * @date 2016年5月13日
     */
    protected int delete(String statement, Object parameter)
                  throws DaoException {
        long startTime = System.currentTimeMillis();

        try {
            return getSqlSession().delete(getFullStatementName(statement), parameter);
        } catch (Exception e) {
            throw new DaoException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(statement, endTime - startTime);
        }
    }

    protected int delete(String statement) throws DaoException {
        long startTime = System.currentTimeMillis();

        try {
            return getSqlSession().delete(getFullStatementName(statement));
        } catch (Exception e) {
            throw new DaoException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(statement, endTime - startTime);
        }
    }

    protected int batchDelete(String statementName, List<?> parameterList) {
        return batchDelete(statementName, parameterList, DEFAULT_MAX_BATCH_SIZE);
    }

    /**
     * 批量删除
     * @param statementName
     * @param parameterList
     * @param num
     * @return
     * @author mayuanchao
     * @date 2016年5月13日
     */
    protected int batchDelete(String statementName, List<?> parameterList, int num) {
        int total = 0;
        long startTime = System.currentTimeMillis();

        SqlSession batchSqlSession = null;

        try {
            if ((parameterList == null) || parameterList.isEmpty()) {
            	logger.warn("event=[BaseDaoImpl#batchDelete] parameterList is null or empty, statementName :{}",statementName);

                return 0;
            }

            batchSqlSession = getSqlSessionForBatch();

            for (int i = 0; i < parameterList.size(); i++) {
                total = i;

                int count = i + 1;

                batchSqlSession.delete(statementName, parameterList.get(i));

                if (((count % num) == 0) || (count == parameterList.size())) {
                    batchSqlSession.commit();
                    batchSqlSession.clearCache();
                }
            }
        } catch (Exception e) {
            if (batchSqlSession != null) {
                batchSqlSession.rollback();
            }

            throw new DaoException(e);
        } finally {
            if (batchSqlSession != null) {
                batchSqlSession.close();
            }

            long endTime = System.currentTimeMillis();
            logRunTime(statementName, endTime - startTime);
        }

        return (total == parameterList.size()) ? total : 0;
    }

    /**
     * query entities
     * @param <T>
     *
     * @param statementId
     * @param entity
     * @return result list
     */
    public <T> List<T> queryList(String statementId, Object entity) {
        List<T> results = getSqlSession().selectList(statementId, entity);

        if (results == null) {
            results = EmptyObject.emptyList();
        }

        return results;
    }

    /**
     * Ibatis 动态条件查询
     * @param statementName
     * @param resultMapId
     * @param baseCriteria
     * @return
     * @throws DaoException
     * @author mayuanchao
     * @date 2016年5月20日
     */
    protected <T> List<T> queryForCriteria(String statementName, HsCriteria baseCriteria, int offset, int limit)
                                    throws DaoException {
        statementName = getFullStatementName(statementName);

        if ((offset == RowBounds.NO_ROW_OFFSET) && (limit == RowBounds.NO_ROW_LIMIT)) {
            return this.queryForList(statementName, baseCriteria);
        }

        return this.queryForList(statementName, baseCriteria, offset, limit);
    }

    /**
     * Ibatis 动态条件查询
     * @param statementName
     * @param detachedCriteria
     * @return
     * @throws DaoException
     * @author mayuanchao
     * @date 2016年5月20日
     */
    protected <T> List<T> queryForCriteria(String statementName, DetachedHsCriteria detachedCriteria, int offset, int limit)
                                    throws DaoException {
        HsCriteria baseCriteria = detachedCriteria.getCriteria();

        return queryForCriteria(statementName, baseCriteria, offset, limit);
    }

    protected <T> T qryObjectForCriteria(String statementName, DetachedHsCriteria detachedCriteria)
                                  throws DaoException {
        HsCriteria baseCriteria = detachedCriteria.getCriteria();

        return this.queryForObject(statementName, baseCriteria);
    }

    public <T> List<T> queryByCriteriaSelect(DetachedHsCriteria criteria)
                                      throws DaoException {
        return queryForCriteria(criteria, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    }

    protected <T> List<T> queryForCriteria(final DetachedHsCriteria criteria, int offset, int limit)
                                    throws DaoException {
        Assertion.notNull(criteria, "DetachedCriteria must not be null");

        List<T> result = null;

        if ((offset == RowBounds.NO_ROW_OFFSET) && (limit == RowBounds.NO_ROW_LIMIT)) {
            result = queryForCriteria(criteria.getDynamicQueryByCriteria(), criteria);
        } else {
            result = queryForCriteria(criteria.getDynamicQueryByCriteria(), criteria, offset, limit);
        }

        return result;
    }

    protected <T> List<T> queryForCriteria(String statementName, DetachedHsCriteria detachedCriteria)
                                    throws DaoException {
        return queryForCriteria(statementName, detachedCriteria, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    }

    protected int updateByIUCriteria(DetachedIUCriteria detachedIUCriteria)
                              throws DaoException {
        int updateRows = 0;
        long startTime = System.currentTimeMillis();
        String statementName = detachedIUCriteria.getDynamicUpdateStatementName();

        try {
            updateRows = this.getSqlSession().update(statementName, detachedIUCriteria.getCriteria());
        } catch (DataAccessException e) {
            throw new DaoException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(statementName, endTime - startTime);
        }

        return updateRows;
    }

    protected boolean insertByIUCriteria(DetachedIUCriteria detachedIUCriteria)
                                  throws DaoException {
        long startTime = System.currentTimeMillis();
        String statementName = detachedIUCriteria.getDynamicInsertStatementName();

        try {
            this.getSqlSession().insert(statementName, detachedIUCriteria.getCriteria());
        } catch (DataAccessException e) {
            throw new DaoException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logRunTime(statementName, endTime - startTime);
        }

        return true;
    }
}
