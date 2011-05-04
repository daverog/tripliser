package org.daverog.tripliser.mapping.model;

import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.daverog.tripliser.mapping.model.Constant;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.mapping.model.Namespace;
import org.daverog.tripliser.mapping.model.PropertyMapping;
import org.daverog.tripliser.mapping.model.ResourceMapping;

public class MappingBuilder {
	
	private HashMap<String, List<PropertyMapping>> properties;
	private List<Namespace> namespaces;
	private List<Constant> constants;
	private List<GraphMapping> graphMappings = new ArrayList<GraphMapping>();;
	private String query;
	
	public Mapping toMapping(){
		Mapping mapping = new Mapping();
		GraphMapping graphMapping = new GraphMapping();
		
		if (!properties.isEmpty()) graphMappings.add(graphMapping);
		
		setInternalState(mapping, "namespaces", namespaces);
		setInternalState(mapping, "constants", constants);
		setInternalState(mapping, "graphs", graphMappings);
		setInternalState(graphMapping, "query", query);
		setInternalState(graphMapping, "resources", new ArrayList<ResourceMapping>());
		
		for(String key : properties.keySet()){
			ResourceMapping resourceMapping = new ResourceMapping();
			setInternalState(resourceMapping, "name", key);
			setInternalState(resourceMapping, "properties", properties.get(key));
			
			graphMapping.getResourceMappings().add(resourceMapping);
		}
		
		return mapping;
	}
	
	public MappingBuilder() {
		properties = new LinkedHashMap<String, List<PropertyMapping>>();
		namespaces = new ArrayList<Namespace>();
		
		query = "";
	}
	
	public MappingBuilder addGraphMapping(GraphMapping graphMapping) {
		graphMappings.add(graphMapping);
		
		return this;
	}
	
	public MappingBuilder addProperty(String resourceName, String name, String query) {
		return addProperty(resourceName, new ValueMappingBuilder().name(name).query(query).toPropertyMapping());
	}

	public MappingBuilder addProperty(String resourceName, PropertyMapping valueMapping) {	
		List<PropertyMapping> propertyList = properties.get(resourceName);
		
		if (propertyList == null) {
			propertyList = new ArrayList<PropertyMapping>();
			properties.put(resourceName, propertyList);
		}
		
		propertyList.add(valueMapping);
		
		return this;
	}

	public MappingBuilder addNamespace(String prefix, String url) {
		Namespace namespace = new NamespaceBuilder().prefix(prefix).url(url).toNamespace();
		
		namespaces.add(namespace);
		
		return this;
	}

	public MappingBuilder addConstant(String name, String value) {
		Constant constant = new ConstantBuilder().name(name).value(value).toConstant();
		
		if (constants == null) constants = new ArrayList<Constant>();
		constants.add(constant);
		
		return this;
	}
	
	public MappingBuilder query(String query){
		this.query = query;
		
		return this;
	}

	public MappingBuilder addNamespace(Namespace namespace) {
		namespaces.add(namespace);
		
		return this;
	}

}
