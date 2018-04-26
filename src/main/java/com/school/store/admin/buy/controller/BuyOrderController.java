package com.school.store.admin.buy.controller;

import com.school.store.admin.buy.entity.BuyOrder;
import com.school.store.admin.buy.entity.BuyOrderItem;
import com.school.store.admin.buy.service.BuyOrderItemService;
import com.school.store.admin.buy.service.BuyOrderService;
import com.school.store.admin.department.entity.Department;
import com.school.store.admin.department.service.DepartmentService;
import com.school.store.admin.good.controller.GoodController;
import com.school.store.admin.good.entity.GoodItem;
import com.school.store.admin.good.service.GoodItemService;
import com.school.store.admin.refine.EntityRefineService;
import com.school.store.admin.store.controller.StoreOperationController;
import com.school.store.admin.store.entity.StoreOperation;
import com.school.store.admin.store.entity.StoreOperationItem;
import com.school.store.admin.user.entity.User;
import com.school.store.annotation.Permiss;
import com.school.store.aspect.PermissionAspect;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.base.model.MPager;
import com.school.store.base.model.SqlParams;
import com.school.store.constant.Permit;
import com.school.store.enums.ResultEnum;
import com.school.store.exception.BaseException;
import com.school.store.utils.HttpUtil;
import com.school.store.utils.MyBeanUtil;
import com.school.store.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin/buyOrder")
@Permiss(and = {Permit.ADMIN})
@Slf4j
public class BuyOrderController extends BaseAdminController {

    @Autowired
    PermissionAspect permissionAspect;
    @Autowired
    private BuyOrderService buyOrderService;
    @Autowired
    private BuyOrderItemService buyOrderItemService;
    @Autowired
    private StoreOperationController storeOperationController;

    @Autowired
    private EntityRefineService entityRefineService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private GoodItemService goodItemService;

    @Autowired
    private GoodController goodController;

    @GetMapping("/testChangeBuyOrder2StoreOperation")
    public StoreOperation testChangeBuyOrder2StoreOperation() {
        return changeBuyOrder2StoreOperation(findBuyOrderById("4028fbdf6140fc96016140fcc9f30000"));
    }


