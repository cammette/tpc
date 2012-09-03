package com.travelsky.pcc.reacc.tpc.jms;

import javax.jms.ConnectionFactory;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

/**
 * 代表jms connection池，通过它避免由于创建jms connection带来的性能消耗
 * @author bingo
 *
 */
public class JmsConnectionPool {

	private GenericObjectPool<JmsConnectionSession> pool = null;

	private ConnectionFactory connectionFactory;
	
	private int acknowledgeMode = -1;
	
	public int getAcknowledgeMode() {
		return acknowledgeMode;
	}

	public void setAcknowledgeMode(int acknowledgeMode) {
		this.acknowledgeMode = acknowledgeMode;
	}

	private Integer maxConnection;
	
	public Integer getMaxConnection() {
		return maxConnection;
	}

	public void setMaxConnection(Integer maxConnection) {
		this.maxConnection = maxConnection;
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	private Logger log = Logger.getLogger(getClass());
	
	public void setConnectionFactory(ConnectionFactory cf) {
		this.connectionFactory = cf;
	}

	public void init() {
		if(acknowledgeMode>0){
			pool = new GenericObjectPool<JmsConnectionSession>(new JmsConnectionSessionFactory(
					connectionFactory,acknowledgeMode));
		}else{
			pool = new GenericObjectPool<JmsConnectionSession>(new JmsConnectionSessionFactory(
					connectionFactory));
		}
		pool.setMaxActive(maxConnection);
		pool.setMaxIdle(maxConnection);
		pool.setTestOnBorrow(true);
	}
	
	public JmsConnectionSession getJmsConnectionSession() throws Exception{
		return (JmsConnectionSession)pool.borrowObject();
	}
	
	public void returnJmsConnectionSession(JmsConnectionSession jmsConnectionSession) throws Exception{
		pool.returnObject(jmsConnectionSession);
	}
	
	public void destroy(){
		try {
			pool.close();
			log.info("destroy the jmsConnectionSession pool");
		} catch (Exception e) {
			log.error("destroy the jmsConnectionSession pool unsuccessfully:",e);
		}
	}
}
