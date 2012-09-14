package com.travelsky.pcc.reacc.tpc.client.test;

import org.apache.log4j.Logger;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.bean.TaskUnitResult;
import com.travelsky.pcc.reacc.tpc.client.inter.test.TestBaseTravelskyParallelComputer;
import com.travelsky.pcc.reacc.tpc.property.StaticProperties;

public class TravelskyParallelComputerPerson<TestBaseP, TestBaseT, TestBaseU> extends TestBaseTravelskyParallelComputer {
	protected Logger log = Logger.getLogger(getClass());

	@Override
	public void join(TaskResult taskResult) {
		if(taskResult.getTotalCount()==StaticProperties.TASK_SIZE_UNKNOW){
			log.info("join: task restart ok:-------------");
		}
		log.info("join "+taskResult.toString());
		for(TaskUnitResult unit : taskResult.getTaskUnitResults()){
			if(""!=unit.getMsg()){
				log.info(unit.getMsg());
			}
		}
	}
	@Override
	public void checkBindObject(Object object) {
		log.info(object.toString());
	}
	@Override
	public com.travelsky.pcc.reacc.tpc.client.inter.test.TestBaseU genResultBean(
			com.travelsky.pcc.reacc.tpc.client.inter.test.TestBaseT t) {
		// TODO Auto-generated method stub
		return new ReturnBean();
	}
	

	
}
