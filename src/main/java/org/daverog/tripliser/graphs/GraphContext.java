package org.daverog.tripliser.graphs;

import java.util.Iterator;
import java.util.Map;

import org.daverog.tripliser.query.Queryable;


public class GraphContext {

	private Map<String, String> constants;
	private Map<String, String> namespaces;
	private Map<String, Queryable> supportingInputMap;

	public GraphContext(Map<String, String> constants,
			Map<String, String> namespaces, Map<String, Queryable> supportingInputMap) {
				this.constants = constants;
				this.namespaces = namespaces;
				this.supportingInputMap = supportingInputMap;
	}

	public String getConstant(String name) {
		return constants.get(name);
	}

	public String getNamespace(String prefix) {
		return namespaces.get(prefix);
	}

	public Queryable getSupportingInput(String name) {
		return supportingInputMap.get(name);
	}

	public Iterator<String> getConstantNames() {
		return constants.keySet().iterator();
	}

	public Iterator<String> getNamespacePrefixes() {
		return namespaces.keySet().iterator();
	}

}
