package org.daverog.tripliser.value;



public class ValueValidationException extends Exception {

	public ValueValidationException(String message){
		super(message);
	}

	public ValueValidationException(String message, Throwable cause){
		super(message, cause);
	}

}
