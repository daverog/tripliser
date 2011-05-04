package org.daverog.tripliser.graphs;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

import org.daverog.tripliser.exception.InvalidPropertyMappingException;
import org.daverog.tripliser.mapping.model.NamespaceProperty;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;


public class JenaModelService {

	public static final HashMap<String, String> BUILT_IN_NAMESPACES = new HashMap<String, String>() {{
		put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	}};

	public Property getProperty(Model model, String name, GraphContext graphContext) throws InvalidPropertyMappingException {
		return model.createProperty(getNamespaceProperty(graphContext, model, name).getFullPropertyName());
	}

	public String convertToUri(Model model, String value, GraphContext graphContext) throws URISyntaxException {
		try{
			return getNamespaceProperty(graphContext, model, value).getFullPropertyName();
		} catch(InvalidPropertyMappingException e) {
			new URI(value);
			return value;
		}
	}

	private NamespaceProperty getNamespaceProperty(GraphContext graphContext, Model model, String name) throws InvalidPropertyMappingException{
		String[] nameParts = splitAndCheckPrefixedPropertyName(name);

		String prefix = nameParts[0];
		String namespace = model.getNsPrefixURI(prefix);

		if (namespace == null) {
			namespace = BUILT_IN_NAMESPACES.get(prefix);

			if (namespace == null) {
				namespace = graphContext.getNamespace(prefix);

				if (namespace == null) {
					throw new InvalidPropertyMappingException("A mapping contains an invalid namespace prefix '" + prefix + "'");
				} else {
					model.setNsPrefix(prefix, namespace);
				}
			}
		}

		return new NamespaceProperty(nameParts[1], namespace);
	}

	private String[] splitAndCheckPrefixedPropertyName(String name) throws InvalidPropertyMappingException{
		String[] nameParts = name.split(":");

		if (nameParts.length != 2 || name.startsWith(":") || name.endsWith(":"))
			throw new InvalidPropertyMappingException("A mapping contains an invalid name '" + name + "' which should be of the form 'prefix:propertyName'");

		return nameParts;
	}

	public void loadNamespacesIntoModel(GraphContext graphContext, Model model) {
		Iterator<String> prefixes = graphContext.getNamespacePrefixes();
		while (prefixes.hasNext()) {
			String prefix = prefixes.next();
			model.setNsPrefix(prefix , graphContext.getNamespace(prefix));
		}
	}

}
