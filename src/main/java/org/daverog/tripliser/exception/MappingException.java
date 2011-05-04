package org.daverog.tripliser.exception;

public class MappingException extends Exception {
	
	public MappingException(String message){
		super(message);
	}
	
	public MappingException(String message, Throwable cause){
		super(message, cause);
	}

	
}
