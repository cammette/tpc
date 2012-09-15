package com.travelsky.pcc.reacc.tpc.status;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.jms.JMSService;

/**
 * 负责监听完成任务，获取到完成的任务列表后，发送任务完成通知
 * @author bingo
 *
 */
public class TaskMonitor {
	
	protected Logger log = Logger.getLogger(getClass());
	
	private boolean isRunning = false;
	
	private TaskContextManager taskContextManager;
	
	private JMSService replyClientService;

	private long interval = 10;
	
	private long unReplyInterval = 1000;

	public void init(){
		isRunning = true;
		TaskMonitorThead taskMonitorThead = new TaskMonitorThead();
		new Thread(taskMonitorThead, "monitor_task-thread").start();
	}
	
	public void destroy(){
		isRunning = false;
	}
	
	class TaskMonitorThead implements Runnable{

		@Override
		public void run() {
			while(isRunning){
				try {
					List<TaskResult> taskResults = taskContextManager.getDoneTask();
					if(taskResults.size()>0){
						taskContextManager.removeTaskResult(taskResults);
						for (TaskResult taskResult : taskResults) {
							taskResult.setEndTime(new Date());
							replyClientService.send(taskResult, taskResult.getBatchNo(),null);
							log.info("replyClient  call");
						}
					}
					Thread.sleep(interval);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public JMSService getReplyClientService() {
		return replyClientService;
	}

	public void setReplyClientService(JMSService replyClientService) {
		this.replyClientService = replyClientService;
	}
	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	public TaskContextManager getTaskContextManager() {
		return taskContextManager;
	}

	public void setTaskContextManager(TaskContextManager taskContextManager) {
		this.taskContextManager = taskContextManager;
	}

	public long getUnReplyInterval() {
		return unReplyInterval;
	}

	public void setUnReplyInterval(long unReplyInterval) {
		this.unReplyInterval = unReplyInterval;
	}

}
