package org.daverog.tripliser.util;

import java.util.Iterator;

import org.apache.commons.lang.NotImplementedException;


public abstract class MetaIterator<T> implements Iterator<T> {
	
	private Iterator<T> currentNestedIterator;
	
	public boolean hasNext() {
		if (currentNestedIterator != null && currentNestedIterator.hasNext()) return true;
		if (currentNestedIterator != null && !currentNestedIterator.hasNext()) return loadNextNestedIteratorAndCheckHasNext();
		if (currentNestedIterator == null && hasNextNestedIterator()) return loadNextNestedIteratorAndCheckHasNext();
		return false;
	}
	
	public T next() {
		if (!hasNext()) throw new RuntimeException("Cannot call next if no more items exist in the iterator");
		if (currentNestedIterator == null) throw new RuntimeException("Cannot call next if nested iterator is null");
		return currentNestedIterator.next();
	}

	private boolean loadNextNestedIteratorAndCheckHasNext() {
		if (!hasNextNestedIterator()) return false;
		currentNestedIterator = nextNestedIterator();
		if (currentNestedIterator == null) throw new RuntimeException("A null iterator was returned");
		if (currentNestedIterator.hasNext()) return true;
		return loadNextNestedIteratorAndCheckHasNext();
	}

	public abstract boolean hasNextNestedIterator();

	public abstract Iterator<T> nextNestedIterator();
	
	@Override
	public void remove() {
		throw new NotImplementedException();
	}

}
