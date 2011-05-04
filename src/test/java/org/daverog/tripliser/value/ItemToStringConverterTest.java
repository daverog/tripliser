package org.daverog.tripliser.value;

import java.util.Arrays;
import java.util.List;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.graphs.GraphContextBuilder;
import org.daverog.tripliser.mapping.model.ValueMapping;
import org.daverog.tripliser.mapping.model.ValueMappingBuilder;
import org.daverog.tripliser.query.Item;
import org.daverog.tripliser.query.SimpleStringValue;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.value.ItemToStringConverter;
import org.daverog.tripliser.value.ValueGenerator;
import org.daverog.tripliser.value.ValueValidationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;



public class ItemToStringConverterTest extends MockitoTestBase {
	
	@Mock TripliserReporter tripliserReporter;
	@Mock ValueGenerator valueGenerator;
	
	private ItemToStringConverter converter;
	
	@Before
	public void setUp() {
		converter = new ItemToStringConverter(valueGenerator);
	}

	@Test
	public void returnsAnEmptyListIfValueOfItemIsNull() throws ValueValidationException {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().required(true).toValueMapping();
		ValueValidationException valueValidationException = new ValueValidationException("Error");
		
		when(valueGenerator.getValue(null, valueMapping, graphContext)).thenThrow(valueValidationException);
		
		Item item = new SimpleStringValue(null);
		List<String> values = converter.convertItemsToStrings(Arrays.asList(item), valueMapping, tripliserReporter, graphContext);
		
		assertTrue(values.isEmpty());
		
		verify(tripliserReporter).addMessage(new ReportEntry(valueValidationException, Status.WARNING, Scope.PROPERTY, valueMapping));
	}
	
	@Test
	public void returnsValuesOfItemsInOrder() throws ValueValidationException {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().required(true).toValueMapping();
		
		when(valueGenerator.getValue("originalValue", valueMapping, graphContext)).thenReturn("resultingValue");
		when(valueGenerator.getValue("originalValue2", valueMapping, graphContext)).thenReturn("resultingValue2");
		
		Item item = new SimpleStringValue("originalValue");
		Item item2 = new SimpleStringValue("originalValue2");
		List<String> values = converter.convertItemsToStrings(Arrays.asList(item, item2), valueMapping, tripliserReporter, graphContext);
		
		assertEquals(2, values.size());
		assertEquals("resultingValue", values.get(0));
		assertEquals("resultingValue2", values.get(1));
		
		verifyZeroInteractions(tripliserReporter);
	}

}
