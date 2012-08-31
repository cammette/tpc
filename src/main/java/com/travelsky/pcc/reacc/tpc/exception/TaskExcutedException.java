package com.travelsky.pcc.reacc.tpc.exception;

public class TaskExcutedException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public TaskExcutedException() {
		super();
	}

	public TaskExcutedException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaskExcutedException(String message) {
		super(message);
	}

	public TaskExcutedException(Throwable cause) {
		super(cause);
	}
	
}
