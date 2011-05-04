package org.daverog.tripliser.triplisers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.graphs.GraphContextBuilder;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.mapping.model.MappingBuilder;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.query.QueryableInputFactory;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.triplisers.InputTripliserFactory;
import org.daverog.tripliser.triplisers.TripleGraphIterator;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;



public class TripleGraphIteratorTest extends MockitoTestBase {

	@Mock TripliserManager tripliserManager;
	@Mock InputTripliserFactory inputTripliserFactory;
	@Mock QueryableInputFactory queryableInputFactory;
	@Mock TripleGraph tripleGraph;
	@Mock TripleGraph tripleGraph2;
	@Mock Queryable input;
	@Mock Queryable input2;
	@Mock Iterator<TripleGraph> inputTripliser;
	@Mock Iterator<TripleGraph> inputTripliser2;
	@Mock Iterator<TripleGraph> processedInputTripliser;
	@Mock Iterator<TripleGraph> processedInputTripliser2;
	@Mock TripliserReporter reporter;

	@Test
	public void createsAQueryableInputForEachInputStream() throws TripliserException {
		List<InputStream> inputs = new ArrayList<InputStream>();
		inputs.add(IOUtils.toInputStream("input"));
		inputs.add(IOUtils.toInputStream("input2"));

		Mapping mapping = new MappingBuilder().toMapping();
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();

		when(queryableInputFactory.createSaxonXPathTripliserDocument(eq("#0"), streamMatches("input"))).thenReturn(input);
		when(queryableInputFactory.createSaxonXPathTripliserDocument(eq("#1"), streamMatches("input2"))).thenReturn(input2);
		when(inputTripliserFactory.createInputTripliser(input, mapping, tripliserManager)).thenReturn(inputTripliser);
		when(inputTripliserFactory.createInputTripliser(input2, mapping, tripliserManager)).thenReturn(inputTripliser2);
		when(tripliserManager.processNestedIterator(inputTripliser, mapping, Scope.INPUT)).thenReturn(processedInputTripliser);
		when(tripliserManager.processNestedIterator(inputTripliser2, mapping, Scope.INPUT)).thenReturn(processedInputTripliser2);
		when(processedInputTripliser.hasNext()).thenReturn(true).thenReturn(false);
		when(processedInputTripliser.next()).thenReturn(tripleGraph);
		when(processedInputTripliser2.hasNext()).thenReturn(true).thenReturn(false);
		when(processedInputTripliser2.next()).thenReturn(tripleGraph2);

		TripleGraphIterator tripleGraphIterator = new TripleGraphIterator(inputs.iterator(), tripliserManager, mapping, graphContext,
				inputTripliserFactory, queryableInputFactory);

		assertEquals(tripleGraph, tripleGraphIterator.next());
		assertEquals(tripleGraph2, tripleGraphIterator.next());
		assertFalse(tripleGraphIterator.hasNext());
	}

	@Test
	public void reportsAFailureMessageAtInputScopeIfInputTripliserThrowsATripliserException() throws TripliserException {
		List<InputStream> inputs = new ArrayList<InputStream>();
		inputs.add(IOUtils.toInputStream("input"));
		Mapping mapping = new MappingBuilder().toMapping();
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		TripliserException tripliserException = new TripliserException("Error!");

		when(tripliserManager.getReporter(mapping, Scope.INPUT)).thenReturn(reporter);
		when(queryableInputFactory.createSaxonXPathTripliserDocument(eq("#0"), streamMatches("input"))).thenReturn(input);
		when(inputTripliserFactory.createInputTripliser(input, mapping, tripliserManager)).thenThrow(tripliserException);

		TripleGraphIterator tripleGraphIterator = new TripleGraphIterator(inputs.iterator(), tripliserManager, mapping, graphContext,
				inputTripliserFactory, queryableInputFactory);

		assertFalse(tripleGraphIterator.hasNext());

		verify(reporter).addMessage(new ReportEntry(tripliserException, Status.FAILURE, Scope.INPUT));
	}

	@Test
	public void reportsAErrorMessageAtInputScopeIfInputTripliserThrowsAnException() throws TripliserException {
		List<InputStream> inputs = new ArrayList<InputStream>();
		inputs.add(IOUtils.toInputStream("input"));
		Mapping mapping = new MappingBuilder().toMapping();
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		RuntimeException exception = new RuntimeException("Error!");

		when(tripliserManager.getReporter(mapping, Scope.INPUT)).thenReturn(reporter);
		when(queryableInputFactory.createSaxonXPathTripliserDocument(eq("#0"), streamMatches("input"))).thenReturn(input);
		when(inputTripliserFactory.createInputTripliser(input, mapping, tripliserManager)).thenThrow(exception);

		TripleGraphIterator tripleGraphIterator = new TripleGraphIterator(inputs.iterator(), tripliserManager, mapping, graphContext,
				inputTripliserFactory, queryableInputFactory);

		assertFalse(tripleGraphIterator.hasNext());

		verify(reporter).addMessage(new ReportEntry(exception, Status.ERROR, Scope.INPUT));
	}

	private InputStream streamMatches(final String streamContent) {
		return argThat(new ArgumentMatcher<InputStream>(){
			@Override
			public boolean matches(Object argument) {
				InputStream stream = (InputStream)argument;
				try {
					return IOUtils.toString(stream).equals(streamContent);
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		});
	}

}
