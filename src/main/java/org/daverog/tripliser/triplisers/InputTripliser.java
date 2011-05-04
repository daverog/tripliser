package org.daverog.tripliser.triplisers;

import java.util.ArrayList;
import java.util.Iterator;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.IgnoredGraphMappingException;
import org.daverog.tripliser.exception.InvalidGraphMappingException;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.util.MetaIterator;


public class InputTripliser extends MetaIterator<TripleGraph> {
	
	private final TripliserManager tripliserManager;
	private final Queryable input;
	private final GraphTripliserFactory graphTripliserFactory;

	private Iterator<GraphMapping> graphMappings;
	
	public InputTripliser(Queryable input, Mapping mapping, TripliserManager tripliserManager, GraphTripliserFactory graphTripliserFactory) throws TripliserException{
		this.input = input;
		this.tripliserManager = tripliserManager;
		this.graphTripliserFactory = graphTripliserFactory;
		
		graphMappings = mapping.getGraphMappings().iterator();
	}

	@Override
	public boolean hasNextNestedIterator() {
		return graphMappings.hasNext();
	}

	@Override
	public Iterator<TripleGraph> nextNestedIterator() {
		GraphMapping graphMapping = graphMappings.next();
		TripliserReporter reporter = tripliserManager.getReporter(graphMapping, Scope.GRAPH_MAPPING);
		try {
			return graphTripliserFactory.createGraphTripliser(input, graphMapping, tripliserManager);
		} catch(IgnoredGraphMappingException e) {
			reporter.addMessage(new ReportEntry(e, Status.WARNING, Scope.GRAPH_MAPPING, graphMapping));
		} catch(InvalidGraphMappingException e) {
			reporter.addMessage(new ReportEntry(e, Status.FAILURE, Scope.GRAPH_MAPPING, graphMapping));
		} catch(Exception e) {
			reporter.addMessage(new ReportEntry(e, Status.ERROR, Scope.GRAPH_MAPPING, graphMapping));
		}
		return new ArrayList<TripleGraph>().iterator();
	}

}
