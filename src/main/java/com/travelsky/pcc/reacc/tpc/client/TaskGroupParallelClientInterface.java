package com.travelsky.pcc.reacc.tpc.client;

import java.util.List;
import java.util.Map;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;

/**
 * 定义执行任务分组，任务分组必须顺序执行
 * @author bingo
 *
 */
public interface TaskGroupParallelClientInterface<T> {
	
	public String GROURP_SIZE_KEY = "__g_s_K";
	
	public String TASK_SIZE_KEY = "__t_s_k";
	
	
	public TaskResult excuteSync(List<TaskGroup<T>> taskGroups,String batchNo,long excuteTimeout,Map<String, String> messageProperties)
			throws TaskExcutedReplyTimeoutException;
	
	public void excuteAsyn(List<TaskGroup<T>> taskGroups,String batchNo,Map<String, String> messageProperties);
}
										