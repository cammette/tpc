package com.travelsky.pcc.reacc.tpc.client.test;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputingInterface;

public class RestartTest extends TestBase{
	private Logger log = Logger.getLogger(getClass());
	@Autowired
	private TravelskyParallelComputingInterface<Object> travelskyParallelComputerInterface;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testShutdown() {
		TtestBean bean = new TtestBean();
		bean.setShutdown(true);
		requestParallelSync(bean, "syn showndown test ", true);
	}
	
	@Test
	public void testAfterShutdown() {
		TtestBean bean = new TtestBean();
		bean.setNull(true);
		requestParallelSync(bean, "syn after showndown test ", true);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
