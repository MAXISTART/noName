package com.school.store.admin.user.controller;


import com.school.store.admin.admin.entity.Admin;
import com.school.store.admin.department.entity.Department;
import com.school.store.admin.department.service.DepartmentService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/user")
public class UserController extends BaseAdminController{

    @Autowired
    private UserService userService;

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


    @PostMapping(value = "/deleteUser")
    public ResultVo deleteUser(@RequestBody User user, @SessionAttribute("admin") Admin admin) {
        // 这里的RequestBody 的 user只需要一个id就行了
        userService.delete(user);
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @PostMapping(value = "/deleteUsers")
    public ResultVo deleteUsers(@RequestBody List<User> users, @SessionAttribute("admin") Admin admin) {
        // 这里的RequestBody 的 users 是一个 user的数组
        userService.delete(users);
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
        });
        return simpleResult(ResultEnum.SUCCESS, users);
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
}
