package com.travelsky.pcc.reacc.tpc.client;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;

/**
 * 并行计算暴露的执行接口
 * @author bingo
 *
 * @param <P>用户定义的类型，并行执行任务的参数
 */
public interface TravelskyParallelComputerInterface<P> {
	/**
	 * 异步执行任务接口
	 * @param p
	 */
	public void excuteAsyn(P p);

	/**
	 * 同步执行任务接口
	 * @param p
	 * @param timeout
	 * @return
	 * @throws TaskExcutedReplyTimeoutException
	 */
	public TaskResult excuteSyn(P p, long timeout)
			throws TaskExcutedReplyTimeoutException;

}
