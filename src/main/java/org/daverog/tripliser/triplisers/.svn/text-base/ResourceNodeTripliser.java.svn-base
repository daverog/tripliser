package org.daverog.tripliser.triplisers;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.IgnoredPropertyMappingException;
import org.daverog.tripliser.exception.IgnoredResourceMappingException;
import org.daverog.tripliser.exception.InvalidPropertyMappingException;
import org.daverog.tripliser.exception.InvalidResourceMappingException;
import org.daverog.tripliser.graphs.MutableTripleGraph;
import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.PropertyMapping;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.ReportEntry.Status;

import com.hp.hpl.jena.rdf.model.Resource;

public class ResourceNodeTripliser {

	public MutableTripleGraph createResource(Queryable input, Node resourceNode, ResourceMapping resourceMapping, MutableTripleGraph tripleGraph, TripliserManager tripliserManager) throws IgnoredResourceMappingException, InvalidResourceMappingException {
		Resource resource = tripleGraph.createMainResource(input, resourceMapping, resourceNode, tripliserManager.getGraphContext());

		for(PropertyMapping propertyMapping : resourceMapping.getProperties()) {
			try {
				tripleGraph.addPropertiesForResourceAndMapping(input, propertyMapping, resource, resourceNode, tripliserManager.getGraphContext());
			} catch(IgnoredPropertyMappingException e) {
				tripleGraph.getTripliserReporter().addMessage(new ReportEntry(e, Status.ADVICE, Scope.PROPERTY, propertyMapping));
			} catch(InvalidPropertyMappingException e) {
				tripleGraph.getTripliserReporter().addMessage(new ReportEntry(e, Status.FAILURE, Scope.PROPERTY, propertyMapping));
			} catch(Exception e) {
				tripleGraph.getTripliserReporter().addMessage(new ReportEntry(e, Status.ERROR, Scope.PROPERTY, propertyMapping));
			}
		}

		return tripleGraph;
	}

}