    /**
     * 这是提供给用户的接口，并不是给管理员用的，requestorId要前台传过来的
     * 虽然是提供给用户，但是也是允许管理员来申请的。
     *
     * @param buyOrder
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping("/addBuyOrder")
    @Permiss(newOr = {Permit.USER, Permit.ADMIN})
    public ResultVo addBuyOrder(@RequestBody BuyOrder buyOrder) {

        if(buyOrder.getBuyOrderItems() == null || buyOrder.getBuyOrderItems().isEmpty()){
            throw new BaseException(ResultEnum.ITEMS_NOT_NULL);
        }

        buyOrder.setId(null);
        // 默认用户提交的是未审核的采购表
        buyOrder.setApprovalResult(2);
        buyOrder.setRequestTime(new Date());
        buyOrder.setHasBeenInput(0);
        // 计算总价
        List<BuyOrderItem> buyOrderItems = buyOrder.getBuyOrderItems();
        BigDecimal totalPrice = new BigDecimal("0");

        for (BuyOrderItem buyOrderItem : buyOrderItems) {
            BigDecimal temp = buyOrderItem.getPrice().multiply(buyOrderItem.getNumber());
            totalPrice = totalPrice.add(temp);
        }
        buyOrder.setRequestTotalPrice(totalPrice);
        log.warn("userId 是 " + HttpUtil.getSessionUserId());
        buyOrder.setRequestorId(HttpUtil.getSessionUserId());
        buyOrderService.save(buyOrder);

        // 保存明细内容，但是要先设置ID
        if (buyOrderItems != null && !buyOrderItems.isEmpty()) {
            buyOrder.getBuyOrderItems().forEach(buyOrderItem -> {
                buyOrderItem.setOrderId(buyOrder.getId());
            });
            buyOrderItemService.save(buyOrderItems);
        }
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    /**
     * 管理员点击审核通过会执行下面的方法，
     * 传递的参数格式如下，千万别多别少
     * [
     * {
     * "id":"",
     * "approvvalResult":""
     * },
     * {...},
     * ]
     *
     * @param buyOrders
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping(value = "/approve")
    public ResultVo approve(@RequestBody List<BuyOrder> buyOrders) {
        buyOrders.forEach(buyOrder -> {
            buyOrderService.dynamicUpdate(buyOrder);
        });
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    /**
     * 快速入库，要先检查是否已经审批了，如果已经审批了才能创建入库单
     * 同样是支持批量快速入库
     * 主要做法是先把buyOrder转化为StoreOperation，同时设置disciption等信息
     * 然后再调用StoreItemController的 addStoreOperation 的接口
     *
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping(value = "/quickInput")
    public ResultVo quickInput(@RequestBody List<String> buyOrderIds) {

        List<StoreOperation> storeOperations = new ArrayList<>();
        for (String buyOrderId : buyOrderIds) {
            BuyOrder buyOrder = findBuyOrderById(buyOrderId);
            if(buyOrder.getHasBeenInput() == null || buyOrder.getHasBeenInput()!= 1){
                // 已经入过一次库的不会再入库了，但是还没入库的会设置该标志
                buyOrder.setHasBeenInput(1);
                buyOrderService.saveOrUpdate(buyOrder);
                storeOperations.add(changeBuyOrder2StoreOperation(buyOrder));
            }
        }
        // 测试用的
        //return storeOperationController.testAddStoreOperations(storeOperations);
        return storeOperationController.addStoreOperations(storeOperations);
    }


    @Transactional(readOnly = false)
    @PostMapping(value = "/updateBuyOrder")
    @Permiss(need = false)
    public ResultVo updateBuyOrder(@RequestBody BuyOrder buyOrder) {


        // 如果审核结果已经出来了，那就不能进行修改了
        int approvalResult = buyOrderService.findById(buyOrder.getId()).getApprovalResult();
        if (approvalResult != 2) {
            // 如果审核结果不是未审核，那么就不能继续操作
            return simpleResult(ResultEnum.RESULT_OUT, null);
        }

        // 先删除明细内容，因为明细有可能变少了或者变多了
        List<BuyOrderItem> buyOrderItems = buyOrderItemService.findByOrderId(buyOrder.getId());
        buyOrderItemService.delete(buyOrderItems);
        // 然后再保存新明细
        buyOrderItems = buyOrder.getBuyOrderItems();
        if (buyOrderItems != null && !buyOrderItems.isEmpty()) {
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
     * 以表单 form 形式 传递参数
     *
     * @param page
     * @param size
     * @param direction
     * @param property
     * @return
     */
    @PostMapping(value = "/findBuyOrdersBySearchParams")
    @Permiss(newOr = {Permit.USER, Permit.ADMIN})
    public ResultVo findBuyOrdersBySearchParams(@RequestParam(required = true) Integer page,
                                                @RequestParam(required = false, defaultValue = "20") Integer size,
                                                @RequestParam(required = false, defaultValue = "DESC") String direction,
                                                @RequestParam(required = false, defaultValue = "lastmodifiedTime") String property,
                                                @RequestParam(required = false, defaultValue = "allDepartment") String departmentId,
                                                @RequestParam(required = false, defaultValue = "allRequestor") String requestorId,
                                                @RequestParam(required = false, defaultValue = "requestTime_all") String requestTime_start,
                                                @RequestParam(required = false, defaultValue = "requestTime_all") String requestTime_end,
                                                @RequestParam(required = false, defaultValue = "allPrice") String price_start,
                                                @RequestParam(required = false, defaultValue = "allPrice") String price_end,
                                                @RequestParam(required = false, defaultValue = "allApprovalResult") String approvalResult
    ) {

        // 先判断当前用户是user还是admin
        boolean isUser = permissionAspect.hasPermission(HttpUtil.getSessionPermissions(), Permit.USER);
        boolean isAdmin = permissionAspect.hasPermission(HttpUtil.getSessionPermissions(), Permit.ADMIN);
        if(isUser && !isAdmin){
            // 如果只有user权限而没有管理员权限，那么就必须判断requestorId
            if(!requestorId.equals(HttpUtil.getSessionUserId())){
                throw new BaseException(ResultEnum.PERMISSION_NOT_ALLOWED);
            }
        }

        SqlParams sqlParams = new SqlParams();
        if (!departmentId.equals("allDepartment") && !StringUtils.isEmpty(departmentId)) {
            sqlParams.put("AND", "departmentId", "=");
            sqlParams.putValue(departmentId);
        }
        if (!requestorId.equals("allRequestor") && !StringUtils.isEmpty(requestorId)) {
            log.warn("requestorId : " + requestorId);
            sqlParams.put("AND", "requestorId", "=");
            sqlParams.putValue(requestorId);
        }
        if (!requestTime_start.equals("requestTime_all") && !StringUtils.isEmpty(requestTime_start)) {
            sqlParams.put("AND", "requestTime", ">=");
            sqlParams.putValue(requestTime_start);
        }
        if (!requestTime_end.equals("requestTime_all") && !StringUtils.isEmpty(requestTime_end)) {
            sqlParams.put("AND", "requestTime", "<=");
            sqlParams.putValue(requestTime_end);
        }
        if (!price_start.equals("allPrice") && !StringUtils.isEmpty(price_start)) {
            sqlParams.put("AND", "requestTotalPrice", ">=");
            sqlParams.putValue(price_start);
        }
        if (!price_end.equals("allPrice") && !StringUtils.isEmpty(price_end)) {
            sqlParams.put("AND", "requestTotalPrice", "<=");
            sqlParams.putValue(price_end);
        }
        if (!approvalResult.equals("allApprovalResult") && !StringUtils.isEmpty(approvalResult)) {
            if(approvalResult.trim().equals("-1")){
                // 当等于-1时表示获取已经审核的（无论是不是审核通过）
                sqlParams.put("OR", "approvalResult", "=");
                sqlParams.putValue("1");
                sqlParams.put("OR", "approvalResult", "=");
                sqlParams.putValue("0");
            }else{
                sqlParams.put("AND", "approvalResult", "=");
                sqlParams.putValue(approvalResult);
            }
        }


        sqlParams.put("ORDER BY", property, direction);
        // 返回的是真正的List<User>
        MPager<BuyOrder> buyOrders = buyOrderService.findByDynamicSqlParams(sqlParams, page, size, BuyOrder.class);
        // 给每个user设置他们对应的departmentName
        entityRefineService.refineList(buyOrders.getData());
        return simpleResult(ResultEnum.SUCCESS, buyOrders);
    }


