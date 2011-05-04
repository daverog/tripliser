package org.daverog.mockito;

import org.mockito.internal.util.reflection.Whitebox;

public class MockitoBuilderBase {

	public void setInternalState(Object target, String field, Object value) {
		Whitebox.setInternalState(target, field, value);
	}

}
