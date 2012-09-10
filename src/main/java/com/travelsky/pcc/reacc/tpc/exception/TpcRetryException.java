package com.travelsky.pcc.reacc.tpc.exception;

public class TpcRetryException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5711789716887207281L;
	
	public TpcRetryException() {
		super();
	}

	public TpcRetryException(String message, Throwable cause) {
		super(message, cause);
	}

	public TpcRetryException(String message) {
		super(message);
	}

	public TpcRetryException(Throwable cause) {
		super(cause);
	}

}
