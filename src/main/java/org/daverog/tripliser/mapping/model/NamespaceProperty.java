package org.daverog.tripliser.mapping.model;

public class NamespaceProperty {
	
	private final String namespace;
	private final String property;

	public NamespaceProperty(String property, String namespace){
		this.property = property;
		this.namespace = namespace;
	}

	public String getProperty() {
		return property;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getFullPropertyName() {
		return namespace + property;
	}

}
