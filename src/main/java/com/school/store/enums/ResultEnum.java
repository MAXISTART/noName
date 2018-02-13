package com.school.store.enums;

import lombok.Getter;

/**
 * Created by 廖师兄
 * 2017-06-11 18:56
 */
@Getter
public enum ResultEnum {

    SUCCESS(0, "成功"),

    UNKNOWN_ERROR(-1, "未知错误"),

    PARAM_ERROR(1, "参数不正确"),

    NAME_SPEC_REPEAT(2, "同样名字和规格的已经重复"),

    STORE_UNSATISFY(3, "库存不足"),

    NAME_REPEAT(4, "种类名字已经重复")

    ;

    private Integer code;

    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
