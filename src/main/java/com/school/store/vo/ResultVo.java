package com.school.store.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResultVo<T> implements Serializable{

    /** 给 redis 识别用的 */
    // shift + ctrl + o 快速生成，前提是你有 implements Serializable
    private static final long serialVersionUID = 7635070909600042405L;

    /** 错误码. */
    private Integer code;

    /** 提示信息. */
    private String msg;

    /** 具体内容. */
    private T data;

}
