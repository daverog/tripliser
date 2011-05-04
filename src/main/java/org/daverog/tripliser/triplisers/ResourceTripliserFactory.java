package org.daverog.tripliser.triplisers;

import java.util.Iterator;

import org.daverog.tripliser.exception.IgnoredResourceMappingException;
import org.daverog.tripliser.exception.InvalidResourceMappingException;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.Queryable;


public class ResourceTripliserFactory {

	private final ResourceNodeTripliser resourceNodeTripliser;
	
	public ResourceTripliserFactory(ResourceNodeTripliser resourceNodeTripliser) {
		this.resourceNodeTripliser = resourceNodeTripliser;
	}

	public Iterator<TripleGraph> createResourceTripliser(
			Queryable input, Node graphNode,
			ResourceMapping resourceMapping, TripliserManager tripliserManager) throws IgnoredResourceMappingException, InvalidResourceMappingException {
		return new ResourceTripliser(input, graphNode, resourceMapping, tripliserManager, resourceNodeTripliser);
	}

}
