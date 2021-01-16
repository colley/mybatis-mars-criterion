/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.sql;

import java.io.Serializable;

/**
 *@FileName  AliasColumn.java
 *@Date  16-5-24 下午12:58
 *@author Colley
 *@version 1.0
 */
public class AliasColumn implements Serializable{
	private static final long serialVersionUID = -1344871030862429156L;
	private final String columnName;
    private String include;
    private String op;

    public AliasColumn(String columnName) {
        this(columnName, false);
    }
    
    public AliasColumn(String columnName,boolean include) {
        this.columnName = columnName;
        this.include = include?"Y":null;
    }
    
    public AliasColumn(String columnName,String op,boolean include) {
        this.columnName = columnName;
        this.include = include?"Y":null;
        this.op = op;
    }

    public String getColumnName() {
        return columnName;
    }

    public static AliasColumn as(String columnName, String aliasName) {
        return as(columnName, aliasName, false);
    }
    
    public static AliasColumn as(String columnName, String aliasName,boolean include) {
        String newcolumnName = columnName + " AS " + aliasName;
        return new AliasColumn(newcolumnName,include);
    }
    
    public static AliasColumn neAs(String columnName,boolean include) {
        return new AliasColumn(columnName,include);
    }
    
    public static AliasColumn neAs(String columnName,String op,boolean include) {
        return new AliasColumn(columnName,op,include);
    }
    
    public static AliasColumn neAs(String columnName) {
        return neAs(columnName, false);
    }

	public String get1Include() {
		return include;
	}

	public String getOp() {
		return op;
	}
}
