package org.daverog.tripliser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Tripliser;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.graphs.GraphContextBuilder;
import org.daverog.tripliser.graphs.GraphContextFactory;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.graphs.TripleGraphCollector;
import org.daverog.tripliser.graphs.TripleGraphCollectorFactory;
import org.daverog.tripliser.graphs.TripleGraphIteratorFactory;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.report.TripliserReport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;


public class TripliserTest extends MockitoTestBase {

	private static final String SUPPORTED_MIME_TYPE = "application/rdf+xml";
	private static final String UNSUPPORTED_MIME_TYPE = "monkey/jazz";
	private static final String BASIC_INPUT_XML = "<xml></xml>";

	@Mock TripleGraphCollectorFactory tripleGraphCollectionMapFactory;
	@Mock GraphContextFactory graphContextFactory;
	@Mock TripleGraphIteratorFactory tripleGraphIteratorFactory;
	@Mock TripleGraph tripleGraph;
	@Mock TripleGraphCollector tripleGraphCollector;
	@Mock Iterator<TripleGraph> tripleGraphIterator;
	@Mock TripliserReport report;

	private Tripliser tripliser;
	private Mapping mapping;
	private GraphContext graphContext;


	@Before
	public void setUp() {
		mapping = new Mapping();

		tripliser = new Tripliser(null, tripleGraphCollectionMapFactory,
				tripleGraphIteratorFactory, graphContextFactory);

		setInternalState(tripliser, "mapping", mapping );

		graphContext = new GraphContextBuilder().toGraphContext();

		when(tripleGraph.getReport()).thenReturn(report);
		when(report.isSuccess()).thenReturn(true);
	}

