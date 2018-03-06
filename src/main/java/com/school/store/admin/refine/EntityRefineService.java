package com.school.store.admin.refine;

import com.school.store.admin.buy.entity.BuyOrderItem;
import com.school.store.admin.buy.service.BuyOrderItemService;
import com.school.store.admin.department.entity.Department;
import com.school.store.admin.department.service.DepartmentService;
import com.school.store.admin.good.entity.GoodItem;
import com.school.store.admin.good.service.GoodItemService;
import com.school.store.admin.permission.entity.Permission;
import com.school.store.admin.permission.entity.UserToPermission;
import com.school.store.admin.permission.service.PermissionService;
import com.school.store.admin.permission.service.UserToPermissionService;
import com.school.store.admin.store.entity.StoreOperationItem;
import com.school.store.admin.store.service.StoreOperationItemService;
import com.school.store.admin.take.entity.TakeOrderItem;
import com.school.store.admin.take.service.TakeOrderItemService;
import com.school.store.admin.user.entity.User;
import com.school.store.admin.user.service.UserService;
import com.school.store.utils.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

// 提供 entity 的修整功能，主要应用是在外键上
@Component
@Slf4j
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

    @Autowired
    private GoodItemService goodItemService;

    @Autowired
    private StoreOperationItemService storeOperationItemService;


    /**
     *  这个方法不对外使用，只能对内，对外只有refine的方法
     *  相当于外键 departmentId的引用，拿到 传过来的属性的值，然后获取到department后设置到注解下的属性中
     */
    private void setDepartmentName(String[] argNames, HashMap resources){

        try {
            Object entity = resources.get("entity");
            Field field = (Field) resources.get("field");

            field.setAccessible(true);

            // 找出对应的department
            Department department = departmentService.findById(ReflectUtil.getFieldValueByName(entity, argNames[0]) + "");

            if(department == null){
                return;
            }
            // 设置department的名字进去
            field.set(entity, department.getName());
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     *  这个方法不对外使用，只能对内，对外只有refine的方法
     *
     */
    private void setPermissions(String[] argNames, HashMap resources){

        try {
            Object entity = resources.get("entity");
            Field field = (Field) resources.get("field");

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
    private void setBuyOrderItems(String[] argNames, HashMap resources){

        try {
            Object entity = resources.get("entity");
            Field field = (Field) resources.get("field");

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
    private void setTakeOrderItems(String[] argNames, HashMap resources){

        try {
            Object entity = resources.get("entity");
            Field field = (Field) resources.get("field");

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
    private void setStoreOperationItems(String[] argNames, HashMap resources){

        try {
            Object entity = resources.get("entity");
            Field field = (Field) resources.get("field");

            field.setAccessible(true);
            // 找到 buyOrderId 的属性，然后修改

            List<StoreOperationItem> storeOperationItems = storeOperationItemService
                    .findByOrderId(ReflectUtil.getFieldValueByName(entity, argNames[0]) + "");


            field.set(entity, storeOperationItems);

        }catch (Exception e){
            e.printStackTrace();
        }
    }




    /**
     *  这个方法不对外使用，只能对内，对外只有refine的方法
     *
     */
    private void setUserName(String[] argNames, HashMap resources){

        try {
            Object entity = resources.get("entity");
            Field field = (Field) resources.get("field");

            field.setAccessible(true);

            // 找出对应的department
            if(userService == null){
                System.out.println("userService is null");
            }
            if(entity == null){
                System.out.println("entity is null");
            }
            if(argNames == null){
                System.out.println("argNames is null");
            }
            User user = userService.findById(ReflectUtil.getFieldValueByName(entity, argNames[0]) + "");

            if(user == null){
                return;
            }
            // 设置department的名字进去
            field.set(entity, user.getName());
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    /**
     *  这个方法不对外使用，只能对内，对外只有refine的方法
     *
     */
    private void setGoodItem(String[] argNames, HashMap resources){

        try {
            Object entity = resources.get("entity");
            Field field = (Field) resources.get("field");

            field.setAccessible(true);

            // 找出对应的goodItem
            GoodItem goodItem = goodItemService.findOne(ReflectUtil.getFieldValueByName(entity, argNames[0]) + "");

            // 设置department的名字进去
            field.set(entity, goodItem);
        }catch (Exception e){
            e.printStackTrace();
        }
    }





    public void refine(Object entity){
        try {
            // 1. 获取所有field
            Field[] fields = ReflectUtil.getAllFields(entity);
            if(fields == null || fields.length == 0){
                return;
            }
            for(Field field : fields){
                if(field.getAnnotation(Refine.class) != null){
                    // 2. 获取包含 Refine 注解的field 提取出来，并且获取他的value，
                    // value实际上是一个enum，获取这个enum的名称，然后根据名称找方法
                    String[] argNames = field.getAnnotation(Refine.class).argNames();
                    String methodName = field.getAnnotation(Refine.class).value().toString();
                    HashMap map = new HashMap<>();
                    map.put("entity", entity);
                    map.put("field", field);
                    // string[] 这种类型比较特殊，要用Object包装一下，否则会报错
                    if(argNames == null || argNames.length == 0){
                        // 如果是无参
                        this.getClass().getDeclaredMethod(methodName,Map.class).invoke(this, map);
                    }else{
                        // 如果有参数
                        Method m = this.getClass().getDeclaredMethod(methodName, argNames.getClass(), map.getClass());
                        m.setAccessible(true); //没有设置就会报错
                        m.invoke(this, argNames, map);
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
