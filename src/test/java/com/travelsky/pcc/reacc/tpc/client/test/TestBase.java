package com.travelsky.pcc.reacc.tpc.client.test;

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
@ContextConfiguration({ "/person-test.xml" })
public class TestBase {
	public Logger log = Logger.getLogger(getClass());
	@Autowired
	public TravelskyParallelComputingInterface<Object> travelskyParallelComputerInterface;

	public void requestParallelSync(TtestBean bean, String title, boolean isSyn) {
		TaskResult taskResult = null;
		try {
			log.info("-------------------" + title + " test start\n");
			if (isSyn) {
				taskResult = travelskyParallelComputerInterface.excuteSyn(bean,
						500000);
				if (null != taskResult) {
					log.info("time spend:"
							+ (taskResult.getEndTime().getTime() - taskResult
									.getStartTime().getTime()) + "ms");
					log.info("taskUnitSize:::"+taskResult.getTaskUnitResults().size());
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

//	public static void deleteJmsFile() {
//		InputStream in = Object.class.getResourceAsStream(
//				"parallel.properties");
//		Properties prop = new Properties();
//		try {
//			prop.load(in);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		File file = new File(prop.getProperty("hornetq.file"));
//		file.deleteOnExit();
//	}
}
