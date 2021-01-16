/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.common;

/**
 *@FileName  IbsSqlText.java
 *@Date  16-5-25 下午2:46
 *@author Colley
 *@version 1.0
 */
public class HsSqlText implements java.io.Serializable{
	private static final long serialVersionUID = 3636153101971514955L;
	private String newSql;
    private Object parameter;

    public HsSqlText(String newSql, Object parameter) {
        this.newSql = newSql;
        this.parameter = parameter;
    }

    public String getNewSql() {
        return newSql;
    }

    public void setNewSql(String newSql) {
        this.newSql = newSql;
    }

    public Object getParameter() {
        return parameter;
    }

    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }
}
