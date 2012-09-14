package com.travelsky.pcc.reacc.tpc.jms;

import javax.jms.JMSException;
import javax.jms.Queue;

import org.apache.log4j.Logger;
import org.hornetq.api.core.SimpleString;
import org.hornetq.core.postoffice.DuplicateIDCache;
import org.hornetq.jms.server.embedded.EmbeddedJMS;

public class DuplicateMessageManger {
  
  private Logger log = Logger.getLogger(getClass());

  private Queue queue;
  
  private EmbeddedJMS embeddedJMS;
  
  private DuplicateIDCache duplicateIDCache;
  
  public void init() {
    try {
      duplicateIDCache = embeddedJMS.getHornetQServer().getPostOffice().getDuplicateIDCache(new SimpleString(queue.getQueueName()));
    } catch (JMSException e) {
      log.error("get duplicateIDCache error ",e);
    }
  }
  
  public Queue getQueue() {
    return queue;
  }

  public void setQueue(Queue queue) {
    this.queue = queue;
  }

  public EmbeddedJMS getEmbeddedJMS() {
    return embeddedJMS;
  }

  public void setEmbeddedJMS(EmbeddedJMS embeddedJMS) {
    this.embeddedJMS = embeddedJMS;
  }

  public DuplicateIDCache getDuplicateIDCache() {
    return duplicateIDCache;
  }

  public void setDuplicateIDCache(DuplicateIDCache duplicateIDCache) {
    this.duplicateIDCache = duplicateIDCache;
  }

  public boolean contains(String batchNo){
    boolean flag = false;
    flag = duplicateIDCache.contains(batchNo.getBytes());
    return flag;
  }

  public void addToCache(String batchNo){
    try {
      duplicateIDCache.addToCache(batchNo.getBytes(), null);
    }catch (Exception e) {
      log.error("addToCache error ",e);
    }
  }
}
