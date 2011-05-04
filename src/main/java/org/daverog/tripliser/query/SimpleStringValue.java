package org.daverog.tripliser.query;

public class SimpleStringValue implements AtomicValue {

	private String value;
	
	public SimpleStringValue(String value) {
		this.value = value;
	}

	@Override
	public String getStringValue() {
		return value;
	}

	@Override
	public Object getValue() {
		return value;
	}

}
