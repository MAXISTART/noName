package com.school.store.admin.user.controller;


import com.school.store.admin.permission.entity.Permission;
import com.school.store.admin.permission.entity.UserToPermission;
import com.school.store.admin.permission.service.PermissionService;
import com.school.store.admin.permission.service.UserToPermissionService;
import com.school.store.admin.refine.EntityRefineService;
import com.school.store.admin.user.entity.User;
import com.school.store.admin.user.service.UserService;
import com.school.store.annotation.Permiss;
import com.school.store.aspect.PermissionAspect;
import com.school.store.base.controller.BaseAdminController;
import com.school.store.base.model.MPager;
import com.school.store.base.model.SqlParams;
import com.school.store.constant.CookieConstant;
import com.school.store.constant.Permit;
import com.school.store.constant.RedisConstant;
import com.school.store.enums.ResultEnum;
import com.school.store.exception.BaseException;
import com.school.store.utils.CookieUtil;
import com.school.store.utils.HttpUtil;
import com.school.store.utils.RegexUtil;
import com.school.store.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/admin/user")
@Permiss(and = { Permit.ADMIN })
@Slf4j
public class UserController extends BaseAdminController{

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserToPermissionService userToPermissionService;


    @Autowired
    private EntityRefineService entityRefineService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PermissionAspect permissionAspect;

    @PostMapping(value = "/addUser")
    public ResultVo addUser(@RequestBody User user) {
        user.setId(null);
        user.setPassword("123456");
        userService.save(user);
        return simpleResult(ResultEnum.SUCCESS, null);
    }

