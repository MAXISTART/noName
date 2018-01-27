package com.school.store.admin.take.service;
import com.school.store.admin.take.entity.TakeOrderItem;
import com.school.store.base.repository.IBaseRepository;

import java.io.Serializable;
import java.util.List;

public interface TakeOrderItemService extends IBaseRepository<TakeOrderItem, Serializable>{
    public List<TakeOrderItem> findByOrderId(String orderId);
}