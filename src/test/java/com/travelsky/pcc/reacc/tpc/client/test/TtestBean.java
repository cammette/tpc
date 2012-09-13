package com.travelsky.pcc.reacc.tpc.client.test;

public class TtestBean {
	
	private boolean isRetry =false;
	private boolean isNull=false;
	private boolean isSizeZero=false;
	private int sendListsize=3;
	private int sendSize=5;
	private boolean isShutdown = false;
	private boolean isJmx = false;
	
	
	public boolean isRetry() {
		return isRetry;
	}
	public void setRetry(boolean isRetry) {
		this.isRetry = isRetry;
	}
	public boolean isNull() {
		return isNull;
	}
	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}
	public boolean isSizeZero() {
		return isSizeZero;
	}
	public void setSizeZero(boolean isSizeZero) {
		this.isSizeZero = isSizeZero;
	}
	public int getSendListsize() {
		return sendListsize;
	}
	public void setSendListsize(int sendListsize) {
		this.sendListsize = sendListsize;
	}
	public int getSendSize() {
		return sendSize;
	}
	public void setSendSize(int sendSize) {
		this.sendSize = sendSize;
	}
	public boolean isShutdown() {
		return isShutdown;
	}
	public void setShutdown(boolean isShutdown) {
		this.isShutdown = isShutdown;
	}
	public boolean isJmx() {
		return isJmx;
	}
	public void setJmx(boolean isJmx) {
		this.isJmx = isJmx;
	}
	
}
