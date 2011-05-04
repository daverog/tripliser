package org.daverog.tripliser.query;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.daverog.tripliser.graphs.GraphContext;


public class QueryService {

	@SuppressWarnings("unchecked")
	private <T> List<T> query(Queryable input,
			QuerySpecification queryMapping, Node contextNode,
			GraphContext graphContext, Class<T> T) throws QueryException {
		Node queryNode = contextNode;
		if (queryMapping.getQuery() == null) {
			if (queryMapping.getValue() == null)
				return Arrays.asList((T)contextNode);

			if (T == Node.class) throw new QueryException("Cannot make node queries when a value is supplied");

			return Arrays.asList((T) new SimpleStringValue(queryMapping
					.getValue()));
		}

		if (StringUtils.isBlank(queryMapping.getQuery())) {
			throw new RuntimeException("Query must be supplied");
		}

		if (queryMapping.getInputName() != null) {
			input = graphContext.getSupportingInput(queryMapping
					.getInputName());
			queryNode = null;

			if (input == null)
				throw new QueryException("Supporting input missing: " + queryMapping.getInputName());


		}

		if (T == Node.class) {
			return (List<T>) input.readNodeList(queryNode, queryMapping.getQuery());
		}
		return (List<T>) input.readList(queryNode, queryMapping.getQuery());
	}

	public List<Item> getItems(Queryable input,
			QuerySpecification queryMapping, Node contextNode,
			GraphContext graphContext) throws QueryException {
		return query(input, queryMapping, contextNode, graphContext, Item.class);
	}

	public List<Node> getNodes(Queryable input,
			QuerySpecification queryMapping, Node contextNode,
			GraphContext graphContext) throws QueryException {
		return query(input, queryMapping, contextNode, graphContext, Node.class);
	}

}
