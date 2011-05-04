package org.daverog.tripliser.graphs;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.mapping.model.Constant;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.mapping.model.Namespace;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.query.QueryableInputFactory;



public class GraphContextFactory {
	
	private final QueryableInputFactory queryableInputFactory;
	
	public GraphContextFactory(QueryableInputFactory queryableInputFactory){
		this.queryableInputFactory = queryableInputFactory;
	}
	
	public GraphContext createGraphContext(Mapping mapping, Map<String, InputStream> supportingInputs) throws TripliserException {
		Map<String, Queryable> supportingInputMap = loadSupportingInputs(supportingInputs);
		
		Map<String, String> constants = new HashMap<String, String>();
		Map<String, String> namespaces = new HashMap<String, String>();

		if (mapping.getConstants() != null) {
			for (Constant constant : mapping.getConstants()) constants.put(constant.getName(), constant.getValue());
		}
		
		for(Namespace namespace : mapping.getNamespaces()) {
			namespaces.put(namespace.getPrefix(), namespace.getUrl());
		}
		
		return new GraphContext(constants, namespaces, supportingInputMap);
	}
	
	private Map<String, Queryable> loadSupportingInputs(
			Map<String, InputStream> supportingInputs) throws TripliserException {
		Map<String, Queryable> supportingInputMap = new HashMap<String, Queryable>();
		
		if (supportingInputs != null) {
			for (String inputName : supportingInputs.keySet()) {
				supportingInputMap.put(inputName, queryableInputFactory.createSaxonXPathTripliserDocument(inputName, supportingInputs.get(inputName)));
			}
		}
		
		return supportingInputMap;
	}

}
