package com.school.store.base.controller;


import com.school.store.enums.ResultEnum;
import com.school.store.vo.ResultVo;
import org.springframework.web.bind.annotation.*;

@RestController
public class BaseAdminController {


    /**
     *  返回 固定的 信息
     * @param resultEnum
     * @param data
     * @param <T>
     * @return
     */
    public <T> ResultVo simpleResult(ResultEnum resultEnum, T data){
        ResultVo vo = new ResultVo();
        vo.setMsg(resultEnum.getMessage());
        vo.setCode(resultEnum.getCode());
        vo.setData(data);
        return vo;
    }

}
