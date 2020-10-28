package com.cg.employeepayroll;

public class PayrollSystemException extends Exception {
	private static final long serialVersionUID = 1L;

	enum ExceptionType {
		UPDATE_FILE_EXCEPTION
	}

	ExceptionType type;

	public PayrollSystemException(String message, ExceptionType type) {
		super(message);
		this.type = type;
	}
}
