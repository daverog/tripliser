package org.daverog.tripliser.report;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.report.xml.ReportBean;
import org.simpleframework.xml.core.Persister;



public class RecordingReporter implements TripliserReport, TripliserReporter {

	protected List<ReportEntry> entries;

	public RecordingReporter(){
		entries = new ArrayList<ReportEntry>();
	}

	public Iterator<ReportEntry> getEntries() {
		return entries.iterator();
	}

	public int getNumberOfEntries() {
		return entries.size();
	}

	public int getNumberOfEntries(Scope scope) {
		int total = 0;
		for (ReportEntry entry : entries) {
			if (entry.getScope() == scope) total++;
		}
		return total;
	}

	public int getNumberOfEntries(Status status) {
		int total = 0;
		for (ReportEntry entry : entries) {
			if (entry.getStatus() == status) total++;
		}
		return total;
	}

	public int getNumberOfEntries(Status status, Scope scope) {
		int total = 0;
		for (ReportEntry entry : entries) {
			if (entry.getScope() == scope && entry.getStatus() == status) total++;
		}
		return total;
	}

	public Status getStatus() {
		Status status = Status.ADVICE;

		for (ReportEntry entry : entries) {
			if (entry.getStatus().equals(Status.WARNING)) {
				if (!status.equals(Status.FAILURE) && !status.equals(Status.ERROR)) {
					status = Status.WARNING;
				}
			}
			if (entry.getStatus().equals(Status.FAILURE)) {
				if (!status.equals(Status.ERROR)) {
					status = Status.FAILURE;
				}
			}
			if (entry.getStatus().equals(Status.ERROR)) {
				status = Status.ERROR;
			}
		}

		return status;
	}

	public boolean isSuccess() {
		return getStatus() == Status.ADVICE || getStatus() == Status.WARNING;
	}

	public String toString() {
		ByteArrayOutputStream boas = new ByteArrayOutputStream();
		writeText(new PrintStream(boas));
		try {
			return boas.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Encoding UTF-8 not supported");
		}
	}

	@Override
	public void addMessage(ReportEntry entry) {
		entries.add(entry);
	}

	@Override
	public TripliserReport getReport() {
		return this;
	}

	@Override
	public void writeXml(OutputStream stream) throws Exception {
		new Persister().write(new ReportBean(entries), stream);
	}

	@Override
	public void writeText(PrintStream out) {
		for (ReportEntry entry : entries) {
			out.println(entry.getReportLineAsString());
		}

		out.println("-----------------------");
		out.println("Overall status: " + getStatus().name().toLowerCase());
		out.println("Success: " + isSuccess());
	}

}
