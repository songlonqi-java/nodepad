package com.ec.test.common.exception;

import com.ec.test.common.exception.errorcode.BaseErrorCode;
import com.ec.test.common.exception.errorcode.IErrorCode;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: 客户端异常，通常是参数校验错误
 * @author: lChen
 * @date: 2023-04-19 11:23
 **/
public class ClientException extends AbstractException {

    public ClientException(IErrorCode errorCode) {
        this(null, null, errorCode);
    }

    public ClientException(String message) {
        this(message, null, BaseErrorCode.CLIENT_ERROR);
    }

    public ClientException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ClientException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode, StringUtils.EMPTY);
    }

    @Override
    public String toString() {
        return "ClientException{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", bizId='" + bizId + '\'' +
                ", traceId='" + traceId + '\'' +
                '}';
    }
}
