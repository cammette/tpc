package com.travelsky.pcc.reacc.tpc.client.inter.test;

public class TestBaseU implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6520906631405535182L;
	
	String id ;
	Object result;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public String toString(){
		return "id="+id+",result="+result+"";
	}
	
}
