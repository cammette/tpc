package com.travelsky.pcc.reacc.tpc.property;

public class StaticProperties {
	/**
	 * 标识同步还是异步 属性key
	 */
	public static final String MS_SYN_OR_ASYN_KEY="syn_or_asyn";
	/**
	 * 同步标识
	 */
	public static final String MS_VALUE_SYN="syn";
	/**
	 * 异步标识
	 */
	public static final String MS_VALUE_ASYN = "asyn";
	/**
	 * 任务id属性key
	 */
	public static final String BATCH_NO = "batch_no";
	/**
	 * message里带jms属性 key
	 */
	public final static String JMS_PROPERTIRES_NAME="JMSXDeliveryCount";
	/**
	 * 用户实现模板类属性key
	 */
	public static final String ParallelComputerSpringBean = "P_C_S";
	/**
	 * 异常重试次数
	 */
	public final static int MAX_RETRY_TIMES = Integer.parseInt(System.getProperty("retry.times"));
	/**
	 * 如果重启后，任务大小丢失，则用该值去初始化taskResult
	 */
	public final static int TASK_SIZE_UNKNOW = -1;
	
}
