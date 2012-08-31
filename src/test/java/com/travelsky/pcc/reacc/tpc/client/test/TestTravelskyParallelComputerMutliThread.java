package com.travelsky.pcc.reacc.tpc.client.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;

public class TestTravelskyParallelComputerMutliThread {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext-parallel.xml" });
		final TravelskyParallelComputer travelskyParallelComputer = (TravelskyParallelComputer) context
				.getBean("travelskyParallelComputer");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 5; i++) {
			new Thread() {
				public void run() {
					for (int j = 0; j < 1000; j++) {
						long a = System.currentTimeMillis();
						TaskResult taskResult;
						try {
							taskResult = travelskyParallelComputer.excuteSyn(500, 20000);
							System.out.println((System.currentTimeMillis()-a)+":"+taskResult.getTaskUnitResults().size());
						} catch (TaskExcutedReplyTimeoutException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
			}.start();
		}

	}

}
