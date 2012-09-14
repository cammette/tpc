package com.travelsky.pcc.reacc.tpc.status;

import java.util.List;
import java.util.Map;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.bean.TaskUnitResult;

/**
 * 
 * 任务管理器
 * 
 */
public interface TaskContextManager {
	/**
	 * 获取结果为完成时标识
	 */
	 int GET_TASK_TYPE_DONE = 1;
    /**
     * 获取结果为任务大小未知时标识
     */
	 int GET_TASK_TYPE_UNKNOW = 2;
	
	 void resume();
	/**
	 * 添加一个批次任务，异步执行
	 * @param batchNo   批次id
	 * @param count    批次中包含unit大小
	 */
	public void addTaskResult(String batchNo, Integer count,String beanName);
	
	/**
	 * 添加一个批次任务
	 * @param batchNo 批次id
	 * @param count 批次中包含unit大小
	 * @param asyn 设置是否异步，true为异步，false为同步
	 */
	public void addTaskResult(String batchNo, Integer count,boolean asyn,String beanName);

	/**
	 * 移除批次任务
	 * @param allBatchBeans
	 */
	public void removeTaskResult(List<TaskResult> taskResult);

	/**
	 * 添加一个任务进行管理
	 * @param batchNo 批次id
	 * @param id   
	 * @param info
	 * @param isSuccessful
	 */
	public void addTaskUnitResult(TaskUnitResult taskUnitResult,String beanName);

	/**
	 * 获取完成的任务
	 * @return
	 */
	public List<TaskResult> getDoneTask();
	/**
	 * 整个map
	 * @return
	 */
	public List<TaskResult> getUnknowTaskSize();
	

}
