package org.daverog.tripliser.mapping.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public class Tag {
	
	@Attribute
	private String name;
	
	@Attribute
	private String query;
	
	@Attribute(required=false)
	private Boolean required;

	private GraphMapping graphMapping;

	public String getName() {
		return name;
	}

	public String getQuery() {
		return query;
	}

	public void setGraphMapping(GraphMapping graphMapping) {
		this.graphMapping = graphMapping;
	}
	
	public GraphMapping getGraphMapping() {
		return graphMapping;
	}
	
	public boolean isRequired() {
		if (required == null) {
			return getGraphMapping().getMapping().isStrict();
		}
		return required;
	}
	
}
