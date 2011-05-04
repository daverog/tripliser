package org.daverog.tripliser.graphs;

import java.io.InputStream;
import java.util.Iterator;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.query.QueryableInputFactory;
import org.daverog.tripliser.report.TripliserReporterFactory;
import org.daverog.tripliser.triplisers.InputTripliserFactory;
import org.daverog.tripliser.triplisers.TripleGraphIterator;


public class TripleGraphIteratorFactory {

	private final Mapping mapping;
	private final QueryableInputFactory queryableInputFactory;
	private final MutableTripleGraphFactory mutableTripleGraphFactory;
	private final InputTripliserFactory inputTripliserFactory;
	private final TripliserReporterFactory tripliserReporterFactory;
	
	public TripleGraphIteratorFactory(Mapping mapping,
			QueryableInputFactory queryableInputFactory,
			MutableTripleGraphFactory mutableTripleGraphFactory,
			InputTripliserFactory inputTripliserFactory,
			TripliserReporterFactory tripliserReporterFactory) {
		this.mapping = mapping;
		this.queryableInputFactory = queryableInputFactory;
		this.mutableTripleGraphFactory = mutableTripleGraphFactory;
		this.inputTripliserFactory = inputTripliserFactory;
		this.tripliserReporterFactory = tripliserReporterFactory;
	}

	public Iterator<TripleGraph> createTripleGraphIterator(Scope mergeScope,
			Iterator<InputStream> inputs, GraphContext graphContext) {
		TripliserManager simpleTripleGraphFactory = new IterationTripliserManager(mergeScope, mutableTripleGraphFactory, tripliserReporterFactory, graphContext);
		return new TripleGraphIterator(inputs, simpleTripleGraphFactory, mapping, graphContext, inputTripliserFactory, queryableInputFactory);
	}

}
