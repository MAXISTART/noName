package com.school.store.admin.store.controller;

import com.school.store.admin.cache.CacheUtil;
import com.school.store.admin.good.entity.GoodItem;
import com.school.store.admin.refine.EntityRefineService;
import com.school.store.admin.store.entity.StoreItem;
import com.school.store.admin.store.entity.StoreOperation;
import com.school.store.admin.store.entity.StoreOperationItem;
import com.school.store.admin.store.service.StoreItemService;
import com.school.store.admin.store.service.StoreOperationItemService;
import com.school.store.admin.store.service.StoreOperationService;
import com.school.store.admin.user.entity.User;
import com.school.store.admin.user.service.UserService;
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
import org.apache.catalina.Store;
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
@RequestMapping("/admin/storeOperation")
@Permiss(and = Permit.ADMIN)
@Slf4j
public class StoreOperationController extends BaseAdminController {

    @Autowired
    private StoreOperationService storeOperationService;

    @Autowired
    private StoreOperationItemService storeOperationItemService;

    @Autowired
    private StoreItemController storeItemController;

    @Autowired
    private EntityRefineService entityRefineService;

    @Autowired
    private PermissionAspect permissionAspect;

    @Autowired
    private CacheUtil cacheUtil;

    @PostMapping("/testAddStoreOperation")
    public ResultVo testAddStoreOperation(@RequestBody StoreOperation storeOperation) {
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    // 系统级别的应用，供快速出入库使用
    @Permiss(need = false)
    @Transactional(readOnly = false)
    public ResultVo quickAddStoreOperation (StoreOperation storeOperation) {

        storeOperation.setId(null);
        // 快速出入库也只有管理员才能做到
        storeOperation.setApprovalResult(1);
        storeOperation.setOpinion("管理员自建入库单");
        storeOperation.setResponsorId(HttpUtil.getSessionId());
        storeOperation.setApprovalTime(new Date());
        // 计算一次总价
        if(storeOperation.getRequestTotalPrice() == null && storeOperation.getStoreOperationItems() != null){
            BigDecimal totalPrice = new BigDecimal("0");
            for (StoreOperationItem storeOperationItem : storeOperation.getStoreOperationItems()) {
                BigDecimal temp = storeOperationItem.getPrice().multiply(storeOperationItem.getNumber());
                totalPrice = totalPrice.add(temp);
                // 因为在创建申领单的时候就已经addLockNumber一次，所以这里不需要再添加。（假设是申领单的话）
            }
            storeOperation.setRequestTotalPrice(totalPrice);
        }
        storeOperationService.save(storeOperation);
        // 应用到库存中去
        applyStoreOperation(storeOperation);
        storeOperationItemService.save(storeOperation.getStoreOperationItems());
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @Transactional(readOnly = false)
    @PostMapping("/addStoreOperation")
    @Permiss(newOr = {Permit.USER, Permit.ADMIN})
    public ResultVo addStoreOperation(@RequestBody StoreOperation storeOperation) {

        // 这个方法供所有用户使用，不分管理员和普通用户，仅仅是添加采购单，如果有出库操作的话会addLockNumber
        storeOperation.setApprovalResult(2);

        if(storeOperation.getStoreOperationItems() == null || storeOperation.getStoreOperationItems().isEmpty()){
            throw new BaseException(ResultEnum.ITEMS_NOT_NULL);
        }
        storeOperation.setId(null);
        storeOperation.setRequestorId(HttpUtil.getSessionUserId());
        storeOperation.setRequestTime(new Date());
        // 计算一次总价
        if(storeOperation.getRequestTotalPrice() == null && storeOperation.getStoreOperationItems() != null){
            BigDecimal totalPrice = new BigDecimal("0");
            for (StoreOperationItem storeOperationItem : storeOperation.getStoreOperationItems()) {
                BigDecimal temp = storeOperationItem.getPrice().multiply(storeOperationItem.getNumber());
                totalPrice = totalPrice.add(temp);
                // 如果是出库类型的话，要先锁定
                if(storeOperation.getType() == 2){
                    storeItemController.addLockNumber(storeOperationItem.getGoodId(), storeOperationItem.getNumber());
                }
            }
            storeOperation.setRequestTotalPrice(totalPrice);
        }
        storeOperationService.save(storeOperation);
        // 下面这一步只是设置orderId
        applyStoreOperation(storeOperation);
        storeOperationItemService.save(storeOperation.getStoreOperationItems());
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    /**
     *  根据提供的storeOperation 以及 其对应的storeOperationItems来操作storeItem，这样分离出来是为了兼顾更新以及创建StoreOperation的问题
     * @param storeOperation
     */
    @Transactional(readOnly = false)
    @Permiss(need = false)
    // 这是系统级别的方法，不提供给任何用户，只是系统调用
    public void applyStoreOperation(StoreOperation storeOperation){
        List<StoreOperationItem> storeOperationItems = storeOperation.getStoreOperationItems();
        if (storeOperationItems != null) {
            for (StoreOperationItem storeOperationItem : storeOperationItems) {

                storeOperationItem.setOrderId(storeOperation.getId());

                if (storeOperation.getApprovalResult() == 1) {
                    // 审核通过是会修改库存的
                    switch (storeOperation.getType()) {
                        case 1:
                            // 如果是带审批的入库类型
                            storeItemController.addNumber(storeOperationItem.getGoodId(), storeOperationItem.getNumber());
                            break;
                        case 2:
                            // 如果是带审批的出库类型
                            if (storeItemController.reduceNumber(storeOperationItem.getGoodId(), storeOperationItem.getNumber())) {
                                // 解放锁住的lockNumber
                                storeItemController.reduceLockNumber(storeOperationItem.getGoodId(), storeOperationItem.getNumber());
                                break;
                            } else {
                                // 事务自动回滚
                                throw new BaseException(ResultEnum.STORE_UNSATISFY);
                            }
                    }
                }

                if(storeOperation.getApprovalResult() == 0){
                    // 如果是审核拒绝通过
                    switch (storeOperation.getType()) {
                        case 1:
                            // 如果是带审批的入库类型, 无任何影响
                            break;
                        case 2:
                            // 如果是带审批的出库类型, 只需要释放申请的数量
                            storeItemController.reduceLockNumber(storeOperationItem.getGoodId(), storeOperationItem.getNumber());
                            break;
                    }
                }
            }
        }
    }

    /**
     * 批量生成操作纪录
     *
     * @param storeOperations
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping("/addStoreOperations")
    public ResultVo addStoreOperations(@RequestBody List<StoreOperation> storeOperations) {
        for (StoreOperation storeOperation : storeOperations) {
            addStoreOperation(storeOperation);
        }
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @Transactional(readOnly = false)
    @PostMapping(value = "/updateStoreOperation")
    public ResultVo updateStoreOperation(@RequestBody StoreOperation storeOperation) {

        // 更新明细内容
        List<StoreOperationItem> storeOperationItems = storeOperation.getStoreOperationItems();
        if (storeOperationItems != null && !storeOperationItems.isEmpty()) {
            storeOperationItemService.save(storeOperationItems);
        }

        storeOperationService.dynamicUpdate(storeOperation);
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteStoreOperation")
    public ResultVo deleteStoreOperation(@RequestBody StoreOperation storeOperation) {

        // 级联删除
        storeOperationService.cascadeDelete(storeOperation);
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteStoreOperations")
    public ResultVo deleteStoreOperations(@RequestBody List<StoreOperation> storeOperations) {

        // 先删除明细，在删除总单
        storeOperations.forEach(storeOperation -> {
            storeOperationService.cascadeDelete(storeOperation);
        });

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
     * @param storeOperations
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping(value = "/approve")
    public ResultVo approve(@RequestBody List<StoreOperation> storeOperations) {
        storeOperations.forEach(storeOperation -> {
            // 这里的storeOperation以及他们的item已经是存在表中的了，所以此时只需要进行更新以及操作库存
            StoreOperation item = storeOperationService.findById(storeOperation.getId());
            entityRefineService.refine(item);
            item.setApprovalResult(storeOperation.getApprovalResult());
            applyStoreOperation(item);
            storeOperationService.saveOrUpdate(item);
        });
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    /**
     * findById
     */
    @PostMapping("/findStoreOperationById")
    public StoreOperation findStoreOperationById(@RequestParam String storeOperationId) {
        StoreOperation storeOperation = storeOperationService.findById(storeOperationId);
        entityRefineService.refine(storeOperation);
        return storeOperation;
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
    @GetMapping(value = "/findAllStoreOperations")
    public ResultVo findAllStoreOperations(@RequestParam(required = true) Integer page,
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

        Page<StoreOperation> storeOperations = storeOperationService.findAll(pager);
        entityRefineService.refinePage(storeOperations);

        return simpleResult(ResultEnum.SUCCESS, storeOperations);
    }


    /**
     * 以表单 form 形式 传递参数
     *
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/findStoreOperationBySearchParams")
    @Permiss(newOr = {Permit.USER, Permit.ADMIN})
    public ResultVo findStoreOperationBySearchParams(@RequestParam(required = true) Integer page,
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
        MPager<StoreOperation> storeOperations = storeOperationService.findByDynamicSqlParams(sqlParams, page, size, StoreOperation.class);

        entityRefineService.refineList(storeOperations.getData());

        return simpleResult(ResultEnum.SUCCESS, storeOperations);
    }





}
