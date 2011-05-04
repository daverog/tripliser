package org.daverog.tripliser.triplisers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.IgnoredGraphMappingException;
import org.daverog.tripliser.exception.InvalidGraphMappingException;
import org.daverog.tripliser.exception.ScopeException;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.QueryException;
import org.daverog.tripliser.query.QueryService;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.util.MetaIterator;


public class GraphTripliser extends MetaIterator<TripleGraph> {
	
	private final Queryable input;
	private final GraphMapping graphMapping;
	private final GraphNodeTripliserFactory graphNodeTripliserFactory;
	private final GraphTagger graphTagger;
	private final TripliserManager tripliserManager;
	
	private Iterator<Node> graphNodes;

	public GraphTripliser(Queryable input, GraphMapping graphMapping, GraphNodeTripliserFactory graphNodeTripliserFactory, 
			TripliserManager tripliserManager, QueryService queryService, GraphTagger graphTagger) 
				throws InvalidGraphMappingException, IgnoredGraphMappingException {
		this.input = input;
		this.graphMapping = graphMapping;
		this.graphNodeTripliserFactory = graphNodeTripliserFactory;
		this.tripliserManager = tripliserManager;
		this.graphTagger = graphTagger;
		
		List<Node> graphNodeList = new ArrayList<Node>();
		graphNodeList.add(null);
		if (graphMapping.getQuery() != null) {
			try {
				graphNodeList = queryService.getNodes(input, graphMapping, null, tripliserManager.getGraphContext());
			} catch (QueryException e) {
				throw new InvalidGraphMappingException("Invalid graph query", e);
			}
			if (graphNodeList.isEmpty()) {
				if (graphMapping.isRequired()) throw new InvalidGraphMappingException("A required graph's query must return at least one result");
				else throw new IgnoredGraphMappingException("A non-required graph's query returned no results");
			}
		}
		
		graphNodes = graphNodeList.iterator();
	}

	@Override
	public boolean hasNextNestedIterator() {
		return graphNodes.hasNext();
	}

	@Override
	public Iterator<TripleGraph> nextNestedIterator() {
		tripliserManager.next(Scope.GRAPH);
		Node graphNode = graphNodes.next();
		try {
			graphTagger.tagGraph(input, tripliserManager.getTripleGraph(graphMapping, Scope.GRAPH), graphMapping, graphNode);
		} catch(ScopeException e) {
			tripliserManager.getReporter(graphMapping, Scope.GRAPH).addMessage(new ReportEntry(e, Status.WARNING, Scope.GRAPH, graphMapping));
		}
		
		return tripliserManager.processNestedIterator(
				graphNodeTripliserFactory.createGraphNodeTripliser(input, graphNode, graphMapping, tripliserManager),
				graphMapping, Scope.GRAPH);
	}

}
