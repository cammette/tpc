package com.travelsky.pcc.reacc.tpc.exception;

public class JoinTaskExcutedException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public JoinTaskExcutedException() {
		super();
	}

	public JoinTaskExcutedException(String message, Throwable cause) {
		super(message, cause);
	}

	public JoinTaskExcutedException(String message) {
		super(message);
	}

	public JoinTaskExcutedException(Throwable cause) {
		super(cause);
	}
	
}
