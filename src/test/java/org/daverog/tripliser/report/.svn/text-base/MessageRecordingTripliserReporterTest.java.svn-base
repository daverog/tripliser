package org.daverog.tripliser.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.List;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.report.RecordingReporter;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


public class MessageRecordingTripliserReporterTest {

	private RecordingReporter reporter;

	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);

		reporter = new RecordingReporter();
	}

	@Test
	public void ifATripliserReporterHasNoMessagesTheStatusIsAdviceAndIsSuccess(){
		List<ReportEntry> entries = new ArrayList<ReportEntry>();

		setInternalState(reporter, "entries", entries );

		assertEquals(Status.ADVICE, reporter.getStatus());
		assertTrue(reporter.isSuccess());
	}

	@Test
	public void ifATripliserReporterHasOnlyAdviceEntriesTheStatusIsAdviceAndIsSuccess(){
		List<ReportEntry> entries = new ArrayList<ReportEntry>();

		entries.add(new ReportEntry(null, Status.ADVICE, Scope.PROPERTY));
		entries.add(new ReportEntry(null, Status.ADVICE, Scope.PROPERTY));

		setInternalState(reporter, "entries", entries );

		assertEquals(Status.ADVICE, reporter.getStatus());
		assertTrue(reporter.isSuccess());
	}

	@Test
	public void ifATripliserReporterHasOnlyAdviceAndWarningEntriesTheStatusIsWarningAndIsSuccess(){
		List<ReportEntry> entries = new ArrayList<ReportEntry>();

		entries.add(new ReportEntry(null, Status.ADVICE, Scope.PROPERTY));
		entries.add(new ReportEntry(null, Status.WARNING, Scope.PROPERTY));

		setInternalState(reporter, "entries", entries );

		assertEquals(Status.WARNING, reporter.getStatus());
		assertTrue(reporter.isSuccess());
	}

	@Test
	public void ifATripliserReporterHasALeastOneWarningEntrieTheStatusIsFailureAndIsNotSuccess(){
		List<ReportEntry> entries = new ArrayList<ReportEntry>();

		entries.add(new ReportEntry(null, Status.ADVICE, Scope.PROPERTY));
		entries.add(new ReportEntry(null, Status.FAILURE, Scope.PROPERTY));

		setInternalState(reporter, "entries", entries );

		assertEquals(Status.FAILURE, reporter.getStatus());
		assertFalse(reporter.isSuccess());
	}

	@Test
	public void ifATripliserReporterHasALeastOneErrorEntrieTheStatusIsErrorAndIsNotSuccess(){
		List<ReportEntry> entries = new ArrayList<ReportEntry>();

		entries.add(new ReportEntry(null, Status.ADVICE, Scope.PROPERTY));
		entries.add(new ReportEntry(null, Status.FAILURE, Scope.PROPERTY));
		entries.add(new ReportEntry(null, Status.ERROR, Scope.PROPERTY));

		setInternalState(reporter, "entries", entries );

		assertEquals(Status.ERROR, reporter.getStatus());
		assertFalse(reporter.isSuccess());
	}

	@Test
	public void theTotalNumberOfEntriesIsReturned() {
		reporter.addMessage(new ReportEntry(null, Status.ADVICE, Scope.PROPERTY));
		reporter.addMessage(new ReportEntry(null, Status.FAILURE, Scope.PROPERTY));

		assertEquals(2, reporter.getNumberOfEntries());
	}

	@Test
	public void theTotalForEachStatusIsReturned() {
		reporter.addMessage(new ReportEntry(null, Status.ADVICE, Scope.PROPERTY));
		reporter.addMessage(new ReportEntry(null, Status.ADVICE, Scope.PROPERTY));
		reporter.addMessage(new ReportEntry(null, Status.ERROR, Scope.PROPERTY));

		assertEquals(2, reporter.getNumberOfEntries(Status.ADVICE));
	}

	@Test
	public void theTotalForEachPropertyLevelIsReturned() {
		reporter.addMessage(new ReportEntry(null, Status.ERROR, Scope.PROPERTY));
		reporter.addMessage(new ReportEntry(null, Status.ADVICE, Scope.PROPERTY));
		reporter.addMessage(new ReportEntry(null, Status.ADVICE, Scope.GRAPH));

		assertEquals(2, reporter.getNumberOfEntries(Scope.PROPERTY));
	}

	@Test
	public void theTotalForEachStatusAndPropertyLevelIsReturned() {
		reporter.addMessage(new ReportEntry(null, Status.ERROR, Scope.PROPERTY));
		reporter.addMessage(new ReportEntry(null, Status.ADVICE, Scope.PROPERTY));
		reporter.addMessage(new ReportEntry(null, Status.ADVICE, Scope.PROPERTY));

		assertEquals(2, reporter.getNumberOfEntries(Status.ADVICE, Scope.PROPERTY));
	}

}
