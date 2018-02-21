package com.school.store.admin.good.controller;

import com.school.store.admin.admin.entity.Admin;
import com.school.store.admin.good.entity.GoodItem;
import com.school.store.admin.good.entity.SortItem;
import com.school.store.admin.good.service.GoodItemService;
import com.school.store.admin.good.service.SortItemService;
import com.school.store.admin.store.controller.StoreItemController;
import com.school.store.admin.store.entity.StoreItem;
import com.school.store.admin.store.service.StoreItemService;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.base.model.SqlParams;
import com.school.store.enums.ResultEnum;
import com.school.store.utils.EntityUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/good")
public class GoodController extends BaseAdminController {


    @Autowired
    GoodItemService goodItemService;

    @Autowired
    SortItemService sortItemService;

    @Autowired
    StoreItemController storeItemController;

    @Transactional(readOnly = false)
    @PostMapping("/addGoodItem")
    public ResultVo addGoodItem(@RequestBody GoodItem goodItem, @SessionAttribute("admin") Admin admin) {

        // 先检查录入的名字是否有问题
        int rs_code = checkGoodItemNameAndSpec(goodItem);
        switch (rs_code){
            case 0:
                goodItemService.save(entityUtil.updateInfoDefault(goodItem, admin.getId(), admin.getId(), true));
                // 同时还存进仓库表中
                storeItemController.addStoreItem(changeGoodItem2StoreItem(goodItem), admin);
                return simpleResult(ResultEnum.SUCCESS, null);
            case 1:
                return simpleResult(ResultEnum.NAME_SPEC_REPEAT, null);
            case 2:
                return simpleResult(ResultEnum.PARAM_ERROR,null);
        }
        return simpleResult(ResultEnum.UNKNOWN_ERROR, null);

    }


