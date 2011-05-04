package org.daverog.tripliser.triplisers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.IgnoredResourceMappingException;
import org.daverog.tripliser.exception.InvalidResourceMappingException;
import org.daverog.tripliser.graphs.MutableTripleGraph;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.mapping.model.GraphMappingBuilder;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.triplisers.GraphNodeTripliser;
import org.daverog.tripliser.triplisers.ResourceNodeTripliser;
import org.daverog.tripliser.triplisers.ResourceTripliser;
import org.daverog.tripliser.triplisers.ResourceTripliserFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class GraphNodeTripliserTest extends MockitoTestBase {

	@Mock TripliserReporter reporter;
	@Mock Queryable input;
	@Mock ResourceTripliser resourceTripliser;
	@Mock MutableTripleGraph tripleGraph;
	@Mock Node graphNode;
	@Mock ResourceNodeTripliser resourceNodeTripliser;
	@Mock TripliserManager tripliserManager;
	@Mock ResourceTripliserFactory resourceTripliserFactory;
	@Mock TripleGraph tripleGraph1;
	@Mock TripleGraph tripleGraph2;

	private Iterator<TripleGraph> iterator1;
	private Iterator<TripleGraph> iterator2;

	@Before
	public void setUp() {
		List<TripleGraph> list1 = new ArrayList<TripleGraph>();
		List<TripleGraph> list2 = new ArrayList<TripleGraph>();
		list1.add(tripleGraph1);
		list2.add(tripleGraph2);
		iterator1 = list1.iterator();
		iterator2 = list2.iterator();
	}

	@Test
	public void oneResourceInTheMappingResultsInTheResourceBeingCreated() throws IgnoredResourceMappingException, InvalidResourceMappingException {
		GraphMapping graphMapping = new GraphMappingBuilder()
			.query(null)
			.addProperty("resource", "name", "query")
			.toGraphMapping();

		when(resourceTripliserFactory.createResourceTripliser(input, graphNode, graphMapping.getResourceMappings().get(0), tripliserManager)).thenReturn(iterator1);

		GraphNodeTripliser graphNodeTripliser = new GraphNodeTripliser(input, graphNode, graphMapping, tripliserManager, resourceTripliserFactory);

		assertEquals(tripleGraph1, graphNodeTripliser.next());
		assertFalse(graphNodeTripliser.hasNext());
	}

	@Test
	public void twoResourcesInTheMappingResultsInBothResourcesBeingCreated() throws IgnoredResourceMappingException, InvalidResourceMappingException {
		GraphMapping graphMapping = new GraphMappingBuilder()
			.query(null)
			.addProperty("resource", "name", "query")
			.addProperty("resource2", "name", "query")
			.toGraphMapping();

		when(resourceTripliserFactory.createResourceTripliser(input, graphNode, graphMapping.getResourceMappings().get(0), tripliserManager)).thenReturn(iterator1);
		when(resourceTripliserFactory.createResourceTripliser(input, graphNode, graphMapping.getResourceMappings().get(1), tripliserManager)).thenReturn(iterator2);

		GraphNodeTripliser graphNodeTripliser = new GraphNodeTripliser(input, graphNode, graphMapping, tripliserManager, resourceTripliserFactory);

		assertEquals(tripleGraph1, graphNodeTripliser.next());
		assertEquals(tripleGraph2, graphNodeTripliser.next());
		assertFalse(graphNodeTripliser.hasNext());
	}


	@Test
	public void aMappingWhichCausesAInvalidResourceMappingExceptionHasAFailureMessageReportedAtResourceMappingLevel() throws IgnoredResourceMappingException, InvalidResourceMappingException {
		GraphMapping graphMapping = new GraphMappingBuilder()
			.query(null)
			.addProperty("resource", "name", "query")
			.toGraphMapping();
		InvalidResourceMappingException cause = new InvalidResourceMappingException("Error!");
		ResourceMapping resourceMapping = graphMapping.getResourceMappings().get(0);

		when(tripliserManager.getReporter(resourceMapping, Scope.RESOURCE_MAPPING)).thenReturn(reporter);
		when(resourceTripliserFactory.createResourceTripliser(input, graphNode, resourceMapping, tripliserManager)).thenThrow(cause);

		GraphNodeTripliser graphNodeTripliser = new GraphNodeTripliser(input, graphNode, graphMapping, tripliserManager, resourceTripliserFactory);

		assertFalse(graphNodeTripliser.hasNext());
		verify(reporter).addMessage(new ReportEntry(cause, Status.FAILURE, Scope.RESOURCE_MAPPING, resourceMapping));
	}

	@Test
	public void aMappingWhichCausesAIgnoredResourceMappingExceptionHasAnAdviceMessageReportedAtResourceMappingLevel() throws IgnoredResourceMappingException, InvalidResourceMappingException {
		GraphMapping graphMapping = new GraphMappingBuilder()
			.query(null)
			.addProperty("resource", "name", "query")
			.toGraphMapping();
		IgnoredResourceMappingException cause = new IgnoredResourceMappingException("Error!");
		ResourceMapping resourceMapping = graphMapping.getResourceMappings().get(0);

		when(tripliserManager.getReporter(resourceMapping, Scope.RESOURCE_MAPPING)).thenReturn(reporter);
		when(resourceTripliserFactory.createResourceTripliser(input, graphNode, resourceMapping, tripliserManager)).thenThrow(cause);

		GraphNodeTripliser graphNodeTripliser = new GraphNodeTripliser(input, graphNode, graphMapping, tripliserManager, resourceTripliserFactory);

		assertFalse(graphNodeTripliser.hasNext());
		verify(reporter).addMessage(new ReportEntry(cause, Status.ADVICE, Scope.RESOURCE_MAPPING, resourceMapping));
	}

	@Test
	public void aMappingWhichCausesAnExceptionHasAnErrorMessageReportedAtResourceMappingLevel() throws IgnoredResourceMappingException, InvalidResourceMappingException {
		GraphMapping graphMapping = new GraphMappingBuilder()
			.query(null)
			.addProperty("resource", "name", "query")
			.toGraphMapping();
		RuntimeException cause = new RuntimeException("Error!");
		ResourceMapping resourceMapping = graphMapping.getResourceMappings().get(0);

		when(tripliserManager.getReporter(resourceMapping, Scope.RESOURCE_MAPPING)).thenReturn(reporter);
		when(resourceTripliserFactory.createResourceTripliser(input, graphNode, resourceMapping, tripliserManager)).thenThrow(cause);

		GraphNodeTripliser graphNodeTripliser = new GraphNodeTripliser(input, graphNode, graphMapping, tripliserManager, resourceTripliserFactory);

		assertFalse(graphNodeTripliser.hasNext());
		verify(reporter).addMessage(new ReportEntry(cause, Status.ERROR, Scope.RESOURCE_MAPPING, resourceMapping));
	}

}
