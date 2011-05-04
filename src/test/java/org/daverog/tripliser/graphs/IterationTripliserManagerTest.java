package org.daverog.tripliser.graphs;

import java.util.Arrays;
import java.util.Iterator;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.graphs.IterationTripliserManager;
import org.daverog.tripliser.graphs.MutableTripleGraph;
import org.daverog.tripliser.graphs.MutableTripleGraphFactory;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.daverog.tripliser.mapping.model.ResourceMappingBuilder;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.TripliserReporterFactory;
import org.junit.Test;
import org.mockito.Mock;


public class IterationTripliserManagerTest extends MockitoTestBase {

	@Mock MutableTripleGraphFactory mutableTripleGraphFactory;
	@Mock TripliserReporterFactory tripliserReporterFactory;

	@Test
	public void aNewGraphIsSuppliedForEachCallToNextAtResourceScope() throws Exception {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		IterationTripliserManager iterationTripliserManager = new IterationTripliserManager(
				Scope.RESOURCE,
				mutableTripleGraphFactory, 
				tripliserReporterFactory, 
				graphContext);
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.name("name")
			.toResourceMapping();
		
		MutableTripleGraph tripleGraph = mock(MutableTripleGraph.class);
		MutableTripleGraph tripleGraph2 = mock(MutableTripleGraph.class);
		TripliserReporter reporter = mock(TripliserReporter.class);
		TripliserReporter reporter2 = mock(TripliserReporter.class);
		
		when(tripliserReporterFactory.createGraphReporter())
			.thenReturn(reporter)
			.thenReturn(reporter2);
		when(mutableTripleGraphFactory.createGraph("name", reporter, graphContext)).thenReturn(tripleGraph);
		when(mutableTripleGraphFactory.createGraph("name", reporter2, graphContext)).thenReturn(tripleGraph2);
		when(tripleGraph.getTripliserReporter()).thenReturn(reporter);
		when(tripleGraph2.getTripliserReporter()).thenReturn(reporter2);
		
		assertEquals(tripleGraph, iterationTripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE));
		assertEquals(reporter, iterationTripliserManager.getReporter(resourceMapping, Scope.RESOURCE));

