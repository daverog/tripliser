package org.daverog.tripliser.graphs;

import java.util.Iterator;
import java.util.List;

import org.daverog.tripliser.report.RecordingReporter;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReporter;


public class CompositeReport extends RecordingReporter {

	public CompositeReport(List<TripliserReporter> reports) {
		for (TripliserReporter reporter : reports) {
			Iterator<ReportEntry> messages = reporter.getReport().getEntries();
			while (messages.hasNext()) {
				addMessage(messages.next());
			}
		}
	}

}
