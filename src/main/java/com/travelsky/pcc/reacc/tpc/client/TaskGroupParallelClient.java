package com.travelsky.pcc.reacc.tpc.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

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
	private TaskContextManager taskContextManager;
	private TaskParallelClientInterface<Object> taskParallelClientInterface;
	private int waitForReplyGroupQueue = 10000;
	private int waitForReplyGroupTask = 10000;
	private JMSService notifyService;

	@Override
	public TaskResult excuteSync(List<TaskGroup<Object>> taskGroups,
			String batchNo, long excuteTimeout,
			Map<String, String> messageProperties)
			throws TaskExcutedReplyTimeoutException {
		TaskResult temp = null;
		int groupNo = 0;
		int totalCount = 0;
		for (TaskGroup<Object> taskGroup : taskGroups) {
			jmsGroupService.send(taskGroup, batchNo, messageProperties);
			totalCount = totalCount + taskGroup.size();
			groupNo++;
		}
		TaskResult taskResult = new TaskResult();
		taskResult.setTotalCount(totalCount);
		taskResult.setStartTime(new Date());
		return this.excute(taskGroups, batchNo, excuteTimeout, messageProperties,taskResult,groupNo);
	}

	@Override
	public void excuteAsyn(final List<TaskGroup<Object>> taskGroups,final String batchNo,
			final Map<String, String> messageProperties) {
		int groupCount = 0;
		int totalCount = 0;
		for (TaskGroup<Object> taskGroup : taskGroups) {
			jmsGroupService.send(taskGroup, batchNo, messageProperties);
			totalCount = totalCount + taskGroup.size();
			groupCount++;
		}
		final TaskResult taskResultTemp = new TaskResult();
		final int groupNo = groupCount;
		taskResultTemp.setTotalCount(totalCount);
		taskResultTemp.setStartTime(new Date());
		new Thread(new Runnable(){
            @Override
            public void run() {
            	TaskResult taskResult = excute(taskGroups, batchNo, waitForReplyGroupTask, messageProperties,taskResultTemp,groupNo);
                taskResult.setBeanName(messageProperties.get(TravelskyParallelComputerTemplate.ParallelComputerSpringBean)
                		);
        		notifyService.send(taskResult, null,null);
            }
        }).start();
	}
	
	
	
	private  TaskResult excute(List<TaskGroup<Object>> taskGroups,
			String batchNo, long excuteTimeout,
			Map<String, String> messageProperties,TaskResult taskResult,int groupNo){
		TaskResult temp = null;
		while (groupNo > 0) {
			ObjectMessage msg = null;
			try {
				msg = (ObjectMessage) jmsGroupService.waitforReply(batchNo,
						waitForReplyGroupQueue);
			} catch (TaskExcutedReplyTimeoutException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			TaskGroup<Object> taskGroup = null;
			try {
				taskGroup = (TaskGroup<Object>) msg.getObject();
			} catch (JMSException e) {
				e.printStackTrace();
			}
			try {
				temp = (TaskResult) taskParallelClientInterface.excuteSync(
						taskGroup, batchNo + "-" + groupNo, excuteTimeout,
						messageProperties);
				taskResult.getTaskUnitResults().addAll(temp.getTaskUnitResults());
				taskResult.setFailureCount(taskResult.getFailureCount()
						+ temp.getFailureCount());
				taskResult.setSuccessfulcount(taskResult.getSuccessfulcount()
						+ temp.getSuccessfulcount());
			} catch (TaskExcutedReplyTimeoutException e) {
				e.printStackTrace();
			}
			groupNo--;
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

	public int getWaitForReplyGroup() {
		return waitForReplyGroupQueue;
	}

	public void setWaitForReplyGroup(int waitForReplyGroup) {
		this.waitForReplyGroupQueue = waitForReplyGroup;
	}

	public int getWaitForReplyTask() {
		return waitForReplyGroupTask;
	}

	public void setWaitForReplyTask(int waitForReplyTask) {
		this.waitForReplyGroupTask = waitForReplyTask;
	}

	public int getWaitForReplyGroupQueue() {
		return waitForReplyGroupQueue;
	}

	public void setWaitForReplyGroupQueue(int waitForReplyGroupQueue) {
		this.waitForReplyGroupQueue = waitForReplyGroupQueue;
	}

	public int getWaitForReplyGroupTask() {
		return waitForReplyGroupTask;
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
