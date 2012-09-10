package com.travelsky.pcc.reacc.tpc.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.bean.TaskUnitResult;

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
	public void addTaskUnitResult(TaskUnitResult taskUnitResult,String beanName) {
		if(null == taskUnitResult){
			return;
		}
		String batchNo =  taskUnitResult.getBatchNo();
		TaskResult allBatchBean = taskResultMap.get(batchNo);
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
		Set<String> keys = taskResultMap.keySet();
		String[] keyArray = keys.toArray(new String[] {});// 目的为了解决遍历该集合时，allBatchBeanMap有添加key的情况。
		List<TaskResult> allBatchBeans = new ArrayList<TaskResult>();
		for (String key : keyArray) {
			TaskResult allBatchBean = taskResultMap.get(key);
			if (allBatchBean.isDone()) {
				allBatchBeans.add(allBatchBean);
			}
		}
		return allBatchBeans;
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
	public void addTaskResult(String batchNo, Integer count,String beanName) {
		addTaskResult(batchNo,  count,  true,beanName);
	}

	@Override
	public void addTaskResult(String batchNo, Integer count, boolean asyn,String beanName) {
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

}
