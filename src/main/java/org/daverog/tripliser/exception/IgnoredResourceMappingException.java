package org.daverog.tripliser.exception;

public class IgnoredResourceMappingException extends Exception {

	public IgnoredResourceMappingException(String message){
		super(message);
	}

	public IgnoredResourceMappingException(String message, Throwable cause){
		super(message, cause);
	}

}
