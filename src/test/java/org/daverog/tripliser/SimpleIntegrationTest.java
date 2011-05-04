package org.daverog.tripliser;

import static org.daverog.tripliser.testutils.XmlFileUtils.loadXmlFile;

import org.apache.commons.io.IOUtils;
import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.TripliserFactory;
import org.daverog.tripliser.exception.TripliserException;
import org.junit.Test;


public class SimpleIntegrationTest extends MockitoTestBase {

	@Test
	public void aBadNamespaceInTheMappingFileResultsInATripliserException() {
		String mappingXml = loadXmlFile("integration-tests/mappings/bad-namespace.xml");

		try {
			TripliserFactory
				.instance()
				.setMapping(IOUtils.toInputStream(mappingXml))
				.create();
			exceptionExpected();
		} catch(TripliserException e) {
			assertEquals("Invalid namespace URI '^invalid'", e.getCause().getCause().getCause().getMessage());
		}
	}

}
