package org.daverog.tripliser.report;

import org.daverog.tripliser.Constants.Scope;


public class DefaultReporterFactory implements TripliserReporterFactory {

	private TripliserReporter globalReporter;
	private final Class<? extends TripliserReporter> graphReporterClass;
	
	public DefaultReporterFactory(Class<? extends TripliserReporter> globalReporterClass, 
			Class<? extends TripliserReporter> graphReporterClass) {
		
		if (globalReporterClass == null) throw new RuntimeException("A global tripliser reporter class must be supplied");
		if (graphReporterClass == null) throw new RuntimeException("A graph tripliser reporter class must be supplied");
		
		this.graphReporterClass = graphReporterClass;
		try {
			globalReporter = globalReporterClass.newInstance();
			graphReporterClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("A tripliser reporter class used is invalid");
		} catch (IllegalAccessException e) {
			throw new RuntimeException("A tripliser reporter class used is invalid");
		}
	}

	@Override
	public TripliserReporter getReporterForScope(Scope resourceMapping) {
		return globalReporter;
	}

	@Override
	public TripliserReporter createGraphReporter() {
		try {
			return graphReporterClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Could not create a graph reporter from the class '" + graphReporterClass.getSimpleName() + "'", e);
		}
	}

}
