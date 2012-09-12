package com.travelsky.pcc.reacc.tpc.client.test;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputingInterface;

/**
 * 测试重启，任务更改为，sleep(5000),即睡眠5秒,先运行测试 testShutdown(),
 * 看到有消息执行任务时，停止jvm，达到模拟重启。
 * 然后运行测试 testAfterShutdown ，发送一个空任务给jms并sleep,可以看到，重复任务
 * 会被过滤，并且任务恢复执行。
 * @author abel.lee
 * @email abel.lee@embracesource.com
 */
public class RestartTest extends TestBase{
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
			Thread.sleep(50000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testBrowse(){
		
	}
	

}
