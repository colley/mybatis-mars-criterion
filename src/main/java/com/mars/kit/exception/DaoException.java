/*
 * Copyright (c) 2016-2017 by colley
 * All rights reserved.
 */
package com.mars.kit.exception;

/**
 * Dao异常类
 *@FileName  DaoException.java
 *@Date  16-5-13 上午11:16
 *@author Ma Yuanchao
 *@version 1.0
 */
public class DaoException extends RuntimeException {
    /*** serial id */
    private static final long serialVersionUID = 1L;

    /**
     *
     * 空构造
     */
    public DaoException() {
        super("DaoException 异常");
    }

    /**
     *
     * 自定义错误日志
     * @param e
     */
    public DaoException(String e) {
        super(e);
    }

    /**
     * 只抛错误信息
     * @param e
     */
    public DaoException(Throwable e) {
        super(e);
    }

    /**
     * 两者皆抛
     * @param er
     * @param e
     */
    public DaoException(String er, Throwable e) {
        super(er, e);
    }
}