    @PostMapping(value = "/updateUser")
    public ResultVo updateUser(@RequestBody User user) {

        userService.dynamicUpdate(user);
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    /**
     *  这个是由任意的user或者admin来更新的，只能更新自己的账号信息，不能更新其他账号的
     * @param user
     * @return
     */
    @PostMapping(value = "/updateUserBySelf")
    @Permiss(newOr = {Permit.ADMIN, Permit.USER})
    public ResultVo updateUserBySelf(@RequestBody User user) {
        user.setId(HttpUtil.getSessionUserId());
        userService.dynamicUpdate(user);
        return simpleResult(ResultEnum.SUCCESS, null);
    }



    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteUser")
    public ResultVo deleteUser(@RequestBody User user) {
        // 要级联删除
        userService.cascadeDelete(user);
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    @Transactional(readOnly = false)
    @PostMapping(value = "/deleteUsers")
    public ResultVo deleteUsers(@RequestBody List<User> users) {
        users.forEach(user -> {
            deleteUser(user);
        });
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    /**
     *  给指定用户添加权限
     * @param userToPermissions
     * @return
     */

    @Transactional(readOnly = false)
    @PostMapping(value = "/addPermissions")
    @CacheEvict(value = "userWithPermission", allEntries = true)
    public ResultVo addPermissions(@RequestBody List<UserToPermission> userToPermissions) {

        // 要先验证是否已经存在了该 用户-权限 对。
        for(int index=0; index < userToPermissions.size(); index++){
            UserToPermission userToPermission = userToPermissions.get(index);
            if(userToPermission.getPermissionId() == null || userToPermission.getUserId() == null
                    || StringUtils.isEmpty(userToPermission.getPermissionId()) || StringUtils.isEmpty(userToPermission.getUserId())){
                continue;
            }
            if(userToPermissionService.findByPermissionIdAndUserId(userToPermission.getPermissionId(), userToPermission.getUserId()) == null){
                userToPermissionService.save(userToPermission);
            }
        }
        return simpleResult(ResultEnum.SUCCESS, null);
    }


    /**
     *  给指定用户删除权限
     * @param userToPermissions
     * @return
     */

    @Transactional(readOnly = false)
    @PostMapping(value = "/removePermissions")
    @CacheEvict(value = "userWithPermission", allEntries = true)
    public ResultVo removePermissions(@RequestBody List<UserToPermission> userToPermissions) {
        userToPermissions.forEach(userToPermission -> {
            userToPermissionService.deleteByPermissionIdAndUserId(userToPermission.getPermissionId(), userToPermission.getUserId());
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
    @GetMapping(value = "/findAllUsers")
    public ResultVo findAllUser(@RequestParam(required = true) Integer page,
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

        Page<User> users = userService.findAll(pager);
        // 给所有的user添加额外属性
        entityRefineService.refinePage(users);
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
        // 给所有的user添加额外属性
        entityRefineService.refine(user);
        return user;
    }


    /**
     *  根据departmentId传回user
     * @param departmentId
     * @return
     */
    @PostMapping(value = "/findUsersByDepartmentId")
    public ResultVo findUsersByDepartmentId(@RequestParam String departmentId){
        List<User> users = userService.findByDepartmentId(departmentId);
        // 给所有的user添加额外属性
        entityRefineService.refineList(users);
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
                                           @RequestParam(required = false, defaultValue = "lastmodifiedTime") String property,
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
        sqlParams.put("ORDER BY", property, direction);
        // 返回的是真正的List<User>
        MPager<User> users = userService.findByDynamicSqlParams(sqlParams,page,size,User.class);
        // 给每个user设置他们对应的departmentName
        entityRefineService.refineList(users.getData());
        return simpleResult(ResultEnum.SUCCESS, users);
    }




    @PostMapping("/login")
    @Permiss(need = false)
    public ResultVo login(@RequestBody User userInput, HttpServletResponse response)
    {
        // 先按照名字查找,如果没有，再按照手机和邮箱来查找
        User userInfo = userService.findByNameAndPassword(userInput.getName(), userInput.getPassword());
        if(userInfo == null){

            //1. 先从 userName 里面解析出是什么格式，是邮箱还是电话还是用户名
            if(RegexUtil.checkEmail(userInput.getName())){
                userInfo = userService.findByMailboxAndPassword(userInput.getName(), userInput.getPassword());
            }
            if(RegexUtil.checkMobile(userInput.getName())){
                userInfo = userService.findByPhoneNumberAndPassword(userInput.getName(), userInput.getPassword());
            }
            if(!RegexUtil.checkEmail(userInput.getName()) && !RegexUtil.checkMobile(userInput.getName())){
                throw new BaseException(ResultEnum.NAME_FORMAT_ERROR);
            }
            if(userInfo == null){
                throw new BaseException(ResultEnum.USER_NOT_FOUND);
            }
        }

        log.warn("userInfo : " + userInfo);

        //2. 设置token至redis
        String token = UUID.randomUUID().toString();
        Integer expire = RedisConstant.EXPIRE;

        log.warn("before redis");
        redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX, token), userInfo.getId(), expire, TimeUnit.SECONDS);
        log.warn("after redis");

        //3. 设置token至cookie
        CookieUtil.set(response, CookieConstant.TOKEN, token, expire);

        // 4. 设置用户到session中
        log.warn("userId 是 " + userInfo.getId());
        HttpUtil.getSession().setAttribute("userId", userInfo.getId());

        // 获取用户的权限，根据权指定用户为管理员还是用户，前端根据这个得知应该跳转 管理员页面 还是 用户页面
        // 这里面的 管理员权限 并不是包含 用户权限的，而是并列的关系
        entityRefineService.refine(userInfo);
        Set<Permission> permissions = userInfo.getPermissions();
        if(permissionAspect.hasPermission(permissions, Permit.ADMIN)){
            return simpleResult(ResultEnum.ADMIN_LOGIN_SUCCESS, userInfo);
        }
        if(permissionAspect.hasPermission(permissions, Permit.USER)){
            return simpleResult(ResultEnum.USER_LOGIN_SUCCESS, userInfo);
        }
        return simpleResult(ResultEnum.USER_LOGIN_SUCCESS, userInfo);

    }




    @PostMapping("/logout")
    @Permiss(newOr = {Permit.USER, Permit.ADMIN})
    public ResultVo logout(HttpServletRequest request, HttpServletResponse response)
    {

        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie != null) {
            //1. 清除redis
            redisTemplate.opsForValue().getOperations().delete(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));

            //2. 清除userId
            HttpUtil.getSession().setAttribute("userId", null);

            //3. 清除cookie
            CookieUtil.set(response, CookieConstant.TOKEN, null, 0);
        }
        return simpleResult(ResultEnum.LOGIN_OUT_SUCCESS, null);
    }


    @PostMapping("/register")
    @Permiss(need = false)
    public ResultVo register(@RequestBody User userInput, HttpServletResponse response){
        userInput.setId(null);
        // 邮箱和手机是一定要的，而且格式要正确
        if(!RegexUtil.checkEmail(userInput.getMailbox())){
            throw new BaseException(ResultEnum.MAIL_FORMAT_ERROR);
        }
        if(!RegexUtil.checkMobile(userInput.getPhoneNumber())){
            throw new BaseException(ResultEnum.MOBILE_FORMAT_ERROR);
        }
        if(userInput == null){
            throw new BaseException(ResultEnum.PARAM_ERROR);
        }
        userService.save(userInput);
        // 默认是user权限
        Permission userPermission = permissionService.findByName(Permit.USER);
        UserToPermission userToPermission = new UserToPermission();
        userToPermission.setPermissionId(userPermission.getId());
        userToPermission.setUserId(userInput.getId());
        userToPermissionService.save(userToPermission);

        return simpleResult(ResultEnum.REGISTER_SUCCESS, null);
    }



}
