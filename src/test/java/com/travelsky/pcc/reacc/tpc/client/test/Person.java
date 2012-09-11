package com.travelsky.pcc.reacc.tpc.client.test;

public class Person implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2388183799406668906L;
	/**
	 * 
	 */
	private String name;
	private String id;
	/**
	 * 用于异常测试
	 */
	private boolean isRetry = false;
	/**
	 * 默认任务执行sleep，测试重启时sleep可以设置长一点
	 */
	private long sleep=1000;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isRetry() {
		return isRetry;
	}
	public void setRetry(boolean isRetry) {
		this.isRetry = isRetry;
	}
	public long getSleep() {
		return sleep;
	}
	public void setSleep(long sleep) {
		this.sleep = sleep;
	}
	
}
