package com.school.store.admin.test.controller;

import com.school.store.base.controller.BaseAdminController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/test")
public class TestController extends BaseAdminController {


    @CrossOrigin
    @PostMapping("/testNode")
    public String testNode(@RequestParam String testString){
        System.out.println(" testString success ");
        return "node请求成功";
    }


    @CrossOrigin
    @GetMapping("/testNodeGet")
    public String testNodeGet(@RequestParam String testString){
        System.out.println(" testString success ");
        return "node请求成功";
    }
}