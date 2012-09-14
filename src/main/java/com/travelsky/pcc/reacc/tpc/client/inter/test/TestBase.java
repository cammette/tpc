package com.travelsky.pcc.reacc.tpc.client.inter.test;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.bean.TaskUnitResult;
import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputingInterface;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/parallel-test.xml" })
public class TestBase {
	public Logger log = Logger.getLogger(getClass());
	@Autowired
	public TravelskyParallelComputingInterface<Object> travelskyParallelComputerInterface;
	@Autowired
	public TestBaseP testBaseP;
	
	public void requestParallelSync(TestBaseP bean, String title, boolean isSyn) {
		TaskResult taskResult = null;
		try {
			log.info("-------------------" + title + " test start\n");
			if (isSyn) {
				taskResult = travelskyParallelComputerInterface.excuteSyn(bean,
						500000);
				log.info("ddddddddddddddddddddffffffffffffffffff");
				if (null != taskResult) {
					log.info("dddddddddddddddddddd");
					log.info("time spend:"
							+ (taskResult.getEndTime().getTime() - taskResult
									.getStartTime().getTime()) + "ms");
				}
				else{
					log.info("taskResult is null!!!!!!!!!!!!!!!!!!!!11");
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
			// retry log
			if (bean.isRetry()) {
				for (TaskUnitResult unit : taskUnitResults) {
					if (!unit.getIsSuccess()) {
						log.info("retry msg:" + unit.getMsg());
					}

				}
			}
			log.info("-------------------" + title + " test code end\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
