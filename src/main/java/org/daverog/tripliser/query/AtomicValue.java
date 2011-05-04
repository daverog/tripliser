package org.daverog.tripliser.query;

/**
 * A wrapper for an atomic value, such as an integer or string, returned from
 * a query.
 */
public interface AtomicValue extends Item {

	public Object getValue();

}
