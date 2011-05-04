package org.daverog.tripliser.mapping.model;

import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.mapping.model.PropertyMapping;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.daverog.tripliser.mapping.model.Tag;

public class GraphMappingBuilder {

	private String name;
	private String query;
	private HashMap<String, List<PropertyMapping>> properties;
	private List<Tag> tags;
	private boolean required;

	public GraphMapping toGraphMapping(){
		GraphMapping graphMapping = new GraphMapping();

		setInternalState(graphMapping, "resources", new ArrayList<ResourceMapping>());
		setInternalState(graphMapping, "query", query);
		setInternalState(graphMapping, "required", required);
		setInternalState(graphMapping, "name", name);
		setInternalState(graphMapping, "tags", tags);

		for(String key : properties.keySet()){
			ResourceMapping resourceMapping = new ResourceMapping();
			setInternalState(resourceMapping, "about", new ValueMappingBuilder().toValueMapping());
			setInternalState(resourceMapping, "name", key);
			setInternalState(resourceMapping, "properties", properties.get(key));

			graphMapping.getResourceMappings().add(resourceMapping);
		}

		for(Tag tag : tags) {
			tag.setGraphMapping(graphMapping);
		}

		graphMapping.setMapping(new MappingBuilder().toMapping());

		return graphMapping;
	}

	public GraphMappingBuilder() {
		query = "//root";

		properties = new LinkedHashMap<String, List<PropertyMapping>>();
		tags = new ArrayList<Tag>();
	}

	public GraphMappingBuilder query(String query) {
		this.query = query;
		return this;
	}

	public GraphMappingBuilder addProperty(String resourceName, String name, String query) {
		return addProperty(resourceName, new ValueMappingBuilder().name(name).query(query).toPropertyMapping());
	}

	public GraphMappingBuilder addProperty(String docRoot, PropertyMapping valueMapping) {
		List<PropertyMapping> propertyList = properties.get(docRoot);

		if (propertyList == null) {
			propertyList = new ArrayList<PropertyMapping>();
			properties.put(docRoot, propertyList);
		}

		propertyList.add(valueMapping);

		return this;
	}

	public GraphMappingBuilder name(String name) {
		this.name = name;
		return this;
	}

	public GraphMappingBuilder addTag(String tagName, String tagQuery) {
		Tag tag = new Tag();
		setInternalState(tag, "name", tagName);
		setInternalState(tag, "query", tagQuery);
		tags.add(tag);
		return this;
	}

	public GraphMappingBuilder required(boolean required) {
		this.required = required;
		return this;
	}

	public GraphMappingBuilder addTag(Tag tag) {
		tags.add(tag);
		return this;
	}

}
