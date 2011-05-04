package org.daverog.tripliser.graphs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.ScopeException;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.mapping.model.GenericMapping;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.daverog.tripliser.report.TripliserReport;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.TripliserReporterFactory;


public class TripleGraphCollector extends TripliserManagerBase implements TripleGraphCollection {

	private Map<String, TripleGraph> graphs;
	private List<TripliserReporter> reporters;

	private Scope mergeScope;
	private int index;

	public TripleGraphCollector(MutableTripleGraphFactory mutableTripleGraphFactory,
			TripliserReporterFactory tripliserReporterFactory,
			GraphContext graphContext,
			Scope mergeScope){
		super(mutableTripleGraphFactory, tripliserReporterFactory, graphContext);
		this.mergeScope = mergeScope;

		graphs = new LinkedHashMap<String, TripleGraph>();
		reporters = new ArrayList<TripliserReporter>();
		index = 1;
	}

	@Override
	public void next(Scope scope){
		if (scope == Scope.MAPPING || scope == Scope.GRAPH_MAPPING || scope == Scope.RESOURCE_MAPPING)
			throw new ScopeException("Cannot call next for " + scope.name() + " scope");
		if (scope == mergeScope) {
			index++;
		}
	}

	@Override
	public MutableTripleGraph getTripleGraph(GenericMapping genericMapping, Scope scope) throws ScopeException {
		String name = getName(genericMapping, scope);
		if (graphs.get(name) == null) {
			TripliserReporter reporter = tripliserReporterFactory.createGraphReporter();
			if (!reporters.contains(reporter)) reporters.add(reporter);
			MutableTripleGraph tripleGraph = mutableTripleGraphFactory.createGraph(name,
				reporter, graphContext);

			graphs.put(name, tripleGraph);
			return tripleGraph;
		} else {
			return (MutableTripleGraph)graphs.get(name);
		}
	}

	private String getName(GenericMapping genericMapping, Scope scope) {
		if (scope.ordinal() > mergeScope.ordinal()) {
			throw new ScopeException("A triple graph does not exist above the scope of " + mergeScope.name());
		}

		String graphMappingName = null;
		String resourceMappingName = null;

		if (genericMapping instanceof ResourceMapping) {
			graphMappingName = ((ResourceMapping)genericMapping).getGraphMapping().getName();
			resourceMappingName = ((ResourceMapping)genericMapping).getName();

		} else if (genericMapping instanceof GraphMapping) {
			graphMappingName = ((GraphMapping)genericMapping).getName();
		}

		switch (mergeScope) {
			case GRAPH_MAPPING :
				if (graphMappingName != null) return graphMappingName;
			case RESOURCE_MAPPING :
				if (resourceMappingName != null) return resourceMappingName;
			default : return index + "";
		}
	}

	@Override
	public TripliserReporter getReporter(GenericMapping genericMapping, Scope scope) {
		try {
			return getTripleGraph(genericMapping, scope).getTripliserReporter();
		} catch (ScopeException e) {
			TripliserReporter reporter = tripliserReporterFactory
				.getReporterForScope(scope);
			if (!reporters.contains(reporter)) reporters.add(reporter);
			return reporter;
		}

	}

	@Override
	public Iterator<TripleGraph> iterator() {
		return graphs.values().iterator();
	}


	@Override
	public TripleGraph getTripleGraphByName(String name) throws TripliserException {
		TripleGraph tripleGraph = graphs.get(name);

		if (tripleGraph == null) {
			throw new TripliserException("The graph '" + name + "' was not found");
		}

		return tripleGraph;
	}

	public List<TripliserReporter> getReporters() {
		return reporters;
	}

	@Override
	public TripliserReport getReport() {
		return new CompositeReport(reporters);
	}

	@Override
	public TripleGraph convertToTripleGraph() throws TripliserException {
		if (mergeScope != Scope.MAPPING) throw new TripliserException(
				"Only if merge scope is set to MAPPING can a conversion to a single triple graph be made");

		if (graphs.isEmpty()) throw new TripliserException("No graphs exist");

		return graphs.values().iterator().next();
	}

	@Override
	public Iterator<TripleGraph> processNestedIterator(
			Iterator<TripleGraph> subIterator, GenericMapping genericMapping, Scope graph) {
		return subIterator;
	}

}
