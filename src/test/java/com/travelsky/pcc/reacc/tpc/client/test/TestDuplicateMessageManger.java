package com.travelsky.pcc.reacc.tpc.client.test;

import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.travelsky.pcc.reacc.tpc.jms.DuplicateMessageManger;
import com.travelsky.pcc.reacc.tpc.jms.JmsConnectionPool;
import com.travelsky.pcc.reacc.tpc.jms.JmsConnectionSession;
import com.travelsky.pcc.reacc.tpc.property.StaticProperties;

public class TestDuplicateMessageManger {

  public static void main(String[] args) throws Exception {

    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-testDuplicate.xml");
    final DuplicateMessageManger duplicateMessageManager = (DuplicateMessageManger) context.getBean("duplicateMessageManager");

    final JmsConnectionPool jmsConnectionPool = (JmsConnectionPool) context.getBean("jmsGroupConnectionPool");
    final Queue queue = (Queue) context.getBean("/queue/taskUnitQueue");
    JmsConnectionSession jmsConnection = jmsConnectionPool.getJmsConnectionSession();
    JmsConnectionSession jmsConnection1 = jmsConnectionPool.getJmsConnectionSession();
    jmsConnection1.getConnection().start();

    MessageProducer producer = jmsConnection.getSession().createProducer(queue);

    MessageConsumer consumer = jmsConnection1.getSession().createConsumer(queue);

    long a = System.currentTimeMillis();
    for (int i = 0; i < 2; i++) {
      System.out.println("--------------------------------------------------");
      TextMessage message = jmsConnection.getSession().createTextMessage("hhehehheheh");
      String batchNo =  "test3" + i;
      message.setStringProperty(StaticProperties.BATCH_NO,batchNo);
      boolean flag = duplicateMessageManager.contains(batchNo);
      System.out.println(batchNo+"------------在catch中存在与否："+flag);
      if(!flag){
        producer.send(message);
        duplicateMessageManager.addToCache(batchNo);
        System.out.println("放入缓存中：：存在否"+duplicateMessageManager.contains(batchNo));
      }
     System.out.println("--------------------------------------------------");
    }
    
    System.out.println(System.currentTimeMillis() - a);

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
