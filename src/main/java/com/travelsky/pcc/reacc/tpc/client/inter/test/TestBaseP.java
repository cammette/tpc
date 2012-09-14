package com.travelsky.pcc.reacc.tpc.client.inter.test;


public  abstract class TestBaseP {
	
	private boolean isRetry =false;
	private boolean isNull=false;
	private boolean isSizeZero=false;
	private int groupSize=3;
	private int listSize=5;
	private boolean isShutdown = false;
	public abstract TestBaseT getSendT(int groupNo, int listIndex);
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
	
	public int getGroupSize() {
		return groupSize;
	}

	public void setGroupSize(int groupSize) {
		this.groupSize = groupSize;
	}

	public int getListSize() {
		return listSize;
	}

	public void setListSize(int listSize) {
		this.listSize = listSize;
	}

	public boolean isShutdown() {
		return isShutdown;
	}
	public void setShutdown(boolean isShutdown) {
		this.isShutdown = isShutdown;
	}
}
