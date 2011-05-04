package org.daverog.tripliser.graphs;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.graphs.GraphContextFactory;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.mapping.model.MappingBuilder;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.query.QueryableInputFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class GraphContextFactoryTest extends MockitoTestBase {

	@Mock QueryableInputFactory queryableInputFactory;
	@Mock Queryable input;

	private GraphContextFactory graphContextFactory;

	@Before
	public void setUp(){
		graphContextFactory = new GraphContextFactory(queryableInputFactory);
	}

	@Test
	public void supportingInputsAreConvertedToQueryableSupportingInputs() throws TripliserException {
		Mapping mapping = new MappingBuilder().toMapping();
		ByteArrayInputStream stream = new ByteArrayInputStream(new byte[0]);
		Map<String, InputStream> supportingInputs = new HashMap<String, InputStream>();
		supportingInputs.put("inputA", stream);

		when(queryableInputFactory.createSaxonXPathTripliserDocument("inputA", stream)).thenReturn(input);

		GraphContext graphContext = graphContextFactory.createGraphContext(mapping, supportingInputs);

		assertEquals(input, graphContext.getSupportingInput("inputA"));
	}

	@Test
	public void ifSupportingInputsAreNullNoneAreLoaded() throws TripliserException {
		Mapping mapping = new MappingBuilder().toMapping();

		graphContextFactory.createGraphContext(mapping, null);

		verifyZeroInteractions(queryableInputFactory);
	}


	@Test
	public void aNamespaceIsAddedToTheGraphContext() throws TripliserException {
		Mapping mapping = new MappingBuilder()
			.addNamespace("x", "http://bbc.co.uk/y")
			.toMapping();

		GraphContext graphContext = graphContextFactory.createGraphContext(mapping, new HashMap<String, InputStream>());

		assertEquals("http://bbc.co.uk/y", graphContext.getNamespace("x"));
	}

	@Test
	public void aConstantIsAddedToTheGraphContext() throws TripliserException {
		Mapping mapping = new MappingBuilder()
			.addConstant("x", "y")
			.toMapping();

		GraphContext graphContext = graphContextFactory.createGraphContext(mapping, new HashMap<String, InputStream>());

		assertEquals("y", graphContext.getConstant("x"));
	}

	@Test
	public void ifConstantsAreNullTheyAreNotAddedToTheGraphContext() throws TripliserException {
		Mapping mapping = new MappingBuilder()
			.toMapping();

		GraphContext graphContext = graphContextFactory.createGraphContext(mapping, new HashMap<String, InputStream>());

		assertEquals(null, graphContext.getConstant("x"));
	}

	@Test
	public void ifNamespacesAreNullTheyAreNotAddedToTheGraphContext() throws TripliserException {
		Mapping mapping = new MappingBuilder()
			.toMapping();

		GraphContext graphContext = graphContextFactory.createGraphContext(mapping, new HashMap<String, InputStream>());

		assertEquals(null, graphContext.getNamespace("x"));
	}

}
