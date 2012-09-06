package com.travelsky.pcc.reacc.tpc.client;

import javax.jms.Message;

public interface TaskSendedCallBack {
	public void callBack(Message message);
}
