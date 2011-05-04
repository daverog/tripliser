package org.daverog.tripliser.mapping.model;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;


@Root
public class DummyXmlModel implements Serializable {
	
	@Attribute
	public String validAttribute;
	
}