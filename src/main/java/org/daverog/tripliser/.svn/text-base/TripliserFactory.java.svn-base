package org.daverog.tripliser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import net.sf.saxon.lib.ExtensionFunctionDefinition;

import org.apache.commons.io.IOUtils;
import org.daverog.tripliser.exception.MappingException;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.graphs.GraphContextFactory;
import org.daverog.tripliser.graphs.JenaModelService;
import org.daverog.tripliser.graphs.MutableTripleGraphFactory;
import org.daverog.tripliser.graphs.TripleGraphCollectorFactory;
import org.daverog.tripliser.graphs.TripleGraphIteratorFactory;
import org.daverog.tripliser.mapping.AutomaticMappingNamingService;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.query.QueryService;
import org.daverog.tripliser.query.QueryableInputFactory;
import org.daverog.tripliser.report.DefaultReporterFactory;
import org.daverog.tripliser.report.RecordingReporter;
import org.daverog.tripliser.report.SystemOutReporter;
import org.daverog.tripliser.report.TripliserReporterFactory;
import org.daverog.tripliser.triplisers.GraphNodeTripliserFactory;
import org.daverog.tripliser.triplisers.GraphTagger;
import org.daverog.tripliser.triplisers.GraphTripliserFactory;
import org.daverog.tripliser.triplisers.InputTripliserFactory;
import org.daverog.tripliser.triplisers.ResourceNodeTripliser;
import org.daverog.tripliser.triplisers.ResourceTripliserFactory;
import org.daverog.tripliser.value.ItemToStringConverter;
import org.daverog.tripliser.value.ValueGenerator;
import org.daverog.tripliser.xml.SimpleXmlDeserializer;


public class TripliserFactory {

	private Set<ExtensionFunctionDefinition> functions;
	private InputStream mappingXml;
	private SimpleXmlDeserializer simpleXmlDeserializer;

	public static TripliserFactory instance() {
		SimpleXmlDeserializer simpleXmlDeserializer = new SimpleXmlDeserializer();
		return new TripliserFactory(simpleXmlDeserializer);
	}

	/**
	 * Create a new <code>Tripliser</code> which can produce triple graphs based on the provided mapping.
	 *
	 * @param mappingXml The XML file that indicates how data from source XML files can be converted into triples
	 * @return A Tripliser, an engine for the generation of triples from input data
	 */
	public static Tripliser create(InputStream mappingXml) throws TripliserException {
		return instance().setMapping(mappingXml).create();
	}

	private TripliserFactory(SimpleXmlDeserializer simpleXmlDeserializer) {
		this.simpleXmlDeserializer = simpleXmlDeserializer;

		functions = new HashSet<ExtensionFunctionDefinition>();
	}

	/**
	 * @param mappingXml The XML file that indicates how data from source XML files can be converted into triples
	 */
	public TripliserFactory setMapping(InputStream mappingXml) {
		this.mappingXml = mappingXml;
		return this;
	}

	/**
	 * Add Saxon XPath 2.0 functions for use in queries
	 */
	public TripliserFactory addFunctions(Set<ExtensionFunctionDefinition> functions) {
		this.functions = functions;
		return this;
	}

	/**
	 * Create a new <code>Tripliser</code> which can produce triple graphs based on the provided mapping.
	 *
	 * @return A Tripliser, an engine for the generation of triples from input data
	 */
	public Tripliser create() throws TripliserException {
		if (mappingXml == null) throw new TripliserException("A mapping file must be supplied");

		try {
			//Handle mapping file
			AutomaticMappingNamingService namingService = new AutomaticMappingNamingService();
			Mapping mapping = simpleXmlDeserializer.deserialise(IOUtils.toString(mappingXml, "UTF-8"), Mapping.class);
			namingService.provideNamesForUnamedMappings(mapping);

			// Basic dependencies
			TripliserReporterFactory tripliserReporterFactory;
			try {
				tripliserReporterFactory = new DefaultReporterFactory(
						SystemOutReporter.class, RecordingReporter.class);
			} catch (Exception e) {
				throw new RuntimeException("Error loading default reporters");
			}
			ValueGenerator valueGenerator = new ValueGenerator();
			JenaModelService jenaModelService = new JenaModelService();
			QueryService queryService = new QueryService();
			ItemToStringConverter itemToStringConverter = new ItemToStringConverter(valueGenerator);
			QueryableInputFactory queryableInputFactory = new QueryableInputFactory(
					mapping.getNamespaceMap(), functions);
			GraphContextFactory graphContextFactory = new GraphContextFactory(queryableInputFactory);
			MutableTripleGraphFactory mutableTripleGraphFactory = new MutableTripleGraphFactory(jenaModelService, queryService, itemToStringConverter);
			GraphTagger graphTagger = new GraphTagger();

			//Create triplise processing chain
			ResourceNodeTripliser resourceNodeTripliser = new ResourceNodeTripliser();
			ResourceTripliserFactory resourceTripliserFactory = new ResourceTripliserFactory(resourceNodeTripliser);
			GraphNodeTripliserFactory graphNodeTripliserFactory = new GraphNodeTripliserFactory(resourceTripliserFactory);
			GraphTripliserFactory graphTripliserFactory = new GraphTripliserFactory(
					graphNodeTripliserFactory, queryService, graphTagger);
			InputTripliserFactory inputTripliserFactory = new InputTripliserFactory(graphTripliserFactory);

			//Create wrapper for iterations
			TripleGraphIteratorFactory tripleGraphIteratorFactory =
				new TripleGraphIteratorFactory(
					mapping,
					queryableInputFactory,
					mutableTripleGraphFactory,
					inputTripliserFactory,
					tripliserReporterFactory);

			//Create wrapper for collection map generation
			TripleGraphCollectorFactory tripleGraphCollectionMapGenerator =
				new TripleGraphCollectorFactory(
					mapping,
					queryableInputFactory,
					mutableTripleGraphFactory,
					tripliserReporterFactory,
					inputTripliserFactory);

			return new Tripliser(mapping, tripleGraphCollectionMapGenerator, tripleGraphIteratorFactory, graphContextFactory);
		} catch(MappingException e) {
			throw new TripliserException("Failed to create tripliser", e, null);
		} catch (IOException e) {
			throw new TripliserException("Error loading mapping XML", e, null);
		}
	}

}
