package org.daverog.tripliser.query.saxon;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.sax.SAXSource;

import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;

import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.query.Item;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.QueryException;
import org.daverog.tripliser.query.Queryable;
import org.xml.sax.InputSource;


public class SaxonXPathQueryableDocument extends Queryable {

    private XPathCompiler xPath;
    private Node source;

    /**
     * @throws TripliserException
     * @see #SaxonXPathQueryableDocument(InputStream, Map, Set)
     */
	public SaxonXPathQueryableDocument(InputStream sourceXml) throws SaxonApiException {
		this(sourceXml, new HashMap<String, String>(), new HashSet<ExtensionFunctionDefinition>());
	}

    /**
     * @throws TripliserException
     * @see #SaxonXPathQueryableDocument(InputStream, Map, Set)
     */
    public SaxonXPathQueryableDocument(InputStream sourceXml, Map<String, String> namespaces) throws SaxonApiException {
    	this(sourceXml, namespaces, new HashSet<ExtensionFunctionDefinition>());
    }

    /**
     * Creates a queryable document powered by the Saxon XPath 2.0 engine.
     * XPath 2.0 is the supported query expression language.
     *
     * @param sourceXml  The XML file on which queries can be made
     * @param namespaces A map of namespaces used in the XPath queries. Use an empty string key for the default namespace.
     * @param functions  A set of extension functions for use in XPath queries
     */
    public SaxonXPathQueryableDocument(InputStream sourceXml, Map<String, String> namespaces, Set<ExtensionFunctionDefinition> functions) throws SaxonApiException {
		Processor proc = new Processor(false);

        for (ExtensionFunctionDefinition func : functions) {
        	proc.registerExtensionFunction(func);
        }

        DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        source = new XdmNodeAdapter(builder.build(new SAXSource(new InputSource(sourceXml))));

        xPath = proc.newXPathCompiler();

        boolean defaultNamespaceSupplied = false;
        for (String prefix : namespaces.keySet()) {
        	if (prefix == "") defaultNamespaceSupplied = true;
        	xPath.declareNamespace(prefix, namespaces.get(prefix));
        }

        if (!defaultNamespaceSupplied) {
			String defaultNamespace = DefaultNamespaceExtractor.extract(source.getNode().toString());

			if (defaultNamespace != null) {
				xPath.declareNamespace("", defaultNamespace);
			}
        }
    }

	protected List<Item> read(Node node, String expression) throws QueryException {
    	if (node == null) node = source;

    	try {
	    	List<Item> items = new ArrayList<Item>();
	    	XPathSelector selector = xPath.compile(expression).load();
	        selector.setContextItem((XdmNode)node.getNode());
	        for (XdmItem item: selector) {
	        	if (item instanceof XdmAtomicValue) {
	        		items.add(new XdmAtomicValueAdapter((XdmAtomicValue)item));
	        	} else if (item instanceof XdmNode) {
	        		items.add(new XdmNodeAdapter((XdmNode)item));
	        	}
	        }

	    	return items;
    	} catch(SaxonApiException e) {
    		throw new QueryException("Problem running XPath query '" + expression + "'", e);
    	}
    }

}
