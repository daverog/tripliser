package org.daverog.tripliser.graphs;

import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.report.TripliserReport;


public interface TripleGraphCollection extends Iterable<TripleGraph> {

	/**
	 * Obtain a triple graph a based on its name provided in the
	 * mapping file with the <code>name</code> attribute.
	 *
	 * @param name The name of the graph
	 * @return The required triple graph
	 * @throws TripliserException If the triple graph was not found
	 */
	public TripleGraph getTripleGraphByName(String name) throws TripliserException;

	/**
	 * Gets the overall report for the collection of triple graphs. Reports are
	 * also accessible from each individual triple graph.
	 */
	public TripliserReport getReport();

}
