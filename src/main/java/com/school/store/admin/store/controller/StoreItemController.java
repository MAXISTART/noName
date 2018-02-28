package com.school.store.admin.store.controller;
import com.school.store.admin.refine.EntityRefineService;
import com.school.store.admin.store.entity.StoreItem;
import com.school.store.admin.store.service.StoreItemService;
import com.school.store.annotation.Permiss;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.constant.Permit;
import com.school.store.enums.ResultEnum;
import com.school.store.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/store")
@Permiss(and = Permit.ADMIN)
@Slf4j
public class StoreItemController extends BaseAdminController {

    @Autowired
    private StoreItemService storeItemService;

    @Autowired
    private EntityRefineService entityRefineService;

    // 测试添加和删除
    @GetMapping("/testAdd")
    public String testAdd(@RequestParam String goodId, @RequestParam Integer number){
        return addNumber(goodId, number) + "";
    }

    @GetMapping("/testReduce")
    public String testReduce(@RequestParam String goodId, @RequestParam Integer number){
        return reduceNumber(goodId, number) + "";
    }


    @PostMapping("/addStoreItem")
    public ResultVo addStoreItem(@RequestBody StoreItem storeItem) {
        storeItem.setId(null);
        storeItemService.save(storeItem);
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @PostMapping(value = "/updateStoreItem")
    public ResultVo updateStoreItem(@RequestBody StoreItem storeItem) {
        // 更新的话不需要更改 创建者和创建时间
        storeItemService.dynamicUpdate(storeItem);
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @PostMapping(value = "/deleteStoreItem")
    public ResultVo deleteStoreItem(@RequestBody StoreItem storeItem) {
        // 这里的RequestBody 的 user只需要一个id就行了
        storeItemService.delete(storeItem);
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @PostMapping(value = "/deleteStoreItems")
    public ResultVo deleteStoreItems(@RequestBody List<StoreItem> storeItems) {
        // 这里的RequestBody 的 storeItems 是一个 storeItem 的数组
        storeItemService.delete(storeItems);
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
    @GetMapping(value = "/findAllStoreItems")
    public ResultVo findAllStoreItems(@RequestParam(required = true) Integer page,
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

        Page<StoreItem> storeItems = storeItemService.findAll(pager);
        entityRefineService.refinePage(storeItems);

        return simpleResult(ResultEnum.SUCCESS, storeItems);
    }



    @Transactional(readOnly = false)
    @Permiss(need = false)
    // 这是系统级别的方法，不提供给任何用户，只是系统调用
    public boolean addNumber(String goodId, Integer number){
        StoreItem storeItem = storeItemService.findByGoodId(goodId);
        storeItem.setNumber(storeItem.getNumber() + number);
        // 这里因为持久化了，必须用merge，而不能用sql或者save
        storeItemService.saveOrUpdate(storeItem);
        return true;
    }

    @Transactional(readOnly = false)
    @Permiss(need = false)
    // 这是系统级别的方法，不提供给任何用户，只是系统调用
    public boolean reduceNumber(String goodId, Integer number){
        StoreItem storeItem = storeItemService.findByGoodId(goodId);
        if(storeItem.getNumber() < number){
            // 如果库存所含数量小于要减少的数量
            return false;
        }
        storeItem.setNumber(storeItem.getNumber() - number);
        // 这里因为持久化了，必须用merge，而不能用sql或者save
        storeItemService.saveOrUpdate(storeItem);
        return true;
    }

    /**
     * 添加正在审批的数量
     * @param goodId
     * @param lockNumber
     * @return
     */
    @Transactional(readOnly = false)
    @Permiss(need = false)
    // 这是系统级别的方法，不提供给任何用户，只是系统调用
    public boolean addLockNumber(String goodId, Integer lockNumber){
        StoreItem storeItem = storeItemService.findByGoodId(goodId);
        storeItem.setLockNumber(storeItem.getLockNumber() + lockNumber);
        // 这里因为持久化了，必须用merge，而不能用sql或者save
        storeItemService.saveOrUpdate(storeItem);
        return true;
    }

    /**
     * 减少正在审批的数量
     * @param goodId
     * @param lockNumber
     * @return
     */
    @Transactional(readOnly = false)
    @Permiss(need = false)
    // 这是系统级别的方法，不提供给任何用户，只是系统调用
    public boolean reduceLockNumber(String goodId, Integer lockNumber){
        StoreItem storeItem = storeItemService.findByGoodId(goodId);
        storeItem.setLockNumber(storeItem.getLockNumber() - lockNumber);
        // 这里因为持久化了，必须用merge，而不能用sql或者save
        storeItemService.saveOrUpdate(storeItem);
        return true;
    }

    /**
     *  检查是否有足够的自由库存， 自由库存 = 库存 - lockNumber
     * @param goodId
     * @param number
     * @return
     */
    @Transactional(readOnly = false)
    @Permiss(need = false)
    // 这是系统级别的方法，不提供给任何用户，只是系统调用
    public boolean checkNumber(String goodId, Integer number){
        // 检查库存是否足够
        StoreItem storeItem = storeItemService.findByGoodId(goodId);
        if(storeItem.getNumber() < number){
            // 如果库存当前存在的数量小于要减少的数量
            return false;
        }else{
            return true;
        }
    }

}