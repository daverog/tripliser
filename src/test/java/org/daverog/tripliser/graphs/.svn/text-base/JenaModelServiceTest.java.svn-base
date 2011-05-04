package org.daverog.tripliser.graphs;

import java.net.URISyntaxException;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.exception.InvalidPropertyMappingException;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

public class JenaModelServiceTest extends MockitoTestBase {

	private Model model;
	private JenaModelService jenaModelService;

	@Before
	public void setUp(){
		model = ModelFactory.createDefaultModel();
		jenaModelService = new JenaModelService();
	}

	@Test
	public void aPropertyCanBeObtainedIfTheNamespaceIsBuiltIn() throws InvalidPropertyMappingException{
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		Property rdfType = jenaModelService.getProperty(model, "rdf:type", graphContext);

		assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#", rdfType.getNameSpace());
		assertEquals("type", rdfType.getLocalName());
	}

	@Test
	public void aPropertyCanBeObtainedIfTheNamespaceIsAddedToTheModel() throws InvalidPropertyMappingException{
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		model.setNsPrefix("jazz", "http://jazz/");

		Property rdfType = jenaModelService.getProperty(model, "jazz:potato", graphContext);

		assertEquals("http://jazz/", rdfType.getNameSpace());
		assertEquals("potato", rdfType.getLocalName());
	}

	@Test
	public void aPropertyCanBeObtainedIfTheNamespaceIsSuppliedAndIsAddedToModel() throws InvalidPropertyMappingException{
		GraphContext graphContext = new GraphContextBuilder()
			.addNamespace("jazz", "http://jazz/")
			.toGraphContext();

		Property rdfType = jenaModelService.getProperty(model, "jazz:potato", graphContext);

		assertEquals("http://jazz/", rdfType.getNameSpace());
		assertEquals("potato", rdfType.getLocalName());
		assertEquals("http://jazz/", model.getNsPrefixURI("jazz"));
	}

	@Test
	public void throwsAnInvalidPropertyMappingExceptionIfAnUnknownNamespaceIsUsed() {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		try {
			jenaModelService.getProperty(model, "turnip:size", graphContext);
			fail("Should have thrown an exception");
		} catch(InvalidPropertyMappingException e) {
			assertEquals("A mapping contains an invalid namespace prefix 'turnip'", e.getMessage());
		}
	}

	@Test
	public void throwsAnInvalidPropertyMappingExceptionIfThePropertyNameDoesNotContainAColon() {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		try {
			jenaModelService.getProperty(model, "rdftype", graphContext);
			fail("Should have thrown an exception");
		} catch(InvalidPropertyMappingException e) {
			assertEquals("A mapping contains an invalid name 'rdftype' which should be of the form 'prefix:propertyName'", e.getMessage());
		}
	}

	@Test
	public void throwsAnInvalidPropertyMappingExceptionIfThePropertyNameContainsTwoColons() {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		try {
			jenaModelService.getProperty(model, "rdf:type:", graphContext);
			fail("Should have thrown an exception");
		} catch(InvalidPropertyMappingException e) {
			assertEquals("A mapping contains an invalid name 'rdf:type:' which should be of the form 'prefix:propertyName'", e.getMessage());
		}
	}

	@Test
	public void returnsTheValueIfRecognisedAsANonHttpUri() throws URISyntaxException {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		String uri = "urn:lemon.colour";
		assertEquals(uri, jenaModelService.convertToUri(model, uri, graphContext));
	}

	@Test
	public void throwsAnInvalidPropertyMappingExceptionIfThePropertyNameIsAnInvalidURI() {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		try {
			jenaModelService.convertToUri(model, "*^R$(^&%*&", graphContext);
			fail("Should have thrown an exception");
		} catch(URISyntaxException e) {
			//Success
		}
	}

	@Test
	public void returnsTheValueIfRecognisedAsAHttpUri() throws URISyntaxException {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		String uri = "http://lemon/colour";
		assertEquals(uri, jenaModelService.convertToUri(model, uri, graphContext));
	}

	@Test
	public void returnsTheConvertedValueIfNotRecognisedAsAUri() throws URISyntaxException {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", jenaModelService.convertToUri(model, "rdf:type", graphContext));
	}

	@Test
	public void loadsNamespacesIntoAModel() {
		GraphContext graphContext = new GraphContextBuilder()
			.addNamespace("prefix", "namespace")
			.toGraphContext();

		jenaModelService.loadNamespacesIntoModel(graphContext, model);

		assertEquals("namespace", model.getNsPrefixURI("prefix"));
	}

}
