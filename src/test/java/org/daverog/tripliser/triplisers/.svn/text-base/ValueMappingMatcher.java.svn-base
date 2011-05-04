package org.daverog.tripliser.triplisers;

import org.daverog.tripliser.mapping.model.PropertyMapping;
import org.daverog.tripliser.mapping.model.ValueMapping;
import org.mockito.ArgumentMatcher;


public class ValueMappingMatcher extends ArgumentMatcher<ValueMapping> {
	
	private String name;
	private String query;
	private String text;
	
	public ValueMappingMatcher(String name, String query){
		this(name, query, true);
	}

	public ValueMappingMatcher(String name, String queryOrText, boolean isQuery){
		this.name = name;
		if (isQuery) this.query = queryOrText;
		else this.text = queryOrText;
	}
	
	@Override
	public boolean matches(Object argument) {
		ValueMapping valueMapping = (ValueMapping)argument;
		
		String mappingName = null;
		if (valueMapping instanceof PropertyMapping) mappingName = ((PropertyMapping)valueMapping).getName();
		
		if (name != null && !name.equals(mappingName)) return false;
		if (query != null && !query.equals(valueMapping.getQuery())) return false;
		if (text != null && !text.equals(valueMapping.getQuery())) return false;
		
		return true;
	}
};
