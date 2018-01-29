package com.school.store.admin.store.service;

import com.school.store.admin.store.entity.StoreOperationItem;
import com.school.store.base.repository.IBaseRepository;

import java.io.Serializable;
import java.util.List;

public interface StoreOperationItemService extends IBaseRepository<StoreOperationItem, Serializable>{

    public List<StoreOperationItem> findByOrderId(String orderId);

    public StoreOperationItem findByGoodId(String goodId);
}
