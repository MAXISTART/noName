package com.school.store.base.controller;


import com.school.store.admin.admin.entity.Admin;
import com.school.store.base.model.BaseEntity;
import com.school.store.enums.ResultEnum;
import com.school.store.utils.EntityUtil;
import com.school.store.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@RestController
public class BaseAdminController {


    /**
     *  导入 entity 的工具类
     */
    @Autowired
    public EntityUtil entityUtil;

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
