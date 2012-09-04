package com.travelsky.pcc.reacc.tpc.client;

import java.util.List;
import java.util.Map;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;

/**
 * 并发执行任务客户端
 * @author bingo
 *
 */
public interface TaskParallelClientInterface<T> {
	/**
	 * 同步执行任务并等待任务执行结果
	 * @param tasks
	 * @param excuteTimeout
	 * @return
	 */
	public TaskResult excuteSync(List<T> tasks,String batchNo,long excuteTimeout,Map<String, String> messageProperties)  throws TaskExcutedReplyTimeoutException ;
	
	/**
	 * 异步执行任务，不等待任务执行结果
	 * @param tasks
	 */
	public void excuteAsyn(List<T> tasks,String batchNo,Map<String, String> messageProperties);
}