	@Test
	public void generatingATripleGraphWhenNoInputsHaveBeenSuppliedThrowsATripliserException(){
		try {
			 tripliser.generateRdf();
			 exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("No inputs have been supplied", e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void ifATripleGraphGenerationFailsAnExceptionIsThrownWhenObtainingTheRdf() throws TripliserException{
		when(graphContextFactory.createGraphContext(eq(mapping), anyMap())).thenReturn(graphContext);
		when(tripleGraphCollector.convertToTripleGraph()).thenReturn(tripleGraph);
		when(tripleGraphCollectionMapFactory.create(streamIteratorContains(BASIC_INPUT_XML), eq(graphContext), eq(Scope.MAPPING))).thenReturn(tripleGraphCollector);
		when(report.isSuccess()).thenReturn(false);

		try {
			tripliser.setInput(BASIC_INPUT_XML).generateRdf();
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("Triple graph generation failed", e.getMessage());
			assertEquals(tripleGraph, e.getTripleGraph());
		}
	}

	@Test
	public void whenANullInputHasBeenSuppliedATripliserExceptionIsThrown(){
		try {
			tripliser.setInput(null);
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("Supplied input is null", e.getMessage());
		}
	}

	@Test
	public void whenANullInputStreamHasBeenSuppliedATripliserExceptionIsThrown() {
		try {
			tripliser.setInputStream(null);
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("Supplied input stream is null", e.getMessage());
		}
	}

	@Test
	public void whenAnEmptyInputHasBeenSuppliedATripliserExceptionIsThrown(){
		try {
			tripliser.setInput("");
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("Supplied input is empty", e.getMessage());
		}
	}

	@Test
	public void whenAnEmptyInputStreamHasBeenSuppliedATripliserExceptionIsThrown() {
		try {
			tripliser.setInputStream(new ByteArrayInputStream(new byte[0]));
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("Supplied input stream is empty", e.getMessage());
		}
	}

	@Test
	public void whenAnEmptyInputHasBeenSuppliedInAListATripliserExceptionIsThrown(){
		try {
			tripliser.setInputs(Arrays.asList(""));
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("Supplied input 1 is empty", e.getMessage());
		}
	}

	@Test
	public void whenAnEmptyInputStreamHasBeenSuppliedInAListATripliserExceptionIsThrown() {
		try {
			List<InputStream> streams = new ArrayList<InputStream>();
			streams.add(new ByteArrayInputStream(new byte[0]));
			tripliser.setInputStreams(streams );
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("Supplied input stream 1 is empty", e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void theSerialisedStringOfAGeneratedTripleGraphIsReturnedForAStringInput() throws TripliserException {
		when(graphContextFactory.createGraphContext(eq(mapping), anyMap())).thenReturn(graphContext);
		when(tripleGraphCollector.convertToTripleGraph()).thenReturn(tripleGraph);
		when(tripleGraphCollectionMapFactory.create(streamIteratorContains(BASIC_INPUT_XML), eq(graphContext), eq(Scope.MAPPING))).thenReturn(tripleGraphCollector);
		when(tripleGraph.toString(anyString())).thenReturn("Serialised string");

		assertEquals("Serialised string", tripliser.setInput(BASIC_INPUT_XML).generateRdf());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void theSerialisedStringOfAGeneratedTripleGraphIsReturnedForAInputStream() throws TripliserException, UnsupportedEncodingException, IOException {
		when(graphContextFactory.createGraphContext(eq(mapping), anyMap())).thenReturn(graphContext);
		when(tripleGraphCollector.convertToTripleGraph()).thenReturn(tripleGraph);
		when(tripleGraphCollectionMapFactory.create(streamIteratorContains(BASIC_INPUT_XML), eq(graphContext), eq(Scope.MAPPING))).thenReturn(tripleGraphCollector);
		when(tripleGraph.toString(anyString())).thenReturn("Serialised string");

		assertEquals("Serialised string", tripliser.setInputStream(new ByteArrayInputStream(BASIC_INPUT_XML.getBytes("UTF-8"))).generateRdf());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void theSerialisedStringOfAGeneratedTripleGraphIsReturnedForAListOfStringInputs() throws TripliserException {
		when(graphContextFactory.createGraphContext(eq(mapping), anyMap())).thenReturn(graphContext);
		when(tripleGraphCollector.convertToTripleGraph()).thenReturn(tripleGraph);
		when(tripleGraphCollectionMapFactory.create(streamIteratorContains(BASIC_INPUT_XML), eq(graphContext), eq(Scope.MAPPING))).thenReturn(tripleGraphCollector);
		when(tripleGraph.toString(anyString())).thenReturn("Serialised string");

		assertEquals("Serialised string", tripliser.setInputs(Arrays.asList(BASIC_INPUT_XML)).generateRdf());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void theSerialisedStringOfAGeneratedTripleGraphIsReturnedForAListOfInputStreams() throws TripliserException, UnsupportedEncodingException, IOException {
		List<InputStream> streams = new ArrayList<InputStream>();
		streams.add(new ByteArrayInputStream(BASIC_INPUT_XML.getBytes("UTF-8")));

		when(graphContextFactory.createGraphContext(eq(mapping), anyMap())).thenReturn(graphContext);
		when(tripleGraphCollector.convertToTripleGraph()).thenReturn(tripleGraph);
		when(tripleGraphCollectionMapFactory.create(streamIteratorContains(BASIC_INPUT_XML), eq(graphContext), eq(Scope.MAPPING))).thenReturn(tripleGraphCollector);
		when(tripleGraph.toString(anyString())).thenReturn("Serialised string");

		assertEquals("Serialised string", tripliser.setInputStreams(streams).generateRdf());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void theSerialisedStringOfAGeneratedTripleGraphIsReturnedForAIteratorOfInputStreams() throws TripliserException, UnsupportedEncodingException, IOException {
		List<InputStream> streams = new ArrayList<InputStream>();
		streams.add(new ByteArrayInputStream(BASIC_INPUT_XML.getBytes("UTF-8")));

		when(graphContextFactory.createGraphContext(eq(mapping), anyMap())).thenReturn(graphContext);
		when(tripleGraphCollector.convertToTripleGraph()).thenReturn(tripleGraph);
		when(tripleGraphCollectionMapFactory.create(streamIteratorContains(BASIC_INPUT_XML), eq(graphContext), eq(Scope.MAPPING))).thenReturn(tripleGraphCollector);
		when(tripleGraph.toString(anyString())).thenReturn("Serialised string");

		assertEquals("Serialised string", tripliser.setInputIterator(streams.iterator()).generateRdf());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void aGeneratedTripleGraphIsReturned() throws TripliserException {
		when(graphContextFactory.createGraphContext(eq(mapping), anyMap())).thenReturn(graphContext);
		when(tripleGraphCollector.convertToTripleGraph()).thenReturn(tripleGraph);
		when(tripleGraphCollectionMapFactory.create(any(Iterator.class), eq(graphContext), eq(Scope.MAPPING))).thenReturn(tripleGraphCollector);

		assertEquals(tripleGraph, tripliser.setInput(BASIC_INPUT_XML).generateTripleGraph());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void aGeneratedTripleGraphCollectionIsReturned() throws TripliserException {
		when(graphContextFactory.createGraphContext(eq(mapping), anyMap())).thenReturn(graphContext);
		when(tripleGraphCollectionMapFactory.create(any(Iterator.class), eq(graphContext), eq(Scope.GRAPH))).thenReturn(tripleGraphCollector);

		assertEquals(tripleGraphCollector, tripliser.setInput(BASIC_INPUT_XML).generateTripleGraphCollection());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void settingTheMimeTypeDeterminesTheMimeTypeOfTheReturnedRdf() throws TripliserException{
		when(graphContextFactory.createGraphContext(eq(mapping), anyMap())).thenReturn(graphContext);
		when(tripleGraphCollector.convertToTripleGraph()).thenReturn(tripleGraph);
		when(tripleGraphCollectionMapFactory.create(any(Iterator.class), eq(graphContext), eq(Scope.MAPPING))).thenReturn(tripleGraphCollector);
		when(tripleGraph.toString(SUPPORTED_MIME_TYPE)).thenReturn("Serialised string");

		String generatedRdf = tripliser.setMimeType(SUPPORTED_MIME_TYPE).setInput(BASIC_INPUT_XML).generateRdf();
		assertEquals("Serialised string", generatedRdf);
	}

	@Test
	public void settingAnUnsupportedMimeTypeThrowsATripliserException() {
		try {
			tripliser.setMimeType(UNSUPPORTED_MIME_TYPE);
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("Mime type " + UNSUPPORTED_MIME_TYPE + " is unsupported", e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void supportingInputsArePassedToTheGraphContextFactory() throws Exception{
		when(graphContextFactory.createGraphContext(eq(mapping), streamMapContains("key", "value"))).thenReturn(graphContext);
		when(tripleGraphCollector.convertToTripleGraph()).thenReturn(tripleGraph);
		when(tripleGraphCollectionMapFactory.create(any(Iterator.class), eq(graphContext), eq(Scope.MAPPING))).thenReturn(tripleGraphCollector);

		Map<String, String> supportingDocs = new HashMap<String, String>();
		supportingDocs.put("key", "value");

		assertEquals(tripleGraph, tripliser.setSupportingInputs(supportingDocs).setInput(BASIC_INPUT_XML).generateTripleGraph());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void supportingInputStreamsArePassedToTheGraphContextFactory() throws Exception{
		when(graphContextFactory.createGraphContext(eq(mapping), streamMapContains("key", "value"))).thenReturn(graphContext);
		when(tripleGraphCollector.convertToTripleGraph()).thenReturn(tripleGraph);
		when(tripleGraphCollectionMapFactory.create(any(Iterator.class), eq(graphContext), eq(Scope.MAPPING))).thenReturn(tripleGraphCollector);

		Map<String, InputStream> supportingDocs = new HashMap<String, InputStream>();
		supportingDocs.put("key", new ByteArrayInputStream("value".getBytes("UTF-8")));

		assertEquals(tripleGraph, tripliser.setSupportingInputStreams(supportingDocs).setInput(BASIC_INPUT_XML).generateTripleGraph());
	}

	@Test
	public void anEmptySupportingInputThrowsAnException() {
		try {
			Map<String, String> supportingDocs = new HashMap<String, String>();
			supportingDocs.put("key", "");
			tripliser.setSupportingInputs(supportingDocs);
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("Supporting input 'key' is empty", e.getMessage());
		}
	}

	@Test
	public void aNullSupportingInputThrowsAnException() {
		try {
			Map<String, String> supportingDocs = new HashMap<String, String>();
			supportingDocs.put("key", null);
			tripliser.setSupportingInputs(supportingDocs);
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("Supporting input 'key' is null", e.getMessage());
		}
	}

	@Test
	public void anEmptySupportingInputStreamThrowsAnException() {
		try {
			Map<String, InputStream> supportingDocs = new HashMap<String, InputStream>();
			supportingDocs.put("key", new ByteArrayInputStream(new byte[0]));
			tripliser.setSupportingInputStreams(supportingDocs);
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("Supporting input stream 'key' is empty", e.getMessage());
		}
	}

	@Test
	public void aNullSupportingInputStreamThrowsAnException() {
		try {
			Map<String, InputStream> supportingDocs = new HashMap<String, InputStream>();
			supportingDocs.put("key", null);
			tripliser.setSupportingInputStreams(supportingDocs);
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("Supporting input stream 'key' is null", e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void aTripleGraphIteratorIsProduced() throws TripliserException{
		when(graphContextFactory.createGraphContext(eq(mapping), anyMap())).thenReturn(graphContext);
		when(tripleGraphIteratorFactory.createTripleGraphIterator(eq(Scope.GRAPH), streamIteratorContains(BASIC_INPUT_XML), eq(graphContext)))
			.thenReturn(tripleGraphIterator);

		assertEquals(tripleGraphIterator, tripliser.setInput(BASIC_INPUT_XML).setMergeScope(Scope.GRAPH).getTripleGraphIterator());
	}

	@Test
	public void whenATripleGraphIteratorIsRequestedAtResourceMappingScopeATripliserExceptionIsThrown() {
		try {
			tripliser.setInput(BASIC_INPUT_XML).setMergeScope(Scope.RESOURCE_MAPPING).getTripleGraphIterator();
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("It is not possible to iterate through graphs per RESOURCE_MAPPING. Try using the collection based approach.",
					e.getMessage());
		}
	}

	@Test
	public void whenATripleGraphIteratorIsRequestedAtGraphMappingScopeATripliserExceptionIsThrown() {
		try {
			tripliser.setInput(BASIC_INPUT_XML).setMergeScope(Scope.GRAPH_MAPPING).getTripleGraphIterator();
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("It is not possible to iterate through graphs per GRAPH_MAPPING. Try using the collection based approach.",
					e.getMessage());
		}
	}

	@Test
	public void whenATripleGraphIteratorIsRequestedAtMappingScopeATripliserExceptionIsThrown() {
		try {
			tripliser.setInput(BASIC_INPUT_XML).setMergeScope(Scope.MAPPING).getTripleGraphIterator();
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("It is not possible to iterate through graphs per MAPPING. Try using the collection based approach.",
					e.getMessage());
		}
	}

	@Test
	public void theScopePropertyIsNotAllowedToBeUsedAsMergeScope(){
		try {
			tripliser.setMergeScope(Scope.PROPERTY);
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("Scope PROPERTY is not a valid merge scope", e.getMessage());
		}
	}

	private Iterator<InputStream> streamIteratorContains(final String streamContent) {
		return argThat(new ArgumentMatcher<Iterator<InputStream>>(){
			@Override
			public boolean matches(Object argument) {
				@SuppressWarnings("unchecked")
				Iterator<InputStream> streams = (Iterator<InputStream>)argument;
				if (!streams.hasNext()) return false;
				InputStream stream = streams.next();
				if (streams.hasNext()) return false;
				try {
					return IOUtils.toString(stream).equals(streamContent);
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		});
	}

	private Map<String, InputStream> streamMapContains(final String name, final String streamContent) {
		return argThat(new ArgumentMatcher<Map<String, InputStream>>(){
			@Override
			public boolean matches(Object argument) {
				@SuppressWarnings("unchecked")
				Map<String, InputStream> streamMap = (Map<String, InputStream>)argument;
				if (streamMap.size() != 1) return false;
				try {
					String key = streamMap.keySet().iterator().next();
					String value = IOUtils.toString(streamMap.get(key));
					return key.equals(name) && value.equals(streamContent);
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		});
	}

}
