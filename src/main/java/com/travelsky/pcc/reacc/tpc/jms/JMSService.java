package com.travelsky.pcc.reacc.tpc.jms;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.apache.log4j.Logger;

import com.travelsky.pcc.reacc.tpc.exception.AddTaskToQueueException;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyException;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;
import com.travelsky.pcc.reacc.tpc.property.StaticProperties;

/**
 * 向队列发送或接受消息
 * 
 * @author bingo
 * 
 */
public class JMSService {
	private Logger log = Logger.getLogger(getClass());

	private Queue queue;

	private Queue replyQueue;

	private JmsConnectionPool jmsConnectionPool;

	private JmsConnectionPool replyJmsConnectionPool;

	public JmsConnectionPool getReplyJmsConnectionPool() {
		return replyJmsConnectionPool;
	}

	public void setReplyJmsConnectionPool(
			JmsConnectionPool replyJmsConnectionPool) {
		this.replyJmsConnectionPool = replyJmsConnectionPool;
	}

	public JmsConnectionPool getJmsConnectionPool() {
		return jmsConnectionPool;
	}

	public void setJmsConnectionPool(JmsConnectionPool jmsConnectionPool) {
		this.jmsConnectionPool = jmsConnectionPool;
	}

	public void setQueue(Queue queue) {
		this.queue = queue;
	}

	public void setReplyQueue(Queue replyQueue) {
		this.replyQueue = replyQueue;
	}

	public Message waitforReply(String batchNo, long timeout)
			throws TaskExcutedReplyTimeoutException {
		JmsConnectionSession jmsConnectionSession = null;
		MessageConsumer consumer = null;
		try {
			jmsConnectionSession = (JmsConnectionSession) replyJmsConnectionPool
					.getJmsConnectionSession();
			Session session = jmsConnectionSession.getSession();
			jmsConnectionSession.getConnection().start();
			StringBuilder batchSelector = new StringBuilder();
			batchSelector.append(StaticProperties.BATCH_NO);
			batchSelector.append("='");
			batchSelector.append(batchNo);
			batchSelector.append("'");
			consumer = session.createConsumer(replyQueue,
					batchSelector.toString());
			Message replyMessage = consumer.receive(timeout);
			if (replyMessage == null) {
				throw new TaskExcutedReplyTimeoutException(
						"waiting for the reply message timeout");
			}
			return replyMessage;
		} catch (TaskExcutedReplyTimeoutException e) {
			throw e;
		} catch (Exception ex) {
			log.error("wait for a message reply unsuccessfully", ex);
			throw new TaskExcutedReplyException(ex);
		} finally {
			if (consumer != null) {
				try {
					consumer.close();
				} catch (Exception e) {
					log.error("close consumer unsuccessfully", e);
				}
			}
			if (jmsConnectionSession != null) {
				try {
					replyJmsConnectionPool
							.returnJmsConnectionSession(jmsConnectionSession);
				} catch (Exception e) {
					log.error(
							"put the jmsConnectionSession to back the pool unsuccessfully",
							e);
				}
			}
		}
	}

	public void send(Serializable msg, String batchNo,
			Map<String, String> messageProperties) {
		this.send(msg, batchNo, messageProperties, null);
	}

	public void send(Serializable msg, String batchNo,
			Map<String, String> messageProperties, Integer priority) {
		JmsConnectionSession jmsConnectionSession = null;
		MessageProducer producer = null;
		try {
			jmsConnectionSession = (JmsConnectionSession) jmsConnectionPool
					.getJmsConnectionSession();
			Session session = jmsConnectionSession.getSession();
			producer = session.createProducer(queue);
			producer.setDisableMessageID(true);
			Message message = null;
			if (msg instanceof String) {
				message = session.createTextMessage((String) msg);
			} else {
				message = session.createObjectMessage(msg);
			}
			if (batchNo != null && batchNo.length() > 0) {
				message.setStringProperty(StaticProperties.BATCH_NO, batchNo);
			}
			if (priority != null && priority > 0) {
				message.setJMSPriority(priority);
			}
			if (messageProperties != null) {
				Set<String> keys = messageProperties.keySet();
				for (String key : keys) {
					String value = messageProperties.get(key);
					message.setStringProperty(key, value);
				}
			}
			producer.send(message);
		} catch (Exception ex) {
			log.error("send a message unsuccessfully", ex);
			throw new AddTaskToQueueException(ex);
		} finally {
			if (producer != null) {
				try {
					producer.close();
				} catch (Exception e) {
					log.error("close producer unsuccessfully", e);
				}
			}
			if (jmsConnectionSession != null) {
				try {
					jmsConnectionPool
							.returnJmsConnectionSession(jmsConnectionSession);
				} catch (Exception e) {
					log.error(
							"put the jmsConnectionSession to back the pool unsuccessfully",
							e);
				}
			}
		}
	}

	public Enumeration browserQueue() {
		return browse(queue);

	}

	public Enumeration browserReplyQueue() {
		return browse(replyQueue);
	}

	private Enumeration browse(Queue queue) {
		if(null==queue){
			return null;
		}
		JmsConnectionSession jmsConnectionSession = null;
		QueueBrowser browser = null;
		try {
			jmsConnectionSession = (JmsConnectionSession) jmsConnectionPool
					.getJmsConnectionSession();
			Session session = jmsConnectionSession.getSession();
			browser = session.createBrowser(queue);
			return browser.getEnumeration();
		} catch (Exception e) {
			try {
				log.error("browse fail:queue name:" + queue.getQueueName(), e);
			} catch (JMSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			if(null!=browser){
				try {
					browser.close();
				} catch (JMSException e) {
					log.error("close browse unsuccessfully",e);
				}
			}
			if (jmsConnectionSession != null) {
				try {
					jmsConnectionPool
							.returnJmsConnectionSession(jmsConnectionSession);
				} catch (Exception e) {
					log.error(
							"put the jmsConnectionSession to back the pool unsuccessfully",
							e);
				}
			}
		}
		return null;
	}

}
