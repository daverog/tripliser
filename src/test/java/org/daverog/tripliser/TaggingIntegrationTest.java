package org.daverog.tripliser;

import static org.daverog.tripliser.testutils.XmlFileUtils.loadXmlFile;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.TripliserFactory;
import org.daverog.tripliser.graphs.TripleGraph;
import org.junit.Before;
import org.junit.Test;


public class TaggingIntegrationTest extends MockitoTestBase {

	@Before
	public void setUp(){
		XMLUnit.setIgnoreWhitespace(true);
	}

	@Test
	public void tagsAreExtractedBasedOnMapping() throws Exception{
		TripleGraph tripleGraph = createTripleGraph("tags", "");

		assertTrue(tripleGraph.getReport().isSuccess());
		assertEquals("Test Name", tripleGraph.getTagValue("tag1"));
		assertNull(tripleGraph.getTagValue("tag2"));
	}

	public TripleGraph createTripleGraph(String filename, String mappingOption) throws Exception {
		String mappingXml = loadXmlFile("integration-tests/mappings/" + filename + mappingOption + ".xml");
		String inputXml = loadXmlFile("integration-tests/source/" + filename + ".xml");

		return TripliserFactory
			.instance()
			.setMapping(IOUtils.toInputStream(mappingXml))
			.create()
			.setInput(inputXml)
			.generateTripleGraph();
	}

}