    /**
     * findById
     */
    @PostMapping("/findBuyOrderById")
    public BuyOrder findBuyOrderById(@RequestParam String buyOrderId) {
        BuyOrder buyOrder = buyOrderService.findById(buyOrderId);
        entityRefineService.refine(buyOrder);
        return buyOrder;
    }


    /**
     * 转换buyOrder 和 StoreOperation
     */
    public StoreOperation changeBuyOrder2StoreOperation(BuyOrder buyOrder) {

        //Map<String, Object> buyOrderMap = MyBeanUtil.transBean2Map(buyOrder);
        StoreOperation storeOperation = new StoreOperation();
        MyBeanUtil.copyProperties(buyOrder, storeOperation);
        // 明细也一起复制过去
        List<StoreOperationItem> storeOperationItems = new ArrayList<>();
        MyBeanUtil.copyPropertiesList(buyOrder.getBuyOrderItems(), storeOperationItems, StoreOperationItem.class);
        storeOperation.setStoreOperationItems(storeOperationItems);
        // 设置类型为 带审批的入库操作
        storeOperation.setType(1);
        storeOperation.setOpinion("管理员从采购那里进行快速入库");
        // 一定要让id置null，这样才会增加纪录
        storeOperation.setId(null);

        return storeOperation;
    }


}