package com.school.store.base.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 判断对象是否为空
 * @author liyuci
 * Mar 24, 2013 3:13:33 PM
 */
public class BlankUtil {

	/**
	 * 判断字符串是否为�?
	 */
	public static boolean isBlank(final String str) {
		return (str == null) || (str.trim().length() <= 0);
	}
	
	public static boolean isNotBlank(final String str)
	{
		return !isBlank(str);
	}

	/**
	 * 判断字符是否为空
	 * @param cha
	 * @return
	 */
	public static boolean isBlank(final Character cha) {
		return (cha == null);
	}
	
	public static boolean isNotBlank(final Character cha)
	{
		return !isBlank(cha);
	}

	/**
	 * 判断对象是否为空
	 */
	public static boolean isBlank(final Object obj) {
		return (obj == null);
	}

	public static boolean isNotBlank(final Object obj)
	{
		return !isBlank(obj);
	}
	
	/**
	 * 判断数组是否为空
	 * @param objs
	 * @return
	 */
	public static boolean isBlank(final Object[] objs) {
		return (objs == null) || (objs.length <= 0) || isBlank(objs[0].toString());
	}
	
	public static boolean isNotBlank(final Object[] objs)
	{
		return !isBlank(objs);
	}
	
	/**
	 * 判断Collectionj是否为空
	 * @param obj
	 * @return
	 */
	public static boolean isBlank(final Collection<?> obj) {
		return (obj == null) || obj.isEmpty();
	}
	
	public static boolean isNotBlank(final Collection<?> obj)
	{
		return !isBlank(obj);
	}
	
	/**
	 * 
	 * @param obj
	 * @return
	 * @description�?   判断对象是否为空
	 * @return: boolean
	 * @method: isBlank
	 */
	public static boolean isBlank(final Properties properties) {
		return (properties == null || (properties != null && properties.isEmpty()));
	}
	
	public static boolean isNotBlank(final Properties properties)
	{
		return !isBlank(properties);
	}
	
	/**
	 * 判断Set是否为空
	 * @param set
	 * @return
	 */
	public static boolean isBlank(final Set<?> set) {
		return (set == null) || set.isEmpty();
	}
	
	public static boolean isNotBlank(final Set<?> set)
	{
		return !isBlank(set);
	}
	
	/**
	 * 判断Serializable是否为空
	 * @param obj
	 * @return
	 */
	public static boolean isBlank(final Serializable obj) {
		return obj == null;
	}
	
	public static boolean isNotBlank(final Serializable obj)
	{
		return !isBlank(obj);
	}
	
	/**
	 * 判断Map是否为空
	 * @param map
	 * @return
	 */
	public static boolean isBlank(final Map<?, ?> map) {
		return (map == null) || map.isEmpty();
	}
	
	public static boolean isNotBlank(final Map<?, ?> map)
	{
		return !isBlank(map);
	}
}