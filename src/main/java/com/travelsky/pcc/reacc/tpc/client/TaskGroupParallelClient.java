package com.travelsky.pcc.reacc.tpc.client;

import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.travelsky.pcc.reacc.tpc.bean.TaskResult;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyException;
import com.travelsky.pcc.reacc.tpc.exception.TaskExcutedReplyTimeoutException;
import com.travelsky.pcc.reacc.tpc.jms.JMSService;
import com.travelsky.pcc.reacc.tpc.property.StaticProperties;
import com.travelsky.pcc.reacc.tpc.status.TaskContextManager;

/**
 * 并行执行任务的入口，可以同步或异步并发执行任务
 * 
 * @author bingo
 * 
 */
public class TaskGroupParallelClient implements
		TaskGroupParallelClientInterface<Object> {

	private JMSService sendAndReplyGroupService;

	@Override
	public TaskResult executeSync(List<TaskGroup<Object>> taskGroups,
			String batchNo, long executeTimeout,
			Map<String, String> messageProperties)
			throws TaskExcutedReplyTimeoutException {
		messageProperties.put(StaticProperties.MS_SYN_OR_ASYN_KEY,StaticProperties.MS_VALUE_SYN);
		sendAndReplyGroupService.send((Serializable) taskGroups, batchNo,
				messageProperties);
		ObjectMessage msg = (ObjectMessage) sendAndReplyGroupService.waitforReply(batchNo, executeTimeout);
		try {
			return (TaskResult) msg.getObject();
		} catch (JMSException e) {
			throw new TaskExcutedReplyException(e);
		}
	}

	@Override
	public void executeAsyn(final List<TaskGroup<Object>> taskGroups,
			final String batchNo, final Map<String, String> messageProperties) {
		messageProperties.put(StaticProperties.MS_SYN_OR_ASYN_KEY,StaticProperties.MS_VALUE_ASYN);
		sendAndReplyGroupService.send((Serializable) taskGroups, batchNo,
				messageProperties);
	}
	

	
	public JMSService getSendAndReplyGroupService() {
		return sendAndReplyGroupService;
	}

	public void setSendAndReplyGroupService(JMSService sendAndReplyGroupService) {
		this.sendAndReplyGroupService = sendAndReplyGroupService;
	}

}
