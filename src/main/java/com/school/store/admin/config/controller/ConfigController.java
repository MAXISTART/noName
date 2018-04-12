package com.school.store.admin.config.controller;


import com.school.store.admin.buy.controller.BuyOrderController;
import com.school.store.admin.buy.entity.BuyOrder;
import com.school.store.admin.department.service.DepartmentService;
import com.school.store.admin.good.controller.GoodController;
import com.school.store.admin.good.service.GoodItemService;
import com.school.store.admin.good.service.SortItemService;
import com.school.store.admin.take.controller.TakeOrderController;
import com.school.store.admin.take.entity.TakeOrder;
import com.school.store.admin.user.service.UserService;
import com.school.store.annotation.Permiss;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.base.excel.ExcelUtils;
import com.school.store.constant.Permit;
import com.school.store.enums.ResultEnum;
import com.school.store.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    ExcelUtils excelUtils;

    @Autowired
    GoodController goodController;

    @Autowired
    BuyOrderController buyOrderController;

    @Autowired
    TakeOrderController takeOrderController;


    // 不需要管理员账号，node系统会直接取到这个数据
    @GetMapping("/getInitData")
    public ResultVo getInitData(){
        Map<String, Object> data  = new HashMap<>();
        data.put("allDepartments", departmentService.findAll());
        data.put("allSorts", sortItemService.findAll());
        return simpleResult(ResultEnum.SUCCESS, data);
    }


    @Permiss(need = false)
    @GetMapping("/testExcel")
    @Transactional
    public ResultVo testExcel(){
        excelUtils.init(null, "办公用品信息表_20180411230729.xlsx");
        excelUtils.getGoodItems().forEach(goodItem -> {
            goodController.addGoodItem(goodItem);
        });

        excelUtils.init(null, "办公用品入库表_20180411233323.xlsx");
        List<BuyOrder> buyOrders = excelUtils.getBuyOrders();
        buyOrders.forEach(buyOrder -> {
            buyOrderController.addBuyOrder(buyOrder);
            buyOrder.setApprovalResult(1);
        });
        buyOrderController.approve(buyOrders);
        buyOrderController.quickInput(buyOrders.stream().map(buyOrder -> buyOrder.getId()).collect(Collectors.toList()));

        excelUtils.init(null, "办公用品申领表_20180412004119.xlsx");
        List<TakeOrder> takeOrders = excelUtils.getTakeOrders();
        takeOrders.forEach(takeOrder -> {
            takeOrderController.addTakeOrder(takeOrder);
            takeOrder.setApprovalResult(1);
        });
        takeOrderController.approve(takeOrders);
        takeOrderController.quickOutput(takeOrders.stream().map(takeOrder -> takeOrder.getId()).collect(Collectors.toList()));

        return simpleResult(ResultEnum.SUCCESS, null);
    }


    /**
     *  下面是专门用来处理excel的
     * @param upfile
     * @throws Exception
     */
    @RequestMapping("/uploadExcels")
    @ResponseBody
    public void uploadExcels(@RequestParam("file") MultipartFile upfile) throws Exception {
        InputStream is = upfile.getInputStream();
        byte[] b = new byte[is.available()];
        is.read(b);
        FileOutputStream fos = new FileOutputStream(new File("E://uploadFiles//"+upfile.getOriginalFilename()));
        fos.write(b);
        fos.flush();
        fos.close();
    }


    /**
     *  下面是专门用来上传文件的
     * @param upfile
     * @throws Exception
     */
    @RequestMapping("/uploadFiles")
    @ResponseBody
    public void uploadFiles(@RequestParam("file") MultipartFile upfile) throws Exception {
        InputStream is = upfile.getInputStream();
        byte[] b = new byte[is.available()];
        is.read(b);
        FileOutputStream fos = new FileOutputStream(new File("E://uploadFiles//"+upfile.getOriginalFilename()));
        fos.write(b);
        fos.flush();
        fos.close();
    }

}
