
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
 * @author bingo
 *
 */
public class TaskGroupParallelClient implements TaskGroupParallelClientInterface<Object> {
	
	private JMSService jmsGroupService;
	private TaskContextManager taskContextManager;
	private TaskParallelClientInterface taskParallelClientInterface;
	private int singleWaitForReply = 10000;
	
	@Override
	public TaskResult excuteSync(List<TaskGroup<Object>> taskGroups, String batchNo,
			long excuteTimeout, Map<String, String> messageProperties)
			throws TaskExcutedReplyTimeoutException {
		// TODO Auto-generated method stub
		String springBean = messageProperties.get(TravelskyParallelComputerTemplate.ParallelComputerSpringBean);
		TaskResult temp = null;
		int groupNo =0;
		int size = 0;
		for(TaskGroup<Object> taskGroup:taskGroups){
			jmsGroupService.send(taskGroup, batchNo, messageProperties);
			size = size + taskGroup.size();
			groupNo++;
		}
		taskContextManager.addTaskResult(batchNo,size,false,springBean);
		TaskResult taskResult = new TaskResult();
		int count = 0;		
		while(count < groupNo){
			ObjectMessage msg = (ObjectMessage)jmsGroupService.waitforReply(batchNo, singleWaitForReply);
			
			TaskGroup<Object> taskGroup=null;
			try {
				taskGroup = (TaskGroup<Object>) msg.getObject();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			temp = (TaskResult) taskParallelClientInterface.excuteSync(taskGroup, batchNo+"-"+count, 
					excuteTimeout, messageProperties);
			taskResult.getTaskUnitResults().addAll(temp.getTaskUnitResults());
			taskResult.setFailureCount(taskResult.getFailureCount()+temp.getFailureCount());
			taskResult.setSuccessfulcount(taskResult.getSuccessfulcount()+temp.getSuccessfulcount());
			count++;
		}
		taskResult.setEndTime(new Date());
		return taskResult;
	}

	@Override
	public void excuteAsyn(List<TaskGroup<Object>> taskGroups, String batchNo,
			Map<String, String> messageProperties) {
		// TODO Auto-generated method stub
		
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

	public TaskParallelClientInterface getTaskParallelClientInterface() {
		return taskParallelClientInterface;
	}

	public void setTaskParallelClientInterface(
			TaskParallelClientInterface taskParallelClientInterface) {
		this.taskParallelClientInterface = taskParallelClientInterface;
	}

	public int getSingleWaitForReply() {
		return singleWaitForReply;
	}

	public void setSingleWaitForReply(int singleWaitForReply) {
		this.singleWaitForReply = singleWaitForReply;
	}

}
