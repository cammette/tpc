package com.travelsky.pcc.reacc.tpc.client;

import java.util.List;
import java.util.Map;

/**
 * 定义执行任务分组，任务分组必须顺序执行
 * @author bingo
 *
 */
public interface TaskGroupParallelClientInterface {
	
	public void excuteSync(List<TaskGroup<Object>> taskGroups,String batchNo,long excuteTimeout,Map<String, String> messageProperties);
	
	public void excuteAsyn(List<TaskGroup<Object>> taskGroups,String batchNo,Map<String, String> messageProperties);
}
