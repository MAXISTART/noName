package com.school.store.admin.good.service;

import com.school.store.admin.good.entity.GoodItem;
import com.school.store.base.repository.IBaseRepository;

import java.io.Serializable;
import java.util.List;

public interface GoodItemService extends IBaseRepository<GoodItem, Serializable>{

    public List<GoodItem> findByNameAndSpec(String name, String spec);

    public List<GoodItem> findBySort(String sort);
}
