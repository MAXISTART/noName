package com.school.store.admin.take.service;
import com.school.store.admin.take.entity.TakeOrder;
import com.school.store.base.repository.IBaseRepository;

import java.io.Serializable;

public interface TakeOrderService extends IBaseRepository<TakeOrder, Serializable>{
    public TakeOrder findById(String id);
}