package org.daverog.tripliser.triplisers;

import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.Queryable;

public class GraphNodeTripliserFactory {

	private final ResourceTripliserFactory resourceTripliserFactory;

	public GraphNodeTripliserFactory(
			ResourceTripliserFactory resourceTripliserFactory) {
		this.resourceTripliserFactory = resourceTripliserFactory;
	}

	public GraphNodeTripliser createGraphNodeTripliser(
			Queryable input, Node graphNode, GraphMapping graphMapping, TripliserManager tripliserManager) {
		return new GraphNodeTripliser(input, graphNode, graphMapping, tripliserManager, resourceTripliserFactory);
	}

}
