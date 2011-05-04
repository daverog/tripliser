package org.daverog.tripliser.triplisers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.graphs.TripliserManager;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.query.QueryableInputFactory;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.util.MetaIterator;


public class TripleGraphIterator extends MetaIterator<TripleGraph> {

	private final QueryableInputFactory queryableInputFactory;
	private final InputTripliserFactory inputTripliserFactory;
	private final Mapping mapping;
	
	private Iterator<InputStream> inputs;
	private int inputIndex;
	private TripliserManager tripliserManager;

	public TripleGraphIterator(Iterator<InputStream> inputs, TripliserManager tripliserManager, Mapping mapping, GraphContext graphContext, InputTripliserFactory inputTripliserFactory, QueryableInputFactory queryableInputFactory){
		this.inputs = inputs;
		this.mapping = mapping;
		this.tripliserManager = tripliserManager;
		this.inputTripliserFactory = inputTripliserFactory;
		this.queryableInputFactory = queryableInputFactory;
		
		inputIndex = 0;
	}

	@Override
	public boolean hasNextNestedIterator() {
		return inputs.hasNext();
	}

	@Override
	public Iterator<TripleGraph> nextNestedIterator() {
		tripliserManager.next(Scope.INPUT);
		TripliserReporter reporter = tripliserManager.getReporter(mapping, Scope.INPUT);
		try {
			Queryable input = queryableInputFactory.createSaxonXPathTripliserDocument("#" + inputIndex++, inputs.next());
			return tripliserManager.processNestedIterator(
					inputTripliserFactory.createInputTripliser(input, mapping, tripliserManager), mapping, Scope.INPUT);
		} catch(TripliserException e) {
			reporter.addMessage(new ReportEntry(e, Status.FAILURE, Scope.INPUT));
		} catch(Exception e) {
			reporter.addMessage(new ReportEntry(e, Status.ERROR, Scope.INPUT));
		}
		return new ArrayList<TripleGraph>().iterator();
	}

	public void flush() {
		while (hasNext()) next();		
	}

}