		//A series of commands that should not affect the current triple graph
		iterationTripliserManager.getReporter(resourceMapping, Scope.RESOURCE_MAPPING);
		iterationTripliserManager.next(Scope.GRAPH_MAPPING);
		iterationTripliserManager.next(Scope.GRAPH);
		iterationTripliserManager.getReporter(resourceMapping, Scope.GRAPH);
		iterationTripliserManager.next(Scope.RESOURCE_MAPPING);
		iterationTripliserManager.getReporter(resourceMapping, Scope.GRAPH_MAPPING);
		iterationTripliserManager.getReporter(resourceMapping, Scope.INPUT);
		assertEquals(tripleGraph, iterationTripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE));
		
		//Now request the next graph
		iterationTripliserManager.next(Scope.RESOURCE);
		
		assertEquals(tripleGraph2, iterationTripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE));
		assertEquals(reporter2, iterationTripliserManager.getReporter(resourceMapping, Scope.RESOURCE));
	}
	

	@Test
	public void aNewGraphIsSuppliedForEachCallToNextAtGraphScope() throws Exception {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		IterationTripliserManager iterationTripliserManager = new IterationTripliserManager(
				Scope.GRAPH,
				mutableTripleGraphFactory, 
				tripliserReporterFactory, 
				graphContext);
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.name("name")
			.toResourceMapping();
		
		MutableTripleGraph tripleGraph = mock(MutableTripleGraph.class);
		MutableTripleGraph tripleGraph2 = mock(MutableTripleGraph.class);
		TripliserReporter reporter = mock(TripliserReporter.class);
		TripliserReporter reporter2 = mock(TripliserReporter.class);
		
		when(tripliserReporterFactory.createGraphReporter())
			.thenReturn(reporter)
			.thenReturn(reporter2);
		when(mutableTripleGraphFactory.createGraph("name", reporter, graphContext)).thenReturn(tripleGraph);
		when(mutableTripleGraphFactory.createGraph("name", reporter2, graphContext)).thenReturn(tripleGraph2);
		when(tripleGraph.getTripliserReporter()).thenReturn(reporter);
		when(tripleGraph2.getTripliserReporter()).thenReturn(reporter2);
		
		assertEquals(tripleGraph, iterationTripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE));
		assertEquals(reporter, iterationTripliserManager.getReporter(resourceMapping, Scope.RESOURCE));

		//A series of commands that should not affect the current triple graph
		iterationTripliserManager.getReporter(resourceMapping, Scope.RESOURCE_MAPPING);
		iterationTripliserManager.next(Scope.GRAPH_MAPPING);
		iterationTripliserManager.getReporter(resourceMapping, Scope.GRAPH);
		iterationTripliserManager.next(Scope.RESOURCE);
		iterationTripliserManager.getReporter(resourceMapping, Scope.GRAPH_MAPPING);
		iterationTripliserManager.getReporter(resourceMapping, Scope.INPUT);
		
		assertEquals(tripleGraph, iterationTripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE));
		
		//Now request the next graph
		iterationTripliserManager.next(Scope.GRAPH);
		
		assertEquals(tripleGraph2, iterationTripliserManager.getTripleGraph(resourceMapping, Scope.RESOURCE));
		assertEquals(reporter2, iterationTripliserManager.getReporter(resourceMapping, Scope.RESOURCE));
	}
	
	@Test
	public void forAllNonResourceScopesAReporterForScopeIsReturned() throws Exception {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		IterationTripliserManager iterationTripliserManager = new IterationTripliserManager(
				Scope.RESOURCE,
				mutableTripleGraphFactory, 
				tripliserReporterFactory, 
				graphContext);
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.name("name")
			.toResourceMapping();
		
		TripliserReporter reporterForResourceMapping = mock(TripliserReporter.class);
		TripliserReporter reporterForGraphMapping = mock(TripliserReporter.class);
		TripliserReporter reporterForInput = mock(TripliserReporter.class);
		
		when(tripliserReporterFactory.getReporterForScope(Scope.RESOURCE_MAPPING)).thenReturn(reporterForResourceMapping);
		when(tripliserReporterFactory.getReporterForScope(Scope.GRAPH_MAPPING)).thenReturn(reporterForGraphMapping);
		when(tripliserReporterFactory.getReporterForScope(Scope.INPUT)).thenReturn(reporterForInput);
		
		assertEquals(reporterForResourceMapping, iterationTripliserManager.getReporter(resourceMapping, Scope.RESOURCE_MAPPING));
		assertEquals(reporterForGraphMapping, iterationTripliserManager.getReporter(resourceMapping, Scope.GRAPH_MAPPING));
		assertEquals(reporterForInput, iterationTripliserManager.getReporter(resourceMapping, Scope.INPUT));
	}
	
	@Test
	public void ifTheScopeEqualsTheMergeScopeTheNestedIteratorIsFlushedAndTheResultingTripleGraphReturned() {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		IterationTripliserManager iterationTripliserManager = new IterationTripliserManager(
				Scope.GRAPH,
				mutableTripleGraphFactory, 
				tripliserReporterFactory, 
				graphContext);
		
		TripliserReporter reporter = mock(TripliserReporter.class);
		MutableTripleGraph tripleGraph = mock(MutableTripleGraph.class);
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.name("name")
			.toResourceMapping();
		
		when(tripliserReporterFactory.createGraphReporter()).thenReturn(reporter);
		when(mutableTripleGraphFactory.createGraph("name", reporter, graphContext)).thenReturn(tripleGraph);
		
		Iterator<TripleGraph> nestedIterator = Arrays.asList(mock(TripleGraph.class), mock(TripleGraph.class)).iterator();
		Iterator<TripleGraph> processedNestedIterator = iterationTripliserManager.processNestedIterator(nestedIterator, resourceMapping, Scope.GRAPH);
		
		assertFalse(nestedIterator.hasNext());
		assertTrue(processedNestedIterator.hasNext());
		assertEquals(tripleGraph, processedNestedIterator.next());
		assertFalse(processedNestedIterator.hasNext());
	}

	@Test
	public void ifTheScopeGreaterThanTheMergeScopeTheNestedIteratorIsReturnedUnaffected() {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		IterationTripliserManager iterationTripliserManager = new IterationTripliserManager(
				Scope.GRAPH,
				mutableTripleGraphFactory, 
				tripliserReporterFactory, 
				graphContext);
		
		TripliserReporter reporter = mock(TripliserReporter.class);
		MutableTripleGraph tripleGraph = mock(MutableTripleGraph.class);
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.name("name")
			.toResourceMapping();
		
		when(tripliserReporterFactory.createGraphReporter()).thenReturn(reporter);
		when(mutableTripleGraphFactory.createGraph("name", reporter, graphContext)).thenReturn(tripleGraph);
		
		Iterator<TripleGraph> nestedIterator = Arrays.asList(mock(TripleGraph.class), mock(TripleGraph.class)).iterator();
		Iterator<TripleGraph> processedNestedIterator = iterationTripliserManager.processNestedIterator(nestedIterator, resourceMapping, Scope.GRAPH_MAPPING);
		
		processedNestedIterator.next();
		processedNestedIterator.next();
		assertFalse(processedNestedIterator.hasNext());
		assertEquals(nestedIterator, processedNestedIterator);
	}
	
	@Test
	public void ifTheScopeLessThanTheMergeScopeTheNestedIteratorIsReturnedUnaffected() {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		IterationTripliserManager iterationTripliserManager = new IterationTripliserManager(
				Scope.GRAPH,
				mutableTripleGraphFactory, 
				tripliserReporterFactory, 
				graphContext);
		
		TripliserReporter reporter = mock(TripliserReporter.class);
		MutableTripleGraph tripleGraph = mock(MutableTripleGraph.class);
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.name("name")
			.toResourceMapping();
		
		when(tripliserReporterFactory.createGraphReporter()).thenReturn(reporter);
		when(mutableTripleGraphFactory.createGraph("name", reporter, graphContext)).thenReturn(tripleGraph);
		
		Iterator<TripleGraph> nestedIterator = Arrays.asList(mock(TripleGraph.class), mock(TripleGraph.class)).iterator();
		Iterator<TripleGraph> processedNestedIterator = iterationTripliserManager.processNestedIterator(nestedIterator, resourceMapping, Scope.RESOURCE_MAPPING);
		
		processedNestedIterator.next();
		processedNestedIterator.next();
		assertFalse(processedNestedIterator.hasNext());
		assertEquals(nestedIterator, processedNestedIterator);
	}
	
}
