package org.daverog.tripliser.report;

import java.util.ArrayList;
import java.util.List;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.graphs.CompositeReport;
import org.daverog.tripliser.report.RecordingReporter;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.junit.Before;
import org.junit.Test;


public class CompositeReportTest extends MockitoTestBase {

	private CompositeReport compositeReport;
	private RecordingReporter reporter1;
	private RecordingReporter reporter2;
	private List<TripliserReporter> reports;

	@Before
	public void setUp(){
		reports = new ArrayList<TripliserReporter>();
		reporter1 = new RecordingReporter();
		reporter2 = new RecordingReporter();
		reports.add(reporter1);
		reports.add(reporter2);
	}
	
	@Test
	public void theNumberOfMessagesIsCombinedFromAllReports() {
		reporter1.addMessage(new ReportEntry(null, Status.ADVICE, Scope.INPUT));
		reporter2.addMessage(new ReportEntry(null, Status.ADVICE, Scope.INPUT));
		compositeReport = new CompositeReport(reports);
		
		assertEquals(2, compositeReport.getNumberOfEntries());
	}
	
	@Test
	public void theNumberOfScopedMessagesIsCombinedFromAllReports() {
		reporter1.addMessage(new ReportEntry(null, Status.ADVICE, Scope.INPUT));
		reporter2.addMessage(new ReportEntry(null, Status.ADVICE, Scope.INPUT));
		reporter2.addMessage(new ReportEntry(null, Status.ADVICE, Scope.RESOURCE));
		compositeReport = new CompositeReport(reports);
		
		assertEquals(2, compositeReport.getNumberOfEntries(Scope.INPUT));
	}
	
}
