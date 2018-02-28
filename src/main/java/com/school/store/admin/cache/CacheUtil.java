package com.school.store.admin.cache;

import com.school.store.admin.permission.entity.Permission;
import com.school.store.admin.refine.EntityRefineService;
import com.school.store.admin.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class CacheUtil {

    @Autowired
    private EntityRefineService entityRefineService;

    /**
     *  从缓存中获取 用户权限 ，将该方法直接写在 permissionAspect的话是不会起作用的（内部调用缓存是不起作用的）
     * @param user
     * @return
     */
    @Cacheable(value = "userWithPermission", key = "#user.id")
    public Set<Permission> getUserPermissionByIdWithCache(User user){
        log.warn("不在缓存里");
        entityRefineService.refine(user);
        return user.getPermissions();
    }

}
