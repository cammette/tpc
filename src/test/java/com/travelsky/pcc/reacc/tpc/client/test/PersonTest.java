package com.travelsky.pcc.reacc.tpc.client.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputingInterface;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedException;


public class PersonTest extends TestBase{
	@Autowired
	private TravelskyParallelComputingInterface<Object> travelskyParallelComputerInterface;

	@Test
	public void testTravelskyParallelComputerExcuteAsyn()
			throws TaskExcutedException {
		TtestBean bean = new TtestBean();
		requestParallelSync(bean, "asyn normal test ", false);
		bean.setNull(true);
		requestParallelSync(bean, "asyn split return null", false);
		bean.setNull(false);
		bean.setSizeZero(true);
		requestParallelSync(bean, "asyn zero group", false);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTravelskyParallelComputerExcuteSync()
			throws TaskExcutedException,
			com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException {
		TtestBean bean = new TtestBean();
		requestParallelSync(bean, "syn normal test ", true);
		bean.setNull(true);
		requestParallelSync(bean, "syn split return null", true);
		bean.setNull(false);
		bean.setSizeZero(true);
		requestParallelSync(bean, "syn zero group", true);
	}

}
