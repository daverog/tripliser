package org.daverog.tripliser.report.xml;

import java.util.ArrayList;
import java.util.List;

import org.daverog.tripliser.report.ReportEntry;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


@Root(name="report")
public class ReportBean {

	@ElementList(inline=true)
	private List<ReportEntryBean> entries;

	public ReportBean(List<ReportEntry> entries) {
		this.entries = new ArrayList<ReportEntryBean>();
		for (ReportEntry reportEntry : entries) {
			this.entries.add(new ReportEntryBean(reportEntry));
		}
	}

}
