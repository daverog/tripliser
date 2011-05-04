package org.daverog.tripliser.query.saxon;

import static org.daverog.tripliser.testutils.XmlFileUtils.loadXmlFileStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.SingletonIterator;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import org.apache.commons.io.IOUtils;
import org.daverog.tripliser.query.Item;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.QueryException;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.query.saxon.SaxonXPathQueryableDocument;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


public class SaxonXPathTripliserDocumentTest {
    
	private Queryable xmlDocument;
	private Queryable xmlDocumentWithDefaultNamespace;
	private Queryable xmlDocumentWithSubNamespace;
	private SaxonXPathQueryableDocument xmlDocumentWithNoNamespace;
	private SaxonXPathQueryableDocument xmlDocumentWithDefaultNamespaceAndNoDefaultSupplied;
	
	@Before
	public void setUp() throws SaxonApiException {
		MockitoAnnotations.initMocks(this);
			
		Map<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("simple", "http://bbc.co.uk/simple/");
		namespaces.put("diff", "http://bbc.co.uk/diff");
		namespaces.put("func", "http://bbc.co.uk/functions");
		
		Set<ExtensionFunctionDefinition> functions = new HashSet<ExtensionFunctionDefinition>();
		
		functions.add(new ExtensionFunctionDefinition() {
			
			@Override
			public ExtensionFunctionCall makeCallExpression() {
				return new ExtensionFunctionCall() {
					
					@Override
					public SequenceIterator call(SequenceIterator[] arguments, XPathContext xPathContext)
							throws XPathException {
						String value = arguments[0].next().getStringValue();
						return SingletonIterator.makeIterator(new StringValue(value.replace("e", "o")));
					}
				};
			}
			
			@Override
			public SequenceType getResultType(SequenceType[] suppliedArguments) {
				return SequenceType.SINGLE_STRING;
			}
			
			@Override
			public StructuredQName getFunctionQName() {
				return new StructuredQName("func", "http://bbc.co.uk/functions", "EsToOs");
			}
			
			@Override
			public SequenceType[] getArgumentTypes() {
				return new SequenceType[] {SequenceType.SINGLE_STRING};
			}
		});
		
		xmlDocument =  new SaxonXPathQueryableDocument(
				loadXmlFileStream("integration-tests/source/simple.xml"), namespaces, functions);
		xmlDocumentWithNoNamespace =  new SaxonXPathQueryableDocument(
				loadXmlFileStream("integration-tests/source/simple-with-no-namespace.xml"), namespaces, functions);
		xmlDocumentWithDefaultNamespace =  new SaxonXPathQueryableDocument(
				loadXmlFileStream("integration-tests/source/simple-with-default-namespace.xml"), namespaces, functions);
		xmlDocumentWithDefaultNamespaceAndNoDefaultSupplied =  new SaxonXPathQueryableDocument(
				loadXmlFileStream("integration-tests/source/simple-with-default-namespace.xml"));
		xmlDocumentWithSubNamespace =  new SaxonXPathQueryableDocument(
				loadXmlFileStream("integration-tests/source/simple-with-sub-namespace.xml"), namespaces, functions);
	}
	
	@Test
	public void anInvalidXmlFileThrowsASaxonApiException() {
		try {
			new SaxonXPathQueryableDocument(IOUtils.toInputStream("oahwegfkjhsaekgjf"), new HashMap<String, String>(), new HashSet<ExtensionFunctionDefinition>());
			fail("An exception should have been thrown");
		} catch(SaxonApiException e) {
			//Success!
		}
	}
	
    @Test
    public void readsXPathFromXml() throws Exception {
    	assertEquals("Test Name", xmlDocument.readItem("//simple:simple/simple:name/text()").getStringValue());
    }
    
    @Test
    public void readsXPathFromXmlWhenADefaultNamespaceHasBeenSet() throws Exception {
    	Map<String, String> namespaces = new HashMap<String, String>();
    	namespaces.put("", "http://bbc.co.uk/simple/");
    	
    	xmlDocument =  new SaxonXPathQueryableDocument(
    			loadXmlFileStream("integration-tests/source/simple.xml"), namespaces, new HashSet<ExtensionFunctionDefinition>());
    	
    	assertEquals("Test Name", xmlDocument.readItem("//simple/name/text()").getStringValue());
    }
    
    @Test
    public void readsXPathWithAFunction() throws Exception {
    	assertEquals("Test Name Append", xmlDocument.readItem("concat(//simple:simple/simple:name,' Append')").getStringValue());
    }
    
