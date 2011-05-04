package org.daverog.tripliser.triplisers;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.graphs.MutableTripleGraph;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.mapping.model.Tag;
import org.daverog.tripliser.query.Item;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.QueryException;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.ReportEntry.Status;

public class GraphTagger {
	
	public void tagGraph(Queryable input, MutableTripleGraph tripleGraph,
			GraphMapping graphMapping, Node graphNode) {
		if (graphMapping.getTags() != null) {
			for (Tag tag : graphMapping.getTags()) {
				try {
					Item item = input.readItem(graphNode, tag.getQuery());
					if (item != null) {
						String value = item.getStringValue();
						tripleGraph.addTag(tag.getName(), value);
					}
				} catch(QueryException e) {
					Status status = Status.WARNING;
					if (tag.isRequired()) {
						status = Status.FAILURE;
					} 
					tripleGraph.getTripliserReporter().addMessage(
							new ReportEntry(e, status, Scope.GRAPH, graphMapping));
				}
			}
		}
	}

}
