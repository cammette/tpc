package com.travelsky.pcc.reacc.tpc.client.test;

import com.travelsky.pcc.reacc.tpc.client.inter.test.TestBaseT;

public class Person extends TestBaseT implements java.io.Serializable {

	private static final long serialVersionUID = -2388183799406668909L;
	@Override
	public Object getObject() {
		Person person = new Person();
		person.setId(new String("123"));
		person.setName("name");
		return person;
	}
	public String toString(){
		return this.getId() +" "+ this.getName();
	}
}
