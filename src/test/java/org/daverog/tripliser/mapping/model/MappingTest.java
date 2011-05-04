package org.daverog.tripliser.mapping.model;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.mapping.model.Mapping;
import org.junit.Test;
import org.simpleframework.xml.core.PersistenceException;


public class MappingTest extends MockitoTestBase {

	@Test
	public void gettingTheNamespaceMapReturnsEachNamespaceMappedPrefixToUrl(){
		Mapping mapping = new MappingBuilder()
			.addNamespace(new NamespaceBuilder()
				.prefix("a")
				.url("b")
				.toNamespace())
			.addNamespace(new NamespaceBuilder()
				.prefix("x")
				.url("y")
				.toNamespace())
			.toMapping();
		
		assertEquals(2, mapping.getNamespaceMap().size());
		assertEquals("b", mapping.getNamespaceMap().get("a"));
		assertEquals("y", mapping.getNamespaceMap().get("x"));
	}
	
	@Test
	public void gettingTheNamespaceMapReturnsTheDefaultNamespaceAsBothEmptyStringAndPrefixMapped(){
		Mapping mapping = new MappingBuilder()
			.addNamespace(new NamespaceBuilder()
				.prefix("a")
				.url("b")
				.defaultNamespace(true)
				.toNamespace())
			.toMapping();
		
		assertEquals(2, mapping.getNamespaceMap().size());
		assertEquals("b", mapping.getNamespaceMap().get(""));
		assertEquals("b", mapping.getNamespaceMap().get("a"));
	}
	
	@Test
	public void anInvalidNamespaceThrowsAPersistenceException() {
		Mapping mapping = new MappingBuilder()
			.addNamespace(new NamespaceBuilder()
				.prefix("a")
				.url("&*^%&*^%")
				.toNamespace())
			.toMapping();
		
		try {
			mapping.validateNamespaces();
			exceptionExpected();
		} catch (PersistenceException e) {
			assertEquals("Invalid namespace URI '&*^%&*^%'", e.getMessage());
		}
	}
	
	@Test
	public void anValidNamespaceDoesNotThrowAPersistenceException() throws PersistenceException {
		Mapping mapping = new MappingBuilder()
			.addNamespace(new NamespaceBuilder()
				.prefix("a")
				.url("http://bbc.co.uk/validuri")
				.toNamespace())
			.toMapping();
		
		mapping.validateNamespaces();
	}
	
}
