package org.daverog.tripliser;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.custommonkey.xmlunit.XMLUnit.buildTestDocument;
import static org.daverog.tripliser.testutils.XmlFileUtils.loadXmlDocument;
import static org.daverog.tripliser.testutils.XmlFileUtils.loadXmlFile;
import static org.daverog.tripliser.testutils.XmlFileUtils.validateXml;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Tripliser;
import org.daverog.tripliser.TripliserFactory;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.graphs.TripleGraphCollection;
import org.daverog.tripliser.report.TripliserReport;
import org.daverog.tripliser.testutils.ChildNodeSorter;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class ScopedMultiGraphIntegrationTest extends MockitoTestBase {

	@Before
	public void setUp(){
		XMLUnit.setIgnoreWhitespace(true);
	}


	@Test
	public void mergingAtMappingScopeCreatesASingleGraphContainingAllTriples() throws Exception {
		genericScopeTest(Scope.MAPPING, 1, new int[]{26}, true);
	}

	@Test
	public void mergingAtInputScopeCreatesGraphsContainingTriplesForEachInput() throws Exception {
		genericScopeTest(Scope.INPUT, 2, new int[]{12, 14}, true);
	}

	@Test
	public void mergingAtGraphMappingScopeCreatesGraphsContainingTriplesForEachGraphMapping() throws Exception {
		genericScopeTest(Scope.GRAPH_MAPPING, 2, new int[]{12, 14}, true);
	}

	@Test
	public void mergingAtGraphScopeCreatesGraphsContainingTriplesForEachGraph() throws Exception {
		genericScopeTest(Scope.GRAPH, 5, new int[]{4, 4, 4, 4, 10}, true);
	}

	@Test
	public void mergingAtResourceMappingScopeCreatesGraphsContainingTriplesForEachResourceMapping() throws Exception {
		genericScopeTest(Scope.RESOURCE_MAPPING, 3, new int[]{12, 12, 2}, true);
	}

	@Test
	public void mergingAtResourceScopeCreatesGraphsContainingTriplesForEachResource() throws Exception {
		genericScopeTest(Scope.RESOURCE, 13, new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, false);
	}


	public void genericScopeTest(Scope mergeScope, int numberOfGraphs, int[] expectedNumberOfTriplesPerGraph, boolean checkFixtureFiles) throws Exception {
		String mappingXml = loadXmlFile("integration-tests/mappings/merge.xml");
		String inputXml = loadXmlFile("integration-tests/source/merge-A.xml");
		String inputXml2 = loadXmlFile("integration-tests/source/merge-B.xml");
		Document[] expectedOutputRdf = new Document[numberOfGraphs];

		if (checkFixtureFiles) {
			for (int i=0; i<numberOfGraphs; i++) {
				expectedOutputRdf[i] = loadXmlDocument("integration-tests/rdf/merge-" + mergeScope.name() + "-" + (i+1) + ".rdf");
			}
		}

		Tripliser tripliser = TripliserFactory
			.instance()
			.setMapping(IOUtils.toInputStream(mappingXml))
			.create();

		TripleGraphCollection collection = tripliser
			.setMergeScope(mergeScope)
			.setInputs(Arrays.asList(inputXml, inputXml2))
			.generateTripleGraphCollection();

		Iterator<TripleGraph> graphs = collection.iterator();
		while (graphs.hasNext()) {
			showOutput(graphs.next());
		}

		graphs = collection.iterator();
		int index = 0;
		while (graphs.hasNext()) {
			TripleGraph graph = graphs.next();
			System.out.println("Validating input for collection-based " + index);
			showOutput(graph);
			basicValidation(graph, expectedNumberOfTriplesPerGraph[index]);
			if (checkFixtureFiles) validateOutput(expectedOutputRdf[index], graph);
			index++;
		}

		assertEquals(numberOfGraphs, index);
		assertFalse(graphs.hasNext());

		if (mergeScope != Scope.GRAPH_MAPPING && mergeScope != Scope.RESOURCE_MAPPING && mergeScope != Scope.MAPPING) {
			//Now test iterator
			Iterator<TripleGraph> graphIterator = tripliser
				.setMergeScope(mergeScope)
				.setInputs(Arrays.asList(inputXml, inputXml2))
				.getTripleGraphIterator();

			index = 0;
			TripliserReport previousReport = null;
			while (graphIterator.hasNext()) {
				TripleGraph graph = graphIterator.next();
				TripliserReport report = graph.getReport();
				assertNotSame(report, previousReport);
				System.out.println("Validating input for iterator-based " + index);
				showOutput(graph);
				basicValidation(graph, expectedNumberOfTriplesPerGraph[index]);
				if (checkFixtureFiles) validateOutput(expectedOutputRdf[index], graph);
				previousReport = report;
				index++;
			}

			assertEquals(numberOfGraphs, index);
			assertFalse(graphIterator.hasNext());
		}
	}

	private void showOutput(TripleGraph tripleGraph) throws TripliserException {
		System.out.println(tripleGraph.getReport().toString());
		String outputRdf = tripleGraph.toString("application/rdf+xml");
		System.out.println("Produced:\n\n" + outputRdf);
		//System.out.println("Comparing:\n\n" + outputXmlDocument(expectedOutputRdf) + "\n\nwith\n\n" + outputXmlDocument(outputRdfDocument));
	}

	private void basicValidation(TripleGraph tripleGraph, int expectedNumberOfTriplesPerGraph) {
		if(!tripleGraph.getReport().isSuccess()){
			fail("Should not have failed when generating graph");
		}

		assertEquals((long)expectedNumberOfTriplesPerGraph, tripleGraph.getModel().size());
	}

	private void validateOutput(Document expectedOutputRdf,
			TripleGraph tripleGraph) throws TripliserException, Exception,
			SAXException, IOException {
		String outputRdf = tripleGraph.toString("application/rdf+xml");
		validateXml(outputRdf);

		Document outputRdfDocument = buildTestDocument(outputRdf);

		ChildNodeSorter.sortRdfChildNodes(expectedOutputRdf, false, 10, null);
		ChildNodeSorter.sortRdfChildNodes(outputRdfDocument, false, 10, null);

		assertXMLEqual(expectedOutputRdf, outputRdfDocument);
	}

}
