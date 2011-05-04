package org.daverog.tripliser.triplisers;

import java.util.Iterator;

import org.daverog.tripliser.exception.IgnoredGraphMappingException;
import org.daverog.tripliser.exception.InvalidGraphMappingException;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.query.QueryService;
import org.daverog.tripliser.query.Queryable;


public class GraphTripliserFactory {

	private final GraphNodeTripliserFactory graphNodeTripliserFactory;
	private final GraphTagger graphTagger;
	private final QueryService queryService;

	public GraphTripliserFactory(
			GraphNodeTripliserFactory graphNodeTripliserFactory, QueryService queryService, GraphTagger graphTagger) {
		this.graphNodeTripliserFactory = graphNodeTripliserFactory;
		this.queryService = queryService;
		this.graphTagger = graphTagger;
	}

	public Iterator<TripleGraph> createGraphTripliser(Queryable input,
			GraphMapping graphMapping, TripliserManager tripliserManager) throws InvalidGraphMappingException, IgnoredGraphMappingException {
		return new GraphTripliser(input, graphMapping, graphNodeTripliserFactory, tripliserManager, queryService, graphTagger);
	}

}
