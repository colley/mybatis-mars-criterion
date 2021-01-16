/**
 * Copyright NewHeight Co.,Ltd. (C)2016
 * File Name: ResponseData.java
 * Encoding: UTF-8
 * Date: 16-11-3 上午10:34
 * History:
 */
package com.mars.kit.criterion.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;


/**
 * @author mayuanchao
 * @version 1.0  Date: 16-11-3 上午10:34
*
 * @param <T>
 */
public class ResponseData<T> implements Serializable {
    private static final long serialVersionUID = 2553161132758377840L;
    public static final String SUCC_CODE = RESPONSE_CODE.SUCCESS_CODE_200.RETCODE;

    /**
     * 返回retCode 200或则0成功 编码
     */
    private String retCode = SUCC_CODE;

    /**
     * error message
     */
    private String retMessage;

    /**
     * 返回的结果对象
     */
    private T result;
    
    /**
     * 服务器端调用耗时，单位毫秒
     */
    private Long consumedTime;

    private Long startTime = System.currentTimeMillis();
    
    
    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
    
    public void endConsumedTime(Long timeMillis){
        this.consumedTime = (timeMillis-startTime);
    }

    public Long getConsumedTime() {
        return consumedTime;
    }

    public ResponseData() {
    }

    public ResponseData(String retCode, String retMessage) {
        this.retCode = retCode;
        this.retMessage = retMessage;
    }

    public ResponseData(String retCode) {
        this.retCode = retCode;
    }

    public ResponseData(RESPONSE_CODE respinseCode) {
        this.retCode = respinseCode.RETCODE;
        this.retMessage = respinseCode.getCodeDesc();
    }

    /**
     * 是否成功： 是 true, 否 false
     */
    public boolean isSuccess() {
        if (RESPONSE_CODE.SUCCESS_CODE_200.RETCODE.equals(retCode)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public void setRetCode(RESPONSE_CODE respinseCode) {
        this.retCode = respinseCode.RETCODE;
    }

    public String getRetMessage() {
        return retMessage;
    }

    public void setRetMessage(String retMessage) {
        this.retMessage = retMessage;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Response [success=");
        builder.append(isSuccess());
        builder.append(", errorCode=");
        builder.append(retCode);
        builder.append(", errorMessage=");
        builder.append(retMessage);
        builder.append(", result=");
        builder.append(JSON.toJSONString(result));
        builder.append("]");

        return builder.toString();
    }
    public enum RESPONSE_CODE {
        SUCCESS_CODE_200("200", "操作成功"),
        SYS_ERROR("900", "系统 异常"),
        OPERATE_DUPLICATE("203", "重复提交");
        
        public String RETCODE;
        private String codeDesc;

        RESPONSE_CODE(String retCode, String codeDesc) {
            this.RETCODE = retCode;
            this.codeDesc = codeDesc;
        }
        
        public String getRetCode() {
            return RETCODE;
        }

        public String getCodeDesc() {
            return codeDesc;
        }

    }
}
