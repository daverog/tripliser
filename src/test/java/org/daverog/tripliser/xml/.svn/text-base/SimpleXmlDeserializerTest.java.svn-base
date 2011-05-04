package org.daverog.tripliser.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.daverog.tripliser.exception.MappingException;
import org.daverog.tripliser.mapping.model.DummyXmlModel;
import org.daverog.tripliser.xml.SimpleXmlDeserializer;
import org.junit.Test;


public class SimpleXmlDeserializerTest {
	
	private SimpleXmlDeserializer deserialiser = new SimpleXmlDeserializer();
	
	@Test
	public void deserialisingANullXmlFileThrowsAnInvalidMappingException(){
		try {
			String xml = null;
			deserialiser.deserialise(xml, DummyXmlModel.class);
			fail();
		} catch(MappingException e) {
			assertEquals("Invalid mapping XML", e.getMessage());
		}
	}
	
	@Test
	public void deserialisingAEmptyXmlFileThrowsAnInvalidMappingException(){
		try {
			String xml = "";
			deserialiser.deserialise(xml, DummyXmlModel.class);
			fail();
		} catch(MappingException e) {
			assertEquals("Invalid mapping XML", e.getMessage());
		}
	}
	
	@Test
	public void deserialisingAnInvalidXmlFileThrowsAnInvalidMappingException(){
		try {
			String xml = "<invalid<Xml>";
			deserialiser.deserialise(xml, DummyXmlModel.class);
			fail();
		} catch(MappingException e) {
			assertEquals("Invalid mapping XML", e.getMessage());
		}
	}
	
	@Test
	public void deserialisingAnIncorrectXmlFileThrowsAnInvalidMappingException(){
		try {
			String xml = "<dummy invalidAttribute=\"value\"/>";
			deserialiser.deserialise(xml, DummyXmlModel.class);
			fail();
		} catch(MappingException e) {
			assertEquals("Invalid mapping XML", e.getMessage());
		}
	}
	
	@Test
	public void deserialisingACorrectXmlFileReturnsTheModelObjectContainingTheCorrectValues() throws MappingException{
		String xml = "<dummy validAttribute=\"correctValue\"/>";
		DummyXmlModel model = deserialiser.deserialise(xml, DummyXmlModel.class);

		assertEquals("correctValue", model.validAttribute);
	}
	
}
