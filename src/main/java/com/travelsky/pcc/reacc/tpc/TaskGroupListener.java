package com.travelsky.pcc.reacc.tpc;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.client.TaskGroup;
import com.travelsky.pcc.reacc.tpc.client.TaskParallelClientInterface;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;
import com.travelsky.pcc.reacc.tpc.jms.JMSService;
import com.travelsky.pcc.reacc.tpc.property.StaticProperties;

public class TaskGroupListener implements MessageListener {

	private long waitForReplyGroupTask = 3000;
	private Logger log = Logger.getLogger(getClass());
	private TaskParallelClientInterface<Object> taskParallelClientInterface;
	private JMSService notifyClientService;
	private JMSService replyGroupService;

	@Override
	@SuppressWarnings("unchecked")
	public void onMessage(Message message) {
		ObjectMessage msg = (ObjectMessage) message;
		Map<String, String> messageProperties = new HashMap<String, String>();
		List<TaskGroup<Object>> taskGroups = null;
		try {
			String proName = "";
			for (Enumeration<String> e = msg.getPropertyNames(); e.hasMoreElements();) {
				proName =  e.nextElement();
				messageProperties.put(proName, msg.getStringProperty(proName));
			}
			String batchNo = msg.getStringProperty(StaticProperties.BATCH_NO);
			String synOrAsyn = msg.getStringProperty(StaticProperties.MS_SYN_OR_ASYN_KEY);
			if (null == synOrAsyn) {
				synOrAsyn = StaticProperties.MS_VALUE_ASYN;
			}
			taskGroups = (List<TaskGroup<Object>>) msg.getObject();
			
			TaskResult taskResult = new TaskResult();
			taskResult.setBatchNo(batchNo);
			taskResult.setStartTime(new Date());
			
			messageProperties.remove(StaticProperties.MS_SYN_OR_ASYN_KEY);
			messageProperties.remove(StaticProperties.JMS_PROPERTIRES_NAME);
			String taskBatchNo = "";
			int groupNo = 1;
			TaskResult temp = null;
			taskResult.setBeanName(messageProperties
					.get(StaticProperties.ParallelComputerSpringBean));
			for (TaskGroup<Object> taskGroup : taskGroups) {
				taskBatchNo = batchNo + groupNo;
				messageProperties.put(StaticProperties.BATCH_NO, taskBatchNo);
				temp = (TaskResult) taskParallelClientInterface.executeSync(
						taskGroup, taskBatchNo, waitForReplyGroupTask,
						messageProperties);
				taskResult.getTaskUnitResults().addAll(
						temp.getTaskUnitResults());
				taskResult.setFailureCount(taskResult.getFailureCount()
						+ temp.getFailureCount());
				taskResult.setSuccessfulcount(taskResult.getSuccessfulcount()
						+ temp.getSuccessfulcount());
				taskResult.setTotalCount(taskResult.getTotalCount()+ temp.getTotalCount());
				groupNo++;
			}
			taskResult.setEndTime(new Date());
			if (synOrAsyn.equals(StaticProperties.MS_VALUE_SYN)) {
				log.info("syn----end");
				replyGroupService.send(taskResult, taskResult.getBatchNo(),
						null);
			}
			else {
				log.info("asyn----end");
				notifyClientService.send(taskResult, null, null);
			}

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TaskExcutedReplyTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public long getWaitForReplyGroupTask() {
		return waitForReplyGroupTask;
	}

	public void setWaitForReplyGroupTask(long waitForReplyGroupTask) {
		this.waitForReplyGroupTask = waitForReplyGroupTask;
	}

	public TaskParallelClientInterface<Object> getTaskParallelClientInterface() {
		return taskParallelClientInterface;
	}

	public void setTaskParallelClientInterface(
			TaskParallelClientInterface<Object> taskParallelClientInterface) {
		this.taskParallelClientInterface = taskParallelClientInterface;
	}

	public JMSService getNotifyClientService() {
		return notifyClientService;
	}

	public void setNotifyClientService(JMSService notifyClientService) {
		this.notifyClientService = notifyClientService;
	}

	public JMSService getReplyGroupService() {
		return replyGroupService;
	}

	public void setReplyGroupService(JMSService replyGroupService) {
		this.replyGroupService = replyGroupService;
	}

}
