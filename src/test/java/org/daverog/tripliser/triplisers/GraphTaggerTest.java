package org.daverog.tripliser.triplisers;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.graphs.MutableTripleGraph;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.mapping.model.GraphMappingBuilder;
import org.daverog.tripliser.mapping.model.TagBuilder;
import org.daverog.tripliser.query.Item;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.QueryException;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.triplisers.GraphTagger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;



public class GraphTaggerTest extends MockitoTestBase {
	
	@Mock MutableTripleGraph tripleGraph;
	@Mock Node graphNode;
	@Mock Queryable input;
	@Mock TripliserReporter reporter;
	
	private GraphTagger tagger;

	@Before
	public void setUp(){
		tagger = new GraphTagger();
		
		when(tripleGraph.getTripliserReporter()).thenReturn(reporter);
	}
	
	@Test
	public void aNullTagListResultsInNoTagging() {
		GraphMapping graphMapping = new GraphMappingBuilder()
			.toGraphMapping();
		setInternalState(graphMapping, "tags", null);
		
		tagger.tagGraph(input, tripleGraph, graphMapping, graphNode);
		
		verifyZeroInteractions(tripleGraph);		
	}
	
	@Test
	public void addsATagToAGraph() throws QueryException{
		GraphMapping graphMapping = new GraphMappingBuilder()
			.addTag("tag1", "query1")
			.toGraphMapping();
		
		when(input.readItem(graphNode, "query1")).thenReturn(new Item(){
			@Override
			public String getStringValue() {
				return "value1";
			}
		});

		tagger.tagGraph(input, tripleGraph, graphMapping, graphNode);
		
		verify(tripleGraph).addTag("tag1", "value1");		
	}
	
	@Test
	public void whenReadItemReturnsNullATagIsNotAdded() throws QueryException{
		GraphMapping graphMapping = new GraphMappingBuilder()
			.addTag("tag1", "query1")
			.toGraphMapping();
		
		when(input.readItem(graphNode, "query1")).thenReturn(null);

		tagger.tagGraph(input, tripleGraph, graphMapping, graphNode);
		
		verifyZeroInteractions(tripleGraph);		
	}

	@Test
	public void tagsAreAddedEvenIfAPriorTagFails() throws QueryException{
		GraphMapping graphMapping = new GraphMappingBuilder()
			.addTag("tag1", "query1")
			.addTag("tag2", "query2")
			.toGraphMapping();
		
		when(input.readItem(graphNode, "query1")).thenThrow(new QueryException("Error!"));
		when(input.readItem(graphNode, "query2")).thenReturn(new Item(){
			@Override
			public String getStringValue() {
				return "value2";
			}
		});
	
		tagger.tagGraph(input, tripleGraph, graphMapping, graphNode);
		
		verify(tripleGraph, times(0)).addTag("tag1", "value1");		
		verify(tripleGraph).addTag("tag2", "value2");		
	}
	
	@Test
	public void aFailureToObtainARequiredTagValueResultsInAFailureMessage() throws QueryException{
		GraphMapping graphMapping = new GraphMappingBuilder()
			.addTag(new TagBuilder().query("abc").required(true).toTag())
			.toGraphMapping();
		QueryException queryException = new QueryException("Error!");
		
		when(input.readItem(graphNode, "abc")).thenThrow(queryException);
		
		tagger.tagGraph(input, tripleGraph, graphMapping, graphNode);
		
		verify(reporter).addMessage(new ReportEntry(queryException, Status.FAILURE, Scope.GRAPH, graphMapping));		
	}
	
	@Test
	public void aFailureToObtainANonRequiredTagValueResultsInAWarningMessage() throws QueryException{
		GraphMapping graphMapping = new GraphMappingBuilder()
			.addTag(new TagBuilder().query("abc").required(false).toTag())
			.toGraphMapping();
		QueryException queryException = new QueryException("Error!");
		
		when(input.readItem(graphNode, "abc")).thenThrow(queryException);
		
		tagger.tagGraph(input, tripleGraph, graphMapping, graphNode);
		
		verify(reporter).addMessage(new ReportEntry(queryException, Status.WARNING, Scope.GRAPH, graphMapping));		
	}
	
}
