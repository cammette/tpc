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
import org.hornetq.jms.client.HornetQObjectMessage;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.travelsky.pcc.reacc.tpc.AbstractTaskListener;
import com.travelsky.pcc.reacc.tpc.bean.TaskUnitResult;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedException;
import com.travelsky.pcc.reacc.tpc.exception.TpcRetryException;
import com.travelsky.pcc.reacc.tpc.jms.DuplicateMessageManger;
import com.travelsky.pcc.reacc.tpc.property.StaticProperties;

/**
 * 实现了任务单元执行的逻辑：
 * 从消息队列中获取任务单元消息，接着获取任务执行单元的spring bean，最后执行它的执行任务单元方法
 * @author bingo
 *
 */
public class TravelskyParallelDoTaskListener extends AbstractTaskListener implements ApplicationContextAware{
	
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
			if(e.getCause() instanceof TpcRetryException){
				HornetQObjectMessage hornetMsg = (HornetQObjectMessage)msg;
                int deliveryCount= hornetMsg.getCoreMessage().getDeliveryCount();
                
                if(deliveryCount==StaticProperties.MAX_RETRY_TIMES){
                	taskUnitResult.setMsg("retry " +StaticProperties.MAX_RETRY_TIMES + " times,but also has exception :" +e.getCause().toString());
    				taskUnitResult.setIsSuccess(false);
                }
                else{
					taskUnitResult = null;
					throw (TpcRetryException) e.getCause();
                }
			}
			else{
				log.error("excuted the task unsuccessfully",e);
				taskUnitResult.setMsg(e.toString());
				taskUnitResult.setIsSuccess(false);
			}
		} 
		return taskUnitResult;
	}

	@Override
	public void setApplicationContext(ApplicationContext appConext)
			throws BeansException {
		springContext = appConext;
	}

}
