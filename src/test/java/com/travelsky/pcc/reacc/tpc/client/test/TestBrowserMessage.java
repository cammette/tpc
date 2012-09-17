package com.travelsky.pcc.reacc.tpc.client.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.hornetq.core.server.Consumer;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.travelsky.pcc.reacc.tpc.client.TaskGroup;
import com.travelsky.pcc.reacc.tpc.jms.JMSService;
import com.travelsky.pcc.reacc.tpc.property.StaticProperties;

public class TestBrowserMessage {
 

  /**
   * 向消息队列发送2w消息，按照200条消息分为一个批次号，有100个批次号。
   * 
   * 
   * @author chang
   * @throws Exception
   */
  @Test
  public void insertData() throws Exception {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-messagetest.xml");
    final JMSService testService = (JMSService) context
        .getBean("testService");
    List<TaskGroup<Person>> taskGroups = new ArrayList<TaskGroup<Person>>();
    TaskGroup<Person> taskGroup = new TaskGroup<Person>();
    Person person = null;
    for (int j = 0; j < 10; j++) {
      taskGroup = new TaskGroup<Person>();
      for (int i = 0; i < 10; i++) {
          person = new Person();
          person.setId("j= "+j+" i= "+i + "");
          person.setName("name插入数据" + i+":"+j);
         
          taskGroup.add(person);
      }
      taskGroups.add(taskGroup);
  }
    
    Map<String, String> messageProperties = new HashMap<String, String>();
    String batchNO = "batchNo:"+new Date().getTime();
   //send 
    int groupNo = 1;
    for(TaskGroup<Person> tt : taskGroups)
    {
      String taskBatchNo = batchNO +"__" + groupNo;
      messageProperties.put(StaticProperties.BATCH_NO, taskBatchNo);
      
      int i = 0;
      for(Person p : tt){
        i++;
        messageProperties.put(StaticProperties.BATCH_NO, taskBatchNo);
        testService.send(p, batchNO, messageProperties);
        System.out.println("发送信息"+groupNo+"组"+i+"号"+"_______"+p.getId()+"----"+p.getName()+"____"+messageProperties.get(StaticProperties.BATCH_NO));
      }
      groupNo++;
    }
    
//    System.out.println("selectBrowserQueue测试：：：：：：：");
//    long start = System.currentTimeMillis();
//    
//    System.out.println("开始时间："+start);
//    boolean result = testService.hasMessage(batchNO+"__1");
//    long endtime =System.currentTimeMillis();
//    long cost = endtime-start;
//    System.out.println("结束时间："+endtime);
//    System.out.println("browserQueue耗时："+cost);
//    System.out.println(result);
//    
//    System.out.println("————————————————————————————————————————————");
//    
//    
//    System.out.println("browserQueue测试：：：：：：：");
//    long a = System.currentTimeMillis();
//    System.out.println("开始时间："+a);
//    try{
//    Enumeration enumeration = testService.browserQueue();
//      while (enumeration.hasMoreElements())
//      {
//         Message p = (Message)enumeration.nextElement();
//         System.out.println(p.toString());
//         if(p.getStringProperty(StaticProperties.BATCH_NO).equals(batchNO+"__1")){
//           long end = System.currentTimeMillis()-a;
//           System.out.println("browserQueue耗时："+end);
//           System.out.println(p.getObjectProperty(Person.class.getName()));
//           return ;
//         }
//     }
//    }
//    catch(Exception e){
//      e.printStackTrace();
//    }
  }

  
  @Test
  public void selectorBrowserTest(){
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-messagetest.xml");
    final JMSService testService = (JMSService) context
        .getBean("testService");
    String batchNo = "batchNo:1347615469981__9";
    long start = System.currentTimeMillis();
    System.out.println("开始时间："+start);
    boolean result = testService.hasMessage(batchNo);
    long endtime =System.currentTimeMillis();
    long cost = endtime-start;
    System.out.println("结束时间："+endtime);
    System.out.println("selectBrowserQueue耗时："+cost);
    System.out.println(result);
  }
  
  
  @Test
  public void browserTest() throws Exception{
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-messagetest.xml");
    final JMSService testService = (JMSService) context
        .getBean("testService");
    String batch = "batchNo:1347615469981__9";
    System.out.println("browserQueue测试：：：：：：：");
    long a = System.currentTimeMillis();
    System.out.println("开始时间："+a);
    List<String> batchNoLists = testService.browserQueue();
    for(String batchNO : batchNoLists){
      if(batchNO.equals(batch)){
        long b = System.currentTimeMillis();
        long cost = b-a;
        System.out.println("结束时间："+b);
        System.out.println("用时："+cost);
        break ;
      }
    }
    }
}
