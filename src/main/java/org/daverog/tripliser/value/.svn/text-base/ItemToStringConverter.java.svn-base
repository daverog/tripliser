package org.daverog.tripliser.value;

import java.util.ArrayList;
import java.util.List;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.mapping.model.ValueMapping;
import org.daverog.tripliser.query.Item;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.ReportEntry.Status;


public class ItemToStringConverter {
	
	private ValueGenerator valueGenerator;
	
	public ItemToStringConverter(ValueGenerator valueGenerator) {
		this.valueGenerator = valueGenerator;
	}

	public List<String> convertItemsToStrings(List<Item> items, ValueMapping valueMapping, TripliserReporter reporter, GraphContext graphContext) {
		List<String> values = new ArrayList<String>();
		
		for(Item item : items) {
			try {
				values.add(valueGenerator.getValue(item.getStringValue(), valueMapping, graphContext));
			} catch(ValueValidationException e) {
				reporter.addMessage(new ReportEntry(e, Status.WARNING, Scope.PROPERTY, valueMapping));
			}
		}
		
		return values;
	}

}
