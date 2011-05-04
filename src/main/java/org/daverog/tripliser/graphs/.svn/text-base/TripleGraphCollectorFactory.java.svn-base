package org.daverog.tripliser.graphs;

import java.io.InputStream;
import java.util.Iterator;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.query.QueryableInputFactory;
import org.daverog.tripliser.report.TripliserReporterFactory;
import org.daverog.tripliser.triplisers.InputTripliserFactory;
import org.daverog.tripliser.triplisers.TripleGraphIterator;


public class TripleGraphCollectorFactory {

	private final Mapping mapping;
	private final QueryableInputFactory queryableInputFactory;
	private final InputTripliserFactory inputTripliserFactory;
	private final MutableTripleGraphFactory mutableTripleGraphFactory;
	private final TripliserReporterFactory tripliserReporterFactory;

	public TripleGraphCollectorFactory(Mapping mapping, QueryableInputFactory queryableInputFactory,
			MutableTripleGraphFactory mutableTripleGraphFactory, TripliserReporterFactory tripliserReporterFactory,
			InputTripliserFactory inputTripliserFactory){
		this.mapping = mapping;
		this.queryableInputFactory = queryableInputFactory;
		this.mutableTripleGraphFactory = mutableTripleGraphFactory;
		this.tripliserReporterFactory = tripliserReporterFactory;
		this.inputTripliserFactory = inputTripliserFactory;
	}

	public TripleGraphCollector create(Iterator<InputStream> inputs, GraphContext graphContext, Scope mergeScope) {
		TripleGraphCollector tripleGraphCollectionMap = new TripleGraphCollector(
				mutableTripleGraphFactory,
				tripliserReporterFactory,
				graphContext,
				mergeScope);
		TripleGraphIterator tripleGraphIterator = new TripleGraphIterator(
				inputs, tripleGraphCollectionMap, mapping, graphContext, inputTripliserFactory, queryableInputFactory);

		tripleGraphIterator.flush();
		return tripleGraphCollectionMap;
	}

}
