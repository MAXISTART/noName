package com.school.store.admin.refine;

import com.school.store.admin.buy.entity.BuyOrderItem;
import com.school.store.admin.buy.service.BuyOrderItemService;
import com.school.store.admin.department.entity.Department;
import com.school.store.admin.department.service.DepartmentService;
import com.school.store.admin.permission.entity.Permission;
import com.school.store.admin.permission.entity.UserToPermission;
import com.school.store.admin.permission.service.PermissionService;
import com.school.store.admin.permission.service.UserToPermissionService;
import com.school.store.admin.take.entity.TakeOrderItem;
import com.school.store.admin.take.service.TakeOrderItemService;
import com.school.store.admin.user.entity.User;
import com.school.store.admin.user.service.UserService;
import com.school.store.utils.ReflectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 提供 entity 的修整功能，主要应用是在外键上
@Component
public class EntityRefineService {

    @Autowired
    DepartmentService departmentService;

    @Autowired
    private UserToPermissionService userToPermissionService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private BuyOrderItemService buyOrderItemService;

    @Autowired
    private TakeOrderItemService takeOrderItemService;

    @Autowired
    private UserService userService;

    private Object entity;
    private Field field;

    /**
     *  这个方法不对外使用，只能对内，对外只有refine的方法
     *  相当于外键 departmentId的引用，拿到 传过来的属性的值，然后获取到department后设置到注解下的属性中
     */
    private void setDepartmentName(String[] argNames){

        try {
            field.setAccessible(true);

            // 找出对应的department
            Department department = departmentService.findById(ReflectUtil.getFieldValueByName(entity, argNames[0]) + "");


            // 设置department的名字进去
            field.set(entity, department.getName());
            System.out.println(entity.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     *  这个方法不对外使用，只能对内，对外只有refine的方法
     *
     */
    private void setPermissions(String[] argNames){

        try {
            field.setAccessible(true);
            // 找到 userId 的属性，然后修改

            Set<Permission> permissions = new HashSet<>();
            List<UserToPermission> userToPermissions = userToPermissionService.
                    findByUserId(ReflectUtil.getFieldValueByName(entity, argNames[0]) + "");

            userToPermissions.forEach(userToPermission -> {
                permissions.add(permissionService.findById(userToPermission.getPermissionId()));
            });


            field.set(entity, permissions);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     *  这个方法不对外使用，只能对内，对外只有refine的方法
     *
     */
    private void setBuyOrderItems(String[] argNames){

        try {
            field.setAccessible(true);
            // 找到 buyOrderId 的属性，然后修改

            List<BuyOrderItem> buyOrderItems =buyOrderItemService
                    .findByOrderId(ReflectUtil.getFieldValueByName(entity, argNames[0]) + "");


            field.set(entity, buyOrderItems);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     *  这个方法不对外使用，只能对内，对外只有refine的方法
     *
     */
    private void setTakeOrderItems(String[] argNames){

        try {
            field.setAccessible(true);
            // 找到 buyOrderId 的属性，然后修改

            List<TakeOrderItem> takeOrderItems = takeOrderItemService
                    .findByOrderId(ReflectUtil.getFieldValueByName(entity, argNames[0]) + "");


            field.set(entity, takeOrderItems);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     *  这个方法不对外使用，只能对内，对外只有refine的方法
     *
     */
    private void setUserName(String[] argNames){

        try {
            field.setAccessible(true);

            // 找出对应的department
            User user = userService.findById(ReflectUtil.getFieldValueByName(entity, argNames[0]) + "");


            // 设置department的名字进去
            field.set(entity, user.getName());
            System.out.println(entity.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }





    public void refine(Object entity){
        this.entity = entity;
        try {
            // 1. 获取所有field
            for(Field field : ReflectUtil.getAllFields(entity)){
                if(field.isAnnotationPresent(Refine.class)){
                    this.field = field;
                    // 2. 获取包含 Refine 注解的field 提取出来，并且获取他的value，
                    // value实际上是一个enum，获取这个enum的名称，然后根据名称找方法
                    String[] argNames = field.getAnnotation(Refine.class).argNames();
                    String methodName = field.getAnnotation(Refine.class).value().toString();
                    // string[] 这种类型比较特殊，要用Object包装一下，否则会报错
                    if(argNames == null || argNames.length == 0){
                        // 如果是无参
                        this.getClass().getDeclaredMethod(methodName).invoke(this);
                    }else{
                        // 如果有参数
                        this.getClass().getDeclaredMethod(methodName, String[].class).invoke(this, new Object[]{argNames});
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public <T> void refineList(List<T> entities){
        for (T entity : entities){
            refine(entity);
        }
    }

    public <T> void refineSet(Set<T> entities){
        for (T entity : entities){
            refine(entity);
        }
    }

    public <T> void refinePage(Page<T> entities){
        for (T entity : entities){
            refine(entity);
        }
    }

}
