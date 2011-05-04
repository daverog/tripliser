package org.daverog.tripliser.graphs;

import org.daverog.tripliser.query.QueryService;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.value.ItemToStringConverter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class MutableTripleGraphFactory {
	
	private final JenaModelService jenaModelService;
	private final QueryService queryService;
	private final ItemToStringConverter itemToStringConverter;
	
	public MutableTripleGraphFactory(JenaModelService jenaModelService, QueryService queryService, ItemToStringConverter itemToStringConverter){
		this.jenaModelService = jenaModelService;
		this.queryService = queryService;
		this.itemToStringConverter = itemToStringConverter;
	}
	
	public MutableTripleGraph createGraph(String name, TripliserReporter reporter, GraphContext graphContext) {
		Model model = ModelFactory.createDefaultModel();
		
		jenaModelService.loadNamespacesIntoModel(graphContext, model);
		
		return new MutableTripleGraph(name, model,  reporter,  jenaModelService,  queryService,  itemToStringConverter);
	}

}