    @PostMapping(value = "/updateGoodItem")
    public ResultVo updateGoodItem(@RequestBody GoodItem goodItem, @SessionAttribute("admin") Admin admin) {

        // 先检查录入的名字是否有问题
        int rs_code = checkGoodItemNameAndSpec(goodItem);
        switch (rs_code){
            case 0:
                goodItemService.save(entityUtil.updateInfoDefault(goodItem, admin.getId(), admin.getId(), false));
                return simpleResult(ResultEnum.SUCCESS, null);
            case 1:
                return simpleResult(ResultEnum.NAME_SPEC_REPEAT, null);
            case 2:
                return simpleResult(ResultEnum.PARAM_ERROR,null);
        }
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





    /**
     * 以表单 form 形式 传递参数
     *
     * @param page
     * @param size
     * @param price_start
     * @param price_end
     * @return
     */
    @PostMapping(value = "/findGoodItemsBySearchParams")
    public ResultVo findGoodItemsBySearchParams(@RequestParam(required = true) Integer page,
                                                     @RequestParam(required = false, defaultValue = "20") Integer size,
                                                     @RequestParam(required = false, defaultValue = "allPrice") String price_start,
                                                     @RequestParam(required = false, defaultValue = "allPrice") String price_end,
                                                     @RequestParam(required = false, defaultValue = "allName") String name
    ) {
        SqlParams sqlParams = new SqlParams();
        if(!name.equals("allName")){
            sqlParams.put("AND","name","LIKE");
            sqlParams.putValue("%"+name+"%");
        }
        if(!price_start.equals("allPrice")){
            sqlParams.put("AND", "price", ">=");
            sqlParams.putValue(price_start);
        }
        if(!price_end.equals("allPrice")){
            sqlParams.put("AND", "price", "<=");
            sqlParams.putValue(price_end);
        }
        if(!name.equals("allName")){
            sqlParams.put("AND", "name", "like");
            sqlParams.putValue("%" + name + "%");
        }

        // 返回的是真正的List<GoodItem>
        List<GoodItem> goodItems = goodItemService.findByDynamicSqlParams("good_items", sqlParams, page, size, GoodItem.class);

        return simpleResult(ResultEnum.SUCCESS, goodItems);
    }




    /**
     *  根据物品种类搜索出所有的物品名称
     * @param sort
     * @return
     */
    @PostMapping(value = "/findAllGoodItemNamesBySort")
    public ResultVo findAllGoodItemNamesBySort(@RequestParam String sort) {
        return simpleResult(ResultEnum.SUCCESS, goodItemService.findBySort(sort).stream().map(T -> T.getName()).collect(Collectors.toList()));
    }


    /**
     *
     * @return
     */
    @PostMapping(value = "/findAllSpecsByGoodItemName")
    public ResultVo findAllSpecsByGoodItemName(@RequestParam String goodItemName){
        return simpleResult(ResultEnum.SUCCESS, goodItemService.findByName(goodItemName).stream().map(T -> T.getSpec()).collect(Collectors.toList()));
    }


    /**
     *  根据物品名称和规格获取该商品的信息
     * @param goodItemName
     * @param spec
     * @return
     */
    @PostMapping(value = "findAllGoodItemsByNameAndSpec")
    public ResultVo findAllGoodItemsByNameAndSpec(@RequestParam String goodItemName,
                                                  @RequestParam String spec)
    {
        return simpleResult(ResultEnum.SUCCESS, goodItemService.findByNameAndSpec(goodItemName, spec));
    }



    @GetMapping("/findAllSorts")
    public ResultVo findAllSorts(){
        return simpleResult(ResultEnum.SUCCESS, sortItemService.findAll());
    }



    @Transactional(readOnly = false)
    @PostMapping("/addSortItem")
    public ResultVo addSortItem(@RequestBody SortItem sortItem, @SessionAttribute("admin") Admin admin) {

        // 先检查录入的名字是否有问题
        int rs_code = checkSortItemName(sortItem);
        switch (rs_code){
            case 0:
                sortItemService.save(entityUtil.updateInfoDefault(sortItem, admin.getId(), admin.getId(), true));
                return simpleResult(ResultEnum.SUCCESS, null);
            case 1:
                return simpleResult(ResultEnum.NAME_REPEAT, null);
            case 2:
                return simpleResult(ResultEnum.PARAM_ERROR,null);
        }
        return simpleResult(ResultEnum.UNKNOWN_ERROR, null);


    }



    @PostMapping(value = "/updateSortItem")
    public ResultVo updateSortItem(@RequestBody SortItem sortItem, @SessionAttribute("admin") Admin admin) {

        // 先检查录入的名字是否有问题
        int rs_code = checkSortItemName(sortItem);
        switch (rs_code){
            case 0:
                sortItemService.save(entityUtil.updateInfoDefault(sortItem, admin.getId(), admin.getId(), false));
                return simpleResult(ResultEnum.SUCCESS, null);
            case 1:
                return simpleResult(ResultEnum.NAME_REPEAT, null);
            case 2:
                return simpleResult(ResultEnum.PARAM_ERROR,null);
        }
        return simpleResult(ResultEnum.UNKNOWN_ERROR, null);
    }



    @PostMapping(value = "/deleteSortItem")
    public ResultVo deleteSortItem(@RequestBody SortItem sortItem, @SessionAttribute("admin") Admin admin) {
        // 这里的RequestBody 的 user只需要一个id就行了
        sortItemService.delete(sortItem);
        return simpleResult(ResultEnum.SUCCESS, null);
    }



    /**
     *  录入物品的时候检查物品名称是否重复 ，0 表示 无重复 ， 1 表示重复 ， 2表示参数错误
     * @return
     */
    private int checkSortItemName(SortItem item) {

        SqlParams sqlParams = new SqlParams();
        if(item.getName() != null && !item.getName().trim().equals("")){
            if(sortItemService.findByName(item.getName()).size() > 0){
                return 1;
            }else{
                return 0;
            }
        }else{
            return 2;
        }

    }



    /**
     *  录入物品的时候检查物品名称是否重复 ，0 表示 无重复 ， 1 表示重复 ， 2表示参数错误
     * @return
     */
    public int checkGoodItemNameAndSpec(GoodItem item){
        SqlParams sqlParams = new SqlParams();
        if(item.getName() != null && !item.getName().trim().equals("")){
            if(goodItemService.findByNameAndSpec(item.getName(), item.getSpec()).size() > 0){
                return 1;
            }else{
                return 0;
            }
        }else{
            return 2;
        }

    }


    /**
     *  转换GoodItem 和 StoreItem
     */
    public StoreItem changeGoodItem2StoreItem(GoodItem goodItem){

        StoreItem storeItem = new StoreItem();
        storeItem.setNumber(goodItem.getNumber());
        storeItem.setGoodId(goodItem.getId());
        storeItem.setLockNumber(0);
        storeItem.setInputTime(EntityUtil.getNowDate());
        storeItem.setTotalPrice(goodItem.getPrice().multiply(new BigDecimal(goodItem.getNumber())));
        return storeItem;
    }


}


