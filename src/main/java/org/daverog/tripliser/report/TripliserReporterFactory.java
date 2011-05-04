package org.daverog.tripliser.report;

import org.daverog.tripliser.Constants.Scope;


public interface TripliserReporterFactory {

	public TripliserReporter getReporterForScope(Scope scope);

	public TripliserReporter createGraphReporter();

}
