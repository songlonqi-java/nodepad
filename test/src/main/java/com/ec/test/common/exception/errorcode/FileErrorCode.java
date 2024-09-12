package com.ec.test.common.exception.errorcode;

import lombok.AllArgsConstructor;

/**
 * @Description: 文件流异常枚举
 * @author: lChen
 * @date: 2023-04-23 15:17
 **/

@AllArgsConstructor
public enum FileErrorCode implements IErrorCode {

    FILE_CREATE_ERROR("B000101", "文件创建异常"),

    FILE_CONVERT_ERROR("B000102", "文件转换异常"),

    ;

    private final String code;

    private final String message;

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }
}
