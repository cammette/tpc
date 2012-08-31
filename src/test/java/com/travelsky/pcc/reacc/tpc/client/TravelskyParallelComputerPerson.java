package com.travelsky.pcc.reacc.tpc.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;

public class TravelskyParallelComputerPerson extends
		TravelskyParallelComputerTemplate<Integer, Person, ReturnBean> {
	protected Logger log = Logger.getLogger(getClass());
	@Override
	protected List<TaskGroup<Person>> split(Integer p) {
		List<TaskGroup<Person>> taskGroups = new ArrayList<TaskGroup<Person>>();
		TaskGroup<Person> taskGroup = new TaskGroup<Person>();
		Person person = null;
		for (int j = 0; j < 2; j++) {
			taskGroup = new TaskGroup<Person>();
			for (int i = 0; i < p; i++) {
				person = new Person();
				person.setId(j+i + "");
				person.setName("name中文测试" + i+j);
				taskGroup.add(person);
			}
			taskGroups.add(taskGroup);
		}
		return taskGroups;
	}

	@Override
	protected String getTaskBatchNo(Integer p) {
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
	}

}
