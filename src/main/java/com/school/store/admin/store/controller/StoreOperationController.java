package com.school.store.admin.store.controller;

import com.school.store.admin.admin.entity.Admin;
import com.school.store.admin.store.entity.StoreItem;
import com.school.store.admin.store.entity.StoreOperation;
import com.school.store.admin.store.entity.StoreOperationItem;
import com.school.store.admin.store.service.StoreItemService;
import com.school.store.admin.store.service.StoreOperationItemService;
import com.school.store.admin.store.service.StoreOperationService;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.enums.ResultEnum;
import com.school.store.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

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
    public ResultVo testAddStoreOperations(@RequestBody List<StoreOperation> storeOperations){
        return simpleResult(ResultEnum.SUCCESS, storeOperations);
    }


    @Transactional(readOnly = false)
    @PostMapping("/addStoreOperation")
    public ResultVo addStoreOperation(@RequestBody StoreOperation storeOperation, @SessionAttribute("admin") Admin admin) {

        storeOperationService.save(entityUtil.updateInfoDefault(storeOperation, admin.getId(), admin.getId(), true));

        // 保存明细内容，但是要先设置ID
        List<StoreOperationItem> storeOperationItems = storeOperation.getStoreOperationItems();
        if(storeOperationItems != null){
            for(StoreOperationItem storeOperationItem : storeOperationItems){
                storeOperationItem.setOrderId(storeOperation.getId());
                if(storeOperation.getApprovalResult() == 1){
                    // 审核通过是会修改库存的
                    switch (storeOperation.getType()){
                        case 1:
                            // 如果是带审批的入库类型
                            storeItemController.addNumber(storeOperationItem.getGoodId(), storeOperationItem.getNumber());
                            break;
                        case 2:
                            // 如果是带审批的出库类型
                            if(storeItemController.reduceNumber(storeOperationItem.getGoodId(), storeOperationItem.getNumber())){
                                // 解放锁住的lockNumber
                                storeItemController.setLockNumber(storeOperationItem.getGoodId(), 0);
                                break;
                            }else{
                                String data = "规格为: " + storeOperationItem.getSpec() + "的"
                                        + storeOperationItem.getName() + " 当前数量不足";
                                return simpleResult(ResultEnum.SUCCESS, data);
                            }
                    }

                }
            }
            storeOperationItemService.save(storeOperationItems);
        }

        return simpleResult(ResultEnum.SUCCESS, null);
    }


    /**
     *  批量生成操作纪录
     * @param storeOperations
     * @param admin
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping("/addStoreOperations")
    public ResultVo addStoreOperations(@RequestBody List<StoreOperation> storeOperations, @SessionAttribute("admin") Admin admin){
        for(StoreOperation storeOperation : storeOperations){
            addStoreOperation(storeOperation, admin);
        }
        return simpleResult(ResultEnum.SUCCESS, null);
    }



    @Transactional(readOnly = false)
    @PostMapping(value = "/updateStoreOperation")
    public ResultVo updateStoreOperation(@RequestBody StoreOperation storeOperation, @SessionAttribute("admin") Admin admin) {

        // 更新明细内容
        List<StoreOperationItem> storeOperationItems = storeOperation.getStoreOperationItems();
        if(storeOperationItems != null && !storeOperationItems.isEmpty()){
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
        if(storeOperationItems != null && !storeOperationItems.isEmpty()){
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
            if(storeOperationItems != null && !storeOperationItems.isEmpty()){
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

        return simpleResult(ResultEnum.SUCCESS, storeOperations);
    }


    /**
     *  防止代码重复的工具代码
     * @param storeOperation
     */
    public void setStoreOperations(StoreOperation storeOperation){
        List<StoreOperationItem> storeOperationItems =storeOperationItemService.findByOrderId(storeOperation.getId());
        if(storeOperationItems != null && !storeOperationItems.isEmpty()){
            storeOperation.setStoreOperationItems(storeOperationItems);
        }
    }


    /**
     * 修改库存
     */
    @Transactional(readOnly = false)
    public int changeStore(StoreOperation storeOperation, Admin admin) throws Exception{

        if(storeOperation == null || storeOperation.getStoreOperationItems().isEmpty()){
            // 传入的参数不符
            return -1;
        }

        Integer type = storeOperation.getType();

        switch (type){

            case 1:
                // 如果是普通入库（不带审批）操作，就向库存添加数量
                storeOperation.getStoreOperationItems().forEach(storeOperationItem -> {
                    StoreItem storeItem = storeItemService.findByGoodId(storeOperationItem.getGoodId());
                    storeItem.setNumber(storeItem.getNumber() + storeOperationItem.getNumber());
                    storeItemService.save(entityUtil.updateInfoDefault(storeItem, admin.getId(), admin.getId(), false));
                });
                // 普通入库成功
                return 1;
            case 2:
                // 如果是普通出库（不带审批）操作，就向库存减
                for(StoreOperationItem storeOperationItem: storeOperation.getStoreOperationItems()){
                    StoreItem storeItem = storeItemService.findByGoodId(storeOperationItem.getGoodId());
                    if(storeItem.getNumber() < storeOperationItem.getNumber()){
                        // 如果库存已经不够了，那么就回滚事务并且报错
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        // 库存不足，普通出库失败
                        return 2;
                    }else{
                        storeItem.setNumber(storeItem.getNumber() - storeOperationItem.getNumber());
                        storeItemService.save(entityUtil.updateInfoDefault(storeItem, admin.getId(), admin.getId(), false));
                    }
                }
                // 普通出库成功
                return 3;

            case 3:
                // 如果是审批入库操作，就需要对审批结果进行判断先
                if(storeOperation.getApprovalResult() == 1){
                    for(StoreOperationItem storeOperationItem: storeOperation.getStoreOperationItems()){
                        StoreItem storeItem = storeItemService.findByGoodId(storeOperationItem.getGoodId());
                        storeItem.setNumber(storeItem.getNumber() - storeOperationItem.getNumber());
                        storeItemService.save(entityUtil.updateInfoDefault(storeItem, admin.getId(), admin.getId(), false));
                    }
                    // 审批入库成功
                    return 4;
                }else {
                    // 该操作尚未审批通过
                    return 5;
                }
            case 4:
                // 如果是审批出库操作，如果还没审批通过就会对库存物品设置
                if(storeOperation.getApprovalResult() == 0){
                    // 如果审批还未通过而且

                }

        }

        return 0;
    }






}
