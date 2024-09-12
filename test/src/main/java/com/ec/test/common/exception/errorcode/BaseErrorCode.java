package com.ec.test.common.exception.errorcode;

/**
 * @Description: 基础错误码定义
 * @author: lChen
 * @date: 2023-04-19 11:20
 **/
public enum BaseErrorCode implements IErrorCode {

    // ========== 一级宏观错误码 客户端错误 ==========
    CLIENT_ERROR("A000001", "用户端错误"),

    NOT_LOGIN("A000002", "未获取到登陆人信息"),

    // ========== 一级宏观错误码 系统执行出错 ==========
    SERVICE_ERROR("B000001", "系统执行出错"),

    GLOBAL_ERROR("B000000", "系统繁忙，请稍后重试"),

    // ========== 二级宏观错误码 系统执行超时 ==========
    SERVICE_TIMEOUT_ERROR("B000100", "系统执行超时"),

    // ========== 一级宏观错误码 调用第三方服务出错 ==========
    REMOTE_ERROR("C000001", "调用第三方服务出错"),

    MQ_PARAM_ERROR("C000002", "消息体错误"),

    ;

    private final String code;

    private final String message;

    BaseErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
