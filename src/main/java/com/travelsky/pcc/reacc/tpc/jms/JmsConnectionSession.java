package com.travelsky.pcc.reacc.tpc.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 代表一个jms connection
 * @author bingo
 *
 */
public class JmsConnectionSession {
	private static Log log = LogFactory.getLog(JmsConnectionSession.class);

	private Session session;

	private Connection connection;
	

	public JmsConnectionSession(Session session, Connection connection) {
		this.session = session;
		this.connection = connection;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void close() {
		if (session != null) {
			try {
				session.close();
			} catch (JMSException e) {
				log.error("关闭jms session失败", e);
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (JMSException e) {
				log.error("关闭jms connection失败", e);
			}
		}
	}

}
