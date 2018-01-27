package com.school.store.admin.store.controller;
import com.school.store.admin.admin.entity.Admin;
import com.school.store.admin.store.entity.StoreItem;
import com.school.store.admin.store.service.StoreItemService;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.enums.ResultEnum;
import com.school.store.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/store")
public class StoreItemController extends BaseAdminController {

    @Autowired
    private StoreItemService storeItemService;

    @PostMapping("/addStoreItem")
    public ResultVo addStoreItem(@RequestBody StoreItem storeItem, @SessionAttribute("admin") Admin admin) {

        storeItemService.save(entityUtil.updateInfoDefault(storeItem, admin.getId(), admin.getId(), true));
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @PostMapping(value = "/updateStoreItem")
    public ResultVo updateStoreItem(@RequestBody StoreItem storeItem, @SessionAttribute("admin") Admin admin) {
        // 更新的话不需要更改 创建者和创建时间
        storeItemService.save(entityUtil.updateInfoDefault(storeItem, null, admin.getId(), false));
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @PostMapping(value = "/deleteStoreItem")
    public ResultVo deleteStoreItem(@RequestBody StoreItem storeItem, @SessionAttribute("admin") Admin admin) {
        // 这里的RequestBody 的 user只需要一个id就行了
        storeItemService.delete(storeItem);
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @PostMapping(value = "/deleteStoreItems")
    public ResultVo deleteStoreItems(@RequestBody List<StoreItem> storeItems, @SessionAttribute("admin") Admin admin) {
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
                                      @RequestParam(required = false, defaultValue = "updateTime") String property) {

        // 配置分页信息
        PageRequest pager = null;
        if (direction.equals("ASC")) {
            pager = new PageRequest(page, size, Sort.Direction.ASC, property);
        }
        if (direction.equals("DESC")) {
            pager = new PageRequest(page, size, Sort.Direction.DESC, property);
        }

        Page<StoreItem> storeItems = storeItemService.findAll(pager);

        return simpleResult(ResultEnum.SUCCESS, storeItems);
    }


    /**
     *  防止代码重复的工具代码
     * @param storeItem
     */
    public void setStoreItems(StoreItem storeItem){

    }

}