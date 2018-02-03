package com.school.store.admin.take.controller;

import com.school.store.admin.admin.entity.Admin;
import com.school.store.admin.buy.entity.BuyOrderItem;
import com.school.store.admin.store.controller.StoreItemController;
import com.school.store.admin.store.controller.StoreOperationController;
import com.school.store.admin.store.entity.StoreItem;
import com.school.store.admin.store.entity.StoreOperation;
import com.school.store.admin.store.entity.StoreOperationItem;
import com.school.store.admin.store.service.StoreItemService;
import com.school.store.admin.take.entity.TakeOrder;
import com.school.store.admin.take.entity.TakeOrderItem;
import com.school.store.admin.take.service.TakeOrderItemService;
import com.school.store.admin.take.service.TakeOrderService;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.enums.ResultEnum;
import com.school.store.exception.BaseException;
import com.school.store.utils.MyBeanUtil;
import com.school.store.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/takeOrder")
public class TakeOrderController extends BaseAdminController {

    @Autowired
    private TakeOrderService takeOrderService;

    @Autowired
    private TakeOrderItemService takeOrderItemService;

    @Autowired
    private StoreItemController storeItemController;

    @Autowired
    private StoreOperationController storeOperationController;

    /**
     *  这是提供给用户的接口，并不是给管理员用的，用户通过这个进行申领表的创建,requestorId要前台传过来
     * @param takeOrder
     * @param admin
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping("/addTakeOrder")
    public ResultVo addTakeOrder(@RequestBody TakeOrder takeOrder, @SessionAttribute("admin") Admin admin) {

        // 默认用户提交的是未审核的申领表
        takeOrder.setApprovalResult(2);
        takeOrder.setRequestTime(entityUtil.getNowDate());
        // 计算总价
        List<TakeOrderItem> takeOrderItems = takeOrder.getTakeOrderItems();
        BigDecimal totalPrice = new BigDecimal("0");

        for(TakeOrderItem takeOrderItem : takeOrderItems){
            BigDecimal temp = takeOrderItem.getPrice().multiply(new BigDecimal(takeOrderItem.getNumber()));
            totalPrice = totalPrice.add(temp);
        }
        takeOrder.setRequestTotalPrice(totalPrice);

        takeOrderService.save(entityUtil.updateInfoDefault(takeOrder, admin.getId(), admin.getId(), true));


        if(takeOrderItems != null && !takeOrderItems.isEmpty()){
            for (TakeOrderItem takeOrderItem : takeOrderItems){
                if(storeItemController.checkNumber(takeOrderItem.getGoodId(), takeOrderItem.getNumber())){
                    takeOrderItem.setOrderId(takeOrder.getId());
                    // 锁住部分库存先
                    storeItemController.addLockNumber(takeOrderItem.getGoodId(), takeOrderItem.getNumber());
                    // 设置returnNumber
                    takeOrderItem.setReturnNumber(0);
                    // 更新创建时间
                    entityUtil.updateInfoDefault(takeOrderItem, admin.getId(), admin.getId(), true);
                }else{
                    // 事务自动回滚
                    throw new BaseException(ResultEnum.STORE_UNSATISFY);
                }
            }
            takeOrderItemService.save(takeOrderItems);
        }

        return simpleResult(ResultEnum.SUCCESS, null);
    }





    /**
     *  管理员点击审核通过会执行下面的方法，
     *  传递的参数格式如下，千万别多别少
     *  [
     *      {
     *          "id":"",
     *          "approvvalResult":""
     *      },
     *      {...},
     *  ]
     * @param takeOrders
     * @param admin
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping(value = "/approve")
    public ResultVo approve(@RequestBody List<TakeOrder> takeOrders, @SessionAttribute("admin") Admin admin) {
        List<TakeOrder> takeOrdersForSql = new ArrayList<>();
        takeOrders.forEach(takeOrder -> {
            TakeOrder takeOrderForSql = takeOrderService.findById(takeOrder.getId());
            takeOrderForSql.setApprovalResult(takeOrder.getApprovalResult());
            takeOrderForSql.setApprovalTime(entityUtil.getNowDate());
            takeOrderForSql = entityUtil.updateInfoDefault(takeOrderForSql, admin.getId(), admin.getId(), true);
            takeOrdersForSql.add(takeOrderForSql);
        });
        takeOrderService.save(takeOrdersForSql);
        return simpleResult(ResultEnum.SUCCESS, null);
    }





    /**
     *  快速出库，要先检查是否已经审批了，如果已经审批了才能创建入库单
     *  同样是支持批量快速入库
     *  主要做法是先把takeOrder转化为StoreOperation，同时设置disciption等信息
     *  然后再调用StoreItemController的 addStoreOperation 的接口
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping(value = "/quickOutput")
    public ResultVo quickOutput( @RequestBody List<String> takeOrderIds, @SessionAttribute("admin") Admin admin) {

        List<StoreOperation> storeOperations = new ArrayList<>();
        for(String takeOrderId : takeOrderIds){
            storeOperations.add(changeTakeOrder2StoreOperation(findTakeOrderById(takeOrderId)));
        }
        // 测试用的
        //return storeOperationController.testAddStoreOperations(storeOperations);
        return storeOperationController.addStoreOperations(storeOperations, admin);
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




    /**
     *  findById
     */
    @PostMapping("/findTakeOrderById")
    public TakeOrder findTakeOrderById(@RequestParam  String takeOrderId){
        TakeOrder takeOrder = takeOrderService.findById(takeOrderId);
        setTakeOrders(takeOrder);
        return takeOrder;
    }


    /**
     *  转换buyOrder 和 StoreOperation
     */
    public StoreOperation changeTakeOrder2StoreOperation(TakeOrder takeOrder){

        StoreOperation storeOperation = new StoreOperation();
        MyBeanUtil.copyProperties(takeOrder, storeOperation);
        // 明细也一起复制过去
        List<StoreOperationItem> storeOperationItems = new ArrayList<>();
        MyBeanUtil.copyPropertiesList(takeOrder.getTakeOrderItems(),storeOperationItems, StoreOperationItem.class);
        storeOperation.setStoreOperationItems(storeOperationItems);
        // 设置类型为 带审批的出库操作
        storeOperation.setType(2);
        storeOperation.setOpinion("管理员从申领那里进行快速出库");
        // 一定要让id置null，这样才会增加纪录
        storeOperation.setId(null);

        return storeOperation;
    }
}
