package com.ec.test.common.exception;

import com.ec.test.common.exception.errorcode.IErrorCode;

import com.google.common.base.Strings;
import datadog.trace.api.GlobalTracer;
import lombok.Getter;

import java.io.Serializable;
import java.util.Optional;

/**
 * @Description: 抽象项目中三类异常体系，客户端异常、服务端异常以及远程服务调用异常
 * @author: lChen
 * @date: 2023-04-19 11:18
 **/
@Getter
public class AbstractException extends RuntimeException implements Serializable {
    public final String errorCode;

    public String errorMessage;

    /**
     * 业务id，通常可以用来追踪异常
     */
    public final String bizId;

    /**
     * trace id,如果接入链路追踪，可以用来定位
     */
    public String traceId;

    public AbstractException(String message, Throwable throwable, IErrorCode errorCode, String reqId) {
        super(message, throwable);
        this.errorCode = errorCode.code();
        this.bizId = reqId;
        this.errorMessage = Optional.ofNullable(Strings.emptyToNull(message)).orElse(errorCode.message());
        this.traceId = GlobalTracer.get().getTraceId();
    }


}
