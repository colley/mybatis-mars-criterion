/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-7 下午3:18
 * History:
 */
package com.mars.kit.criterion.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.alibaba.fastjson.JSON;
import com.mars.kit.criterion.common.HsSqlText;
import com.mars.kit.exception.DaoException;


/**
 * HBoundSqlBuilder.java
 *
 * @author ColleyMa
 * @version 19-5-7 下午3:18
*/
public class HBoundSqlBuilder {
    private static final String PARAMETER_TOKEN = "#";

    public static HsBoundSql parseSql(HsSqlText sqlTxt) {
        return parseSql(sqlTxt.getNewSql(), sqlTxt.getParameter());
    }

    public static HsBoundSql parseSql(String sqlText, Object parameterObject) {
        String newSql = sqlText;
        List<ParamMapping> mappingList = new ArrayList<ParamMapping>();
        StringTokenizer parser = new StringTokenizer(sqlText, PARAMETER_TOKEN, true);
        StringBuffer newSqlBuffer = new StringBuffer();
        String token = null;
        String lastToken = null;

        while (parser.hasMoreTokens()) {
            token = parser.nextToken();

            if (PARAMETER_TOKEN.equals(lastToken)) {
                if (PARAMETER_TOKEN.equals(token)) {
                    newSqlBuffer.append(PARAMETER_TOKEN);
                    token = null;
                } else {
                    ParamMapping mapping = new ParamMapping();
                    StringTokenizer paramParser = new StringTokenizer(token, "=,", false);
                    mapping.setPropertyName(paramParser.nextToken());
                    mappingList.add(mapping);
                    newSqlBuffer.append("?");

                    boolean hasMoreTokens = parser.hasMoreTokens();

                    if (hasMoreTokens) {
                        token = parser.nextToken();
                    }

                    if (!hasMoreTokens || !PARAMETER_TOKEN.equals(token)) {
                        throw new DaoException("Unterminated inline parameter in mapped statement near '" + newSqlBuffer.toString() + "'");
                    }

                    token = null;
                }
            } else {
                if (!PARAMETER_TOKEN.equals(token)) {
                    newSqlBuffer.append(token);
                }
            }

            lastToken = token;
        }

        newSql = newSqlBuffer.toString();

        ParamMapping[] mappingArray = mappingList.toArray(new ParamMapping[mappingList.size()]);
        HsBoundSql boundSql = new HsBoundSql();
        boundSql.setSql(newSql);
        boundSql.setParamMappings(mappingArray);
        boundSql.setParameterObject(parameterObject);

        Object[] paramObjectValues = getParamObjectValues(mappingArray, parameterObject);
        boundSql.setParamObjectValues(paramObjectValues);

        return boundSql;
    }

    public static Object[] getParamObjectValues(ParamMapping[] mappingArray, Object parameterObject) {
        MapDataExchange dataExchange = new MapDataExchange();

        return dataExchange.getData(mappingArray, parameterObject);
    }

    public static void main(String[] args) {
        String sql = "UPDATE CHANNEL_ACCOUNT_REPORT SET name=#name#,age=#age#,createTime=now() WHERE name=#name3#  AND  name=#name4#";
        Map<String, Object> paramObj = JSON.parseObject("{\"name3\":\"张三\",\"name4\":\"张三1\",\"age\":11,\"name\":\"colley\"}");
        HsBoundSql boundSql = HBoundSqlBuilder.parseSql(sql, paramObj);

        System.out.println(JSON.toJSONString(boundSql));
    }
}
