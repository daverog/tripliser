package org.daverog.tripliser.report;

import org.apache.commons.lang.NotImplementedException;


public class SystemOutReporter implements TripliserReporter {

	@Override
	public void addMessage(ReportEntry message) {
		System.out.println(message.toString());
	}

	@Override
	public TripliserReport getReport() {
		throw new NotImplementedException("This reporter does not keep a report");
	}

}
