/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.sql;

/**
 *@FileName  IUColumn.java
 *@Date  16-6-12 上午10:25
 *@author Colley
 *@version 1.0
 */
public class IUColumn implements java.io.Serializable {
    private static final long serialVersionUID = -7365018984537979369L;
    private final String columnName;
    private final Object columnValue;
    private final Integer columnConst;

    private IUColumn(String columnName, Object columnValue, boolean isConst) {
        this.columnName = columnName;
        this.columnValue = columnValue;
        this.columnConst = isConst? 1:0;
    }

    public static IUColumn select(String columnName) {
        return new IUColumn(columnName, null, false);
    }
    
    public static IUColumn set(String columnName, Object columnValue) {
        return new IUColumn(columnName, columnValue, false);
    }

    public static IUColumn consts(String columnName, Object columnValue) {
        return new IUColumn(columnName, columnValue, true);
    }

    public String getColumnName() {
        return columnName;
    }

    public Object getColumnValue() {
        return columnValue;
    }

    public boolean isConst() {
        return this.columnConst>0;
    }

	public Integer getColumnConst() {
		return columnConst;
	}
}
