package org.daverog.tripliser.report.xml;

import org.daverog.tripliser.mapping.model.PropertyMapping;
import org.daverog.tripliser.query.QuerySpecification;
import org.daverog.tripliser.report.ReportEntry;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;


@Root(name="entry")
public class ReportEntryBean {

	@Attribute
	private String status;

	@Attribute
	private String scope;

	@Attribute(required=false)
	private String name;

	@Attribute(required=false)
	private String parent;

	@Attribute(required=false)
	private String cause;

	@Attribute(required=false)
	private String message;

	@Attribute(required=false)
	private String query;

	@Attribute(required=false)
	private String value;

	@Attribute(required=false)
	private String input;

	private String description;

	public ReportEntryBean(ReportEntry reportEntry) {
		status = reportEntry.getStatus().name().toLowerCase();
		scope = reportEntry.getScope().name().toLowerCase();

		StringBuffer description = new StringBuffer();
		description.append("[");
		description.append(status);
		description.append("@");
		description.append(scope);

		if (reportEntry.getMapping() != null) {
			name = reportEntry.getMapping().getName();
			description.append("|");
			description.append(name);

			if (reportEntry.getMapping() instanceof PropertyMapping) {
				PropertyMapping propertyMapping = (PropertyMapping)reportEntry.getMapping();

				if (propertyMapping.getParent() != null) {
					parent = propertyMapping.getParent().getName();
					description.append("|");
					description.append(parent);
				}
			}
		}

		description.append("]");

		if (reportEntry.getCause() != null) {
			description.append(" ");
			cause = reportEntry.getCause().getClass().getSimpleName();
			description.append("(");
			description.append(cause);
			description.append(")");

			if (reportEntry.getCause().getMessage() != null) {
				message = reportEntry.getCause().getMessage();
				description.append(" ");
				description.append(message);
				StringBuffer underlying = new StringBuffer();
				addUnderlyingCauses(underlying, reportEntry.getCause().getCause());
				String underlyingMessage = underlying.toString();
				message = message + underlyingMessage;
				description.append(underlyingMessage);
			}
		}

		if (reportEntry.getMapping() != null && reportEntry.getMapping() instanceof QuerySpecification) {
			QuerySpecification querySpec = (QuerySpecification)reportEntry.getMapping();
			query = querySpec.getQuery();
			value = querySpec.getValue();
			input = querySpec.getInputName();

			description.append(" {");
			description.append(value == null ? "query" : "value");
			description.append("='");
			description.append(value == null ? query : value);
			description.append("'");

			if (querySpec.getInputName() != null) {
				description.append("|input='");
				description.append(input);
				description.append("'");
			}

			description.append("}");
		}

		this.description = description.toString();
	}

	private void addUnderlyingCauses(StringBuffer description, Throwable cause) {
		if (cause == null) return;
		description.append(" (");
		description.append(cause.getClass().getSimpleName());
		if (cause.getMessage() != null) {
			description.append(": ");
			description.append(cause.getMessage());
		}
		addUnderlyingCauses(description, cause.getCause());
		description.append(")");
	}

	@Override
	public String toString() {
		return description;
	}

}
