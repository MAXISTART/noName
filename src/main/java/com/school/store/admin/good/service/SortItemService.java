package com.school.store.admin.good.service;

import com.school.store.admin.good.entity.SortItem;
import com.school.store.base.repository.IBaseRepository;

import java.io.Serializable;
import java.util.List;

public interface SortItemService extends IBaseRepository<SortItem, Serializable>{
        public List<SortItem> findByName(String name);
}
