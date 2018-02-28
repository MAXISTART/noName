package com.school.store.admin.store.service;


import com.school.store.admin.store.entity.StoreItem;
import com.school.store.base.repository.IBaseRepository;

import java.io.Serializable;

// 这个StoreItemCustom可以自己的自定义方法
public interface StoreItemService extends IBaseRepository<StoreItem, Serializable>{

    public StoreItem findByGoodId(String goodId);
}