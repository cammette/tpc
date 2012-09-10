package com.travelsky.pcc.reacc.tpc.client.test;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.bean.TaskUnitResult;
import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputingInterface;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/person-test.xml" })
public class PersonTest {
	private Logger log = Logger.getLogger(getClass());
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
		bean.setNull(false);
		bean.setSizeZero(false);
		bean.setRetry(true);
		requestParallelSync(bean, "syn retry ", true);
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
//		requestParallelSync(bean, "syn normal test ", true);
//		bean.setNull(true);
//		requestParallelSync(bean, "syn split return null", true);
//		bean.setNull(false);
//		bean.setSizeZero(true);
//		requestParallelSync(bean, "syn zero group", true);
//		bean.setNull(false);
//		bean.setSizeZero(false);
		bean.setRetry(true);
		requestParallelSync(bean, "syn retry ", true);
//		try {
//			Thread.sleep(50000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

	private void requestParallelSync(TtestBean bean, String title, boolean isSyn) {
		TaskResult taskResult = null;
		try {
			log.info("-------------------" + title + " test start\n");
			if (isSyn) {
				taskResult = travelskyParallelComputerInterface.excuteSyn(bean,
						500000);
				if (null != taskResult) {
					log.info("time spend:"
							+ (taskResult.getEndTime().getTime() - taskResult
									.getStartTime().getTime())+"ms");
				}
			} else {
				travelskyParallelComputerInterface.excuteAsyn(bean);
				return;
			}

			if (null == taskResult) {
				log.info("taskResult is null");
				return;
			}
			List<TaskUnitResult> taskUnitResults = taskResult
					.getTaskUnitResults();
			log.info(taskResult.toString());
			log.info("-------------------" + title + " test code end\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
