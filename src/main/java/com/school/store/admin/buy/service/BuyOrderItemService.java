package com.school.store.admin.buy.service;


import com.school.store.admin.buy.entity.BuyOrderItem;
import com.school.store.base.repository.IBaseRepository;

import java.io.Serializable;
import java.util.List;

public interface BuyOrderItemService extends IBaseRepository<BuyOrderItem, Serializable>{

    public List<BuyOrderItem> findByOrderId(String orderId);
}
