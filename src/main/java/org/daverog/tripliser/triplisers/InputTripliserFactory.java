package org.daverog.tripliser.triplisers;

import java.util.Iterator;

import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.query.Queryable;


public class InputTripliserFactory {

	private final GraphTripliserFactory graphTripliserFactory;

	public InputTripliserFactory(GraphTripliserFactory graphTripliserFactory) {
		this.graphTripliserFactory = graphTripliserFactory;
	}

	public Iterator<TripleGraph> createInputTripliser(Queryable input, Mapping mapping, TripliserManager tripliserManager) throws TripliserException {
		return new InputTripliser(input, mapping, tripliserManager, graphTripliserFactory);
	}

}
