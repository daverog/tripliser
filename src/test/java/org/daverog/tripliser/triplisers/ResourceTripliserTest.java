package org.daverog.tripliser.triplisers;

import java.util.Arrays;
import java.util.List;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.IgnoredResourceMappingException;
import org.daverog.tripliser.exception.InvalidResourceMappingException;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.graphs.MutableTripleGraph;
import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.daverog.tripliser.mapping.model.ResourceMappingBuilder;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.QueryException;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.triplisers.ResourceNodeTripliser;
import org.daverog.tripliser.triplisers.ResourceTripliser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class ResourceTripliserTest extends MockitoTestBase {

	@Mock TripliserManager tripliserManager;
	@Mock ResourceNodeTripliser resourceNodeTripliser;
	@Mock Queryable input;
	@Mock MutableTripleGraph tripleGraph;
	@Mock TripliserReporter reporter;
	@Mock Node graphNode;
	@Mock Node contextNode;
	@Mock Node contextNode2;
	@Mock GraphContext graphContext;

	@Before
	public void setUp(){
		when(tripliserManager.getGraphContext()).thenReturn(graphContext);
	}

	@Test
	public void ifNoQueryIsSuppliedTheGraphNodeIsUsedAsASingleResourceNode() throws IgnoredResourceMappingException, InvalidResourceMappingException {
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.query(null)
			.toResourceMapping();

		when(tripliserManager.getReporter(resourceMapping, Scope.RESOURCE)).thenReturn(reporter);
		when(tripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE)).thenReturn(tripleGraph);

		ResourceTripliser resourceTripliser = new ResourceTripliser(input, graphNode, resourceMapping, tripliserManager, resourceNodeTripliser);
		resourceTripliser.next();

		assertFalse(resourceTripliser.hasNext());
		verify(resourceNodeTripliser).createResource(input, graphNode, resourceMapping, tripleGraph, tripliserManager);
	}

	@Test
	public void ifNoNodeIsFoundAnIgnoreResourceMappingExceptionIsThrown() throws InvalidResourceMappingException {
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.query("docroot")
			.toResourceMapping();

		try {
			new ResourceTripliser(input, graphNode, resourceMapping, tripliserManager, resourceNodeTripliser);
			exceptionExpected();
		} catch(IgnoredResourceMappingException e) {
			assertEquals(e.getMessage(), "No node was found in the input to match the query docroot");
		}
	}

	@Test
	public void aResourceIsCreatedForTheNodeAndEachPropertyIsAddedToTheResource() throws QueryException, IgnoredResourceMappingException, InvalidResourceMappingException{
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.about("123")
			.toResourceMapping();

		when(input.readNodeList(eq(graphNode), anyString())).thenReturn(Arrays.asList(contextNode));
		when(tripliserManager.getReporter(resourceMapping, Scope.RESOURCE)).thenReturn(reporter);
		when(tripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE)).thenReturn(tripleGraph);

		ResourceTripliser resourceTripliser = new ResourceTripliser(input, graphNode, resourceMapping, tripliserManager, resourceNodeTripliser);
		resourceTripliser.next();

		assertFalse(resourceTripliser.hasNext());
		verify(resourceNodeTripliser).createResource(input, contextNode, resourceMapping, tripleGraph, tripliserManager);
	}

	@Test
	public void aResourceIsCreatedForEachOfMultipleNodesAndEachPropertyIsAddedToTheResource() throws QueryException, IgnoredResourceMappingException, InvalidResourceMappingException{
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.about("123")
			.toResourceMapping();

		List<Node> nodes = Arrays.asList(contextNode, contextNode2);

		when(input.readNodeList(eq(graphNode), anyString())).thenReturn(nodes);
		when(tripliserManager.getReporter(resourceMapping, Scope.RESOURCE)).thenReturn(reporter);
		when(tripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE)).thenReturn(tripleGraph);

		ResourceTripliser resourceTripliser = new ResourceTripliser(input, graphNode, resourceMapping, tripliserManager, resourceNodeTripliser);
		resourceTripliser.next();
		resourceTripliser.next();

		assertFalse(resourceTripliser.hasNext());
		verify(resourceNodeTripliser).createResource(input, contextNode, resourceMapping, tripleGraph, tripliserManager);
		verify(resourceNodeTripliser).createResource(input, contextNode2, resourceMapping, tripleGraph, tripliserManager);
	}

	@Test
	public void aResourceIsCreatedForTheSecondOfTwoNodesIfTheFirstCausesAnIgnoredResourceException() throws QueryException, IgnoredResourceMappingException, InvalidResourceMappingException{
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.about("123")
			.addProperty("foaf:name", "name")
			.toResourceMapping();
		List<Node> nodes = Arrays.asList(contextNode, contextNode2);
		IgnoredResourceMappingException cause = new IgnoredResourceMappingException("Error!");

		when(input.readNodeList(eq(graphNode), anyString())).thenReturn(nodes);
		when(tripliserManager.getReporter(resourceMapping, Scope.RESOURCE)).thenReturn(reporter);
		when(tripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE)).thenReturn(tripleGraph);
		when(resourceNodeTripliser.createResource(input, contextNode, resourceMapping, tripleGraph, tripliserManager)).thenThrow(cause);

		ResourceTripliser resourceTripliser = new ResourceTripliser(input, graphNode, resourceMapping, tripliserManager, resourceNodeTripliser);
		resourceTripliser.next();
		resourceTripliser.next();

		verify(reporter).addMessage(new ReportEntry(cause, Status.WARNING, Scope.RESOURCE, resourceMapping));
		verify(resourceNodeTripliser).createResource(input, contextNode2, resourceMapping, tripleGraph, tripliserManager);
	}

	@Test
	public void aResourceIsCreatedForTheSecondOfTwoNodesIfTheFirstCausesAnInvalidResourceException() throws QueryException, IgnoredResourceMappingException, InvalidResourceMappingException{
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.about("123")
			.addProperty("foaf:name", "name")
			.toResourceMapping();
		List<Node> nodes = Arrays.asList(contextNode, contextNode2);
		InvalidResourceMappingException cause = new InvalidResourceMappingException("Error!");

		when(input.readNodeList(eq(graphNode), anyString())).thenReturn(nodes);
		when(tripliserManager.getReporter(resourceMapping, Scope.RESOURCE)).thenReturn(reporter);
		when(tripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE)).thenReturn(tripleGraph);
		when(resourceNodeTripliser.createResource(input, contextNode, resourceMapping, tripleGraph, tripliserManager)).thenThrow(cause);

		ResourceTripliser resourceTripliser = new ResourceTripliser(input, graphNode, resourceMapping, tripliserManager, resourceNodeTripliser);
		resourceTripliser.next();
		resourceTripliser.next();

		verify(reporter).addMessage(new ReportEntry(cause, Status.FAILURE, Scope.RESOURCE, resourceMapping));
		verify(resourceNodeTripliser).createResource(input, contextNode2, resourceMapping, tripleGraph, tripliserManager);
	}

	@Test
	public void aResourceIsCreatedForTheSecondOfTwoNodesIfTheFirstCausesAnException() throws QueryException, IgnoredResourceMappingException, InvalidResourceMappingException{
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.about("123")
			.addProperty("foaf:name", "name")
			.toResourceMapping();
		List<Node> nodes = Arrays.asList(contextNode, contextNode2);
		RuntimeException cause = new RuntimeException("Error!");

		when(input.readNodeList(eq(graphNode), anyString())).thenReturn(nodes);
		when(tripliserManager.getReporter(resourceMapping, Scope.RESOURCE)).thenReturn(reporter);
		when(tripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE)).thenReturn(tripleGraph);
		when(resourceNodeTripliser.createResource(input, contextNode, resourceMapping, tripleGraph, tripliserManager)).thenThrow(cause);

		ResourceTripliser resourceTripliser = new ResourceTripliser(input, graphNode, resourceMapping, tripliserManager, resourceNodeTripliser);
		resourceTripliser.next();
		resourceTripliser.next();

		verify(resourceNodeTripliser).createResource(input, contextNode2, resourceMapping, tripleGraph, tripliserManager);
	}

}
