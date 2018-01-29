package com.school.store.utils;


import com.school.store.admin.girl.entity.Girl;
import com.school.store.admin.user.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class MyBeanUtil {


    public static void main(String[] args){
        User user = new User();
        user.setDepartmentId("1");
        try {
            System.out.println(transBean2Map(user));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 拷贝实体，source,target不允许为空
     *
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }

    /**
     * 拷贝实体集合，sourceList，targetList不允许为空
     *
     * @param sourceList
     * @param targetList
     */
    public static <T> void copyPropertiesList(List sourceList, List<T> targetList, Class clazz) {
        try{
            if (CollectionUtils.isEmpty(sourceList) || targetList == null ) {
                System.out.println("MyBeanUtil 中的copyPropertiesList报错");
            }
            for(Object items : sourceList){
                T target = (T)clazz.newInstance();
                BeanUtils.copyProperties(items, target);
                targetList.add(target);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Map --> Bean 2: 利用org.apache.commons.beanutils 工具类实现 Map --> Bean
     *
     * @param map
     * @param obj
     */
    public static void transMap2Bean2(Map<String, Object> map, Object obj) throws InvocationTargetException, IllegalAccessException {
        if (map == null || obj == null) {
            return;
        }
        org.apache.commons.beanutils.BeanUtils.populate(obj, map);
    }

    public static <T> List<T> transMapList2BeanList(List<Map<String, Object>> list, Class<T> clazz){
        return list.stream().map(P -> {
            T entity = null;
            try{
                entity = clazz.newInstance();
                transMap2Bean(P, entity);
            }catch (Exception e){
                e.printStackTrace();
            }
            return entity;
        }).collect(Collectors.toList());
    }

    /**
     * Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean
     *
     * @param map
     * @param obj
     */
    public static void transMap2Bean(Map<String, Object> map, Object obj) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (map.containsKey(key)) {
                Object value = map.get(key);
                if(key.contains("Time") || key.contains("Date")){
                    // 如果是时间Date属性，需要转换一下
                    Timestamp timestamp = (Timestamp)value;
                    value = new Date(timestamp.getTime());
                }
                // 得到property对应的setter方法
                Method setter = property.getWriteMethod();
                setter.invoke(obj, value);
            }
        }
    }

    /**
     * Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map
     *
     * @param obj
     */
    public static Map<String, Object> transBean2Map(Object obj){

        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }


    public static List<Map<String, Object>> transBeanList2MapList(List<Object> list){
        return list.stream().map(P -> {
            Map<String,Object> map = null;
            try{
                map = transBean2Map(P);
            }catch (Exception e){
                e.printStackTrace();
            }
            return map;
        }).collect(Collectors.toList());

    }

}
