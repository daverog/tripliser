package org.daverog.tripliser.report.xml;

import java.io.ByteArrayOutputStream;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.mapping.model.GenericMapping;
import org.daverog.tripliser.mapping.model.PropertyMapping;
import org.daverog.tripliser.mapping.model.ValueMappingBuilder;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.report.xml.ReportEntryBean;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;



public class ReportEntryBeanTest extends MockitoTestBase {

	@Test
	public void extractsTheFieldsOfAReportEntry() throws Exception {
		Exception cause = new IllegalArgumentException("message");
		Status status = Status.ERROR;
		Scope scope = Scope.GRAPH_MAPPING;
		GenericMapping mapping = new ValueMappingBuilder()
			.name("name")
			.query("query")
			.toValueMapping();

		ReportEntry reportEntry = new ReportEntry(cause, status, scope, mapping);
		ReportEntryBean reportEntryBean = new ReportEntryBean(reportEntry);

		assertEquals("[error@graph_mapping|name] (IllegalArgumentException) message {query='query'}",
				reportEntryBean.toString());
		assertEquals("<entry status=\"error\" scope=\"graph_mapping\" name=\"name\" cause=\"IllegalArgumentException\" message=\"message\" query=\"query\"/>", toXml(reportEntryBean));
	}

	@Test
	public void extractsTheFieldsOfAReportEntryWhereTheCauseHasNoMessageAndASupportingInputIsSuppliedForTheQuery() throws Exception {
		Exception cause = new IllegalArgumentException();
		Status status = Status.ERROR;
		Scope scope = Scope.GRAPH_MAPPING;
		GenericMapping mapping = new ValueMappingBuilder()
			.name("name")
			.query("query")
			.input("doc")
			.toValueMapping();

		ReportEntry reportEntry = new ReportEntry(cause, status, scope, mapping);
		ReportEntryBean reportEntryBean = new ReportEntryBean(reportEntry);

		assertEquals("[error@graph_mapping|name] (IllegalArgumentException) {query='query'|input='doc'}",
				reportEntryBean.toString());
		assertEquals("<entry status=\"error\" scope=\"graph_mapping\" name=\"name\" cause=\"IllegalArgumentException\" query=\"query\" input=\"doc\"/>", toXml(reportEntryBean));
	}

	@Test
	public void extractsTheFieldsOfAReportEntryWithNoCauseAndWithAValue() throws Exception {
		Status status = Status.ERROR;
		Scope scope = Scope.GRAPH_MAPPING;
		GenericMapping mapping = new ValueMappingBuilder()
			.value("value")
			.name("name")
			.toValueMapping();

		ReportEntry reportEntry = new ReportEntry(status, scope, mapping);
		ReportEntryBean reportEntryBean = new ReportEntryBean(reportEntry);

		assertEquals("[error@graph_mapping|name] {value='value'}",
				reportEntryBean.toString());
		assertEquals("<entry status=\"error\" scope=\"graph_mapping\" name=\"name\" value=\"value\"/>", toXml(reportEntryBean));
	}

	@Test
	public void extractsTheFieldsOfAReportEntryWithNoMappingAndARecursiveCause() throws Exception {
		Exception underlyingUnderlyingUnderlyingCause = new IllegalArgumentException("message4");
		Exception underlyingUnderlyingCause = new IllegalArgumentException(null, underlyingUnderlyingUnderlyingCause);
		Exception underlyngCause = new IllegalArgumentException("message2", underlyingUnderlyingCause);
		Exception cause = new IllegalArgumentException("message", underlyngCause);
		Status status = Status.ERROR;
		Scope scope = Scope.GRAPH_MAPPING;

		ReportEntry reportEntry = new ReportEntry(cause, status, scope);
		ReportEntryBean reportEntryBean = new ReportEntryBean(reportEntry);

		assertEquals("[error@graph_mapping] (IllegalArgumentException) message (IllegalArgumentException: message2 (IllegalArgumentException (IllegalArgumentException: message4)))",
				reportEntryBean.toString());
		assertEquals("<entry status=\"error\" scope=\"graph_mapping\" cause=\"IllegalArgumentException\" message=\"message (IllegalArgumentException: message2 (IllegalArgumentException (IllegalArgumentException: message4)))\"/>", toXml(reportEntryBean));
	}

	@Test
	public void extractsTheFieldsOfAReportEntryForAChildPropertyMappingWhereTheParentsNameContainsXmlCharacters() throws Exception {
		Exception cause = new IllegalArgumentException("message");
		Status status = Status.ERROR;
		Scope scope = Scope.GRAPH_MAPPING;
		PropertyMapping mapping = new ValueMappingBuilder()
			.name("><>\"")
			.addSubPropertyMapping(new ValueMappingBuilder()
				.name("subName")
				.query("query")
				.toPropertyMapping())
			.toPropertyMapping();
		PropertyMapping subMapping = mapping.getPropertyMappings().get(0);

		ReportEntry reportEntry = new ReportEntry(cause, status, scope, subMapping);
		ReportEntryBean reportEntryBean = new ReportEntryBean(reportEntry);

		assertEquals("[error@graph_mapping|subName|><>\"] (IllegalArgumentException) message {query='query'}",
				reportEntryBean.toString());
		assertEquals("<entry status=\"error\" scope=\"graph_mapping\" name=\"subName\" parent=\"&gt;&lt;&gt;&quot;\" cause=\"IllegalArgumentException\" message=\"message\" query=\"query\"/>", toXml(reportEntryBean));
	}


	private String toXml(ReportEntryBean reportEntryBean) throws Exception {
		Serializer serializer = new Persister();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		serializer.write(reportEntryBean, stream);
		return stream.toString("UTF-8");
	}

}
