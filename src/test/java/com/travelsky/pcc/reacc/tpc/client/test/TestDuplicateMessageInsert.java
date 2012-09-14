package com.travelsky.pcc.reacc.tpc.client.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.travelsky.pcc.reacc.tpc.jms.DuplicateMessageManger;

public class TestDuplicateMessageInsert {

  public static void main(String [] args){
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-testDuplicate.xml");
    final DuplicateMessageManger duplicateMessageManager = (DuplicateMessageManger) context.getBean("duplicateMessageManager");

    //单线程向cache中插入1w条batchNo
//    String batchNo = "key";
//    long a = System.currentTimeMillis();
//    for(int i = 0;i<10000;i++){
//      String batchNO = batchNo+i;
//      duplicateMessageManager.addToCache(batchNO);
//      System.out.println(batchNO);
//    }
//    System.out.println("放1W条batchNo到catch中用时：");
//    System.out.println(System.currentTimeMillis() - a);
    //放1W条batchNo到catch中用时： 2519
    
    //启动100个线程每个线程执行100次插入1W条数据
    //每个线程执行100次耗时 最大值为1291毫秒 最小值为25毫秒
    
    String batchNo = "ok";
    System.out.println("开始时间："+System.currentTimeMillis());
    long a = System.currentTimeMillis();
    for(int i=0;i<100;i++){
      DataInsert td = new DataInsert(duplicateMessageManager,batchNo+"_"+i);
      td.start();
    }
    System.out.println("放1W条batchNo到catch中用时：");
    System.out.println(System.currentTimeMillis() - a);
  }
}

class DataInsert extends Thread{
  private DuplicateMessageManger duplicateMessageManager;
  private String batchNO;
  DataInsert(DuplicateMessageManger duplicateMessageManager,String batchNO){
    this.duplicateMessageManager = duplicateMessageManager;
    this.batchNO = batchNO;
  }
  public void run(){
    long b = System.currentTimeMillis();
    for(int i=0;i<100;i++){
//      System.out.println(batchNO+i);
      this.duplicateMessageManager.addToCache(batchNO+"_"+i);
    }
    System.out.println(System.currentTimeMillis()-b);
  }
  
  
  
}
