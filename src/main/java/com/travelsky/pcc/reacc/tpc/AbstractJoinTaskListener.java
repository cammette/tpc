package com.travelsky.pcc.reacc.tpc;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.exception.JoinTaskExcutedException;

/**
 * 监听队列，获取任务完成通知
 * @author bingo
 *
 */
public abstract class AbstractJoinTaskListener implements MessageListener {
	
	protected Logger log = Logger.getLogger(getClass());
	
	public void onMessage(Message msg) {
		try{
			if(msg instanceof ObjectMessage){
				TaskResult allBatchBean = (TaskResult)((ObjectMessage) msg).getObject();
				this.doJoinMessage(allBatchBean);
			}else{
				throw new JoinTaskExcutedException("don't support this type message["+msg.getClass()+"],it must be ObjectMessage");
			}
		}catch(Throwable e){
			String errorMessage = "join tasks unsuccessfully ";
			log.error(errorMessage, e);
			if (e instanceof JoinTaskExcutedException) {
				throw (JoinTaskExcutedException) e;
			} else {
				throw new JoinTaskExcutedException(errorMessage,e);
			}
		}
	}
	
	/**
	 * 该抽象方法在异步执行任务时会被调研，子类可以实现它，处理任务完成后的通知
	 * @param taskResult
	 * @param msg
	 * @throws JMSException
	 */
	protected abstract void doJoinMessage(TaskResult taskResult)  throws JMSException;
	
}
