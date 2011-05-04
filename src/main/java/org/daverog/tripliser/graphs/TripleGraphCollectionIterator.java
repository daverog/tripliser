package org.daverog.tripliser.graphs;

import java.util.Iterator;

import org.daverog.tripliser.util.MetaIterator;


public class TripleGraphCollectionIterator extends MetaIterator<TripleGraph> {

	private final Iterator<TripleGraphCollection> tripleGraphCollections;

	public TripleGraphCollectionIterator(
			Iterator<TripleGraphCollection> tripleGraphCollections) {
				this.tripleGraphCollections = tripleGraphCollections;
	}

	@Override
	public boolean hasNextNestedIterator() {
		return tripleGraphCollections.hasNext();
	}

	@Override
	public Iterator<TripleGraph> nextNestedIterator() {
		return tripleGraphCollections.next().iterator();
	}


}
