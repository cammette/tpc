package com.travelsky.pcc.reacc.tpc.exception;

public class TaskExcutedReplyTimeoutException extends Exception{

	private static final long serialVersionUID = 1L;

	public TaskExcutedReplyTimeoutException() {
		super();
	}

	public TaskExcutedReplyTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaskExcutedReplyTimeoutException(String message) {
		super(message);
	}

	public TaskExcutedReplyTimeoutException(Throwable cause) {
		super(cause);
	}
	
}
