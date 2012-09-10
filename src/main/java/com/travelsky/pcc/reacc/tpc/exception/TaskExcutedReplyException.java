package com.travelsky.pcc.reacc.tpc.exception;

public class TaskExcutedReplyException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public TaskExcutedReplyException() {
		super();
	}

	public TaskExcutedReplyException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaskExcutedReplyException(String message) {
		super(message);
	}

	public TaskExcutedReplyException(Throwable cause) {
		super(cause);
	}
	
}
