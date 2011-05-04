package org.daverog.tripliser.mapping.model;

import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import org.daverog.tripliser.mapping.model.Constant;

public class ConstantBuilder {

	private String name = "name";
	private String value = "value";

	public ConstantBuilder name(String name) {
		this.name = name;
		return this;
	}

	public ConstantBuilder value(String value) {
		this.value = value;
		return this;
	}

	public Constant toConstant() {
		Constant constant = new Constant();
		
		setInternalState(constant, "name", name);
		setInternalState(constant, "value", value);
		return constant;
	}

}
