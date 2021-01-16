/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-15 下午2:45
 * History:
 */
package com.mars.kit.exception;

/**
 * LimitException.java
 * @author ColleyMa
 * @version 19-5-15 下午2:45
 */
public class LimitException extends RuntimeException {
    private static final long serialVersionUID = 2523445413467587274L;
    private String retCode;
    private String retMessage;
    private String resource;

/**
     *
     * 空构造
     */
    public LimitException() {
        super("LimitException 异常");
        this.retCode = "590";
        this.retMessage = "接口访问太频繁~";
    }

    /**
     *
     * 自定义错误日志
     * @param e
     */
    public LimitException(String resource) {
        super();
        this.resource = resource;
        this.retCode = "590";
        this.retMessage = "【"+resource+"】 接口访问太频繁~";
    }

    public String getResource() {
        return resource;
    }

    public String getRetCode() {
        return retCode;
    }


    public String getRetMessage() {
        return retMessage;
    }

}
