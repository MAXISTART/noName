package com.school.store.admin.buy.controller;
import com.school.store.admin.buy.entity.BuyOrder;
import com.school.store.admin.buy.entity.BuyOrderItem;
import com.school.store.admin.buy.service.BuyOrderItemService;
import com.school.store.admin.buy.service.BuyOrderService;
import com.school.store.admin.refine.EntityRefineService;
import com.school.store.admin.store.controller.StoreOperationController;
import com.school.store.admin.store.entity.StoreOperation;
import com.school.store.admin.store.entity.StoreOperationItem;
import com.school.store.annotation.Permiss;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.constant.Permit;
import com.school.store.enums.ResultEnum;
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
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin/buyOrder")
@Permiss(and = {Permit.ADMIN})
public class BuyOrderController extends BaseAdminController {

    @Autowired
    private BuyOrderService buyOrderService;

    @Autowired
    private BuyOrderItemService buyOrderItemService;

    @Autowired
    private StoreOperationController storeOperationController;


    @Autowired
    private EntityRefineService entityRefineService;


    @GetMapping("/testChangeBuyOrder2StoreOperation")
    public StoreOperation testChangeBuyOrder2StoreOperation(){
        return changeBuyOrder2StoreOperation(findBuyOrderById("4028fbdf6140fc96016140fcc9f30000"));
    }


    @Transactional(readOnly = false)
    @PostMapping("/testPermission")
    @Permiss(and = {Permit.USER} )
    public String testPermission(@RequestBody BuyOrder buyOrder){
        return null;
    }

    @GetMapping("/testPermission2")
    public String testPermission2(){
        return null;
    }

