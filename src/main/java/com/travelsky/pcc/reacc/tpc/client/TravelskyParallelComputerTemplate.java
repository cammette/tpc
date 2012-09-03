package com.travelsky.pcc.reacc.tpc.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;

/**
 * 定义了并行执行任务的模板方案，用户可以根据需求定义任务分解，任务执行以及任务结果汇聚
 * @author bingo
 * @param <P> 并发执行任务的输入参数
 * @param <T> 代表一个任务单元，必须支持Java序列化
 * @param <U> 代表一个任务单元的执行结果，必须支持Java序列化
 */
public abstract class TravelskyParallelComputerTemplate<P, T, U> implements
		TravelskyParallelComputerInterface<P> {

	public static final String ParallelComputerSpringBean = "P_C_S";
	
	private TaskGroupParallelClientInterface<T> taskGroupParallelClientInterface;

	
	@Override
	public void excuteAsyn(P p) {
		//this.taskGroupParallelClientInterface.excuteAsyn( this.split(p), getTaskBatchNo(p),
			//	genProperties(p));
	}

	@Override
	public TaskResult excuteSyn(P p, long timeout)
			throws TaskExcutedReplyTimeoutException {
		return this.taskGroupParallelClientInterface.excuteSync(this.split(p), getTaskBatchNo(p),
				timeout, genProperties(p));
	}
	private Map<String, String> genProperties(P p){
		Map<String, String> messageProperties = new HashMap<String, String>();
		messageProperties.put(ParallelComputerSpringBean,
				this.getParallelComputerSpringBeanName());
		messageProperties.putAll(getTaskBindMap(p));
		return messageProperties;
	}

//	private List<Object> mergerTask(P p) {
//		List<TaskGroup<T>> taskGroups = this.split(p);
//		List<Object> taskList = new ArrayList<Object>();
//		for (TaskGroup<T> taskGroup : taskGroups) {
//			taskList.addAll(taskGroup);
//		}
//		return taskList;
//	}
	/**
	 * 获取任务执行的springBeanName，默认是用户实现了该模板类的类名（第一个字母小写）
	 * @return
	 */
	protected String getParallelComputerSpringBeanName() {
		String className = this.getClass().getSimpleName();
		StringBuilder classNameSb = new StringBuilder();
		classNameSb.append(className.substring(0, 1).toLowerCase());
		classNameSb.append(className.substring(1));
		return classNameSb.toString();
	}

	/**
	 * 分解任务
	 * @param p
	 * @return
	 */
	protected abstract List<TaskGroup<T>> split(P p);

	/**
	 * 获取任务的批次号
	 * @param p
	 * @return
	 */
	protected abstract String getTaskBatchNo(P p);
	
	/**
	 * 获取批次号绑定的对象
	 * @param p
	 * @return
	 */
	protected abstract Map<String,String> getTaskBindMap(P p);

	/**
	 * 定义任务单元的执行逻辑
	 * @param t
	 * @return
	 */
	public abstract U doTaskUnit(T t);

	/**
	 * 定义汇聚任务执行结果的通知逻辑
	 * @param taskResult
	 */
	public abstract void join(TaskResult taskResult);

	public TaskGroupParallelClientInterface<T> getTaskGroupParallelClientInterface() {
		return taskGroupParallelClientInterface;
	}

	public void setTaskGroupParallelClientInterface(
			TaskGroupParallelClientInterface<T> taskGroupParallelClientInterface) {
		this.taskGroupParallelClientInterface = taskGroupParallelClientInterface;
	}
	
}
