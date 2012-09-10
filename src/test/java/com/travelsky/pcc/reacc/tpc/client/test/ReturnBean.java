package com.travelsky.pcc.reacc.tpc.client.test;

public class ReturnBean implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6520906631405535182L;
	
	String id ;
	String result;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String toString(){
		return "id="+id+",result="+result+"";
	}
	
}
