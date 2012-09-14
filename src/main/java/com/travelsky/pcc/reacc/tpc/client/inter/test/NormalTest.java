package com.travelsky.pcc.reacc.tpc.client.inter.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputingInterface;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedException;


public class NormalTest extends TestBase{
	@Autowired
	private TravelskyParallelComputingInterface<Object> travelskyParallelComputerInterface;

	public void testTravelskyParallelComputerExcuteAsyn()
			throws TaskExcutedException {
		requestParallelSync(testBaseP, "asyn normal test ", false);
		testBaseP.setNull(true);
		requestParallelSync(testBaseP, "asyn split return null", false);
		testBaseP.setNull(false);
		testBaseP.setSizeZero(true);
		requestParallelSync(testBaseP, "asyn zero group", false);
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
		requestParallelSync(testBaseP, "syn normal test ", true);
		testBaseP.setNull(true);
		requestParallelSync(testBaseP, "syn split return null", true);
		testBaseP.setNull(false);
		testBaseP.setSizeZero(true);
		requestParallelSync(testBaseP, "syn zero group", true);
	}

}
