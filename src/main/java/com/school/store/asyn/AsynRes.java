package com.school.store.asyn;

import java.io.Serializable;
import java.util.Map;

public class AsynRes implements Serializable  {

	private static final long serialVersionUID = 1L;
	/**
	 * 返回码值,默认值0
	 */
	private int res=0;
	/**
	 * 返回码值解析
	 */
	private String resMsg;
	/**
	 * 返回对象
	 */
	private Map<String, Object> map ;
	
	public int getRes() {
		return res;
	}
	public void setRes(int res) {
		this.res = res;
	}
	public String getResMsg() {
		return resMsg;
	}
	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}
	
	public Map<String, Object> getMap() {
		return map;
	}
	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	/**
	 * 设置没有权限返回值
	 * @param auth 原值返回
	 * @return
	 */
	public boolean setNoAuth(boolean auth){
		if(!auth){
			this.map = null;
			this.setRes(100);//100 没有权限
			this.setResMsg("没有权限");
		}
		return auth;
	}
	/**
	 * 设置成功值
	 * @param obj  设置对象  
	 * @param resMsg  设置码值解析
	 */
	public void setSucceed(Map<String, Object> obj,String resMsg){
		this.setResMsg(resMsg);
		this.setSucceed(obj);
	}
	/**
	 * 设置成功值
	 * @param obj 设置对象    
	 */
	public void setSucceed(Map<String, Object> obj){
		this.map = obj;
		this.setRes(1);//1成功
	}
	/**
	 * 设置成功值
	 * @param resMsg 返回码值解析
	 */
	public void setSucceedMsg(String resMsg){
		this.setRes(1);//1成功
		this.setResMsg(resMsg);
	}
	/**
	 * 设置失败值
	 * @param resMsg 返回码值解析
	 */
	public void setFailMsg(String resMsg){
		this.setRes(0);//0失败
		this.setResMsg(resMsg);
	}
	
	@Override
	public String toString() {
		return "AjaxRes [res=" + res + ", resMsg=" + resMsg + ", map=" + map + "]";
	}	
	
}
