package org.daverog.tripliser.mapping.model;

import org.simpleframework.xml.Attribute;

public class Constant {
	
	@Attribute
	private String name;
	
	@Attribute(required = false)
	protected String value;
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Constant other = (Constant) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
