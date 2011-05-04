package org.daverog.tripliser.mapping.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.daverog.tripliser.Constants;
import org.daverog.tripliser.exception.TripliserException;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.PersistenceException;
import org.simpleframework.xml.core.Validate;


@Root
public class Mapping implements GenericMapping {
	
	@Attribute(required=false)
	private boolean strict = true;
	
	@ElementList(required = false)
	private List<Constant> constants;
	
	@ElementList
	private List<Namespace> namespaces;
	
	@ElementList(required=false)
	private List<GraphMapping> graphs;
	
	@Element(required=false)
	private GraphMapping graph;

	public List<Constant> getConstants() {
		return constants;
	}

	public List<Namespace> getNamespaces() {
		return namespaces;
	}
	
	public boolean isStrict() {
		return strict;
	}
	
	public List<GraphMapping> getGraphMappings() throws TripliserException {
		if (graphs == null) {
			if (graph == null) throw new TripliserException("No graphs defined", null);
			graph.setMapping(this);
			return Arrays.asList(graph);
		}
		
		for (GraphMapping graph : graphs) {
			graph.setMapping(this);
		}
		
		return graphs;
	}

	public Map<String, String> getNamespaceMap() {
		Map<String, String> namespaceMap = new HashMap<String, String>();
		for (Namespace namespace : getNamespaces()) {
			namespaceMap.put(namespace.getPrefix(), namespace.getUrl());
			
			if (namespace.isDefault()) {
				namespaceMap.put("", namespace.getUrl());
			}
		}
		return namespaceMap;
	}

	@Override
	public String getName() {
		return Constants.DEFAULT_MAPPING_NAME;
	}
	
	@Validate
	public void validateNamespaces() throws PersistenceException {
		for (Namespace namespace : namespaces) {
			try {
				new URI(namespace.getUrl());
			} catch (URISyntaxException e) {
				throw new PersistenceException("Invalid namespace URI '%s'", namespace.getUrl());
			}
		}
	}
	
}
