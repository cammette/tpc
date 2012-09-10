package com.travelsky.pcc.reacc.tpc.exception;

public class AddTaskToQueueException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public AddTaskToQueueException() {
		super();
	}

	public AddTaskToQueueException(String message, Throwable cause) {
		super(message, cause);
	}

	public AddTaskToQueueException(String message) {
		super(message);
	}

	public AddTaskToQueueException(Throwable cause) {
		super(cause);
	}
	
}
