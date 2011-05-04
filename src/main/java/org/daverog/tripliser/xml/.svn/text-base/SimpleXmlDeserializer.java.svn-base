package org.daverog.tripliser.xml;

import org.daverog.tripliser.exception.MappingException;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;


public class SimpleXmlDeserializer {
	
	public <T> T deserialise(String xml, Class<T> returnClass) throws MappingException  {
		T deserialisedObject = null;

		Serializer serializer = new Persister();

		try {
			deserialisedObject = serializer.read(returnClass, xml);
		} catch (Exception e) {
			throw new MappingException("Invalid mapping XML", e);
		}
		
		if(deserialisedObject == null){
			throw new MappingException("Invalid mapping XML which deserialized to null");
		}
		
		return deserialisedObject;
	}
	
}
