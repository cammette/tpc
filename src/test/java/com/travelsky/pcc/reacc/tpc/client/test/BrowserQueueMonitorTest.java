package com.travelsky.pcc.reacc.tpc.client.test;

import java.util.Date;

import org.junit.Test;

public class BrowserQueueMonitorTest extends TestBase{

	@Test
	public void test() {
		long start = new Date().getTime();

		for(int i=0;i<100;i++){
			TtestBean bean = new TtestBean();
//			bean.setSendListsize(10);
//			bean.setSendSize(50);
			requestParallelSync(bean, "syn normal test ", true);
		}
		long end = new Date().getTime();
		log.info("time :"+(end-start));
		//syn 311646ms
		//asyn 36s
			try {
				Thread.sleep(50000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}
		
	
		// t.test.TwoMethodTest:time :42  asyn
		//syn 59:06-58:45=
	}
}
