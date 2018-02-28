package com.school.store.admin.department.controller;

import com.school.store.admin.department.entity.Department;
import com.school.store.admin.department.service.DepartmentService;
import com.school.store.admin.refine.EntityRefineService;
import com.school.store.admin.user.entity.User;
import com.school.store.admin.user.service.UserService;
import com.school.store.annotation.Permiss;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.constant.Permit;
import com.school.store.enums.ResultEnum;
import com.school.store.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/department")
@Permiss(and = Permit.ADMIN)
public class DepartmentController extends BaseAdminController{

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EntityRefineService entityRefineService;

    @Autowired
    private UserService userService;

    @PostMapping("/addDepartment")
    public ResultVo addDepartment(@RequestBody Department department) {

        department.setId(null);
        departmentService.save(department);
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @PostMapping(value = "/updateDepartment")
    public ResultVo updateDepartment(@RequestBody Department department) {
        departmentService.dynamicUpdate(department);
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @PostMapping(value = "/deleteDepartment")
    public ResultVo deleteDepartment(@RequestBody Department department) {
        // 这里的RequestBody 的 user只需要一个id就行了
        departmentService.cascadeDelete(department);
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @PostMapping(value = "/deleteDepartments")
    public ResultVo deleteDepartments(@RequestBody List<Department> departments) {
        // 这里的RequestBody 的 users 是一个 user的数组
        departments.forEach(department -> {
            departmentService.cascadeDelete(department);
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
    @GetMapping(value = "/findAllDepartments")
    public ResultVo findAllDepartments(@RequestParam(required = true) Integer page,
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

        Page<Department> departments = departmentService.findAll(pager);
        entityRefineService.refinePage(departments);
        return simpleResult(ResultEnum.SUCCESS, departments);
    }


}
