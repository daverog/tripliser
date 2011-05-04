package org.daverog.tripliser.mapping.model;

import java.util.List;

import org.daverog.tripliser.query.QuerySpecification;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;


@Root
public class GraphMapping implements GenericMapping, QuerySpecification {

	@Attribute(required = false)
	private String query;
	
	@ElementList(inline=true, entry="resource")
	private List<ResourceMapping> resources;
	
	@ElementList(required=false)
	private List<Tag> tags;

	@Attribute(required=false)
	private String name;

	@Attribute(required=false)
	private String comment;
	
	@Transient
	private Mapping mapping;

	@Attribute(required=false)
	private Boolean required;
	
	public String getQuery() {
		return query;
	}

	public List<ResourceMapping> getResourceMappings() {
		for (ResourceMapping resourceMapping: resources) {
			resourceMapping.setGraphMapping(this);
		}
		
		return resources;
	}

	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

	public Mapping getMapping() {
		return mapping;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Tag> getTags() {
		if (tags == null) return null;
		
		for (Tag tag: tags) {
			tag.setGraphMapping(this);
		}
		
		return tags;
	}
	
	public boolean isRequired() {
		if (required == null) {
			return getMapping().isStrict();
		}
		return required;
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
