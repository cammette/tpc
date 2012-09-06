package com.travelsky.pcc.reacc.tpc.client;

import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;
import com.travelsky.pcc.reacc.tpc.jms.JMSService;
import com.travelsky.pcc.reacc.tpc.status.TaskContextManager;

/**
 * 并行执行任务的入口，可以同步或异步并发执行任务
 * 
 * @author bingo
 * 
 */
public class TaskGroupForkParallelClient implements
		TaskGroupParallelClientInterface<Object> {

	private JMSService jmsGroupService;
	private Logger log = Logger.getLogger(getClass());
	private TaskContextManager taskContextManager;
	private TaskParallelClientInterface<Object> taskParallelClientInterface;
	private long waitForReplyGroupQueue = 3000;
	private long waitForReplyGroupTask = 3000;
	private JMSService notifyService;

	@Override
	public TaskResult excuteSync(List<TaskGroup<Object>> taskGroups,
			String batchNo, long excuteTimeout,
			Map<String, String> messageProperties)
			throws TaskExcutedReplyTimeoutException {
		sendTaskGroups(taskGroups, batchNo, messageProperties);
		return this.execute(batchNo, excuteTimeout);
	}
	
/**
 * 发送分组任务到groupQueue	
 * @param taskGroups
 * @param batchNo
 * @param messageProperties
 */
	private void sendTaskGroups(List<TaskGroup<Object>> taskGroups,String batchNo,Map<String, String> messageProperties){
		Map<String, String> map = genProperties(taskGroups, batchNo);
		messageProperties.putAll(map);
		jmsGroupService.send((Serializable) taskGroups, batchNo, messageProperties);
	}

	@Override
	public void excuteAsyn(final List<TaskGroup<Object>> taskGroups,
			final String batchNo, final Map<String, String> messageProperties) {
		sendTaskGroups(taskGroups, batchNo, messageProperties);
		new Thread(new Runnable() {
			@Override
			public void run() {
				TaskResult taskResult = execute(batchNo, waitForReplyGroupTask);
				taskResult.setBeanName(messageProperties
						.get(TravelskyParallelComputerTemplate.ParallelComputerSpringBean));
				notifyService.send(taskResult, null, null);
			}
		}).start();
	}
/**
 * 生成properties, 分组大小，任务总数
 * @param taskGroups
 * @param batchNo
 * @return
 */
	private Map<String, String> genProperties(
			List<TaskGroup<Object>> taskGroups, String batchNo) {
		int totalCount = 0;
		for (TaskGroup<Object> taskGroup : taskGroups) {
			totalCount = totalCount + taskGroup.size();
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put(TASK_SIZE_KEY, totalCount + "");
		return map;
	}
/**
 * 初始化taskReslut
 * @param msg
 * @return
 * @throws JMSException
 */
	private TaskResult initTaskResult(ObjectMessage msg) throws JMSException {
		TaskResult taskResult = new TaskResult();
		taskResult.setBatchNo(msg.getStringProperty(JMSService.BATCH_NO));
		taskResult.setStartTime(new Date());
		taskResult.setTotalCount(Integer.parseInt(msg
				.getStringProperty(TASK_SIZE_KEY)));
		return taskResult;

	}

	private TaskResult execute(String batchNo, long waitTimeout) {
		ObjectMessage msg = null;
		TaskResult taskResult = null;
		TaskResult temp = null;
		Map<String, String> messageProperties = new HashMap<String, String>();
		String taskBatchNo="";
		try {
			msg = (ObjectMessage) jmsGroupService.waitforReply(batchNo,
					waitForReplyGroupQueue);
			List<TaskGroup<Object>> taskGroups = null;
			taskGroups = (List<TaskGroup<Object>>) msg.getObject();
			taskResult = initTaskResult(msg);
			String proName = "";
			for (Enumeration e = msg.getPropertyNames(); e
					.hasMoreElements();) {
				proName = (String) e.nextElement();
				messageProperties.put(proName,
						msg.getStringProperty(proName));
			}
			messageProperties.remove(TASK_SIZE_KEY);
			messageProperties.remove(JMSService.JMS_PROPERTIRES_NAME);
			TaskGroup<Object> taskGroup;
			for(int i=0;i<taskGroups.size();i++){
				taskGroup = taskGroups.get(i);
				taskBatchNo = batchNo + i;
				messageProperties.put(JMSService.BATCH_NO, taskBatchNo);
				temp = (TaskResult) taskParallelClientInterface.excuteSync(
						taskGroup, taskBatchNo, waitTimeout,
						messageProperties);
				taskResult.getTaskUnitResults().addAll(
						temp.getTaskUnitResults());
				taskResult.setFailureCount(taskResult.getFailureCount()
						+ temp.getFailureCount());
				taskResult.setSuccessfulcount(taskResult.getSuccessfulcount()
						+ temp.getSuccessfulcount());
			}
			taskResult.setEndTime(new Date());
		} catch (TaskExcutedReplyTimeoutException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (JMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return taskResult;
	}

	public JMSService getJmsGroupService() {
		return jmsGroupService;
	}

	public void setJmsGroupService(JMSService jmsGroupService) {
		this.jmsGroupService = jmsGroupService;
	}

	public TaskContextManager getTaskContextManager() {
		return taskContextManager;
	}

	public void setTaskContextManager(TaskContextManager taskContextManager) {
		this.taskContextManager = taskContextManager;
	}

	public TaskParallelClientInterface<Object> getTaskParallelClientInterface() {
		return taskParallelClientInterface;
	}

	public void setTaskParallelClientInterface(
			TaskParallelClientInterface<Object> taskParallelClientInterface) {
		this.taskParallelClientInterface = taskParallelClientInterface;
	}

	public void setWaitForReplyGroupQueue(int waitForReplyGroupQueue) {
		this.waitForReplyGroupQueue = waitForReplyGroupQueue;
	}

	public long getWaitForReplyGroupQueue() {
		return waitForReplyGroupQueue;
	}

	public void setWaitForReplyGroupQueue(long waitForReplyGroupQueue) {
		this.waitForReplyGroupQueue = waitForReplyGroupQueue;
	}

	public long getWaitForReplyGroupTask() {
		return waitForReplyGroupTask;
	}

	public void setWaitForReplyGroupTask(long waitForReplyGroupTask) {
		this.waitForReplyGroupTask = waitForReplyGroupTask;
	}

	public void setWaitForReplyGroupTask(int waitForReplyGroupTask) {
		this.waitForReplyGroupTask = waitForReplyGroupTask;
	}

	public JMSService getNotifyService() {
		return notifyService;
	}

	public void setNotifyService(JMSService notifyService) {
		this.notifyService = notifyService;
	}

}
