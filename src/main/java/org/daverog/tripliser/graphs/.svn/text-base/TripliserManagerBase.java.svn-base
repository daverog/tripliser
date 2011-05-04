package org.daverog.tripliser.graphs;

import org.daverog.tripliser.report.TripliserReporterFactory;


public abstract class TripliserManagerBase implements TripliserManager {
	
	protected final MutableTripleGraphFactory mutableTripleGraphFactory;
	protected final GraphContext graphContext;
	protected final TripliserReporterFactory tripliserReporterFactory;
	
	public TripliserManagerBase(MutableTripleGraphFactory mutableTripleGraphFactory, 
			TripliserReporterFactory tripliserReporterFactory, GraphContext graphContext){
		this.mutableTripleGraphFactory = mutableTripleGraphFactory;
		this.tripliserReporterFactory = tripliserReporterFactory;
		this.graphContext = graphContext;
	}

	@Override
	public GraphContext getGraphContext() {
		return graphContext;
	}

}
