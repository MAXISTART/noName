package com.school.store.admin.store.controller;

import com.school.store.admin.admin.entity.Admin;
import com.school.store.admin.store.entity.StoreOperation;
import com.school.store.admin.store.entity.StoreOperationItem;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin/storeOperation")
public class StoreOperationController extends BaseAdminController {

    @Autowired
    private StoreOperationService storeOperationService;

    @Autowired
    private StoreOperationItemService storeOperationItemService;

    @Transactional(readOnly = false)
    @PostMapping("/addStoreOperation")
    public ResultVo addStoreOperation(@RequestBody StoreOperation storeOperation, @SessionAttribute("admin") Admin admin) {

        storeOperationService.save(entityUtil.updateInfoDefault(storeOperation, admin.getId(), admin.getId(), true));

        // 保存明细内容，但是要先设置ID
        List<StoreOperationItem> storeOperationItems = storeOperation.getStoreOperationItems();
        if(storeOperationItems != null && !storeOperationItems.isEmpty()){
            storeOperationItems.forEach(storeOperationItem -> {
                storeOperationItem.setOrderId(storeOperation.getId());
            });
            storeOperationItemService.save(storeOperationItems);
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

}
