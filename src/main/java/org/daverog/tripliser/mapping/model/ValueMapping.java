package org.daverog.tripliser.mapping.model;

import org.daverog.tripliser.query.QuerySpecification;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Transient;


public class ValueMapping implements QuerySpecification, GenericMapping {

	@Attribute(required=false)
	private String comment;

	@Attribute(required = false)
	protected String query;

	@Attribute(required = false)
	protected String value;

	@Attribute(required = false)
	protected Boolean resource;

	@Attribute(required = false)
	private Boolean required;

	@Attribute(required = false)
	private String input;

	@Attribute(required = false)
	private String prepend;

	@Attribute(required = false)
	private String append;

	@Attribute(required = false)
	protected String dataType;

	@Attribute(required = false)
	private String validationRegex;

	@Transient
	private ResourceMapping resourceMapping;

	private String name;

	public String getQuery() {
		return query;
	}

	public String getValue() {
		return value;
	}

	public boolean isResource() {
		if (resource == null) return false;
		return resource;
	}

	public boolean isRequired() {
		if (required == null) {
			return resourceMapping.getGraphMapping().getMapping().isStrict();
		}

		return required;
	}

	public String getInputName() {
		return input;
	}

	public String getPrepend() {
		return prepend;
	}

	public String getAppend() {
		return append;
	}

	public String getDataType() {
		return dataType;
	}

	public boolean isXPath() {
		return query != null;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getValidationRegex() {
		return validationRegex;
	}

	public void setResourceMapping(ResourceMapping resourceMapping) {
		this.resourceMapping = resourceMapping;
	}

	public ResourceMapping getResourceMapping() {
		return resourceMapping;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
