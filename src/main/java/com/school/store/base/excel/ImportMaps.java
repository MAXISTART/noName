package com.school.store.base.excel;

import java.util.HashMap;
import java.util.Map;

public class ImportMaps {

    // 入库表
    public Map<String, String> storeOperation;
    // 申领表
    public Map<String, String> takeOrder;
    // 库存表
    public Map<String, String> store;
    // 商品表
    public Map<String, String> good;


    public void init(){
        storeOperation = new HashMap();
        takeOrder = new HashMap();
        store = new HashMap();
        good = new HashMap();


        storeOperation.put("", "");
    }

}
