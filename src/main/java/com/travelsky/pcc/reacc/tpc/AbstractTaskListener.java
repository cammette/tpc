package com.travelsky.pcc.reacc.tpc;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;

import com.travelsky.pcc.reacc.tpc.bean.TaskUnitResult;
import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputerTemplate;
import com.travelsky.pcc.reacc.tpc.jms.JMSService;
import com.travelsky.pcc.reacc.tpc.status.TaskContextManager;
/**
 * 监听队列，获取任务单元并执行任务处理逻辑，同时记录任务执行结果
 * @author bingo
 *
 */
public abstract class AbstractTaskListener implements MessageListener {

	private Logger log = Logger.getLogger(getClass());

	private TaskContextManager taskContextManager;
	
	public TaskContextManager getTaskContextManager() {
		return taskContextManager;
	}

	public void setTaskContextManager(TaskContextManager taskContextManager) {
		this.taskContextManager = taskContextManager;
	}

	@Override
	public void onMessage(Message msg) {
		try {
			String batchNo = msg.getStringProperty(JMSService.BATCH_NO);
			if (batchNo == null || batchNo.length() <= 0) {
				throw new IllegalArgumentException(
						"the message must include the batch_no property");
			}
			TaskUnitResult taskUnitResult = this.doMessage(msg, batchNo);
			String springBeanName = msg.getStringProperty(TravelskyParallelComputerTemplate.ParallelComputerSpringBean);
			taskContextManager.addTaskUnitResult(taskUnitResult,springBeanName);
		} catch (Throwable e) {
			log.error("task do message error : ", e);
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 该抽象方法在接收到任务单元时被调用，可以实现该方法，处理任务执行逻辑
	 * @param msg
	 * @param batchNo
	 * @return
	 * @throws JMSException
	 */
	protected abstract TaskUnitResult doMessage(Message msg, String batchNo)
			throws JMSException;

}
