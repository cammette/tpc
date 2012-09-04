package com.travelsky.pcc.reacc.tpc.client;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyException;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;
import com.travelsky.pcc.reacc.tpc.jms.JMSService;
import com.travelsky.pcc.reacc.tpc.status.TaskContextManager;

/**
 * 并行执行任务的入口，可以同步或异步并发执行任务
 * @author bingo
 *
 */
public class TaskParallelClient implements TaskParallelClientInterface<Object> {

	private JMSService senderAndReplyService;
	
	private TaskContextManager taskContextManager;
	
	public JMSService getSenderAndReplyService() {
		return senderAndReplyService;
	}

	public void setSenderAndReplyService(JMSService senderAndReplyService) {
		this.senderAndReplyService = senderAndReplyService;
	}

	public TaskContextManager getTaskContextManager() {
		return taskContextManager;
	}

	public void setTaskContextManager(TaskContextManager taskContextManager) {
		this.taskContextManager = taskContextManager;
	}

	@Override
	public void excuteAsyn(List<Object> tasks, String batchNo,Map<String, String> messageProperties) {
		String springBean = messageProperties.get( TravelskyParallelComputerTemplate.ParallelComputerSpringBean);
		taskContextManager.addTaskResult(batchNo, tasks.size(),springBean);
		for (Object task : tasks) {
			senderAndReplyService.send((Serializable) task, batchNo,messageProperties);
		}
	}

	@Override
	public TaskResult excuteSync(List<Object> tasks, String batchNo,
			long excuteTimeout,Map<String, String> messageProperties) throws TaskExcutedReplyTimeoutException {
		String springBean = messageProperties.get(TravelskyParallelComputerTemplate.ParallelComputerSpringBean);
		taskContextManager.addTaskResult(batchNo, tasks.size(),false,springBean);
		for (Object task : tasks) {
			senderAndReplyService.send((Serializable) task, batchNo,messageProperties);
		}
		ObjectMessage msg = (ObjectMessage)senderAndReplyService.waitforReply(batchNo, excuteTimeout);
		try {
			return (TaskResult)msg.getObject();
		} catch (JMSException e) {
			throw new TaskExcutedReplyException(e);
		}
	}

}
