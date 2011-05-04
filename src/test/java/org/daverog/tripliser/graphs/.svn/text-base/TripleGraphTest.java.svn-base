package org.daverog.tripliser.graphs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.graphs.JenaModelService;
import org.daverog.tripliser.graphs.MutableTripleGraph;
import org.daverog.tripliser.query.QueryService;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.value.ItemToStringConverter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TripleGraphTest {
	
	@Mock TripliserReporter tripliserReporter;
	@Mock JenaModelService jenaModelService;
	@Mock QueryService queryService;
	@Mock ItemToStringConverter itemToStringConverter;
	
	private MutableTripleGraph tripleGraph;
	private Model model;


	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		
		model = ModelFactory.createDefaultModel();
		
		tripleGraph = new  MutableTripleGraph("graph1", model, tripliserReporter, jenaModelService, queryService, itemToStringConverter);
		model.setNsPrefix("foaf", "http://foaf/");
	}
	
	@Test
	public void anRdfXmlFileIsProducedIfRequested() throws TripliserException {
		String expectedRdfXml = "<rdf:RDF\n" + 
				"    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" + 
				"    xmlns:foaf=\"http://foaf/\" > \n" + 
				"</rdf:RDF>\n";
		assertEquals(expectedRdfXml , tripleGraph.toString("application/rdf+xml"));
	}
	
	@Test
	public void anRdfXmlFileWithABaseUriIsProducedIfRequested() throws TripliserException {
		model.setNsPrefix("bbc", "http://bbc.co.uk/");
		model.add(model.createResource("http://bbc.co.uk/thing"), model.createProperty("http://bbc.co.uk/property"), model.createLiteral("value"));
		
		String expectedRdfXml = "<rdf:RDF\n" + 
		"    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" + 
		"    xmlns:foaf=\"http://foaf/\"\n" + 
		"    xmlns:bbc=\"http://bbc.co.uk/\" > \n"+
		"  <rdf:Description rdf:about=\"thing\">\n"+
		"    <bbc:property>value</bbc:property>\n"+
		"  </rdf:Description>\n"+
		"</rdf:RDF>\n";
		assertEquals(expectedRdfXml , tripleGraph.toString("application/rdf+xml", "http://bbc.co.uk/"));
	}
	
	@Test
	public void anN3FileIsProducedIfRequested() throws TripliserException {
		String expectedRdfXml = "@prefix foaf:    <http://foaf/> .\n\n";
		assertEquals(expectedRdfXml , tripleGraph.toString("text/rdf+n3"));
	}
	
	@Test
	public void anUnsupportedMimeTypeThrowsATripliserException() {
		try {
			tripleGraph.toString("text/html");
			fail();
		} catch(TripliserException e) {
			assertEquals("Mime type text/html is not supported", e.getMessage());
		}
	}
	
}
