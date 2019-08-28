package com.workday.techtest.exception;

public class JobExecutionException extends RuntimeException{

	private static final long serialVersionUID = 7696528072134239344L;

	public JobExecutionException() {
		super();
	}

	public JobExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobExecutionException(String message) {
		super(message);
	}
	
}
