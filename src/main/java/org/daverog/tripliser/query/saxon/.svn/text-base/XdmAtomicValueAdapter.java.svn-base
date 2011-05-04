package org.daverog.tripliser.query.saxon;

import org.daverog.tripliser.query.AtomicValue;

import net.sf.saxon.s9api.XdmAtomicValue;

public class XdmAtomicValueAdapter implements AtomicValue {

	private XdmAtomicValue value;
	
	public XdmAtomicValueAdapter(XdmAtomicValue value) {
		this.value = value;
	}

	@Override
	public String getStringValue() {
		return value.getStringValue();
	}

	@Override
	public Object getValue() {
		return value;
	}
	
}
