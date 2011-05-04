package org.daverog.tripliser.graphs;


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.IgnoredPropertyMappingException;
import org.daverog.tripliser.exception.IgnoredResourceMappingException;
import org.daverog.tripliser.exception.InvalidPropertyMappingException;
import org.daverog.tripliser.exception.InvalidResourceMappingException;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.graphs.JenaModelService;
import org.daverog.tripliser.graphs.MutableTripleGraph;
import org.daverog.tripliser.mapping.model.PropertyMapping;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.daverog.tripliser.mapping.model.ResourceMappingBuilder;
import org.daverog.tripliser.mapping.model.ValueMapping;
import org.daverog.tripliser.mapping.model.ValueMappingBuilder;
import org.daverog.tripliser.query.Item;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.QueryException;
import org.daverog.tripliser.query.QueryService;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.value.ItemToStringConverter;
import org.daverog.tripliser.value.ValueGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class MutableTripleGraphTest extends MockitoTestBase {

	@Mock Node inputNode;
	@Mock Queryable input;
	@Mock Node queryNode;
	@Mock Node queryNode2;
	@Mock ValueGenerator valueGenerator;
	@Mock JenaModelService jenaModelService;
	@Mock GraphContext graphContext;
	@Mock TripliserReporter tripliserReporter;
	@Mock QueryService inputQueryService;
	@Mock ItemToStringConverter itemToStringConverter;

	private Resource resource;
	private MutableTripleGraph tripleGraph;
	private Model model;
	private Property foafName;
	private Property foafCreator;

	@Before
	public void setUp() throws InvalidPropertyMappingException {
		MockitoAnnotations.initMocks(this);

		model = ModelFactory.createDefaultModel();
		resource = model.createResource("123");

		tripleGraph = new MutableTripleGraph("graph1", model, tripliserReporter, jenaModelService, inputQueryService, itemToStringConverter);
		model.setNsPrefix("foaf", "http://foaf/");

		foafName = model.getProperty("http://foaf/", "name");
		foafCreator = model.getProperty("http://foaf/", "creator");

		when(jenaModelService.getProperty(model, "foaf:name", graphContext)).thenReturn(foafName);
		when(jenaModelService.getProperty(model, "foaf:creator", graphContext)).thenReturn(foafCreator);
	}

	@Test
	public void whenDataTypeUriSyntaxIsInvalidAnInvalidPropertyMappingExceptionIsThrown() throws  URISyntaxException, IgnoredPropertyMappingException {
		URISyntaxException uriSyntaxException = new URISyntaxException("", "");
		when(jenaModelService.convertToUri(model, "dataType", graphContext)).thenThrow(uriSyntaxException);

		PropertyMapping map = new ValueMappingBuilder()
			.dataType("dataType")
			.toPropertyMapping();

		try {
			tripleGraph.addPropertiesForResourceAndMapping(input, map, resource, inputNode, graphContext);
		} catch(InvalidPropertyMappingException e) {
			assertEquals(uriSyntaxException, e.getCause());

			assertEquals("The datatype 'dataType' is not a valid URI", e.getMessage());
		}
	}

	@Test
	public void whenRetrievingMappedItemsAQueryExceptionCauseAnInvalidPropertyMappingExceptionToBeThrown() throws QueryException, IgnoredPropertyMappingException{
		QueryException queryException  = new QueryException("Error");

		PropertyMapping map = new ValueMappingBuilder()
			.dataType("dataType")
			.toPropertyMapping();

		when(inputQueryService.getItems(input, map  , inputNode, graphContext)).thenThrow(queryException);
		try {
			tripleGraph.addPropertiesForResourceAndMapping(input, map, resource, inputNode, graphContext);
			exceptionExpected();
		} catch(InvalidPropertyMappingException e) {
			assertEquals(queryException, e.getCause());

			assertEquals("Failed to retrieve mapped items", e.getMessage());
		}
	}

	@Test
	public void whenResourceUriSyntaxIsInvalidAWarningIsAddedToTheReportAndAInvalidPropertyMappingExceptionIsThrown() throws  URISyntaxException, QueryException, IgnoredPropertyMappingException {
		URISyntaxException uriSyntaxException = new URISyntaxException("", "");
		when(jenaModelService.convertToUri(model, "http://me", graphContext)).thenThrow(uriSyntaxException);

		PropertyMapping map = new ValueMappingBuilder()
			.name("foaf:creator")
			.query("creator")
			.resource(true)
			.toPropertyMapping();

		List<Item> nodes = new ArrayList<Item>();
		nodes.add(queryNode);


		when(inputQueryService.getItems(input, map  , inputNode, graphContext)).thenReturn(nodes);
		when(itemToStringConverter.convertItemsToStrings(nodes, map, tripliserReporter, graphContext)).thenReturn(Arrays.asList("http://me"));

		try {
			tripleGraph.addPropertiesForResourceAndMapping(input, map, resource, inputNode, graphContext);
			exceptionExpected();
		} catch(InvalidPropertyMappingException e) {
			verify(tripliserReporter).addMessage(new ReportEntry(uriSyntaxException, Status.WARNING, Scope.PROPERTY));

			assertEquals("A required property did not have any triples created", e.getMessage());
		}
	}

	@Test
	public void whenNonRequiredResourceUriSyntaxIsInvalidAWarningIsAddedToTheReport() throws  URISyntaxException, QueryException, InvalidPropertyMappingException {
		URISyntaxException uriSyntaxException = new URISyntaxException("", "");
		when(jenaModelService.convertToUri(model, "http://me", graphContext)).thenThrow(uriSyntaxException);

		PropertyMapping map = new ValueMappingBuilder()
			.name("foaf:creator")
			.query("creator")
			.resource(true)
			.required(false)
			.toPropertyMapping();

		List<Item> nodes = new ArrayList<Item>();
		nodes.add(queryNode);

		when(inputQueryService.getItems(input, map  , inputNode, graphContext)).thenReturn(nodes);
		when(itemToStringConverter.convertItemsToStrings(nodes, map, tripliserReporter, graphContext)).thenReturn(Arrays.asList("http://me"));

		try{
			tripleGraph.addPropertiesForResourceAndMapping(input, map, resource, inputNode, graphContext);
			exceptionExpected();
		} catch(IgnoredPropertyMappingException e) {
			verify(tripliserReporter).addMessage(new ReportEntry(uriSyntaxException, Status.WARNING, Scope.PROPERTY));
		}
	}

	@Test
	public void createsATripleOnAKnownResource() throws QueryException, InvalidPropertyMappingException, IgnoredPropertyMappingException{
		PropertyMapping map = new ValueMappingBuilder()
			.name("foaf:name")
			.query("abc")
			.toPropertyMapping();
		List<Item> nodes = new ArrayList<Item>();
		nodes.add(queryNode);

		when(inputQueryService.getItems(input, map  , inputNode, graphContext)).thenReturn(nodes);
		when(itemToStringConverter.convertItemsToStrings(nodes, map, tripliserReporter, graphContext)).thenReturn(Arrays.asList("name"));

		tripleGraph.addPropertiesForResourceAndMapping(input, map, resource, inputNode, graphContext);

		assertTrue(model.getResource("123").hasProperty(foafName, "name"));
	}

	@Test
	public void createsATripleForEachNodeResultOnAKnownResource() throws QueryException, InvalidPropertyMappingException, IgnoredPropertyMappingException{
		PropertyMapping map = new ValueMappingBuilder()
			.name("foaf:name")
			.query("abc")
			.toPropertyMapping();

		List<Item> nodes = new ArrayList<Item>();
		nodes.add(queryNode);
		nodes.add(queryNode2);

		when(inputQueryService.getItems(input, map  , inputNode, graphContext)).thenReturn(nodes);
		when(itemToStringConverter.convertItemsToStrings(nodes, map, tripliserReporter, graphContext)).thenReturn(Arrays.asList("name","name2"));

		tripleGraph.addPropertiesForResourceAndMapping(input, map, resource, inputNode, graphContext);

		assertTrue(model.getResource("123").hasProperty(foafName, "name"));
		assertTrue(model.getResource("123").hasProperty(foafName, "name2"));

		System.out.println(tripleGraph.toString());
	}

	@Test
	public void createsATripleBetweenTwoResourcesIfMapIsAResource() throws QueryException, URISyntaxException, InvalidPropertyMappingException, IgnoredPropertyMappingException{
		PropertyMapping map = new ValueMappingBuilder()
			.name("foaf:creator")
			.query("creator")
			.resource(true)
			.toPropertyMapping();

		List<Item> nodes = new ArrayList<Item>();
		nodes.add(queryNode);

		when(queryNode.getStringValue()).thenReturn("http://me");
		when(inputQueryService.getItems(input, map  , inputNode, graphContext)).thenReturn(nodes);
		when(itemToStringConverter.convertItemsToStrings(nodes, map, tripliserReporter, graphContext)).thenReturn(Arrays.asList("http://me"));
		when(jenaModelService.convertToUri(model, "http://me", graphContext)).thenReturn("http://me");

		tripleGraph.addPropertiesForResourceAndMapping(input, map, resource, inputNode, graphContext);

		assertTrue(model.getResource("123").hasProperty(foafCreator, model.getResource("http://me")));
	}

	@Test
	public void anInvalidDatatypeThrowsAInvalidPropertyMappingException() throws QueryException, IgnoredPropertyMappingException {
		PropertyMapping map = new ValueMappingBuilder()
			.name("foaf:name")
			.query("name")
			.dataType("foaf:notADataType")
			.toPropertyMapping();

		List<Item> nodes = new ArrayList<Item>();
		nodes.add(queryNode);

		when(queryNode.getStringValue()).thenReturn("name");
		when(inputQueryService.getItems(input, map  , inputNode, graphContext)).thenReturn(nodes);

		try {
			tripleGraph.addPropertiesForResourceAndMapping(input, map, resource, inputNode, graphContext);
			fail("Should have thrown an exception");
		} catch(InvalidPropertyMappingException e) {
			assertEquals("Datatype 'foaf:notADataType' not found", e.getMessage());
		}
	}

	@Test
	public void anDatatypeWithInvalidDataThrowsAInvalidPropertyMappingException() throws QueryException, IgnoredPropertyMappingException, URISyntaxException {
		PropertyMapping propertyMapping = new ValueMappingBuilder()
			.name("foaf:name")
			.query("name")
			.dataType("xsd:int")
			.toPropertyMapping();

		List<Item> nodes = new ArrayList<Item>();
		nodes.add(queryNode);

		when(inputQueryService.getItems(input, propertyMapping, inputNode, graphContext)).thenReturn(nodes);
		when(jenaModelService.convertToUri(model, "xsd:int", graphContext)).thenReturn("http://www.w3.org/2001/XMLSchema#int");
		when(itemToStringConverter.convertItemsToStrings(nodes, propertyMapping, tripliserReporter, graphContext)).thenReturn(Arrays.asList("name"));

		try {
			tripleGraph.addPropertiesForResourceAndMapping(input, propertyMapping, resource, inputNode, graphContext);
			fail("Should have thrown an exception");
		} catch(InvalidPropertyMappingException e) {
			verify(tripliserReporter).addMessage(new ReportEntry(new DatatypeFormatException(
					"name",
					TypeMapper.getInstance().getTypeByName("http://www.w3.org/2001/XMLSchema#int"),
					"Invalid value format"),
					Status.WARNING, Scope.PROPERTY,
					propertyMapping));

			assertEquals("A required property did not have any triples created", e.getMessage());
		}
	}

	@Test
	public void throwsAnInvalidPropertyMappingExceptionWhenCreatingAResourceBasedOnAnURIQueryThatReturnsMoreThanOneResult() throws IgnoredResourceMappingException, QueryException {
		ValueMapping map = new ValueMappingBuilder()
			.name("about")
			.query("abc")
			.toValueMapping();
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.about(map)
			.toResourceMapping();

		List<Item> nodes = new ArrayList<Item>();
		nodes.add(queryNode);
		nodes.add(queryNode2);

		when(queryNode.getStringValue()).thenReturn("name");
		when(queryNode2.getStringValue()).thenReturn("name2");
		when(inputQueryService.getItems(input, map  , inputNode, graphContext)).thenReturn(nodes);

		try {
			tripleGraph.createMainResource(input, resourceMapping, inputNode, graphContext);
			fail("Should have thrown an exception");
		} catch(InvalidResourceMappingException e) {
			assertEquals("The mapping 'about' returned 2 results where 1 was expected", e.getMessage());
		}
	}

	@Test
	public void throwsAnInvalidPropertyMappingExceptionWhenAddingARequiredPropertyThatReturnsNoResults() throws QueryException, IgnoredPropertyMappingException {
		PropertyMapping propertyMapping = new ValueMappingBuilder()
			.query("abc")
			.required(true)
			.toPropertyMapping();
		List<Item> nodes = new ArrayList<Item>();

		when(inputQueryService.getItems(input, propertyMapping, inputNode, graphContext)).thenReturn(nodes);

		try {
			tripleGraph.addPropertiesForResourceAndMapping(input, propertyMapping, resource, inputNode, graphContext);
			fail("Should have thrown an exception");
		} catch(InvalidPropertyMappingException e) {
			assertEquals("A required property did not have any triples created", e.getMessage());
		}
	}

	@Test
	public void throwsAnIgnoredPropertyMappingExceptionWhenAddingANonRequiredPropertyThatReturnsNoResults() throws QueryException, InvalidPropertyMappingException {
		PropertyMapping propertyMapping = new ValueMappingBuilder()
			.query("abc")
			.required(false)
			.toPropertyMapping();
		List<Item> nodes = new ArrayList<Item>();

		when(inputQueryService.getItems(input, propertyMapping, inputNode, graphContext)).thenReturn(nodes);

		try {
			tripleGraph.addPropertiesForResourceAndMapping(input, propertyMapping, resource, inputNode, graphContext);
			fail("Should have thrown an exception");
		} catch(IgnoredPropertyMappingException e) {
			assertEquals("No triples created for property", e.getMessage());
		}
	}

	@Test
	public void throwsAnInvalidResourceMappingExceptionWhenCreatingAResourceFailsToRetrieveItems() throws IgnoredResourceMappingException, QueryException {
		ValueMapping about = new ValueMappingBuilder()
			.required(true)
			.query("abc")
			.toValueMapping();
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.about(about)
			.toResourceMapping();

		QueryException queryException = new QueryException("Error");
		when(inputQueryService.getItems(input, about  , inputNode, graphContext)).thenThrow(queryException);

		try {
			tripleGraph.createMainResource(input, resourceMapping, inputNode, graphContext);
			fail("Should have thrown an exception");
		} catch(InvalidResourceMappingException e) {
			assertEquals(queryException, e.getCause());
		}
	}

	@Test
	public void throwsAnInvalidResourceMappingExceptionWhenCreatingAResourceBasedOnAnURIQueryThatReturnsNoResultsAndTheURIValueIsRequired() throws IgnoredResourceMappingException {
		ValueMapping about = new ValueMappingBuilder()
			.required(true)
			.query("abc")
			.toValueMapping();
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.about(about)
			.toResourceMapping();

		try {
			tripleGraph.createMainResource(input, resourceMapping, inputNode, graphContext);
			fail("Should have thrown an exception");
		} catch(InvalidResourceMappingException e) {
			assertEquals("Query for creating resource URI returned no results but resource URI mapping was required", e.getMessage());
		}
	}

	@Test
	public void throwsAnIgnoredResourceMappingExceptionWhenCreatingAnNonRequiredResourceBasedOnAnURIQueryThatReturnsNoResults() throws InvalidResourceMappingException {
		ValueMapping about = new ValueMappingBuilder()
		    .required(false)
			.query("abc")
			.toValueMapping();
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
			.about(about)
			.required(false)
			.toResourceMapping();

		try {
			tripleGraph.createMainResource(input, resourceMapping, inputNode, graphContext);
			fail("Should have thrown an exception");
		} catch(IgnoredResourceMappingException e) {
			assertEquals("Query for creating resource URI returned no results but resource mapping was not required", e.getMessage());
		}
	}

	@Test
	public void canCreateAnNonRequiredResourceBasedOnAnURIQuery() throws QueryException, IgnoredResourceMappingException, InvalidResourceMappingException {
		ValueMapping about = new ValueMappingBuilder().required(false)
				.query("abc").toValueMapping();
		ResourceMapping resourceMapping = new ResourceMappingBuilder()
				.about(about).required(false).toResourceMapping();

		List<Item> nodes = new ArrayList<Item>();
		nodes.add(queryNode);

		when(inputQueryService.getItems(input, about  , inputNode, graphContext)).thenReturn(nodes);
		when(itemToStringConverter.convertItemsToStrings(nodes, about, tripliserReporter, graphContext)).thenReturn(Arrays.asList("http://me"));

		Resource res = tripleGraph.createMainResource(input, resourceMapping, inputNode, graphContext);
		assertTrue(res.hasURI("http://me"));



	}

	@Test
	public void createsABlankNodeForAPropertyMappingWithSubMappingsAndAddsPropertiesToTheBlankNode() throws QueryException, InvalidPropertyMappingException, IgnoredPropertyMappingException{
		PropertyMapping submap = new ValueMappingBuilder()
		.name("foaf:name")
		.query("xyz")
		.toPropertyMapping();

		PropertyMapping map = new ValueMappingBuilder()
			.name("foaf:creator")
			.query("abc")
			.addSubPropertyMapping(submap)
			.toPropertyMapping();

		List<Item> nodesForMainProperty = new ArrayList<Item>();
		nodesForMainProperty.add(queryNode);
		List<Item> nodesForSubProperty = new ArrayList<Item>();
		nodesForSubProperty.add(queryNode2);

		when(inputQueryService.getItems(input, map  , inputNode, graphContext)).thenReturn(nodesForMainProperty);
		when(inputQueryService.getItems(input, submap  , queryNode, graphContext)).thenReturn(nodesForSubProperty);
		when(itemToStringConverter.convertItemsToStrings(nodesForSubProperty, submap, tripliserReporter, graphContext)).thenReturn(Arrays.asList("name"));


		tripleGraph.addPropertiesForResourceAndMapping(input, map, resource, inputNode, graphContext);

		Statement creator = model.getResource("123").getProperty(foafCreator);

		assertEquals(creator.getSubject(), resource);
		assertEquals(creator.getPredicate(), foafCreator);
		assertTrue(creator.getObject().isAnon());

		Statement name = creator.getObject().asResource().getProperty(foafName);
		assertNotNull(name);

		assertEquals(name.getSubject(), creator.getObject());
		assertEquals(name.getPredicate(), foafName);
		assertEquals("name", name.getObject().asLiteral().getString());
	}

}
