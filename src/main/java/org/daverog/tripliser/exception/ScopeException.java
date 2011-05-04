package org.daverog.tripliser.exception;

public class ScopeException extends RuntimeException {

	public ScopeException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScopeException(String message) {
		super(message);
	}

}
