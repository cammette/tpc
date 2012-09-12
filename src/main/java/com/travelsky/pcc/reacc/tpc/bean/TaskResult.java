package com.travelsky.pcc.reacc.tpc.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.travelsky.pcc.reacc.tpc.property.StaticProperties;

/**
 * 该类代表任务执行结果
 * @author bingo
 *
 */
public class TaskResult implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 任务批次号，必须是唯一的
	 */
	private String batchNo;

	/**
	 * 任务包含了多少个子任务项
	 */
	private volatile Integer totalCount = 0;

	/**
	 * 任务执行完毕后成功任务数
	 */
	private volatile Integer successfulcount = 0;

	/**
	 * 任务执行完毕后失败任务数
	 */
	private volatile Integer failureCount = 0;

	/**
	 * 任务执行开始时间
	 */
	private Date startTime;

	/**
	 * 任务执行结束时间
	 */
	private Date endTime;

	/**
	 * 任务单元执行结果集
	 */
	private List<TaskUnitResult> taskUnitResults;
	
	/**
	 * 任务单元及任务汇聚时，需要调用类的名称
	 */
	private String beanName;

	/**
	 * 设置是否异步执行，默认为true，为false时表示同步执行
	 */
	private boolean asyn = true;
	
	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getSuccessfulcount() {
		return successfulcount;
	}

	public synchronized void increateSuccessfulCount() {
		successfulcount++;
	}

	public synchronized void increateFailureCount() {
		failureCount++;
	}

	public void setSuccessfulcount(Integer successfulcount) {
		this.successfulcount = successfulcount;
	}

	public Integer getFailureCount() {
		return failureCount;
	}

	public void setFailureCount(Integer failureCount) {
		this.failureCount = failureCount;
	}

	public List<TaskUnitResult> getTaskUnitResults() {
		if(null == taskUnitResults){
			taskUnitResults = new ArrayList<TaskUnitResult>();
		}
		return taskUnitResults;
	}

	public synchronized void addtTaskUnitResults(TaskUnitResult taskUnitResult) {
		if (taskUnitResults == null) {
			taskUnitResults = new ArrayList<TaskUnitResult>();
		}
		taskUnitResults.add(taskUnitResult);
	}

	public void setTaskUnitResults(List<TaskUnitResult> taskUnitResults) {
		this.taskUnitResults = taskUnitResults;
	}

	public boolean isDone() {
		if(totalCount==StaticProperties.TASK_SIZE_UNKNOW){
			return false;
		}
		if ((failureCount + successfulcount) >= totalCount) {
			return true;
		}
		return false;
	}

	public boolean isAsyn() {
		return asyn;
	}

	public void setAsyn(boolean asyn) {
		this.asyn = asyn;
	}

	@Override
	public String toString() {
		return "TaskResult [batchNo=" + batchNo
				+ ", totalCount=" + totalCount + ", successfulcount="
				+ successfulcount +",spendTime(ms)="+(endTime.getTime()-startTime.getTime())+ ", failureCount=" + failureCount
				+ ", startTime=" + startTime + ", endTime=" + endTime
				+ ", taskUnitResults=" + taskUnitResults + "]";
	}

}
