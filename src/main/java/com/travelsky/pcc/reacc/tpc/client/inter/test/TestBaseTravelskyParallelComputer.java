package com.travelsky.pcc.reacc.tpc.client.inter.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.log4j.Logger;
import com.travelsky.pcc.reacc.tpc.client.TaskGroup;
import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputingTemplate;
import com.travelsky.pcc.reacc.tpc.exception.TpcRetryException;

public abstract class TestBaseTravelskyParallelComputer extends
		TravelskyParallelComputingTemplate<TestBaseP, TestBaseT, TestBaseU> {
	
	protected Logger log = Logger.getLogger(getClass());
	
	@Override
	protected List<TaskGroup<TestBaseT>> split(TestBaseP testBaseP) {
		if(testBaseP.isNull()){
			return null;
		}
		List<TaskGroup<TestBaseT>> taskGroups = new ArrayList<TaskGroup<TestBaseT>>();
		if(testBaseP.isSizeZero()){
			return taskGroups;
		}
		TaskGroup<TestBaseT> taskGroup = new TaskGroup<TestBaseT>();
		if(testBaseP.isRetry()){
			testBaseP.setListSize(1);
			testBaseP.setGroupSize(1);
		}
		log.info("groupSize:"+testBaseP.getGroupSize());
		log.info("send single list size:"+testBaseP.getListSize());
		TestBaseT baseT = null;
		for (int j = 0; j < testBaseP.getGroupSize(); j++) {
			taskGroup = new TaskGroup<TestBaseT>();
			for (int i = 0; i < testBaseP.getListSize(); i++) {
				baseT = testBaseP.getSendT(i,j);
				if(testBaseP.isRetry()){
					baseT.setRetry(true);
				}
				if(testBaseP.isShutdown()){
					baseT.setSleep(5000);
				}
				baseT.setObject();
				taskGroup.add(baseT);
			}
			taskGroups.add(taskGroup);
		}
		return taskGroups;
	}
	
	@Override
	protected String getTaskBatchNo(TestBaseP p) {
		// TODO Auto-generated method stub
		return "batchNo:"+UUID.randomUUID().toString();
	}

	@Override
	public TestBaseU doTaskUnit(TestBaseT t) {
		TestBaseU returnBean = genResultBean(t);
		if(t.isRetry()){
			//异常测试
			throw new TpcRetryException("retry exception test");
		}
		log.info(Thread.currentThread().getName()+":doTaskUnit--"+t.getId());
		checkBindObject(t.getObject());
		try {
			Thread.sleep(t.getSleep());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnBean;
	}
	
	public abstract TestBaseU genResultBean(TestBaseT t);
	
	public abstract void checkBindObject(Object object);
	
	@Override
	public Map<String, String> getTaskBindMap(TestBaseP p) {
		Map<String, String> map = new HashMap<String, String>(){{
			put("aa","bb");
			put("cc","dd");
		}};
		return map;
	}
}
