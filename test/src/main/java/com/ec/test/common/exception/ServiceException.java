package com.ec.test.common.exception;


import com.ec.test.common.exception.errorcode.BaseErrorCode;
import com.ec.test.common.exception.errorcode.IErrorCode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @Description: 服务端内部错误
 * @author: lChen
 * @date: 2023-04-19 11:25
 **/
public class ServiceException extends AbstractException implements Serializable {

    public ServiceException(String message) {
        this(message, null, BaseErrorCode.SERVICE_ERROR);
    }

    public ServiceException(String message, String bizId) {
        this(message, null, BaseErrorCode.SERVICE_ERROR, bizId);
    }

    public ServiceException(IErrorCode errorCode) {
        this(null, errorCode);
    }

    public ServiceException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ServiceException(String message, Throwable throwable, IErrorCode errorCode) {
        this(message, throwable, errorCode, StringUtils.EMPTY);
    }

    public ServiceException(String message, Throwable throwable, IErrorCode errorCode, String bizId) {
        super(message, throwable, errorCode, bizId);
    }

    public ServiceException(String message, Throwable throwable){
        super(message, throwable, BaseErrorCode.SERVICE_ERROR, null);
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", bizId='" + bizId + '\'' +
                ", traceId='" + traceId + '\'' +
                '}';
    }
}