    /**
     *  这是提供给用户的接口，并不是给管理员用的，requestorId要前台传过来的
     * @param buyOrder
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping("/addBuyOrder")
    @Permiss(and = {Permit.USER} )
    public ResultVo addBuyOrder(@RequestBody BuyOrder buyOrder) {

        buyOrder.setId(null);
        // 默认用户提交的是未审核的采购表
        buyOrder.setApprovalResult(2);
        buyOrder.setRequestTime(new Date());
        // 计算总价
        List<BuyOrderItem> buyOrderItems = buyOrder.getBuyOrderItems();
        BigDecimal totalPrice = new BigDecimal("0");

        for(BuyOrderItem buyOrderItem : buyOrderItems){
            BigDecimal temp = buyOrderItem.getPrice().multiply(new BigDecimal(buyOrderItem.getNumber()));
            totalPrice = totalPrice.add(temp);
        }
        buyOrder.setRequestTotalPrice(totalPrice);

        buyOrderService.save(buyOrder);

        // 保存明细内容，但是要先设置ID
        if(buyOrderItems != null && !buyOrderItems.isEmpty()){
            buyOrder.getBuyOrderItems().forEach(buyOrderItem -> {
                buyOrderItem.setOrderId(buyOrder.getId());
            });
            buyOrderItemService.save(buyOrderItems);
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
     * @param buyOrders
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping(value = "/approve")
    @Permiss(newAnd = { Permit.TEST })
    public ResultVo approve(@RequestBody List<BuyOrder> buyOrders) {
        buyOrders.forEach(buyOrder -> {
            BuyOrder buyOrderForSql = buyOrderService.findById(buyOrder.getId());
            buyOrderForSql.setApprovalResult(buyOrder.getApprovalResult());
            buyOrderForSql.setApprovalTime(new Date());
            buyOrderService.dynamicUpdate(buyOrderForSql);
        });
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    /**
     *  快速入库，要先检查是否已经审批了，如果已经审批了才能创建入库单
     *  同样是支持批量快速入库
     *  主要做法是先把buyOrder转化为StoreOperation，同时设置disciption等信息
     *  然后再调用StoreItemController的 addStoreOperation 的接口
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping(value = "/quickInput")
    public ResultVo quickInput( @RequestBody List<String> buyOrderIds) {

        List<StoreOperation> storeOperations = new ArrayList<>();
        for(String buyOrderId : buyOrderIds){
            storeOperations.add(changeBuyOrder2StoreOperation(findBuyOrderById(buyOrderId)));
        }
        // 测试用的
        //return storeOperationController.testAddStoreOperations(storeOperations);
        return storeOperationController.addStoreOperations(storeOperations);
    }




    @Transactional(readOnly = false)
    @PostMapping(value = "/updateBuyOrder")
    public ResultVo updateBuyOrder(@RequestBody BuyOrder buyOrder) {

        // 如果审核结果已经出来了，那就不能进行修改了
        int approvalResult = buyOrderService.findById(buyOrder.getId()).getApprovalResult();
        if(approvalResult != 2){
            // 如果审核结果不是未审核，那么就不能继续操作
            return simpleResult(ResultEnum.RESULT_OUT, null);
        }

        // 先删除明细内容，因为明细有可能变少了或者变多了
        List<BuyOrderItem> buyOrderItems = buyOrderItemService.findByOrderId(buyOrder.getId());
        buyOrderItemService.delete(buyOrderItems);
        // 然后再保存新明细
        buyOrderItems = buyOrder.getBuyOrderItems();
        if(buyOrderItems != null && !buyOrderItems.isEmpty()){
            buyOrderItems.forEach(buyOrderItem -> {
                // 置id为null，保证存进去的id是由数据库自增的
                buyOrderItem.setId(null);
                buyOrderItem.setOrderId(buyOrder.getId());
            });
            buyOrderItemService.save(buyOrderItems);
        }
        // 更新的话不需要更改 创建者和创建时间
        buyOrderService.dynamicUpdate(buyOrder);
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteBuyOrder")
    public ResultVo deleteBuyOrder(@RequestBody BuyOrder buyOrder) {
        // 级联删除
        buyOrderService.cascadeDelete(buyOrder);

        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteBuyOrders")
    public ResultVo deleteBuyOrders(@RequestBody List<BuyOrder> buyOrders) {

        // 级联删除
        buyOrders.forEach(buyOrder -> {
            buyOrderService.cascadeDelete(buyOrder);
        });

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
                                     @RequestParam(required = false, defaultValue = "lastmodifiedTime") String property) {

        // 配置分页信息
        PageRequest pager = null;
        if (direction.equals("ASC")) {
            pager = new PageRequest(page, size, Sort.Direction.ASC, property);
        }
        if (direction.equals("DESC")) {
            pager = new PageRequest(page, size, Sort.Direction.DESC, property);
        }

        Page<BuyOrder> buyOrders = buyOrderService.findAll(pager);

        entityRefineService.refinePage(buyOrders);

        return simpleResult(ResultEnum.SUCCESS, buyOrders);
    }



    /**
     *  findById
     */
    @PostMapping("/findBuyOrderById")
    public BuyOrder findBuyOrderById(@RequestParam  String buyOrderId){
        BuyOrder buyOrder = buyOrderService.findById(buyOrderId);
        entityRefineService.refine(buyOrder);
        return buyOrder;
    }


    /**
     *  转换buyOrder 和 StoreOperation
     */
    public StoreOperation changeBuyOrder2StoreOperation(BuyOrder buyOrder){

        //Map<String, Object> buyOrderMap = MyBeanUtil.transBean2Map(buyOrder);
        StoreOperation storeOperation = new StoreOperation();
        MyBeanUtil.copyProperties(buyOrder, storeOperation);
        // 明细也一起复制过去
        List<StoreOperationItem> storeOperationItems = new ArrayList<>();
        MyBeanUtil.copyPropertiesList(buyOrder.getBuyOrderItems(),storeOperationItems, StoreOperationItem.class);
        storeOperation.setStoreOperationItems(storeOperationItems);
        // 设置类型为 带审批的入库操作
        storeOperation.setType(1);
        storeOperation.setOpinion("管理员从采购那里进行快速入库");
        // 一定要让id置null，这样才会增加纪录
        storeOperation.setId(null);

        return storeOperation;
    }


}