package com.school.store.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  提供反射的各种方法
 */

public class ReflectUtil {


    public static Field getFieldByName(Object entity, String name){
        Field[] fields = getAllFields(entity);
        for (Field field : fields){
            if(field.getName().equals(name)){
                return field;
            }
        }
        return null;
    }


    public static Object getFieldValueByName(Object entity, String name){
        try {
            Field[] fields = getAllFields(entity);
            for (Field field : fields){
                if(field.getName().equals(name)){
                    field.setAccessible(true);
                    return field.get(entity);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Field[] getAllFields(Object object){
        Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null){
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }
}
