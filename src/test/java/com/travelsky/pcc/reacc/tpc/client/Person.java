package com.travelsky.pcc.reacc.tpc.client;

public class Person implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2388183799406668906L;
	/**
	 * 
	 */
	String name;
	String id;
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
	

}
