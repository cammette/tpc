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
import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputerTemplate;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;
import com.travelsky.pcc.reacc.tpc.jms.JMSService;
import com.travelsky.pcc.reacc.tpc.property.StaticProperties;

public class TaskGroupListener implements MessageListener  {
	
	private long waitForReplyGroupTask = 3000;
	private Logger log = Logger.getLogger(getClass());
	private TaskParallelClientInterface<Object> taskParallelClientInterface;
	private JMSService notifyService;
	private JMSService replyGroupService;
	@Override
	public void onMessage(Message message) {
		ObjectMessage msg = (ObjectMessage)message;
		Map<String, String> messageProperties = new HashMap<String, String>();
		List<TaskGroup<Object>> taskGroups = null;
		try {
			String proName = "";
			for (Enumeration e = msg.getPropertyNames(); e
					.hasMoreElements();) {
				proName = (String) e.nextElement();
				messageProperties.put(proName,
						msg.getStringProperty(proName));
			}
			String batchNo = msg.getStringProperty(StaticProperties.BATCH_NO);
			String timeOutStr = msg.getStringProperty(StaticProperties.MSG_TIME_OUT_KEY);
			long timeOut = waitForReplyGroupTask;
			boolean syn = false;
			if(null != timeOutStr){
				timeOut = Long.parseLong(timeOutStr);
				syn = true;
			}
			taskGroups = (List<TaskGroup<Object>>) msg.getObject();
			TaskResult taskResult = initTaskResult(batchNo);
			messageProperties.remove(StaticProperties.TASK_SIZE_KEY);
			messageProperties.remove(StaticProperties.JMS_PROPERTIRES_NAME);
			String taskBatchNo = "";
			int groupNo = 1;
			TaskResult temp=null;
			taskResult.setBeanName(messageProperties.get(TravelskyParallelComputerTemplate.ParallelComputerSpringBean));
			for(TaskGroup<Object> taskGroup : taskGroups){
				taskBatchNo = batchNo + groupNo;
				messageProperties.put(StaticProperties.BATCH_NO, taskBatchNo);
				temp = (TaskResult) taskParallelClientInterface.excuteSync(
						taskGroup, taskBatchNo, timeOut,
						messageProperties);
				taskResult.getTaskUnitResults().addAll(
						temp.getTaskUnitResults());
				taskResult.setFailureCount(taskResult.getFailureCount()
						+ temp.getFailureCount());
				taskResult.setSuccessfulcount(taskResult.getSuccessfulcount()
						+ temp.getSuccessfulcount());
			}
			taskResult.setEndTime(new Date());
			if(!syn){
				notifyService.send(taskResult, null, null);
			}
			else{
				replyGroupService.send(taskResult, taskResult.getBatchNo(),null);
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TaskExcutedReplyTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private TaskResult initTaskResult(String batchNo) throws JMSException {
		TaskResult taskResult = new TaskResult();
		taskResult.setBatchNo(batchNo);
		taskResult.setStartTime(new Date());
		return taskResult;
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

	public JMSService getNotifyService() {
		return notifyService;
	}

	public void setNotifyService(JMSService notifyService) {
		this.notifyService = notifyService;
	}

	public JMSService getReplyGroupService() {
		return replyGroupService;
	}

	public void setReplyGroupService(JMSService replyGroupService) {
		this.replyGroupService = replyGroupService;
	}
	

}
