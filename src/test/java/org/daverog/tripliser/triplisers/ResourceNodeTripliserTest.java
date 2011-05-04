package org.daverog.tripliser.triplisers;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.IgnoredPropertyMappingException;
import org.daverog.tripliser.exception.IgnoredResourceMappingException;
import org.daverog.tripliser.exception.InvalidPropertyMappingException;
import org.daverog.tripliser.exception.InvalidResourceMappingException;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.graphs.MutableTripleGraph;
import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.PropertyMapping;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.daverog.tripliser.mapping.model.ResourceMappingBuilder;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.triplisers.ResourceNodeTripliser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


import com.hp.hpl.jena.rdf.model.Resource;

public class ResourceNodeTripliserTest extends MockitoTestBase {

	ResourceNodeTripliser resourceNodeTripliser;

	@Mock Queryable input;
	@Mock MutableTripleGraph tripleGraph;
	@Mock TripliserReporter reporter;
	@Mock Node contextNode;
	@Mock Resource resource;
	@Mock GraphContext graphContext;
	@Mock TripliserManager tripliserManager;

	@Before
	public void setUp(){
		resourceNodeTripliser = new ResourceNodeTripliser();
		when(tripliserManager.getGraphContext()).thenReturn(graphContext);
		when(tripleGraph.getTripliserReporter()).thenReturn(reporter);
	}

	@Test
	public void aResourceIsCreatedForTheNodeAndEachPropertyIsAddedToTheResourceAndTheGraphIsValidatedEchTime() throws IgnoredResourceMappingException, InvalidResourceMappingException, InvalidPropertyMappingException, IgnoredPropertyMappingException {
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.about("123")
			.addProperty("foaf:name", "name")
			.addProperty("foaf:homepage", "name")
			.toResourceMapping();

		when(tripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE_MAPPING)).thenReturn(tripleGraph);
		when(tripleGraph.createMainResource(eq(input), argThat(new ResourceMappingMatcher("123")), eq(contextNode), eq(graphContext))).thenReturn(resource);

		resourceNodeTripliser.createResource(input, contextNode, resourceMapping, tripleGraph, tripliserManager);

		verify(tripleGraph).addPropertiesForResourceAndMapping(eq(input), (PropertyMapping) argThat(new ValueMappingMatcher("foaf:name", "name")), eq(resource), eq(contextNode), eq(graphContext));
		verify(tripleGraph).addPropertiesForResourceAndMapping(eq(input), (PropertyMapping) argThat(new ValueMappingMatcher("foaf:homepage", "name")), eq(resource), eq(contextNode), eq(graphContext));
	}

	@Test
	public void aAdviceAtPropertyLevelMessageIsAddedToTheTripleGraphIfAIgnoredPropertyMappingExceptionOccurs() throws IgnoredResourceMappingException, InvalidResourceMappingException, InvalidPropertyMappingException, IgnoredPropertyMappingException {
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.about("123")
			.addProperty("foaf:name", "name")
			.toResourceMapping();

		when(tripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE_MAPPING)).thenReturn(tripleGraph);
		when(tripleGraph.createMainResource(eq(input), argThat(new ResourceMappingMatcher("123")), eq(contextNode), eq(graphContext))).thenReturn(resource);

		IgnoredPropertyMappingException cause = new IgnoredPropertyMappingException("Error!");
		doThrow(cause)
			.when(tripleGraph).addPropertiesForResourceAndMapping(eq(input), any(PropertyMapping.class), eq(resource), eq(contextNode), eq(graphContext));

		resourceNodeTripliser.createResource(input, contextNode, resourceMapping, tripleGraph, tripliserManager);

		verify(reporter).addMessage(new ReportEntry(cause, Status.ADVICE, Scope.PROPERTY, resourceMapping.getProperties().get(0)));
	}

	@Test
	public void aFailureAtPropertyLevelMessageIsAddedToTheTripleGraphIfAInvalidPropertyMappingExceptionOccurs() throws IgnoredResourceMappingException, InvalidResourceMappingException, InvalidPropertyMappingException, IgnoredPropertyMappingException {
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.about("123")
			.addProperty("foaf:name", "name")
			.toResourceMapping();

		when(tripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE_MAPPING)).thenReturn(tripleGraph);
		when(tripleGraph.createMainResource(eq(input), argThat(new ResourceMappingMatcher("123")), eq(contextNode), eq(graphContext))).thenReturn(resource);

		InvalidPropertyMappingException cause = new InvalidPropertyMappingException("Error!");
		doThrow(cause)
		.when(tripleGraph).addPropertiesForResourceAndMapping(eq(input), any(PropertyMapping.class), eq(resource), eq(contextNode), eq(graphContext));

		resourceNodeTripliser.createResource(input, contextNode, resourceMapping, tripleGraph, tripliserManager);

		verify(reporter).addMessage(new ReportEntry(cause, Status.FAILURE, Scope.PROPERTY, resourceMapping.getProperties().get(0)));
	}

	@Test
	public void anErrorAtPropertyLevelMessageIsAddedToTheTripleGraphIfAnExceptionOccurs() throws IgnoredResourceMappingException, InvalidResourceMappingException, InvalidPropertyMappingException, IgnoredPropertyMappingException {
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.about("123")
			.addProperty("foaf:name", "name")
			.toResourceMapping();

		when(tripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE_MAPPING)).thenReturn(tripleGraph);
		when(tripleGraph.createMainResource(eq(input), argThat(new ResourceMappingMatcher("123")), eq(contextNode), eq(graphContext))).thenReturn(resource);
		RuntimeException cause = new RuntimeException("Error!");
		doThrow(cause)
			.when(tripleGraph).addPropertiesForResourceAndMapping(eq(input), any(PropertyMapping.class), eq(resource), eq(contextNode), eq(graphContext));

		resourceNodeTripliser.createResource(input, contextNode, resourceMapping, tripleGraph, tripliserManager);

		verify(reporter).addMessage(new ReportEntry(cause, Status.ERROR, Scope.PROPERTY, resourceMapping.getProperties().get(0)));
	}

}
