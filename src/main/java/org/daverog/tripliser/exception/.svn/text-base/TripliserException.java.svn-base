package org.daverog.tripliser.exception;

import org.daverog.tripliser.graphs.TripleGraph;

public class TripliserException extends Exception {
	
	private final TripleGraph graph;

	public TripliserException(String message, TripleGraph graph) {
		super(message);
		this.graph = graph;
	}

	public TripliserException(String message, Throwable cause, TripleGraph graph) {
		super(message, cause);
		this.graph = graph;
	}

	public TripliserException(String message) {
		super(message);
		this.graph = null;
	}

	public TripleGraph getTripleGraph() {
		return graph;
	}

}
