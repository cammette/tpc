package com.travelsky.pcc.reacc.tpc.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;
import com.travelsky.pcc.reacc.tpc.property.StaticProperties;

/**
 * 定义了并行执行任务的模板方案，用户可以根据需求定义任务分解，任务执行以及任务结果汇聚
 * @author bingo
 * @param <P> 并发执行任务的输入参数
 * @param <T> 代表一个任务单元，必须支持Java序列化
 * @param <U> 代表一个任务单元的执行结果，必须支持Java序列化
 */
public abstract class TravelskyParallelComputerTemplate<P, T, U> implements
		TravelskyParallelComputerInterface<P> {

	private TaskGroupParallelClientInterface<T> taskGroupParallelClientInterface;
	
	private TaskParallelClientInterface<T> taskParallelClientInterface;

	
	@Override
	public void excuteAsyn(P p) {
		if(null==p){
			return;
		}
		List<TaskGroup<T>>  taskGroups= this.split(p);
		if(null==taskGroups||taskGroups.size()==0){
			return;
		}
		if(isSingleGroup(taskGroups)){
			 this.taskParallelClientInterface.executeAsyn(taskGroups.get(0), getTaskBatchNo(p),
					 genProperties(p));
		}
		else{
			this.taskGroupParallelClientInterface.executeAsyn(taskGroups, getTaskBatchNo(p),
					 genProperties(p));
		}
	}

	@Override
	public TaskResult excuteSyn(P p, long timeout)
			throws TaskExcutedReplyTimeoutException {
		if(null==p){
			return null;
		}
		List<TaskGroup<T>>  taskGroups= this.split(p);
		if(null==taskGroups||taskGroups.size()==0){
			return null;
		}
		if(isSingleGroup(taskGroups)){
			return this.taskParallelClientInterface.executeSync(taskGroups.get(0), getTaskBatchNo(p),
					timeout, genProperties(p));
		}
		return this.taskGroupParallelClientInterface.executeSync(taskGroups, getTaskBatchNo(p),
				timeout, genProperties(p));
	}
	private boolean isSingleGroup(List<TaskGroup<T>>  taskGroups){
		if(taskGroups.size()==1){
			return true;
		}
		return false;
	}
	private Map<String, String> genProperties(P p){
		Map<String, String> messageProperties = new HashMap<String, String>();
		messageProperties.put(StaticProperties.ParallelComputerSpringBean,
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
	 * 获取绑定的属性，可以用来传递基本类型的值
	 * @param p
	 * @return
	 */
	protected Map<String,String> getTaskBindMap(P p){
		return new HashMap<String, String>();
	}

	/**
	 * 定义任务单元的执行逻辑
	 * @param t
	 * @return
	 */
	public abstract U doTaskUnit(T t) throws Exception;

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

	public TaskParallelClientInterface<T> getTaskParallelClientInterface() {
		return taskParallelClientInterface;
	}

	public void setTaskParallelClientInterface(
			TaskParallelClientInterface<T> taskParallelClientInterface) {
		this.taskParallelClientInterface = taskParallelClientInterface;
	}
	
}
