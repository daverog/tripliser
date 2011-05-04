package org.daverog.tripliser.triplisers;

import java.util.ArrayList;
import java.util.Iterator;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.IgnoredResourceMappingException;
import org.daverog.tripliser.exception.InvalidResourceMappingException;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.util.MetaIterator;


public class GraphNodeTripliser extends MetaIterator<TripleGraph> {
	
	private final Queryable input;
	private final Node graphNode;
	private final TripliserManager tripliserManager;
	private final ResourceTripliserFactory resourceTripliserFactory;
	
	private Iterator<ResourceMapping> resourceMappings;
	
	public GraphNodeTripliser(Queryable input, Node graphNode, 
			GraphMapping graphMapping, 
			TripliserManager tripliserManager,
			ResourceTripliserFactory resourceTripliserFactory) {
		this.input = input;
		this.graphNode = graphNode;
		this.tripliserManager = tripliserManager;
		this.resourceTripliserFactory = resourceTripliserFactory;
		
		resourceMappings = graphMapping.getResourceMappings().iterator();
	}

	@Override
	public boolean hasNextNestedIterator() {
		return resourceMappings.hasNext();
	}

	@Override
	public Iterator<TripleGraph> nextNestedIterator() {
		ResourceMapping resourceMapping = resourceMappings.next();
		TripliserReporter reporter = tripliserManager.getReporter(resourceMapping, Scope.RESOURCE_MAPPING);
		try {
			return resourceTripliserFactory.createResourceTripliser(input, graphNode, resourceMapping, tripliserManager);
		} catch(IgnoredResourceMappingException e) {
			reporter.addMessage(new ReportEntry(e, Status.ADVICE, Scope.RESOURCE_MAPPING, resourceMapping));
		} catch (InvalidResourceMappingException e) {
			reporter.addMessage(new ReportEntry(e, Status.FAILURE, Scope.RESOURCE_MAPPING, resourceMapping));
		} catch(Exception e) {
			reporter.addMessage(new ReportEntry(e, Status.ERROR, Scope.RESOURCE_MAPPING, resourceMapping));
		}
		return new ArrayList<TripleGraph>().iterator();
	}

}
