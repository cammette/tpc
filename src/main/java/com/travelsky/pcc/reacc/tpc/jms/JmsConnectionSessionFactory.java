package com.travelsky.pcc.reacc.tpc.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Session;

import org.apache.commons.pool.PoolableObjectFactory;
import org.hornetq.jms.client.HornetQSession;

/**
 * 
 * @author bingo
 *
 */
public class JmsConnectionSessionFactory implements PoolableObjectFactory<JmsConnectionSession> {

	private ConnectionFactory connectionFactory;

	public JmsConnectionSessionFactory(ConnectionFactory connectionFactory) {
		super();
		this.connectionFactory = connectionFactory;
	}

	@Override
	public JmsConnectionSession makeObject() throws Exception {
		Connection connection = connectionFactory.createConnection();
		Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
		return new JmsConnectionSession(session, connection);
	}

	@Override
	public void destroyObject(JmsConnectionSession obj) throws Exception {
		((JmsConnectionSession) obj).close();
	}

	@Override
	public boolean validateObject(JmsConnectionSession obj) {
		return !((HornetQSession) ((JmsConnectionSession) obj).getSession())
				.getCoreSession().isClosed();
	}

	@Override
	public void activateObject(JmsConnectionSession obj) throws Exception {

	}

	@Override
	public void passivateObject(JmsConnectionSession obj) throws Exception {

	}

}
