package com.travelsky.pcc.reacc.tpc.client.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.bean.TaskUnitResult;
import com.travelsky.pcc.reacc.tpc.client.TaskGroup;
import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputingTemplate;
import com.travelsky.pcc.reacc.tpc.exception.TpcRetryException;
import com.travelsky.pcc.reacc.tpc.property.StaticProperties;

public class TravelskyParallelComputerPerson extends
		TravelskyParallelComputingTemplate<TtestBean, Person, ReturnBean> {
	protected Logger log = Logger.getLogger(getClass());
	
	@Override
	protected List<TaskGroup<Person>> split(TtestBean testBean) {
		if(testBean.isNull()){
			return null;
		}
		List<TaskGroup<Person>> taskGroups = new ArrayList<TaskGroup<Person>>();
		if(testBean.isSizeZero()){
			return taskGroups;
		}
		TaskGroup<Person> taskGroup = new TaskGroup<Person>();
		Person person = null;
		if(testBean.isRetry()){
			testBean.setSendListsize(1);
			testBean.setSendSize(1);
		}
		log.info("groupSize:"+testBean.getSendListsize());
		log.info("send single list size:"+testBean.getSendSize());
		for (int j = 0; j < testBean.getSendListsize(); j++) {
			taskGroup = new TaskGroup<Person>();
			for (int i = 0; i < testBean.getSendSize(); i++) {
				person = new Person();
				person.setId("j= "+j+" i= "+i + "");
				person.setName("name中文测试" + i+":"+j);
				if(testBean.isRetry()){
					person.setRetry(true);
				}
				if(testBean.isShutdown()){
					person.setSleep(5000);
				}
				if(testBean.isJmx()){
					person.setSleep(50000);
				}
				taskGroup.add(person);
			}
			taskGroups.add(taskGroup);
		}
		return taskGroups;
	}

	@Override
	protected String getTaskBatchNo(TtestBean p) {
		// TODO Auto-generated method stub
		return "batchNo:"+UUID.randomUUID().toString();
	}

	@Override
	public ReturnBean doTaskUnit(Person person) {
		ReturnBean returnBean = new ReturnBean();
		returnBean.setId(person.getId());
		if(person.isRetry()){
			//异常测试
			throw new TpcRetryException("retry exception test");
		}

		returnBean.setResult(person.getName()+" doTaskUnit sleep:"+person.getSleep());
		log.info(Thread.currentThread().getName()+":doTaskUnit--"+person.getId()+"sleep:"+person.getSleep());
		try {
			Thread.sleep(person.getSleep());
			log.info(Thread.currentThread().getName()+":doTaskUnit--"+person.getId()+"after----sleep:"+person.getSleep());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnBean;
	}

	@Override
	public void join(TaskResult taskResult) {
		if(taskResult.getTotalCount()==StaticProperties.TASK_SIZE_UNKNOW){
			log.info("join: task restart ok:-------------");
		}
		log.info("join "+taskResult.toString());
		log.info("unint size:"+taskResult.getTaskUnitResults().size());
		for(TaskUnitResult unit : taskResult.getTaskUnitResults()){
			if(""!=unit.getMsg()){
				log.info(unit.getMsg());
			}
		}
	}

	@Override
	protected Map<String, String> getTaskBindMap(TtestBean p) {
		Map<String, String> map = new HashMap<String, String>(){{
			put("aa","bb");
			put("cc","dd");
		}};
		return map;
	}

	
}