    @Test
    public void readsXPathWithAnXPath2Function() throws Exception {
    	assertEquals("test name", xmlDocument.readItem("lower-case(//simple:simple/simple:name)").getStringValue());
    }

    @Test
    public void readsXPathWithAnExtensionFunction() throws Exception {
    	assertEquals("Tost Namo", xmlDocument.readItem("func:EsToOs(//simple:simple/simple:name)").getStringValue());
    }
    
    @Test
    public void readsXPathFromXmlWithNoNamespace() throws Exception {
    	assertEquals("Test Name", xmlDocumentWithNoNamespace.readItem("//simple/name").getStringValue());
    }
    
    @Test
    public void readsXPathFromXmlWithDefaultNamespace() throws Exception {
    	assertEquals("Hello", xmlDocumentWithDefaultNamespace.readItem("//simple:simple/diff:extra").getStringValue());
    }
    
    @Test
    public void readsXPathFromXmlWithDefaultNamespaceWhenNoDefaultIsSupplied() throws Exception {
    	assertEquals("Test Name", xmlDocumentWithDefaultNamespaceAndNoDefaultSupplied.readItem("//simple/name").getStringValue());
    }
    
    @Test
    public void readsXPathFromXmlWithANamespaceDeclaredOnASubNode() throws Exception {
    	assertEquals("Hello", xmlDocumentWithSubNamespace.readItem("//simple:simple/diff:extra").getStringValue());
    }
    
    @Test
    public void readingUpTheDocumentFromASubNodeUisngDotDotWorks() throws Exception {
    	Node node = xmlDocument.readNode("//simple:simple/simple:sub");
    	
    	assertEquals("Test Name", xmlDocument.readItem(node, "../simple:name").getStringValue());
    }
    
    @Test
    public void readsXPathFromXmlNode() throws Exception {
    	Node node = xmlDocument.readNode("//simple:simple/simple:sub");
    	
    	assertEquals("Sub Sub", xmlDocument.readItem(node, "simple:subsub").getStringValue());
    }
    
    @Test
    public void readsXPathFromXmlNodeListItem() throws Exception {
    	List<Item> nodes = xmlDocument.readList("//simple:simple/simple:name");
    	
    	assertEquals(1, nodes.size());
    	assertEquals("Test Name", xmlDocument.readItem((Node)nodes.get(0), "text()").getStringValue());
    }
    
    @Test
    public void readsXPathUsingXPath2FunctionOnNodes() throws Exception {
    	List<Item> strings = xmlDocument.readList("//simple:simple/simple:items/simple:item/simple:a/concat(text(), ../simple:b/text())");
    	
    	assertEquals(2, strings.size());
    	assertEquals("AB", strings.get(0).getStringValue());
    	assertEquals("CD", strings.get(1).getStringValue());
    }
	
    @Test
    public void anXPathWithAnUndefinedPrefixResultsInATripliserQueryException() {
    	try {
    		xmlDocument.readItem("//jazz:simple/simple:name/text()");
    		fail("An exception should have been thrown");
    	} catch(QueryException e){
    		assertEquals("Problem running XPath query '//jazz:simple/simple:name/text()'", e.getMessage());
    		assertTrue("Does not contain 'jazz': "+ e.getCause().getMessage(), e.getCause().getMessage().contains("jazz"));
    	}
    }
    
    @Test
    public void anXPathWithAnUndefinedFunctionResultsInATripliserQueryException() {
    	try {
    		xmlDocument.readItem("//simple:simple/simple:name/func:jazz(text())");
    		fail("An exception should have been thrown");
    	} catch(QueryException e){
    		assertEquals("Problem running XPath query '//simple:simple/simple:name/func:jazz(text())'", e.getMessage());
    		assertTrue("Does not contain 'jazz': "+ e.getCause().getMessage(), e.getCause().getMessage().contains("jazz"));
    	}
    }
     
    @Test
    public void throwsExceptionForInvalidXPath() {
    	try {
    		xmlDocument.readItem("{$}()invalidXPath");
    		fail("Should have thrown an exception");
    	} catch(Exception e) {
    		//success
    	}
    }

    @Test
    public void readsXPathWhereResultContainsHtmlEntities() throws Exception {
    	assertEquals('\'', xmlDocument.readItem("//simple:simple/simple:entities/text()").getStringValue().charAt(0));
    	assertEquals('&', xmlDocument.readItem("//simple:simple/simple:entities/text()").getStringValue().charAt(1));
    }
    
}
