package org.daverog.tripliser.report;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.report.DefaultReporterFactory;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReport;
import org.daverog.tripliser.report.TripliserReporter;
import org.junit.Test;


public class DefaultReporterFactoryTest extends MockitoTestBase {
	
	@Test
	public void creatingADefaultReporterFactoryWithANullGlobalClassFails() {
		try {
			new DefaultReporterFactory(null, ValidReporter.class);
		} catch(RuntimeException e) {
			assertEquals("A global tripliser reporter class must be supplied", e.getMessage());
		}
	}
	
	@Test
	public void creatingADefaultReporterFactoryWithANullGraphClassFails() {
		try {
			new DefaultReporterFactory(ValidReporter.class, null);
		} catch(RuntimeException e) {
			assertEquals("A graph tripliser reporter class must be supplied", e.getMessage());
		}
	}

	@Test
	public void creatingADefaultReporterFactoryWithAClassWithoutAnEmptyConstructorFails() {
		 try {
			 new DefaultReporterFactory(ReporterWithNoEmptyConstructor.class, ReporterWithNoEmptyConstructor.class);
		 } catch(RuntimeException e) {
			 assertEquals("A tripliser reporter class used is invalid", e.getMessage());
		 }
	}
	
	private class ReporterWithNoEmptyConstructor implements TripliserReporter {

		private ReporterWithNoEmptyConstructor(String param){}
		
		@Override
		public void addMessage(ReportEntry message) {}

		@Override
		public TripliserReport getReport() {
			return null;
		}
		
	}
	
	private class ValidReporter implements TripliserReporter {
		
		@Override
		public void addMessage(ReportEntry message) {}
		
		@Override
		public TripliserReport getReport() {
			return null;
		}
		
	}
	
	
}
