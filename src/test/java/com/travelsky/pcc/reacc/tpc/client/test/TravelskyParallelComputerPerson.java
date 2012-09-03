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
		List<TaskUnitResult>  taskUnitResults = taskResult.getTaskUnitResults();
		for (TaskUnitResult taskUnitResult : taskUnitResults) {
			ReturnBean returnBean = (ReturnBean)taskUnitResult.getTaskResult();
		}
	}

	@Override
	protected Map<String, String> getTaskBindMap(Integer p) {
		Map<String, String> map = new HashMap<String, String>(){{
			put("aa","bb");
			put("cc","dd");
		}};
		return map;
	}

	
}
