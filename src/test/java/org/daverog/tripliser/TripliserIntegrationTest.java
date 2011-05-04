package org.daverog.tripliser;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.custommonkey.xmlunit.XMLUnit.buildTestDocument;
import static org.daverog.tripliser.testutils.XmlFileUtils.loadXmlDocument;
import static org.daverog.tripliser.testutils.XmlFileUtils.loadXmlFile;
import static org.daverog.tripliser.testutils.XmlFileUtils.loadXmlFileStream;
import static org.daverog.tripliser.testutils.XmlFileUtils.validateXml;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.SingletonIterator;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.daverog.tripliser.Tripliser;
import org.daverog.tripliser.TripliserFactory;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.testutils.ChildNodeSorter;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TripliserIntegrationTest {

	private Map<String, String> supportingDocuments;

	@Before
	public void setUp(){
		XMLUnit.setIgnoreWhitespace(true);

		supportingDocuments = new HashMap<String, String>();
	}

	@Test
	public void simpleXmlInputMappedToRdfXmlOutput() throws Exception{
		standardTestOfXmlInputMappedToRdfXmlOutput("simple");
	}

	@Test
	public void simpleXmlInputsMappedToRdfXmlOutput() throws Exception{
		standardTestOfXmlInputMappedToRdfXmlOutput("simple", "", "", false, false, true, null);
	}

	@Test
	public void multiPartXmlInputMappedToRdfXmlOutput() throws Exception{
		standardTestOfXmlInputMappedToRdfXmlOutput("multiple");
	}

	@Test
	public void cbbcXmlInputMappedToRdfXmlOutput() throws Exception{
		standardTestOfXmlInputMappedToRdfXmlOutput("cbbc");
	}

	@Test
	public void scientistXmlInputMappedToRdfXmlOutput() throws Exception{
		standardTestOfXmlInputMappedToRdfXmlOutput("scientists");
	}

	@Test
	public void pipsEpisodeXmlInputMappedToRdfXmlOutput() throws Exception{
		supportingDocuments.put("series", loadXmlFile("integration-tests/source/pips-series.xml"));
		supportingDocuments.put("version", loadXmlFile("integration-tests/source/pips-version.xml"));

		standardTestOfXmlInputMappedToRdfXmlOutput("pips-episode");
	}

	@Test
	public void simpleXmlDoesNotFailCompletelyForMappingError() throws Exception {
		standardTestOfXmlInputMappedToRdfXmlOutput("simple", "-badXPath", "", true, false, false, null);
	}

	@Test
	public void simpleXmlDoesNotFailCompletelyForAnInvalidDataType() throws Exception {
		standardTestOfXmlInputMappedToRdfXmlOutput("simple", "-invalidDataType", "-no-name", true, false, false, null);
	}

	@Test
	public void simpleXmlDoesNotFailCompletelyForABadUri() throws Exception {
		standardTestOfXmlInputMappedToRdfXmlOutput("simple", "-badUri", "", true, false, false, null);
	}

	@Test
	public void simpleXmlInputMappedWithNestedPropertiesToBlankNodes() throws Exception {
		standardTestOfXmlInputMappedToRdfXmlOutput("simple", "-blanknodes", "-blanknodes", false, false, false, null);
	}

	@Test
	public void simpleXmlInputMappedWithNestedPropertiesToBlankNodesWithNoBlankNodeQuery() throws Exception {
		standardTestOfXmlInputMappedToRdfXmlOutput("simple", "-blanknodes-noquery", "-blanknodes", false, false, false, null);
	}

	@Test
	public void simpleXmlInvalidIfValueSpecifiedForBlankNode() throws Exception {
		standardTestOfXmlInputMappedToRdfXmlOutput("simple", "-blanknodes-invalidvalue", "", false, true, false, null);
	}

	@Test
	public void simpleXmlInvalidIfNoQueryOrValueSpecifiedForProperty() throws Exception {
		standardTestOfXmlInputMappedToRdfXmlOutput("simple", "-noqueryorvalue", "", false, true, false, null);
	}

	@Test
	public void simpleXmlDoesNotFailWhenUnstrictWithXPathThatDoesNotExist() throws Exception {
		standardTestOfXmlInputMappedToRdfXmlOutput("simple", "-unstrict", "", false, false, false, null);
	}

	@Test
	public void exampleUniverseXmlInputMappedToRdfXmlOutput() throws Exception{
		standardTestOfXmlInputMappedToRdfXmlOutput("universe");
	}

	@Test
	public void simpleXmlMappingWithExtensionFunctionMappedToRdfXmlOutput() throws Exception {
		Set<ExtensionFunctionDefinition> functions = new HashSet<ExtensionFunctionDefinition>();

		functions.add(new ExtensionFunctionDefinition() {

			@Override
			public ExtensionFunctionCall makeCallExpression() {
				return new ExtensionFunctionCall() {

					@Override
					public SequenceIterator call(SequenceIterator[] arguments, XPathContext xPathContext)
							throws XPathException {
						String value = arguments[0].next().getStringValue();

						if (value.equals("Test Name")) {
							return SingletonIterator.makeIterator(new StringValue("Result of special function"));
						} else {
							return SingletonIterator.makeIterator(new StringValue("Error result from special function"));
						}
					}
				};
			}

			@Override
			public SequenceType getResultType(SequenceType[] suppliedArguments) {
				return SequenceType.SINGLE_STRING;
			}

			@Override
			public StructuredQName getFunctionQName() {
				return new StructuredQName("special", "http://bbc.co.uk/functions", "function");
			}

			@Override
			public SequenceType[] getArgumentTypes() {
				return new SequenceType[] {SequenceType.SINGLE_STRING};
			}
		});

		standardTestOfXmlInputMappedToRdfXmlOutput("simple", "-extensionFunction", "-extensionFunction", false, false, false, functions);
	}

	public void standardTestOfXmlInputMappedToRdfXmlOutput(String filename) throws Exception{
		standardTestOfXmlInputMappedToRdfXmlOutput(filename, "", "", false, false, false, null);
	}

	public void standardTestOfXmlInputMappedToRdfXmlOutput(String filename, String mappingOption, String resultOption, boolean shouldFail, boolean invalidMapping, boolean additionalInput, Set<ExtensionFunctionDefinition> functions) throws Exception {
		String folder = "integration-tests";

		String mappingXml = null;

		try {
			mappingXml = loadXmlFile(folder + "/mappings/" + filename + mappingOption + ".xml");
		} catch(AssertionError e) {
			folder = "examples";
			mappingXml = loadXmlFile(folder + "/mappings/" + filename + mappingOption + ".xml");
		}

		String additionalSourceXml = null;

		String inputXml = loadXmlFile(folder + "/source/" + filename + ".xml");
		String[] inputsXml = new String[]{inputXml};
		if (additionalInput) {
			additionalSourceXml = loadXmlFile(folder + "/source/" + filename + "-additional.xml");
			inputsXml = new String[]{inputXml, additionalSourceXml};
		}

		Document expectedOutputRdf = loadXmlDocument(folder + "/rdf/" + filename + resultOption + ".rdf");
		InputStream expectedRdfAsStream = loadXmlFileStream(folder + "/rdf/" + filename + resultOption + ".rdf");
		if (additionalInput) {
			expectedOutputRdf = loadXmlDocument(folder + "/rdf/" + filename + resultOption + "-additional.rdf");
		}

		if (functions == null) functions = new HashSet<ExtensionFunctionDefinition>();

		Tripliser tripliser = null;
		try {
			tripliser = TripliserFactory
				.instance()
				.setMapping(IOUtils.toInputStream(mappingXml))
				.addFunctions(functions)
				.create();
		} catch(TripliserException e) {
			if (!invalidMapping) {
				throw e;
			}
			return;
		}

		if (invalidMapping) fail("The mapping should have been invalid");

		TripleGraph tripleGraph = null;
		tripleGraph = tripliser
			.setSupportingInputs(supportingDocuments)
			.setInputs(Arrays.asList(inputsXml))
			.generateTripleGraph();

		tripleGraph.getReport().writeXml(System.out);
		System.out.println();
		System.out.println(tripleGraph.getReport().toString());

		Model expectedModel = ModelFactory.createDefaultModel();
		expectedModel.read(expectedRdfAsStream, null);

		if (expectedModel.isIsomorphicWith(tripleGraph.getModel())) {
			System.out.println("Models are isomorphic");
			return;
		}

		String outputRdf = tripleGraph.toString("application/rdf+xml");
		validateXml(outputRdf);

		Document outputRdfDocument = buildTestDocument(outputRdf);

		ChildNodeSorter.sortRdfChildNodes(expectedOutputRdf, false, 10, null);
		ChildNodeSorter.sortRdfChildNodes(outputRdfDocument, false, 10, null);

		System.out.println("Produced:\n\n" + outputRdf);
		//System.out.println("Comparing:\n\n" + outputXmlDocument(expectedOutputRdf) + "\n\nwith\n\n" + outputXmlDocument(outputRdfDocument));

		assertXMLEqual(expectedOutputRdf, outputRdfDocument);

		if (shouldFail && tripleGraph.getReport().isSuccess()) {
			fail("Should have failed when generating graph");
		} else if(!shouldFail && !tripleGraph.getReport().isSuccess()){
			fail("Should not have failed when generating graph");
		}

	}

}
