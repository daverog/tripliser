package org.daverog.tripliser.graphs;

import java.util.HashMap;
import java.util.Map;

import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.query.Queryable;


public class GraphContextBuilder {

	private Map<String, Queryable> supportingInputMap;
	private Map<String, String> namespaces;
	private Map<String, String> constants;

	public GraphContextBuilder() {
		this.supportingInputMap = new HashMap<String, Queryable>();
		this.namespaces = new HashMap<String, String>();;
		this.constants = new HashMap<String, String>();;
	}

	public GraphContext toGraphContext() {
		return new GraphContext(constants, namespaces, supportingInputMap);
	}

	public GraphContextBuilder addNamespace(String prefix, String uri) {
		namespaces.put(prefix, uri);
		return this;
	}

	public GraphContextBuilder addConstant(String name, String value) {
		constants.put(name, value);
		return this;
	}

	public GraphContextBuilder addSupportingInput(String name,
			Queryable supportingInput) {
		supportingInputMap.put(name, supportingInput);
		return this;
	}

}
