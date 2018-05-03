package com.school.store.admin.take.controller;

import com.school.store.admin.department.entity.Department;
import com.school.store.admin.department.service.DepartmentService;
import com.school.store.admin.refine.EntityRefineService;
import com.school.store.admin.store.controller.StoreItemController;
import com.school.store.admin.store.controller.StoreOperationController;
import com.school.store.admin.store.entity.StoreOperation;
import com.school.store.admin.store.entity.StoreOperationItem;
import com.school.store.admin.take.entity.TakeOrder;
import com.school.store.admin.take.entity.TakeOrderItem;
import com.school.store.admin.take.service.TakeOrderItemService;
import com.school.store.admin.take.service.TakeOrderService;
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
import lombok.extern.log4j.Log4j;
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
@RequestMapping("/admin/takeOrder")
@Permiss(and = Permit.ADMIN)
public class TakeOrderController extends BaseAdminController {


    @Autowired
    PermissionAspect permissionAspect;

    @Autowired
    private TakeOrderService takeOrderService;

    @Autowired
    private TakeOrderItemService takeOrderItemService;

    @Autowired
    private StoreItemController storeItemController;

    @Autowired
    private StoreOperationController storeOperationController;

    @Autowired
    private EntityRefineService entityRefineService;

    @Autowired
    private DepartmentService departmentService;

