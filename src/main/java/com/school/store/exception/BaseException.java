package com.school.store.exception;

import com.school.store.enums.ResultEnum;

/**
 * Created by 廖师兄
 * 2017-06-11 18:55
 */
public class BaseException extends RuntimeException {

    private Integer code;

    public BaseException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());

        this.code = resultEnum.getCode();
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
