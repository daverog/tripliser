package org.daverog.tripliser.report;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.report.ReportEntry.Status;



public interface TripliserReport {

	/**
	 * Returns all individual entries for this report
	 */
	public Iterator<ReportEntry> getEntries();

	/**
	 *Returns the total number of entries for this report
	 */
	public int getNumberOfEntries();

	/**
	 * Returns the number of entries that were recorded at a particular scope
	 */
	public int getNumberOfEntries(Scope scope);

	/**
	 * Returns the number of entries that were recorded with a particular status
	 */
	public int getNumberOfEntries(Status status);

	/**
	 * Returns the number of entries that were recorded with a particular status and at a particular scope
	 */
	public int getNumberOfEntries(Status status, Scope scope);

	/**
	 * Returns the overall status of this report (the status of the most severe report entry).
	 */
	public Status getStatus();

	/**
	 * Returns a string representation of the entire report; includes some detail for every report entry, and an overall summary.
	 */
	public String toString();

	/**
	 * Write the report as XML to an output stream
	 */
	public void writeXml(OutputStream stream) throws Exception;

	/**
	 * Write the report as plain text to an output stream
	 */
	public void writeText(PrintStream out);

	/**
	 * Returns true if no report entries were recorded with a status of error or fail
	 */
	public boolean isSuccess();


}
