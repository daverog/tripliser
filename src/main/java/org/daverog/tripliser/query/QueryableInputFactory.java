package org.daverog.tripliser.query;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.query.saxon.SaxonXPathQueryableDocument;

import net.sf.saxon.lib.ExtensionFunctionDefinition;

public class QueryableInputFactory {

	private final Map<String, String> namespaces;
	private final Set<ExtensionFunctionDefinition> functions;

	public QueryableInputFactory(Map<String, String> namespaces, Set<ExtensionFunctionDefinition> functions) {
		this.namespaces = namespaces;
		this.functions = functions;
	}
	
	public Queryable createSaxonXPathTripliserDocument(String name, InputStream xmlSource) throws TripliserException {
		try {
			return new SaxonXPathQueryableDocument(xmlSource, namespaces, functions);
		} catch(Exception e) {
			if (name != null) {
				name = " '"+name+"'";
			} else {
				name = "";
			}
			throw new TripliserException("The input" + name + " is not valid XML", e, null);
		}
	}

}
