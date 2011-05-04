package org.daverog.tripliser.triplisers;

import org.daverog.tripliser.mapping.model.PropertyMapping;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.mockito.ArgumentMatcher;



public class ResourceMappingMatcher extends ArgumentMatcher<ResourceMapping> {
	
	private String name;
	private String query;
	private String text;
	
	public ResourceMappingMatcher(String query){
		this(null, query, true);
	}

	public ResourceMappingMatcher(String name, String queryOrText, boolean isQuery){
		this.name = name;
		if (isQuery) this.query = queryOrText;
		else this.text = queryOrText;
	}
	
	@Override
	public boolean matches(Object argument) {
		ResourceMapping resourceMapping = (ResourceMapping)argument;
		
		String mappingName = null;
		if (resourceMapping.getAbout() instanceof PropertyMapping) mappingName = ((PropertyMapping)resourceMapping.getAbout()).getName();
		
		if (name != null && !name.equals(mappingName)) return false;
		if (query != null && !query.equals(resourceMapping.getAbout().getQuery())) return false;
		if (text != null && !text.equals(resourceMapping.getAbout().getQuery())) return false;
		
		return true;
	}
};