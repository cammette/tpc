package com.travelsky.pcc.reacc.tpc.jms;

import javax.jms.Message;

public interface JMSCallBack {
	public void callBack(Message message);
}
