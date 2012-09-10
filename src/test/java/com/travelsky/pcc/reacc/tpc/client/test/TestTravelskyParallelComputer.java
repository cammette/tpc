package com.travelsky.pcc.reacc.tpc.client.test;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.bean.TaskUnitResult;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/applicationContext-parallel.xml" })
public class TestTravelskyParallelComputer {

	@Autowired
	private TravelskyParallelComputer travelskyParallelComputer;
	
	@Test
	public void testTravelskyParallelComputerExcuteAsyn() throws TaskExcutedReplyTimeoutException{
		travelskyParallelComputer.excuteAsyn(100);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(100, TravelskyParallelComputer.size);
	}
	
	@Test
	public void testTravelskyParallelComputerExcuteSync() throws TaskExcutedReplyTimeoutException{
		long a = System.currentTimeMillis();
		TaskResult taskResult = travelskyParallelComputer.excuteSyn(5000, 1000);
		List<TaskUnitResult> taskUnitResults = taskResult.getTaskUnitResults();
		System.out.println(System.currentTimeMillis()-a);
		Assert.assertEquals(5000,taskUnitResults.size());
		for (TaskUnitResult taskUnitResult : taskUnitResults) {
			System.out.println(taskUnitResult.getTaskResult());
		}
		
	}
	
	
}
