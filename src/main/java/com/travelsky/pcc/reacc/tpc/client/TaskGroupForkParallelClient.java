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
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyException;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;
import com.travelsky.pcc.reacc.tpc.jms.JMSService;
import com.travelsky.pcc.reacc.tpc.property.StaticProperties;
import com.travelsky.pcc.reacc.tpc.status.TaskContextManager;

/**
 * 并行执行任务的入口，可以同步或异步并发执行任务
 * 
 * @author bingo
 * 
 */
public class TaskGroupForkParallelClient implements
		TaskGroupParallelClientInterface<Object> {

	private JMSService sendAndReplyGroupService;
	private Logger log = Logger.getLogger(getClass());
	private long waitForReplyGroupQueue = 3000;
	private long waitForReplyGroupTask = 3000;

	@Override
	public TaskResult excuteSync(List<TaskGroup<Object>> taskGroups,
			String batchNo, long excuteTimeout,
			Map<String, String> messageProperties)
			throws TaskExcutedReplyTimeoutException {
		sendTaskGroups(taskGroups, batchNo, messageProperties);
		ObjectMessage msg = (ObjectMessage) sendAndReplyGroupService.waitforReply(batchNo, excuteTimeout);
		try {
			return (TaskResult) msg.getObject();
		} catch (JMSException e) {
			throw new TaskExcutedReplyException(e);
		}
	}

	/**
	 * 发送分组任务到groupQueue
	 * 
	 * @param taskGroups
	 * @param batchNo
	 * @param messageProperties
	 */
	private void sendTaskGroups(List<TaskGroup<Object>> taskGroups,
			String batchNo, Map<String, String> messageProperties) {
		Map<String, String> map = genProperties(taskGroups, batchNo);
		messageProperties.putAll(map);
		sendAndReplyGroupService.send((Serializable) taskGroups, batchNo,
				messageProperties);
	}

	@Override
	public void excuteAsyn(final List<TaskGroup<Object>> taskGroups,
			final String batchNo, final Map<String, String> messageProperties) {
		Map<String, String> map = genProperties(taskGroups, batchNo);
		messageProperties.putAll(map);
		sendAndReplyGroupService.send((Serializable) taskGroups, batchNo,
				messageProperties);
	}

	/**
	 * 生成properties, 分组大小，任务总数
	 * 
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
		map.put(StaticProperties.TASK_SIZE_KEY, totalCount + "");
		return map;
	}

	/**
	 * 初始化taskReslut
	 * 
	 * @param msg
	 * @return
	 * @throws JMSException
	 */
	private TaskResult initTaskResult(ObjectMessage msg) throws JMSException {
		TaskResult taskResult = new TaskResult();
		taskResult.setBatchNo(msg.getStringProperty(StaticProperties.BATCH_NO));
		taskResult.setStartTime(new Date());
		taskResult.setTotalCount(Integer.parseInt(msg
				.getStringProperty(StaticProperties.TASK_SIZE_KEY)));
		return taskResult;

	}

	public JMSService getSendAndReplyGroupService() {
		return sendAndReplyGroupService;
	}

	public void setSendAndReplyGroupService(JMSService sendAndReplyGroupService) {
		this.sendAndReplyGroupService = sendAndReplyGroupService;
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


}
