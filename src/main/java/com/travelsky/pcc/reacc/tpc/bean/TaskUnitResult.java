package com.travelsky.pcc.reacc.tpc.bean;

import java.io.Serializable;
import java.util.Map;

/**
 * 代表一个任务单元执行的结果
 * @author bingo
 *
 */
public class TaskUnitResult implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 任务单元的唯一标识
	 */
	private String taskUintId;
	
	/**
	 * 存放用户自定义的结果
	 */
	private Serializable taskResult;
	
	/**
	 * 一些信息的记录，比如错误信息
	 */
	private String msg;
    /**
     * 是否成功
     */
	private Boolean isSuccess;
	
   /**
    * 任务批次号
    */
	private String batchNo;
	
	/**
	 * 绑定到task的轻量级属性
	 */
	private Map<String,String> taskBindProperties;

	

	public TaskUnitResult() {
		super();
	}

	public TaskUnitResult(String taskUintId, String msg, Boolean isSuccess) {
		super();
		this.taskUintId = taskUintId;
		this.msg = msg;
		this.isSuccess = isSuccess;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	

	public Serializable getTaskResult() {
		return taskResult;
	}

	public void setTaskResult(Serializable taskResult) {
		this.taskResult = taskResult;
	}

	public String getTaskUintId() {
		return taskUintId;
	}

	public void setTaskUintId(String taskUintId) {
		this.taskUintId = taskUintId;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public Map<String, String> getTaskBindProperties() {
		return taskBindProperties;
	}

	public void setTaskBindProperties(Map<String, String> taskBindProperties) {
		this.taskBindProperties = taskBindProperties;
	}
	
}
