package org.daverog.tripliser.report;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.mapping.model.GenericMapping;
import org.daverog.tripliser.report.xml.ReportEntryBean;


public class ReportEntry {

	public enum Status {
		SUCCESS,
		ADVICE,
		WARNING,
		FAILURE,
		ERROR
	}

	private final Exception cause;
	private final Status status;
	private final Scope scope;
	private final GenericMapping mapping;

	public ReportEntry(Status status, Scope scope, GenericMapping mapping) {
		this(null, status, scope, mapping);
	}

	public ReportEntry(Exception cause, Status status, Scope scope) {
		this(cause, status, scope, null);
	}

	public ReportEntry(Exception cause, Status status, Scope scope, GenericMapping mapping) {
		this.cause = cause;
		this.status = status;
		this.scope = scope;
		this.mapping = mapping;
	}

	/**
	 * Returns the exception that cause this entry to be made, if NULL if no such exception exists.
	 */
	public Exception getCause() {
		return cause;
	}

	/**
	 * Returns the status (WARNING, ERROR, etc..) of this entry
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Returns the scope (MAPPING, RESOURCE, etc..) at which this entry was recorded
	 */
	public Scope getScope() {
		return scope;
	}

	/**
	 * Get the underlying mapping associated with this report entry
	 */
	public GenericMapping getMapping() {
		return mapping;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportEntry other = (ReportEntry) obj;
		if (cause == null) {
			if (other.cause != null)
				return false;
		} else {
			if (cause.getMessage() != null && other.cause.getMessage() != null) {
				if (!cause.getMessage().equals(other.cause.getMessage()))
					return false;
			} else {
				if (!cause.equals(other.cause))
					return false;
			}
		}
		if (status != other.status)
			return false;
		if (scope != other.scope)
			return false;
		return true;
	}

	/**
	 * Returns the report entry data described in a <code>String<code>
	 */
	public String getReportLineAsString() {
		return new ReportEntryBean(this).toString();
	}

}
