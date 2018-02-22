package com.school.store.exception;

import com.school.store.enums.ResultEnum;



public class BaseException extends RuntimeException {

    private Integer code;
    private String msg;

    public BaseException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());

        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMessage();
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.msg = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
