package com.school.store.admin.admin.controller;

import com.school.store.admin.admin.entity.Admin;
import com.school.store.admin.admin.service.AdminService;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.enums.ResultEnum;
import com.school.store.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/admin/administor")
public class AdminController extends BaseAdminController {

    @Autowired
    AdminService adminService;

    @PostMapping("/addAdmin")
    public ResultVo addAdmin(@RequestBody Admin administor, @SessionAttribute("admin") Admin admin) {

        adminService.save(entityUtil.updateInfoDefault(administor, admin.getId(), admin.getId(), true));
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @PostMapping(value = "/updateAdmin")
    public ResultVo updateAdmin(@RequestBody Admin administor, @SessionAttribute("admin") Admin admin) {
        // 更新的话不需要更改 创建者和创建时间
        adminService.save(entityUtil.updateInfoDefault(administor, null, admin.getId(), false));
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @PostMapping(value = "/deleteAdmin")
    public ResultVo deleteAdmin(@RequestBody Admin administor, @SessionAttribute("admin") Admin admin) {
        // 这里的RequestBody 的 user只需要一个id就行了
        adminService.delete(administor);
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @PostMapping(value = "/deleteAdmins")
    public ResultVo deleteAdmins(@RequestBody List<Admin> administors, @SessionAttribute("admin") Admin admin) {
        // 这里的RequestBody 的 users 是一个 user的数组
        adminService.delete(administors);
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
    @GetMapping(value = "/findAllAdmins")
    public ResultVo findAllAdmins(@RequestParam(required = true) Integer page,
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

        Page<Admin> administors = adminService.findAll(pager);

        return simpleResult(ResultEnum.SUCCESS, administors);
    }


}
