package com.travelsky.pcc.reacc.tpc.client.test;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputingInterface;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedException;


public class RetryTest extends TestBase{
	private Logger log = Logger.getLogger(getClass());
	@Autowired
	private TravelskyParallelComputingInterface<Object> travelskyParallelComputerInterface;

	@Test
	public void testTravelskyParallelComputerExcuteAsyn()
			throws TaskExcutedException {
		TtestBean bean = new TtestBean();
		bean.setRetry(true);
		requestParallelSync(bean, "asyn retry ", false);
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
		bean.setRetry(true);
		requestParallelSync(bean, "syn retry ", true);

	}

}
