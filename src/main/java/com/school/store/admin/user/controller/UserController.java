package com.school.store.admin.user.controller;


import com.school.store.admin.admin.entity.Admin;
import com.school.store.admin.department.entity.Department;
import com.school.store.admin.department.service.DepartmentService;
import com.school.store.admin.permission.entity.Permission;
import com.school.store.admin.permission.entity.UserToPermission;
import com.school.store.admin.permission.service.PermissionService;
import com.school.store.admin.permission.service.UserToPermissionService;
import com.school.store.admin.user.entity.User;
import com.school.store.admin.user.service.UserService;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.base.model.SqlParams;
import com.school.store.enums.ResultEnum;
import com.school.store.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/admin/user")
public class UserController extends BaseAdminController{

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserToPermissionService userToPermissionService;


    @Autowired
    private DepartmentService departmentService;

    @PostMapping(value = "/addUser")
    public ResultVo addUser(@RequestBody User user, @SessionAttribute("admin") Admin admin) {

        userService.save(entityUtil.updateInfoDefault(user, admin.getId(), admin.getId(), true));
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @PostMapping(value = "/updateUser")
    public ResultVo updateUser(@RequestBody User user, @SessionAttribute("admin") Admin admin) {
        // 更新的话不需要更改 创建者和创建时间
        userService.save(entityUtil.updateInfoDefault(user, null, admin.getId(), false));
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteUser")
    public ResultVo deleteUser(@RequestBody User user, @SessionAttribute("admin") Admin admin) {
        // 先删除 用户-权限 键值对
        userToPermissionService.delete(userToPermissionService.findByUserId(user.getId()));
        userService.delete(user);
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteUsers")
    public ResultVo deleteUsers(@RequestBody List<User> users, @SessionAttribute("admin") Admin admin) {
        // 先删除 用户-权限 键值对
        users.forEach(user -> {
            userToPermissionService.delete(userToPermissionService.findByUserId(user.getId()));
        });
        userService.delete(users);
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    /**
     *  给指定用户添加权限
     * @param userToPermissions
     * @param admin
     * @return
     */

    @Transactional(readOnly = false)
    @PostMapping(value = "/addPermissions")
    public ResultVo addPermissions(@RequestBody List<UserToPermission> userToPermissions, @SessionAttribute("admin") Admin admin) {
        // 要先验证是否已经存在了该 用户-权限 对。
        for(int index=0; index < userToPermissions.size(); index++){
            UserToPermission userToPermission = userToPermissions.get(index);
            if(userToPermissionService.findByPermissionIdAndUserId(userToPermission.getPermissionId(), userToPermission.getUserId()) != null){
                userToPermissions.remove(index);
            }
        }
        userToPermissionService.save(userToPermissions);
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
    @GetMapping(value = "/findAllUsers")
    public ResultVo findAllUser(@RequestParam(required = true) Integer page,
                                @RequestParam(required = false, defaultValue = "20") Integer size,
                                @RequestParam(required = false, defaultValue = "DESC") String direction,
                                @RequestParam(required = false, defaultValue = "updateTime") String property) {

        // 配置分页信息
        PageRequest pager = null;
        if (direction.equals("ASC")) {
            pager = new PageRequest(page, size, Sort.Direction.ASC, property);
        }
        if (direction.equals("DESC")) {
            pager = new PageRequest(page, size, Sort.Direction.DESC, property);
        }

        Page<User> users = userService.findAll(pager);
        // 给每个user设置他们对应的departmentName
        users.forEach(user -> {
            setDepartmentName(user);
            setPermissions(user);
        });
        return simpleResult(ResultEnum.SUCCESS, users);
    }


    /**
     *  根据id传回user
     * @param userId
     * @return
     */
    @PostMapping(value = "/findUserByUserId")
    public User findUserByUserId(@RequestParam String userId){
        User user = userService.findById(userId);
        setPermissions(user);
        setDepartmentName(user);
        return user;
    }





    /**
     *  根据部门id传回users
     * @param departmentId
     * @return
     */
    @PostMapping(value = "/findUserByUserId")
    public List<User> findUsersByDepartmentId(@RequestParam String departmentId){
        List<User> users = userService.findByDepartmentId(departmentId);
        users.forEach(user -> {
            setPermissions(user);
            setDepartmentName(user);
        });
        return users;
    }



    /**
     *  以表单 form 形式 传递参数
     * @param page
     * @param size
     * @param direction
     * @param property
     * @return
     */
    @PostMapping(value = "/findUserBySearchParams")
    public ResultVo findUserBySearchParams(@RequestParam(required = true) Integer page,
                                           @RequestParam(required = false, defaultValue = "20") Integer size,
                                           @RequestParam(required = false, defaultValue = "DESC") String direction,
                                           @RequestParam(required = false, defaultValue = "updateTime") String property,
                                           @RequestParam(required = false, defaultValue = "allDepartment") String departmentId,
                                           @RequestParam(required = false, defaultValue = "allName") String name
    ) {
        SqlParams sqlParams = new SqlParams();
        if(!departmentId.equals("allDepartment")){
            sqlParams.put("AND","departmentId","=");
            sqlParams.putValue(departmentId);
        }
        if(!name.equals("allName")){
            sqlParams.put("AND","name","LIKE");
            sqlParams.putValue("%"+name+"%");
        }

        // 返回的是真正的List<User>
        List<User> users = userService.findByDynamicSqlParams("users",sqlParams,page,size,User.class);
        // 给每个user设置他们对应的departmentName
        users.forEach(user -> {
            setDepartmentName(user);
            setPermissions(user);
        });
        return simpleResult(ResultEnum.SUCCESS, users);
    }


    /**
     *  防止代码重复的工具代码
     * @param user
     */
    public void setDepartmentName(User user){
        Department department = departmentService.findById(user.getDepartmentId());
        if(department != null){
            user.setDepartmentName(department.getName());
        }
    }

    /**
     *  防止代码重复的工具代码
     * @param user
     */
    @Transactional(readOnly = false)
    public void setPermissions(User user){
        Set<Permission> permissions = new HashSet<>();
        List<UserToPermission> userToPermissions = userToPermissionService.findByUserId(user.getId());
        userToPermissions.forEach(userToPermission -> {
            permissions.add(permissionService.findById(userToPermission.getPermissionId()));
        });
        user.setPermissions(permissions);
    }
}
