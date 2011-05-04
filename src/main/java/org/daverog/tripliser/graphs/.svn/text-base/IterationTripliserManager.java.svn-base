package org.daverog.tripliser.graphs;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.lang.NotImplementedException;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.ScopeException;
import org.daverog.tripliser.mapping.model.GenericMapping;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.TripliserReporterFactory;


public class IterationTripliserManager extends TripliserManagerBase {

	private final Scope mergeScope;

	private MutableTripleGraph currentTripleGraph;
	
	public IterationTripliserManager(Scope mergeScope, MutableTripleGraphFactory mutableTripleGraphFactory, TripliserReporterFactory tripliserReporterFactory, GraphContext graphContext) {
		super(mutableTripleGraphFactory, tripliserReporterFactory, graphContext);
		this.mergeScope = mergeScope;
	}

	@Override
	public MutableTripleGraph getTripleGraph(GenericMapping genericMapping, Scope scope) throws ScopeException {
		if (scope.ordinal() <= mergeScope.ordinal()) {
			if (currentTripleGraph == null) {
				currentTripleGraph = mutableTripleGraphFactory.createGraph(genericMapping.getName(), 
						tripliserReporterFactory.createGraphReporter(), graphContext);
			}
			return currentTripleGraph;
		}
		
		throw new ScopeException("Cannot obtain a graph when in a scope above " + mergeScope.name());
	}

	@Override
	public TripliserReporter getReporter(GenericMapping genericMapping, Scope scope) throws ScopeException {
		if (scope.ordinal() <= mergeScope.ordinal()) {
			return getTripleGraph(genericMapping, scope).getTripliserReporter();
		}
		
		return tripliserReporterFactory.getReporterForScope(scope);
	}

	@Override
	public TripleGraph convertToTripleGraph() {
		throw new NotImplementedException("An iteration of triple graphs cannot produce a single merged triple graph");
	}

	@Override
	public void next(Scope scope) {
		if (scope == mergeScope) {
			currentTripleGraph = null;
		}	
	}

	@Override
	public Iterator<TripleGraph> processNestedIterator(
			Iterator<TripleGraph> nestedIterator, GenericMapping genericMapping, Scope scope) {
		if(scope == mergeScope){
			while(nestedIterator.hasNext()) nestedIterator.next();
			return Arrays.asList((TripleGraph) getTripleGraph(genericMapping, scope)).iterator();
		}
		return nestedIterator;
	}
	
}
