package com.school.store.admin.good.controller;

import com.school.store.admin.admin.entity.Admin;
import com.school.store.admin.good.entity.GoodItem;
import com.school.store.admin.good.service.GoodItemService;
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
@RequestMapping("/admin/good")
public class GoodController extends BaseAdminController {


    @Autowired
    GoodItemService goodItemService;

    @PostMapping("/addGoodItem")
    public ResultVo addUser(@RequestBody GoodItem goodItem, @SessionAttribute("admin") Admin admin) {

        goodItemService.save(entityUtil.updateInfoDefault(goodItem, admin.getId(), admin.getId(), true));
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @PostMapping(value = "/updateGoodItem")
    public ResultVo updateGoodItem(@RequestBody GoodItem goodItem, @SessionAttribute("admin") Admin admin) {
        // 更新的话不需要更改 创建者和创建时间
        goodItemService.save(entityUtil.updateInfoDefault(goodItem, null, admin.getId(), false));
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @PostMapping(value = "/deleteGoodItem")
    public ResultVo deleteGoodItem(@RequestBody GoodItem goodItem, @SessionAttribute("admin") Admin admin) {
        // 这里的RequestBody 的 user只需要一个id就行了
        goodItemService.delete(goodItem);
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @PostMapping(value = "/deleteGoodItems")
    public ResultVo deleteGoodItems(@RequestBody List<GoodItem> goodItems, @SessionAttribute("admin") Admin admin) {
        // 这里的RequestBody 的 goodItems 是一个 goodItem 的数组
        goodItemService.delete(goodItems);
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
    @GetMapping(value = "/findAllGoodItems")
    public ResultVo findAllGoodItems(@RequestParam(required = true) Integer page,
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

        Page<GoodItem> goodItems = goodItemService.findAll(pager);

        return simpleResult(ResultEnum.SUCCESS, goodItems);
    }



}


