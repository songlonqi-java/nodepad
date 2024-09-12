package com.ec.test.common.exception;


import com.ec.test.common.exception.errorcode.BaseErrorCode;
import com.ec.test.common.exception.errorcode.IErrorCode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @Description: 远程服务调用异常
 * @author: lChen
 * @date: 2023-04-19 11:22
 **/
public class RemoteException extends AbstractException implements Serializable {

    public RemoteException(String message, Throwable e) {
        this(message, e, BaseErrorCode.REMOTE_ERROR);
    }

    public RemoteException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public RemoteException(String message, Throwable throwable, IErrorCode errorCode) {
        this(message, throwable, errorCode, StringUtils.EMPTY);
    }

    public RemoteException(String message, Throwable throwable, IErrorCode errorCode, String bizId) {
        super(message, throwable, errorCode, bizId);
    }

    @Override
    public String toString() {
        return "RemoteException{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", bizId='" + bizId + '\'' +
                ", traceId='" + traceId + '\'' +
                '}';
    }
}
