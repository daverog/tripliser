package org.daverog.tripliser.triplisers;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.IgnoredGraphMappingException;
import org.daverog.tripliser.exception.InvalidGraphMappingException;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.graphs.MutableTripleGraph;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.mapping.model.GraphMappingBuilder;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.mapping.model.MappingBuilder;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.report.TripliserReporter;
import org.junit.Test;
import org.mockito.Mock;


public class InputTripliserTest extends MockitoTestBase {

	@Mock TripliserReporter reporter;
	@Mock Queryable input;
	@Mock Queryable input2;
	@Mock MutableTripleGraph tripleGraph;
	@Mock GraphTripliserFactory graphTripliserFactory;
	@Mock TripliserManager tripliserManager;
	@Mock TripleGraph tripleGraph1;
	@Mock TripleGraph tripleGraph2;
	@Mock GraphTripliser graphTripliser;
	@Mock GraphTripliser graphTripliser2;

	@Test
	public void whenAMappingHasASingleGraphMappingASingleGraphTripliserIsCreated() throws TripliserException, InvalidGraphMappingException, IgnoredGraphMappingException {
		GraphMapping graphMapping = new GraphMappingBuilder().toGraphMapping();;
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(graphMapping)
			.toMapping();

		when(graphTripliserFactory.createGraphTripliser(input, graphMapping, tripliserManager)).thenReturn(graphTripliser);
		when(graphTripliser.hasNext()).thenReturn(true).thenReturn(false);
		when(graphTripliser.next()).thenReturn(tripleGraph1);

		InputTripliser inputTripliser = new InputTripliser(input, mapping, tripliserManager, graphTripliserFactory);

		assertEquals(tripleGraph1, inputTripliser.next());
		assertFalse(inputTripliser.hasNext());
	}

	@Test
	public void whenAMappingHasATwoGraphMappingsTwoGraphTriplisersAreCreated() throws TripliserException, InvalidGraphMappingException, IgnoredGraphMappingException {
		GraphMapping graphMapping = new GraphMappingBuilder().toGraphMapping();;
		GraphMapping graphMapping2 = new GraphMappingBuilder().toGraphMapping();;
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(graphMapping)
			.addGraphMapping(graphMapping2)
			.toMapping();

		when(graphTripliserFactory.createGraphTripliser(input, graphMapping, tripliserManager)).thenReturn(graphTripliser);
		when(graphTripliserFactory.createGraphTripliser(input, graphMapping2, tripliserManager)).thenReturn(graphTripliser2);
		when(graphTripliser.hasNext()).thenReturn(true).thenReturn(false);
		when(graphTripliser.next()).thenReturn(tripleGraph1);
		when(graphTripliser2.hasNext()).thenReturn(true).thenReturn(false);
		when(graphTripliser2.next()).thenReturn(tripleGraph2);

		InputTripliser inputTripliser = new InputTripliser(input, mapping, tripliserManager, graphTripliserFactory);

		assertEquals(tripleGraph1, inputTripliser.next());
		assertEquals(tripleGraph2, inputTripliser.next());
		assertFalse(inputTripliser.hasNext());
	}

	@Test
	public void whenAnInvalidGraphMappingExceptionIsThrownWhenCreatingAGraphTripliserAGraphMappingLevelFailureIsReported() throws TripliserException, InvalidGraphMappingException, IgnoredGraphMappingException {
		GraphMapping graphMapping = new GraphMappingBuilder().toGraphMapping();;
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(graphMapping)
			.toMapping();
		InvalidGraphMappingException cause = new InvalidGraphMappingException("Error!");

		when(tripliserManager.getReporter(graphMapping, Scope.GRAPH_MAPPING)).thenReturn(reporter);
		when(graphTripliserFactory.createGraphTripliser(input, graphMapping, tripliserManager)).thenThrow(cause);

		InputTripliser inputTripliser = new InputTripliser(input, mapping, tripliserManager, graphTripliserFactory);

		assertFalse(inputTripliser.hasNext());
		verify(reporter).addMessage(new ReportEntry(cause, Status.FAILURE, Scope.GRAPH_MAPPING, graphMapping));
	}

	@Test
	public void whenAnIgnoredGraphMappingExceptionIsThrownWhenCreatingAGraphTripliserAGraphMappingLevelWarningIsReported() throws TripliserException, InvalidGraphMappingException, IgnoredGraphMappingException {
		GraphMapping graphMapping = new GraphMappingBuilder().toGraphMapping();;
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(graphMapping)
			.toMapping();
		IgnoredGraphMappingException cause = new IgnoredGraphMappingException("Error!");

		when(tripliserManager.getReporter(graphMapping, Scope.GRAPH_MAPPING)).thenReturn(reporter);
		when(graphTripliserFactory.createGraphTripliser(input, graphMapping, tripliserManager)).thenThrow(cause);

		InputTripliser inputTripliser = new InputTripliser(input, mapping, tripliserManager, graphTripliserFactory);

		assertFalse(inputTripliser.hasNext());
		verify(reporter).addMessage(new ReportEntry(cause, Status.WARNING, Scope.GRAPH_MAPPING, graphMapping));
	}

	@Test
	public void whenAnExceptionIsThrownWhenCreatingAGraphTripliserAGraphMappingLevelErrorIsReported() throws TripliserException, InvalidGraphMappingException, IgnoredGraphMappingException {
		GraphMapping graphMapping = new GraphMappingBuilder().toGraphMapping();;
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(graphMapping)
			.toMapping();
		RuntimeException cause = new RuntimeException("Error!");

		when(tripliserManager.getReporter(graphMapping, Scope.GRAPH_MAPPING)).thenReturn(reporter);
		when(graphTripliserFactory.createGraphTripliser(input, graphMapping, tripliserManager)).thenThrow(cause);

		InputTripliser inputTripliser = new InputTripliser(input, mapping, tripliserManager, graphTripliserFactory);

		assertFalse(inputTripliser.hasNext());
		verify(reporter).addMessage(new ReportEntry(cause, Status.ERROR, Scope.GRAPH_MAPPING, graphMapping));
	}

}
