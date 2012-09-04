package com.travelsky.pcc.reacc.tpc.client.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.bean.TaskUnitResult;
import com.travelsky.pcc.reacc.tpc.client.TaskGroup;
import com.travelsky.pcc.reacc.tpc.client.TravelskyParallelComputerTemplate;

public class TravelskyParallelComputerPerson extends
		TravelskyParallelComputerTemplate<TtestBean, Person, ReturnBean> {
	protected Logger log = Logger.getLogger(getClass());
	@Override
	protected List<TaskGroup<Person>> split(TtestBean p) {
		if(p.isNull()){
			return null;
		}
		List<TaskGroup<Person>> taskGroups = new ArrayList<TaskGroup<Person>>();
		if(p.isSingle()){
			p.setSendListsize(1);
		}
		if(p.isSizeZero()){
			return taskGroups;
		}
		TaskGroup<Person> taskGroup = new TaskGroup<Person>();
		Person person = null;
		for (int j = 0; j < p.getSendListsize(); j++) {
			taskGroup = new TaskGroup<Person>();
			for (int i = 0; i < p.getSendSize(); i++) {
				person = new Person();
				person.setId("j= "+j+" i= "+i + "");
				person.setName("name中文测试" + i+":"+j);
				taskGroup.add(person);
			}
			taskGroups.add(taskGroup);
		}
		return taskGroups;
	}

	@Override
	protected String getTaskBatchNo(TtestBean p) {
		// TODO Auto-generated method stub
		return p+"batchNofffffffffff";
	}

	@Override
	public ReturnBean doTaskUnit(Person t) {
		ReturnBean returnBean = new ReturnBean();
		returnBean.setId(t.getId());
		returnBean.setResult(t.getName()+" doTaskUnit");
		return returnBean;
	}

	@Override
	public void join(TaskResult taskResult) {
		log.info("results size:"+taskResult.getTaskUnitResults().size());
		List<TaskUnitResult>  taskUnitResults = taskResult.getTaskUnitResults();
		for (TaskUnitResult taskUnitResult : taskUnitResults) {
			ReturnBean returnBean = (ReturnBean)taskUnitResult.getTaskResult();
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