    /**
     *  这是提供给用户的接口，并不是给管理员用的，用户通过这个进行申领表的创建,requestorId要前台传过来
     * @param takeOrder
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping("/addTakeOrder")
    @Permiss(newOr = {Permit.USER, Permit.ADMIN})
    public ResultVo addTakeOrder(@RequestBody TakeOrder takeOrder) {

        if(takeOrder.getTakeOrderItems() == null || takeOrder.getTakeOrderItems().isEmpty()){
            throw new BaseException(ResultEnum.ITEMS_NOT_NULL);
        }

        takeOrder.setId(null);

        // 默认用户提交的是未审核的申领表
        takeOrder.setApprovalResult(2);
        takeOrder.setRequestTime(new Date());
        takeOrder.setHasBeenOutput(0);
        // 计算总价
        List<TakeOrderItem> takeOrderItems = takeOrder.getTakeOrderItems();
        BigDecimal totalPrice = new BigDecimal("0");

        for(TakeOrderItem takeOrderItem : takeOrderItems){
            BigDecimal temp = takeOrderItem.getPrice().multiply(takeOrderItem.getNumber());
            totalPrice = totalPrice.add(temp);
        }
        takeOrder.setRequestTotalPrice(totalPrice);
        takeOrder.setRequestorId(HttpUtil.getSessionUserId());
        takeOrderService.save(takeOrder);

        if(takeOrderItems != null && !takeOrderItems.isEmpty()){
            for (TakeOrderItem takeOrderItem : takeOrderItems){
                if(storeItemController.checkNumber(takeOrderItem.getGoodId(), takeOrderItem.getNumber())){
                    takeOrderItem.setOrderId(takeOrder.getId());
                    // 添加申请的数量
                    storeItemController.addLockNumber(takeOrderItem.getGoodId(), takeOrderItem.getNumber());
                    // 设置returnNumber
                    takeOrderItem.setReturnNumber(0);
                }else{
                    // 事务自动回滚
                    System.out.println(" takeOrder insert fail");
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
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping(value = "/approve")
    public ResultVo approve(@RequestBody List<TakeOrder> takeOrders) {
        takeOrders.forEach(takeOrder -> {
            TakeOrder takeOrderForSql = takeOrderService.findById(takeOrder.getId());
            takeOrderForSql.setApprovalResult(takeOrder.getApprovalResult());
            takeOrderForSql.setApprovalTime(new Date());
            takeOrderService.dynamicUpdate(takeOrderForSql);
        });
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
    public ResultVo quickOutput( @RequestBody List<String> takeOrderIds) {
        List<StoreOperation> storeOperations = new ArrayList<>();
        for (String takeOrderId : takeOrderIds) {
            TakeOrder takeOrder = findTakeOrderById(takeOrderId);
            if(takeOrder.getHasBeenOutput() == null || takeOrder.getHasBeenOutput()!= 1){
                // 已经入过一次库的不会再入库了，但是还没入库的会设置该标志
                takeOrder.setHasBeenOutput(1);
                takeOrderService.saveOrUpdate(takeOrder);
                storeOperations.add(changeTakeOrder2StoreOperation(takeOrder));
            }
        }
        // 测试用的
        //return storeOperationController.testAddStoreOperations(storeOperations);
        return storeOperationController.addStoreOperations(storeOperations);
    }


    /**
     *  这个是用户端的操作，是会影响到库存中的锁存操作的
     * @param takeOrder
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping(value = "/updateTakeOrder")
    public ResultVo updateTakeOrder(@RequestBody TakeOrder takeOrder) {

        // 如果审核结果已经出来了，那就不能进行修改了
        int approvalResult = takeOrderService.findById(takeOrder.getId()).getApprovalResult();
        if(approvalResult != 2){
            // 如果审核结果不是未审核，那么就不能继续操作
            return simpleResult(ResultEnum.RESULT_OUT, null);
        }

        // 先将明细中的内容全部释放lockNumber然后删除明细内容，因为明细有可能变少了或者变多了
        List<TakeOrderItem> takeOrderItems = takeOrderItemService.findByOrderId(takeOrder.getId());
        for(TakeOrderItem takeOrderItem : takeOrderItems){
            storeItemController.reduceLockNumber(takeOrderItem.getGoodId(), takeOrderItem.getNumber());
        }
        takeOrderItemService.delete(takeOrderItems);

        // 然后再保存新明细
        takeOrderItems = takeOrder.getTakeOrderItems();
        if(takeOrderItems != null && !takeOrderItems.isEmpty()){
            takeOrderItems.forEach(takeOrderItem -> {
                // 置id为null，保证存进去的id是由数据库自增的
                takeOrderItem.setId(null);
                takeOrderItem.setOrderId(takeOrder.getId());
                // 添加库存中的锁定数量（申请数量）
                storeItemController.addLockNumber(takeOrderItem.getGoodId(), takeOrderItem.getNumber());
            });
            takeOrderItemService.save(takeOrderItems);
        }
        // 更新的话不需要更改 创建者和创建时间
        takeOrderService.dynamicUpdate(takeOrder);
        return simpleResult(ResultEnum.SUCCESS, null);
    }



    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteTakeOrder")
    public ResultVo deleteTakeOrder(@RequestBody TakeOrder takeOrder) {
        // 级联删除
        takeOrderService.cascadeDelete(takeOrder);
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteTakeOrders")
    public ResultVo deleteTakeOrders(@RequestBody List<TakeOrder> takeOrders) {

        takeOrders.forEach(takeOrder -> {
            // 级联删除
            takeOrderService.cascadeDelete(takeOrder);
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
    @GetMapping(value = "/findAllTakeOrders")
    public ResultVo findAllTakeOrders(@RequestParam(required = true) Integer page,
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

        Page<TakeOrder> takeOrders = takeOrderService.findAll(pager);

        entityRefineService.refinePage(takeOrders);

        return simpleResult(ResultEnum.SUCCESS, takeOrders);
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
    @PostMapping(value = "/findTakeOrdersBySearchParams")
    @Permiss(newOr = {Permit.USER, Permit.ADMIN})
    public ResultVo findTakeOrdersBySearchParams(@RequestParam(required = true) Integer page,
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
            sqlParams.put(" AND departmentId = ? ");
            sqlParams.putValue(departmentId);
        }
        if (!requestorId.equals("allRequestor") && !StringUtils.isEmpty(requestorId)) {
            sqlParams.put(" AND requestorId = ? ");
            sqlParams.putValue(requestorId);
        }
        if (!requestTime_start.equals("requestTime_all") && !StringUtils.isEmpty(requestTime_start)) {
            sqlParams.put(" AND requestTime >= ? ");
            sqlParams.putValue(requestTime_start);
        }
        if (!requestTime_end.equals("requestTime_all") && !StringUtils.isEmpty(requestTime_end)) {
            sqlParams.put(" AND requestTime <= ? ");
            sqlParams.putValue(requestTime_end);
        }
        if (!price_start.equals("allPrice") && !StringUtils.isEmpty(price_start)) {
            sqlParams.put(" AND requestTotalPrice >= ? ");
            sqlParams.putValue(price_start);
        }
        if (!price_end.equals("allPrice") && !StringUtils.isEmpty(price_end)) {
            sqlParams.put(" AND requestTotalPrice <= ? ");
            sqlParams.putValue(price_end);
        }
        if (!approvalResult.equals("allApprovalResult") && !StringUtils.isEmpty(approvalResult)) {
            if(approvalResult.trim().equals("-1")){
                // 当等于-1时表示获取已经审核的（无论是不是审核通过）
                sqlParams.put(" AND (approvalResult = ? OR approvalResult = ?) ");
                sqlParams.putValue("1");
                sqlParams.putValue("0");
            }else{
                sqlParams.put(" AND approvalResult = ? ");
                sqlParams.putValue(approvalResult);
            }
        }


        sqlParams.put(" ORDER BY " + property + " " + direction);
        // 返回的是真正的List<User>
        MPager<TakeOrder> takeOrders = takeOrderService.findByDynamicSqlParams(sqlParams, page, size, TakeOrder.class);
        // 给每个user设置他们对应的departmentName
        entityRefineService.refineList(takeOrders.getData());
        return simpleResult(ResultEnum.SUCCESS, takeOrders);
    }



    /**
     *  findById
     */
    @PostMapping("/findTakeOrderById")
    public TakeOrder findTakeOrderById(@RequestParam  String takeOrderId){
        TakeOrder takeOrder = takeOrderService.findById(takeOrderId);
        entityRefineService.refine(takeOrder);
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
