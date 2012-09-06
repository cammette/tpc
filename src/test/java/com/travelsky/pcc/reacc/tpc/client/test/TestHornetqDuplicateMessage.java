package com.travelsky.pcc.reacc.tpc.client.test;

import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.TextMessage;

import org.hornetq.api.core.SimpleString;
import org.hornetq.core.postoffice.DuplicateIDCache;
import org.hornetq.jms.server.embedded.EmbeddedJMS;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;
import com.travelsky.pcc.reacc.tpc.jms.JmsConnectionPool;
import com.travelsky.pcc.reacc.tpc.jms.JmsConnectionSession;

public class TestHornetqDuplicateMessage {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext-parallel.xml","applicationContext-test.xml" });
		final TravelskyParallelComputer travelskyParallelComputer = (TravelskyParallelComputer) context
				.getBean("travelskyParallelComputer");
		final EmbeddedJMS embeddedJMS = (EmbeddedJMS) context
				.getBean("EmbeddedJms");
		
		final JmsConnectionPool jmsConnectionPool = (JmsConnectionPool) context
				.getBean("jmsConnectionPool");
		final Queue queue = (Queue) context
				.getBean("/queue/test");
		JmsConnectionSession jmsConnection = jmsConnectionPool.getJmsConnectionSession();
		JmsConnectionSession jmsConnection1 = jmsConnectionPool.getJmsConnectionSession();
		jmsConnection1.getConnection().start();
		
		MessageProducer producer = jmsConnection.getSession().createProducer(queue);
		
    	MessageConsumer consumer  = jmsConnection1.getSession().createConsumer(queue);
    	
    	
       long a = System.currentTimeMillis();
       for (int i = 0; i < 2; i++) {
    	   TextMessage message = jmsConnection.getSession().createTextMessage("test");
           message.setStringProperty("_HQ_DUPL_ID", "test"+i);
    	   producer.send(message);
	   }
       DuplicateIDCache duplicateIDCache = embeddedJMS.getHornetQServer().getPostOffice().getDuplicateIDCache(new SimpleString("jms.queue.test"));
      System.out.println( duplicateIDCache.contains(new SimpleString("test1").getData()));
       System.out.println(System.currentTimeMillis()-a);
    	
     	for (int i = 0; i < 2; i++) {
			System.out.println(consumer.receive(100));
		}
    	
     	try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
