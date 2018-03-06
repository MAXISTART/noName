package com.school.store.admin.good.controller;

import com.school.store.admin.good.entity.GoodItem;
import com.school.store.admin.good.entity.SortItem;
import com.school.store.admin.good.service.GoodItemService;
import com.school.store.admin.good.service.SortItemService;
import com.school.store.admin.refine.EntityRefineService;
import com.school.store.admin.store.controller.StoreItemController;
import com.school.store.admin.store.entity.StoreItem;
import com.school.store.annotation.Permiss;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.base.model.MPager;
import com.school.store.base.model.SqlParams;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/good")
@Permiss(and = Permit.ADMIN)
@Slf4j
public class GoodController extends BaseAdminController {


    @Autowired
    private GoodItemService goodItemService;

    @Autowired
    private SortItemService sortItemService;

    @Autowired
    private StoreItemController storeItemController;

    @Autowired
    private EntityRefineService entityRefineService;

    /**
     *  這是管理員的操作，並不是用戶的，正因為是管理員，所以管理員並不需要建立采购表，也不需要审核。
     *  但是采购得申请可以不需要管理员权限，普通用户也能申请
     * @param goodItem
     * @return
     */
    @Transactional(readOnly = false)
    @PostMapping("/addGoodItem")
    public ResultVo addGoodItem(@RequestBody GoodItem goodItem) {
        int rs_code = checkGoodItemNameAndSpec(goodItem);
        switch (rs_code){
            case 0:
                goodItemService.save(goodItem);
                // 同时还存进仓库表中
                storeItemController.addStoreItem(changeGoodItem2StoreItem(goodItem));
                return simpleResult(ResultEnum.SUCCESS, null);
            case 1:
                return simpleResult(ResultEnum.NAME_SPEC_REPEAT, null);
            case 2:
                return simpleResult(ResultEnum.PARAM_ERROR,null);
        }
        return simpleResult(ResultEnum.UNKNOWN_ERROR, null);

    }

    @PostMapping(value = "/updateGoodItem")
    public ResultVo updateGoodItem(@RequestBody GoodItem goodItem) {

        List<GoodItem> goodItems = goodItemService.findByNameAndSpec(goodItem.getName(), goodItem.getSpec());
        if(goodItems !=null && !goodItems.isEmpty()){

            // 如果所找到的id是不同的，说明这个找出来的goodItem是不同于这个输入的goodItem，但是现在输入的goodItem是和找出来的goodItem的规格和名字重复
            // 所以需要报错
            if(!goodItems.get(0).getId().equals(goodItem.getId())){
                return simpleResult(ResultEnum.NAME_SPEC_REPEAT,null);
            }else{
                goodItemService.dynamicUpdate(goodItem);
                return simpleResult(ResultEnum.SUCCESS, null);
            }
        }else{
            // 找不到就说明现在是完全可行的
            goodItemService.dynamicUpdate(goodItem);
            return simpleResult(ResultEnum.SUCCESS, null);
        }

    }


    @PostMapping(value = "/deleteGoodItem")
    @Transactional(readOnly = false)
    public ResultVo deleteGoodItem(@RequestBody GoodItem goodItem) {
        // 这里的RequestBody 的 user只需要一个id就行了
        goodItemService.cascadeDelete(goodItem);
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @PostMapping(value = "/deleteGoodItems")
    @Transactional(readOnly = false)
    public ResultVo deleteGoodItems(@RequestBody List<GoodItem> goodItems) {
        // 这里的RequestBody 的 goodItems 是一个 goodItem 的数组
        goodItems.forEach(goodItem -> {
            goodItemService.cascadeDelete(goodItem);
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
    @GetMapping(value = "/findAllGoodItems")
    public ResultVo findAllGoodItems(@RequestParam(required = true) Integer page,
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

        Page<GoodItem> goodItems = goodItemService.findAll(pager);
        entityRefineService.refinePage(goodItems);

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
        if(!name.equals("allName") && !name.equals("")){
            sqlParams.put("AND","name","LIKE");
            sqlParams.putValue("%"+name+"%");
        }
        if(!price_start.equals("allPrice") && !price_start.equals("")){
            sqlParams.put("AND", "price", ">=");
            sqlParams.putValue(price_start);
        }
        if(!price_end.equals("allPrice") && !price_end.equals("")){
            sqlParams.put("AND", "price", "<=");
            sqlParams.putValue(price_end);
        }

        // 返回的是真正的List<GoodItem>
        MPager<GoodItem> goodItems = goodItemService.findByDynamicSqlParams( sqlParams, page, size, GoodItem.class);
        return simpleResult(ResultEnum.SUCCESS, goodItems);
    }




    /**
     *  根据物品名称搜索出所有的物品
     * @param name
     * @return
     */
    @GetMapping(value = "/findAllGoodItemByName")
    public ResultVo findAllGoodItemByName(@RequestParam String name) {
        return simpleResult(ResultEnum.SUCCESS, goodItemService.findByNameLike("%" + name + "%"));
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
    public ResultVo addSortItem(@RequestBody SortItem sortItem) {

        // 先检查录入的名字是否有问题
        int rs_code = checkSortItemName(sortItem);
        switch (rs_code){
            case 0:
                sortItemService.save(sortItem);
                return simpleResult(ResultEnum.SUCCESS, null);
            case 1:
                return simpleResult(ResultEnum.NAME_REPEAT, null);
            case 2:
                return simpleResult(ResultEnum.PARAM_ERROR,null);
        }
        return simpleResult(ResultEnum.UNKNOWN_ERROR, null);


    }



    @PostMapping(value = "/updateSortItem")
    public ResultVo updateSortItem(@RequestBody SortItem sortItem) {

        // 先检查录入的名字是否有问题
        int rs_code = checkSortItemName(sortItem);
        switch (rs_code){
            case 0:
                sortItemService.dynamicUpdate(sortItem);
                return simpleResult(ResultEnum.SUCCESS, null);
            case 1:
                return simpleResult(ResultEnum.NAME_REPEAT, null);
            case 2:
                return simpleResult(ResultEnum.PARAM_ERROR,null);
        }
        return simpleResult(ResultEnum.UNKNOWN_ERROR, null);
    }



    @PostMapping(value = "/deleteSortItem")
    public ResultVo deleteSortItem(@RequestBody SortItem sortItem) {
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
        storeItem.setInputTime(new Date());
        storeItem.setTotalPrice(goodItem.getPrice().multiply(new BigDecimal(goodItem.getNumber())));
        return storeItem;
    }


}


