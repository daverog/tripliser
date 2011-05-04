package org.daverog.tripliser.query;

import java.util.ArrayList;
import java.util.List;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.graphs.GraphContextBuilder;
import org.daverog.tripliser.mapping.model.ValueMapping;
import org.daverog.tripliser.mapping.model.ValueMappingBuilder;
import org.daverog.tripliser.query.Item;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.QueryException;
import org.daverog.tripliser.query.QueryService;
import org.daverog.tripliser.query.QuerySpecification;
import org.daverog.tripliser.query.Queryable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class QueryServiceTest extends MockitoTestBase {

	@Mock Queryable input;
	@Mock List<Item> expectedItems;
	@Mock Node contextNode;
	@Mock QuerySpecification queryMapping;

	private QueryService queryService;

	@Before
	public void setUp() {
		queryService = new QueryService();
	}

	@Test
	public void anEmptyQueryResultsInARuntimeException() throws QueryException {
		GraphContext graphContext = new GraphContextBuilder()
			.toGraphContext();


		when(queryMapping.getQuery()).thenReturn("");

		try {
			queryService.getItems(input, queryMapping, contextNode, graphContext);
			exceptionExpected();
		} catch(RuntimeException e) {
			assertEquals("Query must be supplied", e.getMessage());
		}
	}

	@Test
	public void aSupportingInputIsMissingFromGraphContext() {
		GraphContext graphContext = new GraphContextBuilder()
			.toGraphContext();

		when(queryMapping.getQuery()).thenReturn("abc");
		when(queryMapping.getInputName()).thenReturn("nonexistingdoc");


		try {
			queryService.getItems(input, queryMapping, contextNode, graphContext);
			exceptionExpected();
		} catch(QueryException e) {
			assertEquals("Supporting input missing: nonexistingdoc", e.getMessage());
		}


	}

	@Test
	public void queriesInputIfNoSupportingInputNameSupplied() throws QueryException {
		QueryService queryService = new QueryService();

		GraphContext graphContext = new GraphContextBuilder()
			.toGraphContext();

		when(queryMapping.getQuery()).thenReturn("abc");
		when(queryMapping.getInputName()).thenReturn(null);
		when(input.readList(contextNode, "abc")).thenReturn(expectedItems);

		List<Item> items = queryService.getItems(input, queryMapping, contextNode, graphContext);

		assertEquals(expectedItems, items);
	}

	@Test
	public void whenQueryFailsThrowsQueryException() throws QueryException {
		GraphContext graphContext = new GraphContextBuilder()
			.toGraphContext();

		QueryException queryException = new QueryException("Error");
		when(queryMapping.getQuery()).thenReturn("abc");
		when(queryMapping.getInputName()).thenReturn(null);
		when(input.readList(contextNode, "abc")).thenThrow(queryException);

		try {
			queryService.getItems(input, queryMapping, contextNode, graphContext);
			exceptionExpected();
		} catch (QueryException e) {
			assertEquals(queryException, e);
		}

	}

	@Test
	public void whenQueryIsNullAndValueIsNullReturnsContextNodeAsList() throws QueryException {
		GraphContext graphContext = new GraphContextBuilder()
			.toGraphContext();

		when(queryMapping.getQuery()).thenReturn(null);
		when(queryMapping.getInputName()).thenReturn(null);

		List<Item> items = queryService.getItems(input, queryMapping, contextNode, graphContext);

		assertEquals(1, items.size());
		assertEquals(contextNode, items.get(0));
	}

	@Test
	public void queriesInputIfOnSupportingWhenNameSupplied() throws QueryException {
		Queryable supportingInput = mock(Queryable.class);

		GraphContext graphContext = new GraphContextBuilder()
			.addSupportingInput("doc1", supportingInput)
			.toGraphContext();

		when(queryMapping.getQuery()).thenReturn("abc");
		when(queryMapping.getInputName()).thenReturn("doc1");

		when(supportingInput.readList(null , "abc")).thenReturn(expectedItems);

		List<Item> items =  queryService.getItems(input, queryMapping, contextNode, graphContext);

		assertEquals(expectedItems, items);
	}


	@Test
	public void returnsSimpleStringValueIfQueryIsNull() throws QueryException {
		QueryService queryService = new QueryService();

		GraphContext graphContext = new GraphContextBuilder()
			.toGraphContext();

		when(queryMapping.getQuery()).thenReturn(null);
		when(queryMapping.getValue()).thenReturn("string");

		List<Item> items = queryService.getItems(input, queryMapping, contextNode, graphContext);

		assertEquals("string", items.get(0).getStringValue());
	}

	@Test
	public void returnsEmptyListWhenQueryReturnsNoResults() throws  QueryException {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().required(true).query("abc").toValueMapping();
		List<Item> noNodes = new ArrayList<Item>();

		when(input.readList(contextNode, "abc")).thenReturn(noNodes);

		assertTrue(queryService.getItems(input, valueMapping, contextNode, graphContext).isEmpty());
	}

	@Test
	public void mappedItemsContainsFoundItemForAQuery() throws QueryException{
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().query("abc").toValueMapping();
		List<Item> nodes = new ArrayList<Item>();
		Item xPathNode = mock(Item.class);
		nodes.add(xPathNode);

		when(input.readList(contextNode,"abc")).thenReturn(nodes);

		List<Item> mappedItems = queryService.getItems(input, valueMapping, contextNode, graphContext);

		assertEquals(1, mappedItems.size());
		assertEquals(xPathNode, mappedItems.get(0));
	}

	@Test
	public void mappedItemsContainsFoundNodeForAQuery() throws QueryException{
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().query("abc").toValueMapping();
		List<Node> nodes = new ArrayList<Node>();
		Node xPathNode = mock(Node.class);
		nodes.add(xPathNode);

		when(input.readNodeList(contextNode,"abc")).thenReturn(nodes);

		List<Node> mappedNodes = queryService.getNodes(input, valueMapping, contextNode, graphContext);

		assertEquals(1, mappedNodes.size());
		assertEquals(xPathNode, mappedNodes.get(0));
	}

	@Test
	public void whenRequestingNodesWithAMappingWithAValueThrowsAQueryException() {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().value("abc").toValueMapping();

		try {
			queryService.getNodes(input, valueMapping, contextNode, graphContext);
			exceptionExpected();
		} catch(QueryException e) {
			assertEquals("Cannot make node queries when a value is supplied", e.getMessage());
		}

	}

	@Test
	public void mappedItemsContainsTheTwoFoundNodesForAQuery() throws QueryException{
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().query("abc").toValueMapping();
		List<Item> nodes = new ArrayList<Item>();
		Item xPathNode = mock(Item.class);
		Item xPathNode2 = mock(Item.class);
		nodes.add(xPathNode);
		nodes.add(xPathNode2);

		when(input.readList(contextNode,"abc")).thenReturn(nodes);

		List<Item> mappedItems = queryService.getItems(input, valueMapping, contextNode, graphContext);


		assertEquals(2, mappedItems.size());
		assertEquals(xPathNode, mappedItems.get(0));
		assertEquals(xPathNode2, mappedItems.get(1));
	}

	@Test
	public void mappedValueContainsTheFoundNodeForAQueryThatUsesASupportingInput() throws QueryException{
		List<Item> nodes = new ArrayList<Item>();
		Item xPathNode = mock(Item.class);
		Queryable supportingInput = mock(Queryable.class);
		GraphContext graphContext = new GraphContextBuilder().addSupportingInput("doc1", supportingInput ).toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().query("abc").input("doc1").toValueMapping();
		nodes.add(xPathNode);

		when(supportingInput.readList(null,"abc")).thenReturn(nodes);

		List<Item> mappedItems = queryService.getItems(input, valueMapping, contextNode, graphContext);
		assertEquals(1, mappedItems.size());
		assertEquals(xPathNode, mappedItems.get(0));
	}

	@Test
	public void throwsAQueryExceptionIfQueryFails() throws
			QueryException {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().query("abc").toValueMapping();
		QueryException queryException = new QueryException("Error");

		when(input.readList(contextNode,"abc")).thenThrow(queryException);

		try {
			queryService.getItems(input, valueMapping, contextNode, graphContext);
			fail("Exception should have been thrown");
		} catch(QueryException e) {
			assertEquals(queryException, e);
		}
	}

}
