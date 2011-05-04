package org.daverog.tripliser;

import java.util.HashMap;
import java.util.Map;

import org.daverog.tripliser.mapping.model.GenericMapping;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.mapping.model.ResourceMapping;


public class Constants {
	
	public static final String MIME_TYPE_TEXT_RDF_N3 = "text/rdf+n3";
	public static final String MIME_TYPE_APPLICATION_X_TURTLE = "application/x-turtle";
	public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
	public static final String MIME_TYPE_APPLICATION_RDF_XML_ABBR = "application/rdf+xml+abbr";
	public static final String MIME_TYPE_APPLICATION_RDF_XML = "application/rdf+xml";
	
	public static final HashMap<String, String> MIME_TYPE_TO_JENA_LANG = new HashMap<String, String>() {{
		put(MIME_TYPE_APPLICATION_RDF_XML, "RDF/XML");
		put(MIME_TYPE_APPLICATION_RDF_XML_ABBR, "RDF/XML-ABBREV"); //Custom mime-type to support abbreviated RDF XML
		put(MIME_TYPE_TEXT_PLAIN, "N-TRIPLE");
		put(MIME_TYPE_APPLICATION_X_TURTLE, "TURTLE");
		put(MIME_TYPE_TEXT_RDF_N3, "N3");
	}};
	
	public enum Scope {
		PROPERTY,
		RESOURCE,
		RESOURCE_MAPPING,
		GRAPH,
		GRAPH_MAPPING,
		INPUT,
		MAPPING
	}
	
	public static final Map<Class<? extends GenericMapping>, Scope> MAPPING_CLASS_TO_SCOPE = 
		new HashMap<Class<? extends GenericMapping>, Scope>() {{
			put(ResourceMapping.class, Scope.RESOURCE_MAPPING);
			put(GraphMapping.class, Scope.GRAPH_MAPPING);
			put(Mapping.class, Scope.MAPPING);
	}};
	
	public static final String DEFAULT_MAPPING_NAME = "MAIN-MAPPING";

}
