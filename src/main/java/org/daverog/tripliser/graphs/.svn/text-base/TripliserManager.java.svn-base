package org.daverog.tripliser.graphs;

import java.util.Iterator;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.mapping.model.GenericMapping;
import org.daverog.tripliser.report.TripliserReporter;


public interface TripliserManager {

	public MutableTripleGraph getTripleGraph(GenericMapping genericMapping, Scope scope);

	public TripliserReporter getReporter(GenericMapping genericMapping, Scope scope);

	public void next(Scope scope);

	public GraphContext getGraphContext();

	public TripleGraph convertToTripleGraph() throws TripliserException;

	public Iterator<TripleGraph> processNestedIterator(
			Iterator<TripleGraph> nestedIterator, GenericMapping genericMapping, Scope scope);

}
