package org.daverog.tripliser.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.saxon.s9api.SaxonApiException;

import org.daverog.tripliser.query.Item;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.QueryException;
import org.daverog.tripliser.query.Queryable;
import org.junit.Before;
import org.junit.Test;


public class QueryableTest {

	private Queryable input;
	private Queryable singleItemInput;
	private Queryable noItemsInput;
	private Queryable singleNodeInput;

	@SuppressWarnings("unused")
	@Before
	public void setUp() throws SaxonApiException {
		input = new Queryable() {

			@Override
			protected List<Item> read(Node node, String expression)
					throws QueryException {
				return Arrays.asList((Item)new SimpleItem(), (Item)new SimpleItem());
			}
		};
		singleItemInput = new Queryable() {

			@Override
			protected List<Item> read(Node node, String expression)
			throws QueryException {
				return Arrays.asList((Item)new SimpleItem());
			}
		};
		singleNodeInput = new Queryable() {

			@Override
			protected List<Item> read(Node node, String expression)
			throws QueryException {
				return Arrays.asList((Item)new SimpleNode());
			}
		};
		noItemsInput = new Queryable() {

			@Override
			protected List<Item> read(Node node, String expression)
			throws QueryException {
				return new ArrayList<Item>();
			}
		};
	}

    @Test
    public void readingASingleItemWhereAListIsReturnedFromTheInnerReadThrowsAQueryException() {
    	try {
    		input.readItem("abc");
    		fail("An exception should have been thrown");
    	} catch(QueryException e){
    		assertEquals("The query 'abc' was expected to return a single result, but returned 2 items", e.getMessage());
    	}
    }

    @Test
    public void readingANodeWhereAListOfItemsIsReturnedFromTheInnerReadThrowsAQueryException() {
    	try {
    		singleItemInput.readNode("abc");
    		fail("An exception should have been thrown");
    	} catch(QueryException e){
    		assertEquals("A node was not returned by the query 'abc'", e.getMessage());
    	}
    }

    @Test
    public void readingANodeListWhereAListOfItemsIsReturnedFromTheInnerReadThrowsAQueryException() {
    	try {
    		singleItemInput.readNodeList("abc");
    		fail("An exception should have been thrown");
    	} catch(QueryException e){
    		assertEquals("At least one non-node was not returned by the query 'abc'", e.getMessage());
    	}
    }

    @Test
    public void readingANodeWhereAListOfNodesIsReturnedFromTheInnerReadDoesNotThrowsQueryException() throws QueryException {
    	singleNodeInput.readNode("abc");
    }

    @Test
    public void readingANodeListWhereAListOfNodesIsReturnedFromTheInnerReadDoesNotThrowAQueryException() throws QueryException {
    	singleNodeInput.readNodeList("abc");
    }


    @Test
    public void readingANodeWhereNullIsReturnedFromTheInnerReadDoesNotThrowAQueryException() throws QueryException {
    	noItemsInput.readNode("abc");
    }

    public class SimpleItem implements Item {

		@Override
		public String getStringValue() {
			return "value";
		}

    }

    public class SimpleNode implements Node {

    	@Override
    	public String getStringValue() {
    		return "value";
    	}

		@Override
		public Object getNode() {
			return null;
		}

    }

}
