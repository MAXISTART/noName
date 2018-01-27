package com.school.store.admin.take.controller;

import com.school.store.admin.admin.entity.Admin;
import com.school.store.admin.take.entity.TakeOrder;
import com.school.store.admin.take.entity.TakeOrderItem;
import com.school.store.admin.take.service.TakeOrderItemService;
import com.school.store.admin.take.service.TakeOrderService;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.enums.ResultEnum;
import com.school.store.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/takeOrder")
public class TakeOrderController extends BaseAdminController {

    @Autowired
    private TakeOrderService takeOrderService;

    @Autowired
    private TakeOrderItemService takeOrderItemService;

    @Transactional(readOnly = false)
    @PostMapping("/addTakeOrder")
    public ResultVo addTakeOrder(@RequestBody TakeOrder takeOrder, @SessionAttribute("admin") Admin admin) {

        takeOrderService.save(entityUtil.updateInfoDefault(takeOrder, admin.getId(), admin.getId(), true));

        // 保存明细内容，但是要先设置ID
        List<TakeOrderItem> takeOrderItems = takeOrder.getTakeOrderItems();
        if(takeOrderItems != null && !takeOrderItems.isEmpty()){
            takeOrderItems.forEach(takeOrderItem -> {
                takeOrderItem.setOrderId(takeOrder.getId());
            });
            takeOrderItemService.save(takeOrderItems);
        }

        Integer approvalResult =takeOrder.getApprovalResult();
        if(approvalResult!=null && approvalResult == 1){
            // 如果审批结果是通过的，就执行微信通知

        }

        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @Transactional(readOnly = false)
    @PostMapping(value = "/updateTakeOrder")
    public ResultVo updateTakeOrder(@RequestBody TakeOrder takeOrder, @SessionAttribute("admin") Admin admin) {

        // 更新明细内容
        List<TakeOrderItem> takeOrderItems = takeOrder.getTakeOrderItems();
        if(takeOrderItems != null && !takeOrderItems.isEmpty()){
            takeOrderItemService.save(takeOrderItems);
        }

        // 更新的话不需要更改 创建者和创建时间
        takeOrderService.save(entityUtil.updateInfoDefault(takeOrder, null, admin.getId(), false));
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteTakeOrder")
    public ResultVo deleteTakeOrder(@RequestBody TakeOrder takeOrder, @SessionAttribute("admin") Admin admin) {

        // 先删除明细，在删除总单
        List<TakeOrderItem> takeOrderItems = takeOrder.getTakeOrderItems();
        if(takeOrderItems != null && !takeOrderItems.isEmpty()){
            takeOrderItemService.delete(takeOrderItems);
        }

        takeOrderService.delete(takeOrder);
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteTakeOrders")
    public ResultVo deleteTakeOrders(@RequestBody List<TakeOrder> takeOrders, @SessionAttribute("admin") Admin admin) {

        // 先删除明细，在删除总单
        takeOrders.forEach(takeOrder -> {
            List<TakeOrderItem> takeOrderItems = takeOrder.getTakeOrderItems();
            if(takeOrderItems != null && !takeOrderItems.isEmpty()){
                takeOrderItemService.delete(takeOrderItems);
            }
        });

        takeOrderService.delete(takeOrders);
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    /**
     * 参数 page ,size 是一定要有的 ，另外两个可以默认
     *
     * @param page      第几页
     * @param size      每页的包含多少纪录
     * @param direction 按照顺序还是逆序排列 （ASC 或者 DESC）
     * @param property  按照什么排列
     * @return
     */
    @GetMapping(value = "/findAllTakeOrders")
    public ResultVo findAllTakeOrders(@RequestParam(required = true) Integer page,
                                      @RequestParam(required = false, defaultValue = "20") Integer size,
                                      @RequestParam(required = false, defaultValue = "DESC") String direction,
                                      @RequestParam(required = false, defaultValue = "updateTime") String property) {

        // 配置分页信息
        PageRequest pager = null;
        if (direction.equals("ASC")) {
            pager = new PageRequest(page, size, Sort.Direction.ASC, property);
        }
        if (direction.equals("DESC")) {
            pager = new PageRequest(page, size, Sort.Direction.DESC, property);
        }

        Page<TakeOrder> takeOrders = takeOrderService.findAll(pager);

        takeOrders.forEach(takeOrder -> {
            setTakeOrders(takeOrder);
        });

        return simpleResult(ResultEnum.SUCCESS, takeOrders);
    }


    /**
     *  防止代码重复的工具代码
     * @param takeOrder
     */
    public void setTakeOrders(TakeOrder takeOrder){
        List<TakeOrderItem> takeOrderItems =takeOrderItemService.findByOrderId(takeOrder.getId());
        if(takeOrderItems != null && !takeOrderItems.isEmpty()){
            takeOrder.setTakeOrderItems(takeOrderItems);
        }
    }

}
