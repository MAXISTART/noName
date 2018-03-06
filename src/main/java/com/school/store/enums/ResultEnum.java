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

    NAME_REPEAT(4, "种类名字已经重复"),

    RESULT_OUT(5, "审核结果已经出了，不能再修改订单"),

    PERMISSION_NOT_ALLOWED(6, "用户权限不足"),

    NAME_FORMAT_ERROR(7, "用户名格式不符"),

    USER_NOT_FOUND(8, "用户不存在或者密码错误"),

    USER_LOGIN_SUCCESS(9, "用户登录成功，当前用户为:普通用户"),

    ADMIN_LOGIN_SUCCESS(10, "用户登录成功，当前用户为:管理员"),

    NOT_LOGIN(11, "用户尚未登录或者登录已经过期"),

    LOGIN_OUT_SUCCESS(12, "用户登出成功"),

    MAIL_FORMAT_ERROR(13, "邮箱格式错误"),

    MOBILE_FORMAT_ERROR(14, "手机号格式错误"),

    REGISTER_SUCCESS(15, "注册成功"),

    USER_AREADLY_IN_SESSION_BEFORE_REGISTER(16, "当前已经有用户登录，请先登出用户再注册"),

    GET_SORTS_FAIL(17, "获取种类失败"),

    ITEMS_NOT_NULL(18, "明细不能为空")
    ;

    private Integer code;

    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public String toJson(){
        return "{code: " + this.code + ", msg: \"" + this.message + "\"}";
    }
}
