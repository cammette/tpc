package com.travelsky.pcc.reacc.tpc.client.test;

import com.travelsky.pcc.reacc.tpc.client.inter.test.TestBaseP;
import com.travelsky.pcc.reacc.tpc.client.inter.test.TestBaseT;

public class TtestBean extends TestBaseP{
	@Override
	public TestBaseT getSendT(int groupNo, int listIndex) {
		Person person = new Person();
		person.setId("groupNo:"+groupNo+" listIndex:"+listIndex);
		person.getObject();
		return person;
	}
}
