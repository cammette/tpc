package com.travelsky.pcc.reacc.tpc.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.bean.TaskUnitResult;
import com.travelsky.pcc.reacc.tpc.property.StaticProperties;

/**
 * 任务执行状态管理的内存实现
 * 
 * @author bingo
 * 
 */
public class DefaultTaskContextManager implements TaskContextManager {

	private ConcurrentHashMap<String, TaskResult> taskResultMap = new ConcurrentHashMap<String, TaskResult>();

	public void init() {
	}

	@Override
	public void addTaskUnitResult(TaskUnitResult taskUnitResult, String beanName) {
		if (null == taskUnitResult) {
			return;
		}
		String batchNo = taskUnitResult.getBatchNo();
		TaskResult allBatchBean = taskResultMap.get(batchNo);
		if (null == allBatchBean) {
			addTaskResult(batchNo, StaticProperties.TASK_SIZE_UNKNOW, beanName);
			allBatchBean = taskResultMap.get(batchNo);
		}
		taskUnitResult.setBatchNo(batchNo);
		allBatchBean.addtTaskUnitResults(taskUnitResult);
		if (taskUnitResult.getIsSuccess()) {
			allBatchBean.increateSuccessfulCount();
		} else {
			allBatchBean.increateFailureCount();
		}
	}

	@Override
	public List<TaskResult> getDoneTask() {
		return getTaskResult(GET_TASK_TYPE_DONE);
	}

	@Override
	public void resume() {

	}

	@Override
	public void removeTaskResult(List<TaskResult> allBatchBeans) {
		for (TaskResult allBatchBean : allBatchBeans) {
			taskResultMap.remove(allBatchBean.getBatchNo());
		}
	}

	@Override
	public void addTaskResult(String batchNo, Integer count, String beanName) {
		addTaskResult(batchNo, count, true, beanName);
	}

	@Override
	public void addTaskResult(String batchNo, Integer count, boolean asyn,
			String beanName) {
		if (taskResultMap.containsKey(batchNo)) {
			taskResultMap.remove(batchNo);
		}
		TaskResult allBatchBean = new TaskResult();
		allBatchBean.setBatchNo(batchNo);
		allBatchBean.setTotalCount(count);
		allBatchBean.setStartTime(new Date());
		allBatchBean.setAsyn(asyn);
		allBatchBean.setBeanName(beanName);
		taskResultMap.put(batchNo, allBatchBean);

	}

	@Override
	public List<TaskResult> getUnknowTaskSize() {
		return getTaskResult(GET_TASK_TYPE_UNKNOW);
	}

	private List<TaskResult> getTaskResult(int type) {
		Set<String> keys = taskResultMap.keySet();
		String[] keyArray = keys.toArray(new String[] {});// 目的为了解决遍历该集合时，allBatchBeanMap有添加key的情况。
		List<TaskResult> allBatchBeans = new ArrayList<TaskResult>();
		for (String key : keyArray) {
			TaskResult taskResult = taskResultMap.get(key);
			switch (type) {
			case (GET_TASK_TYPE_DONE):
				if (taskResult.isDone()) {
					allBatchBeans.add(taskResult);
				}
				break;
			case (GET_TASK_TYPE_UNKNOW):
				if (taskResult.getTotalCount().equals(
						StaticProperties.TASK_SIZE_UNKNOW)) {
					allBatchBeans.add(taskResult);
				}
			    break;
			case (GET_TASK_ALL):
				allBatchBeans.add(taskResult);
				break;
			case (GET_SEND_FINISH):
				if(taskResult.isSendFinish()){
					allBatchBeans.add(taskResult);
					System.out.println(" send fish:"+taskResult.isSendFinish());
				}
			break;
			case (GET_TAG_ZERO):
				if(taskResult.getTag()==0){
					allBatchBeans.add(taskResult);
					System.out.println(" tag is zero:"+taskResult.isSendFinish());
				}
			break;
			default:
				break;
			}

		}
		return allBatchBeans;
	}

	@Override
	public List<TaskResult> getSednFinishTask() {
		return  getTaskResult(GET_SEND_FINISH) ;
	}

	@Override
	public void changeFinishByBatchNo(String batchNo) {
		TaskResult sendFinishTask = taskResultMap.get(batchNo);
		if(null!=sendFinishTask){
			sendFinishTask.setSendFinish(true);
		}
	}

	@Override
	public void incTag(String batchNo) {
		TaskResult taskResult = taskResultMap.get(batchNo);
		if(null!=taskResult){
			taskResult.incTag();
		}
	}

	@Override
	public void decTag(String batchNo) {
		TaskResult taskResult = taskResultMap.get(batchNo);
		if(null!=taskResult){
			taskResult.decTag();
		}
	}

	@Override
	public List<TaskResult> getTagZeroTask() {
		return  getTaskResult(GET_TAG_ZERO) ;
	}

}
