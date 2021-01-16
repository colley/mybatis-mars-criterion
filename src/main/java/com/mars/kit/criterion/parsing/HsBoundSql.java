/**
 * Copyright (C), 2011-2017  yhd.com
 * File Name: HBoundSql.java
 * Encoding: UTF-8
 * Date: 17-2-28 下午1:11
 * History:
 */
package com.mars.kit.criterion.parsing;

/**
 * @author mayuanchao
 * @version 1.0  Date: 17-2-28 下午1:11
 */
@SuppressWarnings("serial")
public class HsBoundSql implements java.io.Serializable {
    private String sql;
    private Object[] paramObjectValues;
    private Object parameterObject;
    
    private ParamMapping[] paramMappings;
    
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public Object[] getParamObjectValues() {
		return paramObjectValues;
	}
	public void setParamObjectValues(Object[] paramObjectValues) {
		this.paramObjectValues = paramObjectValues;
	}
	public Object getParameterObject() {
		return parameterObject;
	}
	public void setParameterObject(Object parameterObject) {
		this.parameterObject = parameterObject;
	}
	public ParamMapping[] getParamMappings() {
		return paramMappings;
	}
	public void setParamMappings(ParamMapping[] paramMappings) {
		this.paramMappings = paramMappings;
	}
}
