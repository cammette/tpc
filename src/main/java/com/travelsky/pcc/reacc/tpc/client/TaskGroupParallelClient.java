package com.travelsky.pcc.reacc.tpc.client;

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
public class TaskGroupParallelClient implements
		TaskGroupParallelClientInterface<Object> {

	private JMSService jmsGroupService;
	private Logger log = Logger.getLogger(getClass());
	private TaskContextManager taskContextManager;
	private TaskParallelClientInterface<Object> taskParallelClientInterface;
	private long waitForReplyGroupQueue = 3000;
	private long waitForReplyGroupTask = 3000;
	private JMSService notifyService;
	private int startGroupIden = 9;

	@Override
	public TaskResult excuteSync(List<TaskGroup<Object>> taskGroups,
			String batchNo, long excuteTimeout,
			Map<String, String> messageProperties)
			throws TaskExcutedReplyTimeoutException {
		sendTaskGroups(taskGroups, batchNo, messageProperties);
		return this.execute(batchNo, excuteTimeout);
	}
	
	private void sendTaskGroups(List<TaskGroup<Object>> taskGroups,String batchNo,Map<String, String> messageProperties){
		Map<String, String> map = genProperties(taskGroups, batchNo);
		messageProperties.putAll(map);
		int priority = startGroupIden;
		for (TaskGroup<Object> taskGroup : taskGroups) {
			jmsGroupService.send(taskGroup, batchNo+priority, messageProperties);
			priority--;
		}
	}

	@Override
	public void excuteAsyn(final List<TaskGroup<Object>> taskGroups,
			final String batchNo, final Map<String, String> messageProperties) {
		sendTaskGroups(taskGroups, batchNo, messageProperties);
		final TaskResult taskResultTemp = new TaskResult();
		int totalCount = Integer.parseInt(messageProperties.get(TASK_SIZE_KEY));
		taskResultTemp.setTotalCount(totalCount);
		taskResultTemp.setStartTime(new Date());
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

	private Map<String, String> genProperties(
			List<TaskGroup<Object>> taskGroups, String batchNo) {
		int groupCount = 0;
		int totalCount = 0;
		for (TaskGroup<Object> taskGroup : taskGroups) {
			totalCount = totalCount + taskGroup.size();
			groupCount++;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put(GROURP_SIZE_KEY, groupCount + "");
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
		int groupNo = 0;
		TaskResult temp = null;
		Map<String, String> messageProperties = new HashMap<String, String>();
		String taskBatchNo="";
		int priority = startGroupIden;
		while (taskResult == null || groupNo > 0) {
			log.info("groupNo:"+groupNo);
			try {
				msg = (ObjectMessage) jmsGroupService.waitforReply(batchNo+priority,
						waitForReplyGroupQueue);
				TaskGroup<Object> taskGroup = null;
				priority--;
				taskGroup = (TaskGroup<Object>) msg.getObject();
				if (null == taskResult) {
					taskResult = initTaskResult(msg);
					groupNo = Integer.parseInt(msg
							.getStringProperty(GROURP_SIZE_KEY));
					String proName = "";
					for (Enumeration e = msg.getPropertyNames(); e
							.hasMoreElements();) {
						proName = (String) e.nextElement();
						messageProperties.put(proName,
								msg.getStringProperty(proName));
					}
					messageProperties.remove(GROURP_SIZE_KEY);
					messageProperties.remove(TASK_SIZE_KEY);
				}
				log.info("batchNo:"+batchNo + groupNo);
				taskBatchNo = batchNo + groupNo;
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
			} catch (TaskExcutedReplyTimeoutException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JMSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			finally{
				groupNo--;
			}
		}
		taskResult.setEndTime(new Date());
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
