package org.daverog.tripliser.triplisers;

import java.util.ArrayList;
import java.util.Arrays;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.IgnoredGraphMappingException;
import org.daverog.tripliser.exception.InvalidGraphMappingException;
import org.daverog.tripliser.exception.ScopeException;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.graphs.GraphContextBuilder;
import org.daverog.tripliser.graphs.MutableTripleGraph;
import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.mapping.model.GraphMappingBuilder;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.QueryException;
import org.daverog.tripliser.query.QueryService;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.triplisers.GraphNodeTripliser;
import org.daverog.tripliser.triplisers.GraphNodeTripliserFactory;
import org.daverog.tripliser.triplisers.GraphTagger;
import org.daverog.tripliser.triplisers.GraphTripliser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class GraphTripliserTest extends MockitoTestBase {

	@Mock Queryable input;
	@Mock GraphNodeTripliserFactory graphNodeTripliserFactory;
	@Mock QueryService queryService;
	@Mock TripliserManager tripliserManager;
	@Mock GraphContext graphContext;
	@Mock MutableTripleGraph tripleGraph1;
	@Mock MutableTripleGraph tripleGraph2;
	@Mock Node graphNode1;
	@Mock Node graphNode2;
	@Mock GraphNodeTripliser graphNodeTripliser;
	@Mock GraphNodeTripliser graphNodeTripliser2;
	@Mock GraphTagger graphTagger;
	@Mock TripliserReporter reporter;

	@Before
	public void setUp() {
		when(tripliserManager.getGraphContext()).thenReturn(graphContext);
	}

	@Test
	public void whenAGraphQueryIsNotPresentTheAGraphNodeTripliserIsCreatedWithANullNode() throws InvalidGraphMappingException, IgnoredGraphMappingException {
		GraphMapping graphMapping = new GraphMappingBuilder()
			.query(null)
			.toGraphMapping();

		when(graphNodeTripliserFactory.createGraphNodeTripliser(input, null, graphMapping, tripliserManager)).thenReturn(graphNodeTripliser);
		when(graphNodeTripliser.hasNext()).thenReturn(true).thenReturn(false);
		when(graphNodeTripliser.next()).thenReturn(tripleGraph1);
		when(tripliserManager.getTripleGraph(graphMapping, Scope.GRAPH)).thenReturn(tripleGraph1);
		when(tripliserManager.processNestedIterator(graphNodeTripliser, graphMapping, Scope.GRAPH)).thenReturn(graphNodeTripliser);

		GraphTripliser graphTripliser = new GraphTripliser(input, graphMapping, graphNodeTripliserFactory, tripliserManager, queryService, graphTagger);

		assertEquals(tripleGraph1, graphTripliser.next());
		assertFalse(graphTripliser.hasNext());

		verify(graphTagger).tagGraph(input, tripleGraph1, graphMapping, null);
	}

	@Test
	public void whenAGraphQueryReturnsNoNodesAndTheGraphIsRequiredAnInvalidGraphMappingExceptionIsThrown() throws QueryException, IgnoredGraphMappingException {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		GraphMapping graphMapping = new GraphMappingBuilder()
			.query("abc")
			.required(true)
			.toGraphMapping();

		when(queryService.getNodes(input, graphMapping, null, graphContext )).thenReturn(new ArrayList<Node>());

		try {
			new GraphTripliser(input, graphMapping, graphNodeTripliserFactory, tripliserManager, queryService, graphTagger);
			exceptionExpected();
		} catch(InvalidGraphMappingException e) {
			assertEquals("A required graph's query must return at least one result", e.getMessage());
		}
	}

	@Test
	public void whenAGraphQueryReturnsNoNodesAndTheGraphIsNotRequiredAnIgnoredGraphMappingExceptionIsThrown() throws QueryException, InvalidGraphMappingException {
		GraphMapping graphMapping = new GraphMappingBuilder()
			.query("abc")
			.required(false)
			.toGraphMapping();

		when(queryService.getNodes(input, graphMapping, null, graphContext )).thenReturn(new ArrayList<Node>());

		try {
			new GraphTripliser(input, graphMapping, graphNodeTripliserFactory, tripliserManager, queryService, graphTagger);
			exceptionExpected();
		} catch(IgnoredGraphMappingException e) {
			assertEquals("A non-required graph's query returned no results", e.getMessage());
		}
	}

	@Test
	public void whenAGraphQueryThrowsAQueryExceptionItIsWrappedAsAnInvalidGraphMappingException() throws QueryException, IgnoredGraphMappingException {
		QueryException queryException = new QueryException("Error");
		GraphMapping graphMapping = new GraphMappingBuilder()
			.query("abc")
			.required(false)
			.toGraphMapping();

		when(queryService.getNodes(input, graphMapping, null, graphContext)).thenThrow(queryException);

		try {
			new GraphTripliser(input, graphMapping, graphNodeTripliserFactory, tripliserManager, queryService, graphTagger);
			exceptionExpected();
		} catch(InvalidGraphMappingException e) {
			assertEquals(queryException, e.getCause());
		}
	}

	@Test
	public void whenAGraphQueryReturnsTwoNodesAGraphNodeTripliserIsCreatedWithEachNode() throws QueryException, InvalidGraphMappingException, IgnoredGraphMappingException {
		GraphMapping graphMapping = new GraphMappingBuilder()
			.query("x")
			.toGraphMapping();

		when(queryService.getNodes(input, graphMapping, null, graphContext)).thenReturn(Arrays.asList(graphNode1, graphNode2));
		when(graphNodeTripliserFactory.createGraphNodeTripliser(input, graphNode1, graphMapping, tripliserManager)).thenReturn(graphNodeTripliser);
		when(graphNodeTripliserFactory.createGraphNodeTripliser(input, graphNode2, graphMapping, tripliserManager)).thenReturn(graphNodeTripliser2);
		when(graphNodeTripliser.hasNext()).thenReturn(true).thenReturn(false);
		when(graphNodeTripliser.next()).thenReturn(tripleGraph1);
		when(graphNodeTripliser2.hasNext()).thenReturn(true).thenReturn(false);
		when(graphNodeTripliser2.next()).thenReturn(tripleGraph2);
		when(tripliserManager.getTripleGraph(graphMapping, Scope.GRAPH)).thenReturn(tripleGraph1).thenReturn(tripleGraph2);
		when(tripliserManager.processNestedIterator(graphNodeTripliser, graphMapping, Scope.GRAPH)).thenReturn(graphNodeTripliser);
		when(tripliserManager.processNestedIterator(graphNodeTripliser2, graphMapping, Scope.GRAPH)).thenReturn(graphNodeTripliser2);

		GraphTripliser graphTripliser = new GraphTripliser(input, graphMapping, graphNodeTripliserFactory, tripliserManager, queryService, graphTagger);

		assertEquals(tripleGraph1, graphTripliser.next());
		assertEquals(tripleGraph2, graphTripliser.next());
		assertFalse(graphTripliser.hasNext());

		verify(graphTagger).tagGraph(input, tripleGraph1, graphMapping, graphNode1);
		verify(graphTagger).tagGraph(input, tripleGraph2, graphMapping, graphNode2);
	}

	@Test
	public void scopeExceptionsAreCaughtWhenAttemptingToTagAGraph() throws QueryException, InvalidGraphMappingException, IgnoredGraphMappingException {
		GraphMapping graphMapping = new GraphMappingBuilder()
			.query("x")
			.toGraphMapping();
		ScopeException scopeException = new ScopeException("Error!");

		when(queryService.getNodes(input, graphMapping, null, graphContext)).thenReturn(Arrays.asList(graphNode1));
		when(graphNodeTripliserFactory.createGraphNodeTripliser(input, graphNode1, graphMapping, tripliserManager)).thenReturn(graphNodeTripliser);
		when(graphNodeTripliser.hasNext()).thenReturn(true).thenReturn(false);
		when(graphNodeTripliser.next()).thenReturn(tripleGraph1);
		when(tripliserManager.getTripleGraph(graphMapping, Scope.GRAPH)).thenThrow(scopeException);
		when(tripliserManager.getReporter(graphMapping, Scope.GRAPH)).thenReturn(reporter);
		when(tripliserManager.processNestedIterator(graphNodeTripliser, graphMapping, Scope.GRAPH)).thenReturn(graphNodeTripliser);

		GraphTripliser graphTripliser = new GraphTripliser(input, graphMapping, graphNodeTripliserFactory, tripliserManager, queryService, graphTagger);

		assertEquals(tripleGraph1, graphTripliser.next());
		assertFalse(graphTripliser.hasNext());

		verify(reporter).addMessage(new ReportEntry(scopeException, Status.WARNING, Scope.GRAPH, graphMapping));
		verifyZeroInteractions(graphTagger);
	}

}
