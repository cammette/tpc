package com.travelsky.pcc.reacc.tpc.client.inter.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputingInterface;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedException;


public class RetryTest extends TestBase{
	@Autowired
	private TravelskyParallelComputingInterface<Object> travelskyParallelComputerInterface;

	@Test
	public void testTravelskyParallelComputerExcuteAsyn()
			throws TaskExcutedException {
		testBaseP.setRetry(true);
		requestParallelSync(testBaseP, "asyn retry ", false);
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
		testBaseP.setRetry(true);
		requestParallelSync(testBaseP, "syn retry ", true);

	}

}
