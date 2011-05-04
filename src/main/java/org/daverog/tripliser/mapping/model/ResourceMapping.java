package org.daverog.tripliser.mapping.model;

import java.util.List;

import org.daverog.tripliser.query.QuerySpecification;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Transient;


public class ResourceMapping implements GenericMapping, QuerySpecification {

	@Attribute(required=false)
	private String query;

	@Attribute(required=false)
	private String input;

	private String name;

	@Attribute(required=false)
	private String comment;

	@Attribute(required=false)
	private Boolean required;

	@ElementList
	private List<PropertyMapping> properties;

	@Element
	private ValueMapping about;

	@Transient
	private GraphMapping graph;

	public String getQuery() {
		return query;
	}

	public String getComment() {
		return comment;
	}

	public List<PropertyMapping> getProperties() {
		for (PropertyMapping propertyMapping: properties) {
			propertyMapping.setResourceMapping(this);
		}

		return properties;
	}

	public ValueMapping getAbout() {
		about.setResourceMapping(this);

		return about;
	}

	public boolean isRequired() {
		if (required == null) {
			return graph.getMapping().isStrict();
		}
		return required;
	}

	public void setGraphMapping(GraphMapping graphMapping) {
		this.graph = graphMapping;
	}

	public GraphMapping getGraphMapping() {
		return graph;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		about.setName(name + ":about");
	}

	@Override
	public String getInputName() {
		return null;
	}

	@Override
	public String getValue() {
		return null;
	}

}
