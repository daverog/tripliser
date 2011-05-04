package org.daverog.tripliser;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.TripliserFactory;
import org.daverog.tripliser.exception.TripliserException;
import org.junit.Test;



public class TripliserFactoryTest extends MockitoTestBase {

	@Test
	public void ifNoMappingFileIsSuppliedATripliserExceptionIsThrown() {
		try {
			TripliserFactory.instance().create();
			exceptionExpected();
		} catch (TripliserException e) {
			assertEquals("A mapping file must be supplied", e.getMessage());
		}
	}

}
