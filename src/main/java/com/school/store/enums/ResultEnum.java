package com.school.store.enums;

import lombok.Getter;

/**
 * Created by 廖师兄
 * 2017-06-11 18:56
 */
@Getter
public enum ResultEnum {

    SUCCESS(0, "成功"),

    PARAM_ERROR(1, "参数不正确")


    ;

    private Integer code;

    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
