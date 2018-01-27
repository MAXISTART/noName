package com.school.store.admin.buy.controller;
import com.school.store.admin.admin.entity.Admin;
import com.school.store.admin.buy.entity.BuyOrder;
import com.school.store.admin.buy.entity.BuyOrderItem;
import com.school.store.admin.buy.service.BuyOrderItemService;
import com.school.store.admin.buy.service.BuyOrderService;
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
@RequestMapping("/admin/buyOrder")
public class BuyOrderController extends BaseAdminController {

    @Autowired
    private BuyOrderService buyOrderService;

    @Autowired
    private BuyOrderItemService buyOrderItemService;

    @Transactional(readOnly = false)
    @PostMapping("/addBuyOrder")
    public ResultVo addBuyOrder(@RequestBody BuyOrder buyOrder, @SessionAttribute("admin") Admin admin) {

        buyOrderService.save(entityUtil.updateInfoDefault(buyOrder, admin.getId(), admin.getId(), true));

        // 保存明细内容，但是要先设置ID
        List<BuyOrderItem> buyOrderItems = buyOrder.getBuyOrderItems();
        if(buyOrderItems != null && !buyOrderItems.isEmpty()){
            buyOrder.getBuyOrderItems().forEach(buyOrderItem -> {
                buyOrderItem.setOrderId(buyOrder.getId());
            });
            buyOrderItemService.save(buyOrderItems);
        }

        Integer approvalResult =buyOrder.getApprovalResult();
        if(approvalResult!=null && approvalResult == 1){
            // 如果审批结果是通过的，就执行微信通知

        }
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @Transactional(readOnly = false)
    @PostMapping(value = "/updateBuyOrder")
    public ResultVo updateBuyOrder(@RequestBody BuyOrder buyOrder, @SessionAttribute("admin") Admin admin) {

        // 更新明细内容
        List<BuyOrderItem> buyOrderItems = buyOrder.getBuyOrderItems();
        if(buyOrderItems != null && !buyOrderItems.isEmpty()){
            buyOrderItemService.save(buyOrderItems);
        }

        // 更新的话不需要更改 创建者和创建时间
        buyOrderService.save(entityUtil.updateInfoDefault(buyOrder, null, admin.getId(), false));

        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteBuyOrder")
    public ResultVo deleteBuyOrder(@RequestBody BuyOrder buyOrder, @SessionAttribute("admin") Admin admin) {

        // 先删除明细，在删除总单
        List<BuyOrderItem> buyOrderItems = buyOrder.getBuyOrderItems();
        if(buyOrderItems != null && !buyOrderItems.isEmpty()){
            buyOrderItemService.delete(buyOrderItems);
        }

        buyOrderService.delete(buyOrder);

        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteBuyOrders")
    public ResultVo deleteBuyOrders(@RequestBody List<BuyOrder> buyOrders, @SessionAttribute("admin") Admin admin) {

        // 先删除明细，在删除总单
        buyOrders.forEach(buyOrder -> {
            List<BuyOrderItem> buyOrderItems = buyOrder.getBuyOrderItems();
            if(buyOrderItems != null && !buyOrderItems.isEmpty()){
                buyOrderItemService.delete(buyOrderItems);
            }
        });

        buyOrderService.delete(buyOrders);

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
    @GetMapping(value = "/findAllBuyOrders")
    public ResultVo findAllBuyOrders(@RequestParam(required = true) Integer page,
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

        Page<BuyOrder> buyOrders = buyOrderService.findAll(pager);

        buyOrders.forEach(buyOrder -> {
            setBuyOrderItems(buyOrder);
        });

        return simpleResult(ResultEnum.SUCCESS, buyOrders);
    }





    /**
     *  防止代码重复的工具代码
     * @param buyOrder
     */
    public void setBuyOrderItems(BuyOrder buyOrder){
        List<BuyOrderItem> buyOrderItems =buyOrderItemService.findByOrderId(buyOrder.getId());
        if(buyOrderItems != null && !buyOrderItems.isEmpty()){
            buyOrder.setBuyOrderItems(buyOrderItems);
        }
    }

}