package com.travelsky.pcc.reacc.tpc.client.listener;

import javax.jms.JMSException;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.travelsky.pcc.reacc.tpc.AbstractJoinTaskListener;
import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.exception.JoinTaskExcutedException;
/**
 * 实现了任务汇聚的逻辑：
 * 从任务执行结果获取到spring bean，并执行它的任务汇聚方法
 * @author bingo
 *
 */
public class TravelskyParallelJoinListener extends AbstractJoinTaskListener
		implements ApplicationContextAware {
	private Logger log = Logger.getLogger(getClass());

	private ApplicationContext springContext;

	private final static String doTaskMethodName = "join";

	@Override
	protected void doJoinMessage(TaskResult taskResult) throws JMSException {
	    String springBeanName = taskResult.getBeanName();
		Object taskBean = springContext.getBean(springBeanName);
		try {
			MethodUtils.invokeMethod(taskBean, doTaskMethodName, taskResult);
		} catch (Throwable e) {
			String errorMessage = "invoker the doTask method unsuccessfully";
			log.error(errorMessage, e);
			throw new JoinTaskExcutedException(errorMessage,e);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		springContext = ctx;
	}

}
