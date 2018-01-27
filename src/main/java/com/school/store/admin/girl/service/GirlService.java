package com.school.store.admin.girl.service;


import com.school.store.base.repository.IBaseRepository;
import com.school.store.admin.girl.entity.Girl;

import java.io.Serializable;
import java.util.List;

public interface GirlService extends IBaseRepository<Girl,Serializable> {

       public List<Girl> findByBoyId(String id);

}
