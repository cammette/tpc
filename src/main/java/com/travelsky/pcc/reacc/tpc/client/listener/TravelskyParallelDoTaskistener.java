package com.travelsky.pcc.reacc.tpc.client.listener;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.travelsky.pcc.reacc.tpc.AbstractTaskListener;
import com.travelsky.pcc.reacc.tpc.bean.TaskUnitResult;
import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputerTemplate;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedException;
import com.travelsky.pcc.reacc.tpc.jms.JMSService;
import com.travelsky.pcc.reacc.tpc.property.StaticProperties;

/**
 * 实现了任务单元执行的逻辑：
 * 从消息队列中获取任务单元消息，接着获取任务执行单元的spring bean，最后执行它的执行任务单元方法
 * @author bingo
 *
 */
public class TravelskyParallelDoTaskistener extends AbstractTaskListener implements ApplicationContextAware{
	
	private Logger log = Logger.getLogger(getClass());
	
	private ApplicationContext springContext;
	
	private final static String doJoinTaskMethodName = "doTaskUnit";
	
	@Override
	@SuppressWarnings("unchecked")
	protected TaskUnitResult doMessage(Message msg, String batchNo) throws JMSException {
		TaskUnitResult taskUnitResult = new TaskUnitResult();
		taskUnitResult.setBatchNo(batchNo);
		String springBeanName = "";
		Map<String, String> taskBindMap = new HashMap<String, String>();
		String proName="";
		for (Enumeration<String> e = msg.getPropertyNames(); e.hasMoreElements();){
			 proName=e.nextElement();
			 if(proName.equals(StaticProperties.ParallelComputerSpringBean)){
				 springBeanName = msg.getStringProperty(proName);
			 }
			 else if(!proName.equals(StaticProperties.BATCH_NO)&&!proName.equals(StaticProperties.JMS_PROPERTIRES_NAME)){
				 taskBindMap.put(proName, msg.getStringProperty(proName));
			 }
		}
		taskUnitResult.setTaskBindProperties(taskBindMap);
		Object taskBean = springContext.getBean(springBeanName);
		Serializable paramObject = null; 
		if (msg instanceof ObjectMessage) {
			paramObject = ((ObjectMessage)msg).getObject();
		} else if(msg instanceof TextMessage){
			paramObject = ((TextMessage)msg).getText();
		}else {
			throw new TaskExcutedException(
					"not support this type message,it must be ObjectMessage");
		}
		
		try {
			Object returnObject = MethodUtils.invokeMethod(taskBean, doJoinTaskMethodName, paramObject);
			taskUnitResult.setIsSuccess(true);
			taskUnitResult.setTaskResult((Serializable)returnObject);
		} catch (Throwable e) {
			log.error("excuted the task unsuccessfully",e);
			taskUnitResult.setMsg(e.toString());
			taskUnitResult.setIsSuccess(false);
		} 
		return taskUnitResult;
	}

	@Override
	public void setApplicationContext(ApplicationContext appConext)
			throws BeansException {
		springContext = appConext;
	}

}
