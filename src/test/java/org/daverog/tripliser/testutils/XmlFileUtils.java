package org.daverog.tripliser.testutils;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.xml.serialize.XMLSerializer;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;

public class XmlFileUtils {

	public static String loadXmlFile(String filename){
		try {
			String xml = IOUtils.toString(ClassLoader.getSystemResourceAsStream(filename));
			validateXml(xml);
			return xml;
		} catch (Exception e) {
			fail("Could not load fixture file " + filename + ": " + e.getMessage());
			return "";
		}
	}
	
	public static InputStream loadXmlFileStream(String filename){
		try {
			return ClassLoader.getSystemResourceAsStream(filename);
		} catch (Exception e) {
			fail("Could not load fixture file " + filename + ": " + e.getMessage());
			return null;
		}
	}
	
	public static Document loadXmlDocument(String filename){
		try {
			return XMLUnit.buildTestDocument(IOUtils.toString(ClassLoader.getSystemResourceAsStream(filename)));
		} catch (Exception e) {
			fail("Could not load fixture file " + filename + ": " + e.getMessage());
			return null;
		}
	}
	
	public static Properties loadPropertiesFile(String filename){
		try {
			Properties props = new Properties();
			props.load(ClassLoader.getSystemResourceAsStream(filename));
			return props;
		} catch (Exception e) {
			fail("Could not load properties file " + filename + ": " + e.getMessage());
			return null;
		}
	}
	
	public static void validateXml(String xml) throws Exception{
		try {
			XMLUnit.buildTestDocument(xml);
		} catch(Exception e) {
			System.out.println(xml);
			throw e;
		}
	}
	
	public static String outputXmlDocument(Document document) throws IOException{
        XMLSerializer serializer = new XMLSerializer();
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
		serializer.setOutputByteStream(boas);
        serializer.serialize(document);
        return boas.toString("UTF-8");
	}

}
