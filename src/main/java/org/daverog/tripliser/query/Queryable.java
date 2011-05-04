package org.daverog.tripliser.query;

import java.util.ArrayList;
import java.util.List;


/**
 * A input that can be queried by a query language to return Node or Item results.
 *
 * Abstracts an input such as an XML file or SQL database to provide the data source
 * on which queries can be made.
 */
public abstract class Queryable {

	public Node readNode(String expression) throws QueryException {
		return readNode(null, expression);
	}

	public Item readItem(String expression) throws QueryException {
		return readItem(null, expression);
	}

	public Node readNode(Node node, String expression) throws QueryException {
		Item item = readItem(node, expression);

		if (item != null && !(item instanceof Node)) {
			throw new QueryException("A node was not returned by the query '"+expression+"'");
		}

		return (Node)item;
	}

    public Item readItem(Node node, String expression) throws QueryException {
    	List<Item> items = read(node, expression);

    	if (items.isEmpty()) return null;
    	if (items.size() > 1) throw new QueryException("The query '" + expression + "' was expected to return a single result, but returned " + items.size() + " items");

    	return items.get(0);
    }

    public List<Item> readList(String expression) throws QueryException {
    	return read(null, expression);
    }

    public List<Item> readList(Node node, String expression) throws QueryException {
    	return read(node, expression);
    }

	protected abstract List<Item> read(Node node, String expression) throws QueryException;

	public List<Node> readNodeList(String expression) throws QueryException {
		return readNodeList(null, expression);
	}

	public List<Node> readNodeList(Node node, String expression) throws QueryException {
		List<Item> items = readList(node, expression);

		List<Node> nodes = new ArrayList<Node>();
		for (Item item : items) {
			if (!(item instanceof Node)) {
				throw new QueryException("At least one non-node was not returned by the query '"+expression+"'");
			}
			nodes.add((Node)item);
		}
		return nodes;
	}

}
