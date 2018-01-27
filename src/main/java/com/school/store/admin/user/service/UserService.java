package com.school.store.admin.user.service;

import com.school.store.admin.user.entity.User;
import com.school.store.base.repository.IBaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;

public interface UserService extends IBaseRepository<User, Serializable> {

    public Page<User> findAll(Pageable pageable);

    public User findById(String id);
}
