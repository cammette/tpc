package com.travelsky.pcc.reacc.tpc.client.test;

import java.util.ArrayList;
import java.util.List;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.bean.TaskUnitResult;
import com.travelsky.pcc.reacc.tpc.client.TaskGroup;
import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputingTemplate;

public class TravelskyParallelComputer extends
		TravelskyParallelComputingTemplate<Integer, String, String> {
	
	public static int size = 0;

	@Override
	protected List<TaskGroup<String>> split(Integer p) {
		List<TaskGroup<String>> taskGroups = new ArrayList<TaskGroup<String>>();
		TaskGroup<String> taskGroup = new TaskGroup<String>();
		for (int i = 0; i < p; i++) {
			taskGroup.add(i+"");
		}
		taskGroups.add(taskGroup);
		return taskGroups;
	}

	@Override
	protected String getTaskBatchNo(Integer p) {
		return p+"_batch_no_"+Thread.currentThread().getName();
	}

	@Override
	public String doTaskUnit(String t) {
		return t+"_done";
	}

	@Override
	public void join(TaskResult taskResult) {
		size = taskResult.getTaskUnitResults().size();
		List<TaskUnitResult> taskUnitResults = taskResult.getTaskUnitResults();
		for (TaskUnitResult taskUnitResult : taskUnitResults) {
			System.out.println(taskUnitResult.getTaskResult());
		}
	}

	

}
