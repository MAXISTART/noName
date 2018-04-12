package com.school.store.admin.store.controller;
import com.school.store.admin.good.entity.GoodItem;
import com.school.store.admin.good.service.GoodItemService;
import com.school.store.admin.refine.EntityRefineService;
import com.school.store.admin.store.entity.StoreItem;
import com.school.store.admin.store.service.StoreItemService;
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
import java.util.ArrayList;
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

    @Autowired
    private GoodItemService goodItemService;

    // 测试添加和删除
    @GetMapping("/testAdd")
    public String testAdd(@RequestParam String goodId, @RequestParam BigDecimal number){
        return addNumber(goodId, number) + "";
    }

    @GetMapping("/testReduce")
    public String testReduce(@RequestParam String goodId, @RequestParam BigDecimal number){
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
        for(StoreItem storeItem : storeItems){
            GoodItem goodItem = goodItemService.findById(storeItem.getGoodId());
            storeItem.setTotalPrice(goodItem.getPrice().multiply(storeItem.getNumber()));
            storeItem.setGoodItem(goodItem);
        }
        return simpleResult(ResultEnum.SUCCESS, storeItems);
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
    @PostMapping(value = "/findStoreItemsBySearchParams")
    public ResultVo findStoreItemsBySearchParams(@RequestParam(required = true) Integer page,
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
        // 找到goodItems的Id， 然后根据goodId返回storeItem
        List<StoreItem> storeItems = new ArrayList<>();
        for(GoodItem goodItem : goodItems.getData()){
            StoreItem storeItem = storeItemService.findByGoodId(goodItem.getId());
            storeItem.setGoodItem(goodItem);
            storeItem.setTotalPrice(goodItem.getPrice().multiply(storeItem.getNumber()));
            storeItems.add(storeItem);
        }
        entityRefineService.refineList(storeItems);
        MPager<StoreItem> storeItemMPager = new MPager<>();
        storeItemMPager.setData(storeItems);
        storeItemMPager.setTotal(goodItems.getTotal());
        storeItemMPager.setPage(goodItems.getPage());
        storeItemMPager.setPageSize(goodItems.getPageSize());

        return simpleResult(ResultEnum.SUCCESS, storeItemMPager);
    }




    @Transactional(readOnly = false)
    @Permiss(need = false)
    // 这是系统级别的方法，不提供给任何用户，只是系统调用
    public boolean addNumber(String goodId, BigDecimal number){
        StoreItem storeItem = storeItemService.findByGoodId(goodId);
        storeItem.setNumber(storeItem.getNumber().add(number));
        // 这里因为持久化了，必须用merge，而不能用sql或者save
        storeItemService.saveOrUpdate(storeItem);
        return true;
    }

    @Transactional(readOnly = false)
    @Permiss(need = false)
    // 这是系统级别的方法，不提供给任何用户，只是系统调用
    public boolean reduceNumber(String goodId, BigDecimal number){
        StoreItem storeItem = storeItemService.findByGoodId(goodId);
        if(storeItem.getNumber().compareTo(number) == -1){
            // 如果库存所含数量小于要减少的数量
            return false;
        }
        storeItem.setNumber(storeItem.getNumber().subtract(number));
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
    public boolean addLockNumber(String goodId, BigDecimal lockNumber){
        StoreItem storeItem = storeItemService.findByGoodId(goodId);
        storeItem.setLockNumber(storeItem.getLockNumber().add(lockNumber));
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
    public boolean reduceLockNumber(String goodId, BigDecimal lockNumber){
        StoreItem storeItem = storeItemService.findByGoodId(goodId);
        storeItem.setLockNumber(storeItem.getLockNumber().subtract(lockNumber));
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
    public boolean checkNumber(String goodId, BigDecimal number){
        // 检查库存是否足够
        StoreItem storeItem = storeItemService.findByGoodId(goodId);
        if(storeItem == null){
            return false;
        }
        if(storeItem.getNumber().compareTo(number) == -1){
            // 如果库存当前存在的数量小于要减少的数量
            return false;
        }else{
            return true;
        }
    }

}