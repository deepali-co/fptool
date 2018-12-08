package com.myapps.fptool;

public class FPToolException extends Exception {
	private static final long serialVersionUID = 1L;

	public FPToolException() {
		super();
	}

	public FPToolException(String message) {
		super(message);
	}

	public FPToolException(String message, Throwable cause) {
		super(message, cause);
	}

	public FPToolException(Throwable cause) {
		super(cause);
	}

	protected FPToolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
