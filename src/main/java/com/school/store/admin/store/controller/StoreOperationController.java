package com.school.store.admin.store.controller;

import com.school.store.admin.admin.entity.Admin;
import com.school.store.admin.store.entity.StoreItem;
import com.school.store.admin.store.entity.StoreOperation;
import com.school.store.admin.store.entity.StoreOperationItem;
import com.school.store.admin.store.service.StoreItemService;
import com.school.store.admin.store.service.StoreOperationItemService;
import com.school.store.admin.store.service.StoreOperationService;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.base.model.SqlParams;
import com.school.store.enums.ResultEnum;
import com.school.store.exception.BaseException;
import com.school.store.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/admin/storeOperation")
public class StoreOperationController extends BaseAdminController {

    @Autowired
    private StoreOperationService storeOperationService;

    @Autowired
    private StoreOperationItemService storeOperationItemService;

    @Autowired
    private StoreItemController storeItemController;

    @Autowired
    private StoreItemService storeItemService;

    @PostMapping("/testAddStoreOperations")
    public ResultVo testAddStoreOperations(@RequestBody List<StoreOperation> storeOperations) {
        return simpleResult(ResultEnum.SUCCESS, storeOperations);
    }


    @Transactional(readOnly = false)
    @PostMapping("/addStoreOperation")
    public ResultVo addStoreOperation(@RequestBody StoreOperation storeOperation, @SessionAttribute("admin") Admin admin) {

        storeOperationService.save(entityUtil.updateInfoDefault(storeOperation, admin.getId(), admin.getId(), true));

        // 保存明细内容，但是要先设置ID
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
            storeOperationItemService.save(storeOperationItems);
        }

        return simpleResult(ResultEnum.SUCCESS, null);
    }


    /**
     * 批量生成操作纪录
     *
     * @param storeOperations
     * @param admin
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping("/addStoreOperations")
    public ResultVo addStoreOperations(@RequestBody List<StoreOperation> storeOperations, @SessionAttribute("admin") Admin admin) {
        for (StoreOperation storeOperation : storeOperations) {
            addStoreOperation(storeOperation, admin);
        }
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @Transactional(readOnly = false)
    @PostMapping(value = "/updateStoreOperation")
    public ResultVo updateStoreOperation(@RequestBody StoreOperation storeOperation, @SessionAttribute("admin") Admin admin) {

        // 更新明细内容
        List<StoreOperationItem> storeOperationItems = storeOperation.getStoreOperationItems();
        if (storeOperationItems != null && !storeOperationItems.isEmpty()) {
            storeOperationItemService.save(storeOperationItems);
        }

        storeOperationService.save(entityUtil.updateInfoDefault(storeOperation, null, admin.getId(), false));
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteStoreOperation")
    public ResultVo deleteStoreOperation(@RequestBody StoreOperation storeOperation, @SessionAttribute("admin") Admin admin) {

        // 先删除明细，在删除总单
        List<StoreOperationItem> storeOperationItems = storeOperation.getStoreOperationItems();
        if (storeOperationItems != null && !storeOperationItems.isEmpty()) {
            storeOperationItemService.delete(storeOperationItems);
        }
        storeOperationService.delete(storeOperation);
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteStoreOperations")
    public ResultVo deleteStoreOperations(@RequestBody List<StoreOperation> storeOperations, @SessionAttribute("admin") Admin admin) {

        // 先删除明细，在删除总单
        storeOperations.forEach(storeOperation -> {
            List<StoreOperationItem> storeOperationItems = storeOperation.getStoreOperationItems();
            if (storeOperationItems != null && !storeOperationItems.isEmpty()) {
                storeOperationItemService.delete(storeOperationItems);
            }
        });

        storeOperationService.delete(storeOperations);
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
    @GetMapping(value = "/findAllStoreOperations")
    public ResultVo findAllStoreOperations(@RequestParam(required = true) Integer page,
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

        Page<StoreOperation> storeOperations = storeOperationService.findAll(pager);

        storeOperations.forEach(storeOperation -> {
            setStoreOperations(storeOperation);
        });

        return simpleResult(ResultEnum.SUCCESS, storeOperations);
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
    @PostMapping(value = "/findStoreOperationBySearchParams")
    public ResultVo findStoreOperationBySearchParams(@RequestParam(required = true) Integer page,
                                                     @RequestParam(required = false, defaultValue = "20") Integer size,
                                                     @RequestParam(required = false, defaultValue = "allDepartment") String departmentId,
                                                     @RequestParam(required = false, defaultValue = "allRequestorId") String requestorId,
                                                     @RequestParam(required = false, defaultValue = "allRequestTotalPrice") String requestTotalPrice_start,
                                                     @RequestParam(required = false, defaultValue = "allRequestTotalPrice") String requestTotalPrice_end,
                                                     @RequestParam(required = false, defaultValue = "allTime") String requestTime_start,
                                                     @RequestParam(required = false, defaultValue = "allTime") String requestTime_end
                                                     ) {
        SqlParams sqlParams = new SqlParams();
        if (!departmentId.equals("allDepartment")) {
            sqlParams.put("AND", "departmentId", "=");
            sqlParams.putValue(departmentId);
        }
        if (!requestorId.equals("allRequestorId")) {
            sqlParams.put("AND", "requestorId", "=");
            sqlParams.putValue(requestorId);
        }
        if(!requestTotalPrice_start.equals("allRequestTotalPrice")){
            sqlParams.put("AND", "requestTotalPrice", ">=");
            sqlParams.putValue(requestTotalPrice_start);
        }
        if(!requestTotalPrice_end.equals("allRequestTotalPrice")){
            sqlParams.put("AND", "requestTotalPrice", "<=");
            sqlParams.putValue(requestTotalPrice_end);
        }
        if(!requestTime_start.equals("allTime") && !requestTime_end.equals("allTime")){
            sqlParams.put("AND", "requestTime", "BETWEEN");
            sqlParams.putValue(requestTime_start, requestTime_end);
        }

        // 返回的是真正的List<User>
        List<StoreOperation> storeOperations = storeOperationService.findByDynamicSqlParams("store_operations", sqlParams, page, size, StoreOperation.class);

        storeOperations.forEach(storeOperation -> {
            setStoreOperations(storeOperation);
        });

        return simpleResult(ResultEnum.SUCCESS, storeOperations);
    }


    /**
     * 防止代码重复的工具代码
     *
     * @param storeOperation
     */
    public void setStoreOperations(StoreOperation storeOperation) {
        List<StoreOperationItem> storeOperationItems = storeOperationItemService.findByOrderId(storeOperation.getId());
        if (storeOperationItems != null && !storeOperationItems.isEmpty()) {
            storeOperation.setStoreOperationItems(storeOperationItems);
        }
    }


}
