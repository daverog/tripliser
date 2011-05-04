package org.daverog.tripliser.mapping.model;

import java.util.ArrayList;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.util.List;

import org.daverog.tripliser.mapping.model.PropertyMapping;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.daverog.tripliser.mapping.model.ValueMapping;


public class ResourceMappingBuilder {
	
	private List<PropertyMapping> properties;
	private ValueMapping about;
	private String query;
	private boolean required = true;
	private String name;
	
	public ResourceMapping toResourceMapping(){
		ResourceMapping resourceMapping = new ResourceMapping();
		
		setInternalState(resourceMapping, "properties", properties);
		setInternalState(resourceMapping, "about", about);
		setInternalState(resourceMapping, "required", required);
		setInternalState(resourceMapping, "query", query);
		setInternalState(resourceMapping, "name", name);
		
		return resourceMapping;
	}
	
	public ResourceMappingBuilder() {
		properties = new ArrayList<PropertyMapping>();
		query = "//root";
	}
	
	public ResourceMappingBuilder addProperty(String name, String query) {
		properties.add(new ValueMappingBuilder().name(name).query(query).toPropertyMapping());
		return this;
	}

	public ResourceMappingBuilder addProperty(PropertyMapping valueMapping) {
		properties.add(valueMapping);
		return this;
	}
	
	public ResourceMappingBuilder about(String about) {
		this.about = new ValueMappingBuilder().query(about).toValueMapping();
		return this;
	}
	
	public ResourceMappingBuilder about(ValueMapping about) {
		this.about = about;
		return this;
	}
	
	public ResourceMappingBuilder query(String query) {
		this.query = query;
		return this;
	}

	public ResourceMappingBuilder required(boolean required) {
		this.required = required;
		return this;
	}

	public ResourceMappingBuilder name(String name) {
		this.name = name;
		return this;
	}


}
