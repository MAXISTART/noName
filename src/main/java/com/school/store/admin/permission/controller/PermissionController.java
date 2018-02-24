package com.school.store.admin.permission.controller;
import com.school.store.admin.permission.entity.Permission;
import com.school.store.admin.permission.service.PermissionService;
import com.school.store.admin.permission.service.UserToPermissionService;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.enums.ResultEnum;
import com.school.store.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/permission")
public class PermissionController extends BaseAdminController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserToPermissionService userToPermissionService;

    @PostMapping("/addPermission")
    public ResultVo addPermission(@RequestBody Permission permission) {

        permission.setId(null);
        permissionService.save(permission);
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @PostMapping(value = "/updatePermission")
    public ResultVo updatePermission(@RequestBody Permission permission) {

        permissionService.dynamicUpdate(permission);
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @Transactional(readOnly = false)
    @PostMapping(value = "/deletePermission")
    public ResultVo deletePermission(@RequestBody Permission permission) {
        // 级联删除
        permissionService.cascadeDelete(permission);
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @PostMapping(value = "/deletePermissions")
    public ResultVo deletePermissions(@RequestBody List<Permission> permissions) {
        // 这里的RequestBody 的 permissions 是一个 permission 的数组
        permissions.forEach(permission -> {
            permissionService.cascadeDelete(permission);
        });
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    /**
     * 参数 page ,size 是一定要有的 ，另外两个可以默认
     *
     * @param page      第几页
     * @param size      每页的包含多少纪录
     * @param direction 按照顺序还是逆序排列 （ASC 或者 DESC）
     * @param property  按照什么排列
     * @return
     */
    @GetMapping(value = "/findAllPermissions")
    public ResultVo findAllPermissions(@RequestParam(required = true) Integer page,
                                       @RequestParam(required = false, defaultValue = "20") Integer size,
                                       @RequestParam(required = false, defaultValue = "DESC") String direction,
                                       @RequestParam(required = false, defaultValue = "lastmodifiedTime") String property) {

        // 配置分页信息
        PageRequest pager = null;
        if (direction.equals("ASC")) {
            pager = new PageRequest(page, size, Sort.Direction.ASC, property);
        }
        if (direction.equals("DESC")) {
            pager = new PageRequest(page, size, Sort.Direction.DESC, property);
        }

        Page<Permission> permissions = permissionService.findAll(pager);

        return simpleResult(ResultEnum.SUCCESS, permissions);
    }


    /**
     *  防止代码重复的工具代码
     * @param permission
     */
    public void setPermissions(Permission permission){

    }

}