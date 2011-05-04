package org.daverog.tripliser.mapping.model;

import org.simpleframework.xml.Attribute;

public class Namespace {

	@Attribute
	private String prefix;

	@Attribute
	private String url;

	@Attribute(required=false, name="default")
	private Boolean defaultNamespace = false;

	public String getPrefix() {
		return prefix;
	}

	public String getUrl() {
		return url;
	}

	public boolean isDefault() {
		return defaultNamespace;
	}

}
