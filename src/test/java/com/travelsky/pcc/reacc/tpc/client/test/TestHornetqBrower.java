package com.travelsky.pcc.reacc.tpc.client.test;

import javax.jms.Queue;
import javax.jms.QueueBrowser;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;
import com.travelsky.pcc.reacc.tpc.jms.JmsConnectionPool;
import com.travelsky.pcc.reacc.tpc.jms.JmsConnectionSession;

public class TestHornetqBrower {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext-parallel.xml","applicationContext-test.xml" });
		final TravelskyParallelComputer travelskyParallelComputer = (TravelskyParallelComputer) context
				.getBean("travelskyParallelComputer");
		
		final JmsConnectionPool jmsConnectionPool = (JmsConnectionPool) context
				.getBean("jmsConnectionPool");
		final Queue queue = (Queue) context
				.getBean("/queue/travelskyQueue");
		JmsConnectionSession jmsConnection = jmsConnectionPool.getJmsConnectionSession();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 1; i++) {
			new Thread() {
				public void run() {
					for (int j = 0; j < 1; j++) {
						long a = System.currentTimeMillis();
						TaskResult taskResult;
						try {
							taskResult = travelskyParallelComputer.excuteSyn(100, 20000);
							System.out.println((System.currentTimeMillis()-a)+":"+taskResult.getTaskUnitResults().size());
						} catch (TaskExcutedReplyTimeoutException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
			}.start();
		}
    	QueueBrowser qb  = 	jmsConnection.getSession().createBrowser(queue);
    	while(true){
    		System.out.println(qb.getEnumeration().hasMoreElements());
    		Thread.sleep(100);
    	}

	}

}
