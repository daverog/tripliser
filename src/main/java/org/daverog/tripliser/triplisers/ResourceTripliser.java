package org.daverog.tripliser.triplisers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.IgnoredResourceMappingException;
import org.daverog.tripliser.exception.InvalidResourceMappingException;
import org.daverog.tripliser.graphs.MutableTripleGraph;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.QueryException;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.ReportEntry.Status;


public class ResourceTripliser implements Iterator<TripleGraph> {

	private final Queryable input;
	private final ResourceMapping resourceMapping;
	private final TripliserManager tripliserManager;
	private final ResourceNodeTripliser resourceNodeTripliser;

	private Iterator<Node> nodeIterator;

	public ResourceTripliser(Queryable input, Node graphNode, ResourceMapping resourceMapping, TripliserManager tripliserManager, ResourceNodeTripliser resourceNodeTripliser) throws IgnoredResourceMappingException, InvalidResourceMappingException {
		this.input = input;
		this.resourceMapping = resourceMapping;
		this.tripliserManager = tripliserManager;
		this.resourceNodeTripliser = resourceNodeTripliser;

		List<Node> nodes = new ArrayList<Node>();
		String query = resourceMapping.getQuery();

		if (query == null) nodes.add(graphNode);
		else {
			try {
				nodes = input.readNodeList(graphNode, query);
			} catch (QueryException e) {
				throw new InvalidResourceMappingException("'" + query + "' is not a valid query", e);
			}
		}

		if (nodes.isEmpty()) throw new IgnoredResourceMappingException("No node was found in the input to match the query " + query);
		nodeIterator = nodes.iterator();
	}

	@Override
	public boolean hasNext() {
		return nodeIterator.hasNext();
	}

	@Override
	public TripleGraph next() {
		tripliserManager.next(Scope.RESOURCE);
		TripliserReporter tripliserReporter = tripliserManager.getReporter(resourceMapping, Scope.RESOURCE);
		MutableTripleGraph tripleGraph = tripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE);
		Node node = nodeIterator.next();

		try {
			resourceNodeTripliser.createResource(input, node, resourceMapping, tripleGraph, tripliserManager);

			tripleGraph.getTripliserReporter().addMessage(new ReportEntry(Status.SUCCESS, Scope.RESOURCE, resourceMapping));
		} catch(IgnoredResourceMappingException e) {
			tripliserReporter.addMessage(new ReportEntry(e, Status.WARNING, Scope.RESOURCE, resourceMapping));
		} catch(InvalidResourceMappingException e) {
			tripliserReporter.addMessage(new ReportEntry(e, Status.FAILURE, Scope.RESOURCE, resourceMapping));
		} catch(Exception e) {
			tripliserReporter.addMessage(new ReportEntry(e, Status.ERROR, Scope.RESOURCE, resourceMapping));
		}

		return tripleGraph;
	}

	@Override
	public void remove() {
		throw new NotImplementedException();
	}

}
