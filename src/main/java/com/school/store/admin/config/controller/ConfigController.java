package com.school.store.admin.config.controller;


import com.school.store.admin.department.service.DepartmentService;
import com.school.store.admin.good.service.GoodItemService;
import com.school.store.admin.good.service.SortItemService;
import com.school.store.admin.user.service.UserService;
import com.school.store.annotation.Permiss;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.constant.Permit;
import com.school.store.enums.ResultEnum;
import com.school.store.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 *  node服务器开始跑的时候就会从这里取初始数据或者系统数据
 */

@RestController
@RequestMapping(value = "/admin/config")
@Permiss(and = Permit.ADMIN)
public class ConfigController extends BaseAdminController{


    @Autowired
    UserService userService;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    SortItemService sortItemService;

    @Autowired
    GoodItemService goodItemService;

    // 不需要管理员账号，node系统会直接取到这个数据
    @GetMapping("/getInitData")
    public ResultVo getInitData(){
        Map<String, Object> data  = new HashMap<>();
        data.put("allUsers", userService.findAll());
        data.put("allDepartments", departmentService.findAll());
        data.put("allSorts", sortItemService.findAll());
        return simpleResult(ResultEnum.SUCCESS, data);
    }




}
