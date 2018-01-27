package com.school.store.admin.boy.service;


import com.school.store.base.repository.IBaseRepository;
import com.school.store.admin.boy.entity.Boy;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;
import java.util.List;

@Qualifier("BoyService")
public interface BoyService extends IBaseRepository<Boy,Serializable> {

    public List<Boy> findByAgeIn(int[] a);
    public List<Boy> findAll();
    public Boy findById(String id);


}
