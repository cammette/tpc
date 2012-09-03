package com.travelsky.pcc.reacc.tpc.client.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.bean.TaskUnitResult;
import com.travelsky.pcc.reacc.tpc.client.TaskGroupParallelClientInterface;
import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputerInterface;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedException;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/person-group-test.xml" })
public class PersonGroupTest {
	
	@Autowired
	private TravelskyParallelComputerInterface travelskyParallelComputerInterface;
//	@Test
//	public void testTravelskyParallelComputerExcuteAsyn() throws TaskExcutedException{
//		travelskyParallelComputerInterface.excuteAsyn(5);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
	
	@Test
	public void testTravelskyParallelComputerExcuteSync() throws TaskExcutedException, com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException{
		long a = System.currentTimeMillis();
		TaskResult taskResult = travelskyParallelComputerInterface.excuteSyn(5, 1000);
		List<TaskUnitResult> taskUnitResults = taskResult.getTaskUnitResults();
		for (TaskUnitResult taskUnitResult : taskUnitResults) {
			System.out.println(taskUnitResult.getTaskResult());
		}
		
	}

}
