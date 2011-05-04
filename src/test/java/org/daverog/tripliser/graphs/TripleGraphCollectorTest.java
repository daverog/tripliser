package org.daverog.tripliser.graphs;

import java.util.Iterator;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.ScopeException;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.graphs.MutableTripleGraph;
import org.daverog.tripliser.graphs.MutableTripleGraphFactory;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.graphs.TripleGraphCollector;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.mapping.model.GraphMappingBuilder;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.mapping.model.MappingBuilder;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.daverog.tripliser.report.RecordingReporter;
import org.daverog.tripliser.report.SystemOutReporter;
import org.daverog.tripliser.report.TripliserReporterFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class TripleGraphCollectorTest extends MockitoTestBase {

	@Mock MutableTripleGraphFactory mutableTripleGraphFactory;
	@Mock MutableTripleGraph tripleGraph;
	@Mock MutableTripleGraph tripleGraph2;
	@Mock TripliserReporterFactory tripliserReporterFactory;
	@Mock RecordingReporter reporter;
	@Mock SystemOutReporter systemOutReporter;

	@Before
	public void setUp(){
		when(tripliserReporterFactory.createGraphReporter()).thenReturn(reporter);
		when(tripleGraph.getTripliserReporter()).thenReturn(reporter);
		when(tripleGraph2.getTripliserReporter()).thenReturn(reporter);
	}

	@Test
	public void mergingAtResourceScopeResultsInAGraphCreatedForEachResourceInstanceAndEachReporterIsCollectedAlso() throws TripliserException, ScopeException{
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		TripleGraphCollector collector =
			new TripleGraphCollector(mutableTripleGraphFactory, tripliserReporterFactory,
					graphContext, Scope.RESOURCE);
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(new GraphMappingBuilder()
				.name("name")
				.addProperty("resource1", "name", "query")
				.toGraphMapping()
			).toMapping();
		GraphMapping graphMapping = mapping.getGraphMappings().get(0);
		ResourceMapping resourceMapping = graphMapping.getResourceMappings().get(0);

		when(tripliserReporterFactory.getReporterForScope(any(Scope.class))).thenReturn(systemOutReporter);
		when(mutableTripleGraphFactory.createGraph("1", reporter, graphContext)).thenReturn(tripleGraph);
		when(mutableTripleGraphFactory.createGraph("2", reporter, graphContext)).thenReturn(tripleGraph2);

		assertEquals(tripleGraph, collector.getTripleGraph(resourceMapping, Scope.RESOURCE));
		assertEquals(reporter, collector.getReporter(resourceMapping, Scope.RESOURCE));
		collector.next(Scope.RESOURCE);
		assertEquals(tripleGraph2, collector.getTripleGraph(resourceMapping, Scope.RESOURCE));
		assertEquals(reporter, collector.getReporter(resourceMapping, Scope.RESOURCE));
		assertEquals(systemOutReporter, collector.getReporter(resourceMapping, Scope.RESOURCE_MAPPING));

		verify(mutableTripleGraphFactory).createGraph("1", reporter, graphContext);
		verify(mutableTripleGraphFactory).createGraph("2", reporter, graphContext);
		verify(tripliserReporterFactory, times(2)).createGraphReporter();

		Iterator<TripleGraph> graphs = collector.iterator();

		assertEquals(tripleGraph, graphs.next());
		assertEquals(tripleGraph2, graphs.next());
		assertFalse(graphs.hasNext());

		assertEquals(reporter, collector.getReporters().get(0));
		assertEquals(systemOutReporter, collector.getReporters().get(1));
		assertEquals(2, collector.getReporters().size());
	}

	@Test
	public void mergingAtResourceScopeResultsInGlobalReporterBeingUsedForProblemsAtResourceMappingLevelAndAbove() throws TripliserException {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		TripleGraphCollector collector =
			new TripleGraphCollector(mutableTripleGraphFactory, tripliserReporterFactory,
					graphContext, Scope.RESOURCE);
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(new GraphMappingBuilder()
			.name("name")
			.addProperty("resource1", "name", "query")
			.toGraphMapping()
		).toMapping();
		GraphMapping graphMapping = mapping.getGraphMappings().get(0);
		ResourceMapping resourceMapping = graphMapping.getResourceMappings().get(0);

		when(tripliserReporterFactory.getReporterForScope(Scope.RESOURCE_MAPPING)).thenReturn(systemOutReporter);
		when(tripliserReporterFactory.getReporterForScope(Scope.GRAPH)).thenReturn(systemOutReporter);
		when(tripliserReporterFactory.getReporterForScope(Scope.GRAPH_MAPPING)).thenReturn(systemOutReporter);
		when(tripliserReporterFactory.getReporterForScope(Scope.MAPPING)).thenReturn(systemOutReporter);

		assertEquals(systemOutReporter, collector.getReporter(resourceMapping, Scope.RESOURCE_MAPPING));
		assertEquals(systemOutReporter, collector.getReporter(graphMapping, Scope.GRAPH_MAPPING));
		assertEquals(systemOutReporter, collector.getReporter(graphMapping, Scope.GRAPH_MAPPING));
		assertEquals(systemOutReporter, collector.getReporter(mapping, Scope.MAPPING));
	}

	@Test
	public void mergingAtResourceMappingScopeResultsInAGraphCreatedForEachResourceMapping() throws TripliserException, ScopeException{
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		TripleGraphCollector collector =
			new TripleGraphCollector(mutableTripleGraphFactory, tripliserReporterFactory,
					graphContext, Scope.RESOURCE_MAPPING);
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(new GraphMappingBuilder()
				.name("name")
				.addProperty("resource1", "name", "query")
				.addProperty("resource2", "name", "query")
				.toGraphMapping()
			).toMapping();
		GraphMapping graphMapping = mapping.getGraphMappings().get(0);
		ResourceMapping resourceMapping1 = graphMapping.getResourceMappings().get(0);
		ResourceMapping resourceMapping2 = graphMapping.getResourceMappings().get(1);

		when(tripliserReporterFactory.getReporterForScope(any(Scope.class))).thenReturn(systemOutReporter);
		when(mutableTripleGraphFactory.createGraph("resource1", reporter, graphContext)).thenReturn(tripleGraph);
		when(mutableTripleGraphFactory.createGraph("resource2", reporter, graphContext)).thenReturn(tripleGraph2);

		assertEquals(tripleGraph, collector.getTripleGraph(resourceMapping1, Scope.RESOURCE_MAPPING));
		assertEquals(tripleGraph2, collector.getTripleGraph(resourceMapping2, Scope.RESOURCE_MAPPING));
		assertEquals(reporter, collector.getReporter(resourceMapping1, Scope.RESOURCE_MAPPING));
		assertEquals(reporter, collector.getReporter(resourceMapping2, Scope.RESOURCE_MAPPING));

		verify(mutableTripleGraphFactory).createGraph("resource1", reporter, graphContext);
		verify(mutableTripleGraphFactory).createGraph("resource2", reporter, graphContext);
		verify(tripliserReporterFactory, times(2)).createGraphReporter();

		Iterator<TripleGraph> graphs = collector.iterator();

		assertEquals(tripleGraph, graphs.next());
		assertEquals(tripleGraph2, graphs.next());
		assertFalse(graphs.hasNext());
	}


	@Test
	public void mergingAtGraphScopeResultsInAGraphCreatedForEachGraphAndReturnsTheseAtAllLowerScopes() throws TripliserException, ScopeException{
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		TripleGraphCollector collector =
			new TripleGraphCollector(mutableTripleGraphFactory, tripliserReporterFactory,
					graphContext, Scope.GRAPH);
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(new GraphMappingBuilder()
				.name("name")
				.addProperty("resource1", "name", "query")
				.toGraphMapping()
			).toMapping();
		GraphMapping graphMapping = mapping.getGraphMappings().get(0);
		ResourceMapping resourceMapping = graphMapping.getResourceMappings().get(0);

		when(tripliserReporterFactory.getReporterForScope(any(Scope.class))).thenReturn(systemOutReporter);
		when(mutableTripleGraphFactory.createGraph("1", reporter, graphContext)).thenReturn(tripleGraph);
		when(mutableTripleGraphFactory.createGraph("2", reporter, graphContext)).thenReturn(tripleGraph2);

		assertEquals(tripleGraph, collector.getTripleGraph(resourceMapping, Scope.RESOURCE));
		assertEquals(reporter, collector.getReporter(resourceMapping, Scope.RESOURCE));
		collector.next(Scope.RESOURCE);
		assertEquals(reporter, collector.getReporter(resourceMapping, Scope.RESOURCE_MAPPING));
		assertEquals(tripleGraph, collector.getTripleGraph(resourceMapping, Scope.RESOURCE_MAPPING));
		assertEquals(reporter, collector.getReporter(resourceMapping, Scope.GRAPH));
		assertEquals(tripleGraph, collector.getTripleGraph(resourceMapping, Scope.GRAPH));
		collector.next(Scope.GRAPH);
		assertEquals(tripleGraph2, collector.getTripleGraph(resourceMapping, Scope.GRAPH));
		assertEquals(reporter, collector.getReporter(resourceMapping, Scope.GRAPH));

		verify(mutableTripleGraphFactory).createGraph("1", reporter, graphContext);
		verify(mutableTripleGraphFactory).createGraph("2", reporter, graphContext);
		verify(tripliserReporterFactory, times(2)).createGraphReporter();

		Iterator<TripleGraph> graphs = collector.iterator();

		assertEquals(tripleGraph, graphs.next());
		assertEquals(tripleGraph2, graphs.next());
		assertFalse(graphs.hasNext());
	}

	@Test
	public void mergingAtMappingScopeResultsInASingleGraph() throws TripliserException, ScopeException{
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		TripleGraphCollector collector =
			new TripleGraphCollector(mutableTripleGraphFactory, tripliserReporterFactory,
					graphContext, Scope.MAPPING);
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(new GraphMappingBuilder()
			.name("name")
			.addProperty("resource1", "name", "query")
			.toGraphMapping()
		).toMapping();
		GraphMapping graphMapping = mapping.getGraphMappings().get(0);
		ResourceMapping resourceMapping = graphMapping.getResourceMappings().get(0);

		when(tripliserReporterFactory.getReporterForScope(any(Scope.class))).thenReturn(systemOutReporter);
		when(mutableTripleGraphFactory.createGraph("1", reporter, graphContext)).thenReturn(tripleGraph);

		assertEquals(tripleGraph, collector.getTripleGraph(resourceMapping, Scope.MAPPING));
		assertEquals(tripleGraph, collector.getTripleGraph(resourceMapping, Scope.MAPPING));
		assertEquals(tripleGraph, collector.getTripleGraph(resourceMapping, Scope.MAPPING));
		assertEquals(tripleGraph, collector.getTripleGraph(resourceMapping, Scope.MAPPING));
		assertEquals(reporter, collector.getReporter(resourceMapping, Scope.MAPPING));
		assertEquals(reporter, collector.getReporter(resourceMapping, Scope.MAPPING));
		assertEquals(reporter, collector.getReporter(resourceMapping, Scope.MAPPING));
		assertEquals(reporter, collector.getReporter(resourceMapping, Scope.MAPPING));

		verify(mutableTripleGraphFactory).createGraph("1", reporter, graphContext);
		verify(tripliserReporterFactory).createGraphReporter();

		Iterator<TripleGraph> graphs = collector.iterator();

		assertEquals(tripleGraph, graphs.next());
		assertFalse(graphs.hasNext());
	}

	@Test
	public void callingNextAtMappingScopeIsNotAllowed() {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		TripleGraphCollector collector =
			new TripleGraphCollector(mutableTripleGraphFactory, tripliserReporterFactory, graphContext, null);

		try {
			collector.next(Scope.MAPPING);
			exceptionExpected();
		} catch(ScopeException e) {
			assertEquals("Cannot call next for MAPPING scope", e.getMessage());
		}
	}

	@Test
	public void callingNextAtGraphMappingScopeIsNotAllowed() {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		TripleGraphCollector collector =
			new TripleGraphCollector(mutableTripleGraphFactory, tripliserReporterFactory, graphContext, null);

		try {
			collector.next(Scope.GRAPH_MAPPING);
			exceptionExpected();
		} catch(ScopeException e) {
			assertEquals("Cannot call next for GRAPH_MAPPING scope", e.getMessage());
		}
	}

	@Test
	public void callingNextAtResourceMappingScopeIsNotAllowed() {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		TripleGraphCollector collector =
			new TripleGraphCollector(mutableTripleGraphFactory, tripliserReporterFactory, graphContext, null);

		try {
			collector.next(Scope.RESOURCE_MAPPING);
			exceptionExpected();
		} catch(ScopeException e) {
			assertEquals("Cannot call next for RESOURCE_MAPPING scope", e.getMessage());
		}
	}

	@Test
	public void whenAGraphDoesNotExistAnExceptionIsThrown() {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		TripleGraphCollector collector =
			new TripleGraphCollector(mutableTripleGraphFactory, tripliserReporterFactory, graphContext, null);

		try {
			collector.getTripleGraphByName("non-existant");
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("The graph 'non-existant' was not found", e.getMessage());
		}
	}

	@Test
	public void convertingToATripleGraphReturnsTheFirstItemOfAnIteratorIfOnlyOneExists() throws TripliserException, ScopeException{
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		TripleGraphCollector collector =
			new TripleGraphCollector(mutableTripleGraphFactory, tripliserReporterFactory,
					graphContext, Scope.MAPPING);
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(new GraphMappingBuilder()
				.name("name")
				.addProperty("resource1", "name", "query")
				.toGraphMapping()
			).toMapping();
		GraphMapping graphMapping = mapping.getGraphMappings().get(0);
		ResourceMapping resourceMapping = graphMapping.getResourceMappings().get(0);

		when(tripliserReporterFactory.getReporterForScope(any(Scope.class))).thenReturn(systemOutReporter);
		when(mutableTripleGraphFactory.createGraph("1", reporter, graphContext)).thenReturn(tripleGraph);

		collector.getTripleGraph(resourceMapping, Scope.MAPPING);

		assertEquals(tripleGraph, collector.convertToTripleGraph());
	}

	@Test
	public void convertingToATripleGraphWhenNoneExistThrowsAnException() {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		TripleGraphCollector collector =
			new TripleGraphCollector(mutableTripleGraphFactory, tripliserReporterFactory,
					graphContext, Scope.MAPPING);
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(new GraphMappingBuilder()
			.name("name")
			.addProperty("resource1", "name", "query")
			.toGraphMapping()
		).toMapping();

		when(tripliserReporterFactory.getReporterForScope(any(Scope.class))).thenReturn(systemOutReporter);
		when(mutableTripleGraphFactory.createGraph(mapping.getName(), reporter, graphContext)).thenReturn(tripleGraph);

		try {
			collector.convertToTripleGraph();
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("No graphs exist", e.getMessage());
		}
	}

	@Test
	public void convertingToATripleGraphWhenTheMergeScopeIsNotMappingThrowsAnException() throws TripliserException, ScopeException{
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		TripleGraphCollector collector =
			new TripleGraphCollector(mutableTripleGraphFactory, tripliserReporterFactory,
					graphContext, Scope.RESOURCE);
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(new GraphMappingBuilder()
			.name("name")
			.addProperty("resource1", "name", "query")
			.toGraphMapping()
		).toMapping();
		GraphMapping graphMapping = mapping.getGraphMappings().get(0);
		ResourceMapping resourceMapping = graphMapping.getResourceMappings().get(0);

		when(tripliserReporterFactory.getReporterForScope(any(Scope.class))).thenReturn(systemOutReporter);
		when(mutableTripleGraphFactory.createGraph("1", reporter, graphContext)).thenReturn(tripleGraph);

		collector.getTripleGraph(resourceMapping, Scope.RESOURCE);

		try {
			collector.convertToTripleGraph();
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("Only if merge scope is set to MAPPING can a conversion to a single triple graph be made", e.getMessage());
		}
	}

}
