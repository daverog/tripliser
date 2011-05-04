package org.daverog.tripliser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.graphs.GraphContextFactory;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.graphs.TripleGraphCollection;
import org.daverog.tripliser.graphs.TripleGraphCollectorFactory;
import org.daverog.tripliser.graphs.TripleGraphIteratorFactory;
import org.daverog.tripliser.mapping.model.Mapping;


public class Tripliser {

	private TripleGraphCollectorFactory tripleGraphCollectorFactory;
	private GraphContextFactory graphContextFactory;
	private TripleGraphIteratorFactory tripleGraphIteratorFactory;

	private Mapping mapping;
	private Map<String, InputStream> supportingInputs;
	private List<InputStream> inputs;
	private Iterator<InputStream> inputIterator;
	private String mimeType;
	private Scope mergeScope;

	Tripliser(Mapping mapping, TripleGraphCollectorFactory tripleGraphCollectionMapGenerator, TripleGraphIteratorFactory tripleGraphIteratorFactory, GraphContextFactory graphContextFactory){
		this.mapping = mapping;
		this.tripleGraphCollectorFactory = tripleGraphCollectionMapGenerator;
		this.tripleGraphIteratorFactory = tripleGraphIteratorFactory;
		this.graphContextFactory = graphContextFactory;

		mimeType = Constants.MIME_TYPE_APPLICATION_RDF_XML;
		inputs = new ArrayList<InputStream>();
		supportingInputs = new HashMap<String, InputStream>();
	}

	/**
	 * Generate a RDF serialisation.
	 *
	 * The default mime type is application/xml+rdf which can
	 * be overridden with the setMimeType() method
	 *
	 * Warning: this method can be inefficient for large input sets,
	 * where the getTripleGraphIterator() method is preferred.
	 */
	public String generateRdf() throws TripliserException {
		TripleGraph tripleGraph = generateTripleGraph();

		if (!tripleGraph.getReport().isSuccess()) {
			throw new TripliserException("Triple graph generation failed", tripleGraph);
		}

		return tripleGraph.toString(mimeType);
	}

	/**
	 * Writes an RDF serialisation to an output stream.
	 *
	 * The default mime type is application/xml+rdf which can
	 * be overridden with the setMimeType() method
	 *
	 * Warning: this method can be inefficient for large input sets,
	 * where the getTripleGraphIterator() method is preferred.
	 */
	public void writeRdf(OutputStream outputStream) throws TripliserException {
		TripleGraph tripleGraph = generateTripleGraph();

		if (!tripleGraph.getReport().isSuccess()) {
			throw new TripliserException("Triple graph generation failed", tripleGraph);
		}

		tripleGraph.write(outputStream, mimeType, null);
	}

	/**
	 * Generate a triple graph, which contains a Jena
	 * model from which serialisations can be extracted.
	 *
	 * The triple graph also contain meta-data tags and
	 * reporting data about the conversion process.
	 *
	 * Ignores any scope previous defined via <code>setMergeScope()</code>
	 *
	 * Warning: this method can be inefficient for large input sets,
	 * where the <code>getTripleGraphIterator()</code> method is preferred.
	 */
	public TripleGraph generateTripleGraph() throws TripliserException {
		return tripleGraphCollectorFactory.create(getInputIterator(), getGraphContext(), Scope.MAPPING).convertToTripleGraph();
	}

	/**
	 * Generate a triple graph collection, merged according to the
	 * <code>mergeScope</code> property.
	 *
	 * Merges at GRAPH scope if not defined via <code>setMergeScope</code>
	 *
	 * Warning: this method can be inefficient for large input sets,
	 * where the getTripleGraphIterator() method is preferred.
	 */
	public TripleGraphCollection generateTripleGraphCollection() throws TripliserException {
		applyDefaultMergeScopeIfNull(Scope.GRAPH);
		return tripleGraphCollectorFactory.create(getInputIterator(), getGraphContext(), mergeScope);
	}

	/**
	 * Returns a graph per resource created
	 * Most efficient for large input sets,
	 *
	 * Merges at GRAPH scope if not defined via <code>setMergeScope</code>
	 *
	 * GRAPH_MAPPING and RESOURCE_MAPPING are not valid scope for
	 * iteration based approach.
	 *
	 * This is because the entire input set would need to be processed before
	 * all triples per mapping could be determined.
	 *
	 * The collection based approach is more suitable for mapping-scope graphs.
	 * @see #generateTripleGraphCollection()
	 */
	public Iterator<TripleGraph> getTripleGraphIterator() throws TripliserException {
		if(mergeScope == Scope.GRAPH_MAPPING ||
		   mergeScope == Scope.RESOURCE_MAPPING ||
		   mergeScope == Scope.MAPPING)
			throw new TripliserException("It is not possible to iterate through graphs per " +
					mergeScope +". Try using the collection based approach.");
		applyDefaultMergeScopeIfNull(Scope.GRAPH);
		return tripleGraphIteratorFactory.createTripleGraphIterator(mergeScope, getInputIterator(), getGraphContext());
	}

	private Iterator<InputStream> getInputIterator() throws TripliserException {
		if (inputIterator == null && inputs.isEmpty()) throw new TripliserException("No inputs have been supplied");
		if (inputIterator != null) return inputIterator;
		return inputs.iterator();
	}

	/**
	 * Defines the scope at which triples are merged into a graph.
	 *
	 * MAPPING - All triples are placed into a single graph,
	 * INPUT - A graph is created from the data in each input,
	 * GRAPH_MAPPING - For each &lt;graph/&gt; a triple graph is created,
	 * GRAPH - For each query result from a &lt;graph query="?"/&gt;,
	 * RESOURCE_MAPPING - For each &lt;resource&gt; (per graph query result),
	 * RESOURCE - A triple graph is created for every resource definition constructed
	 *
	 * @param mergeScope The scope at which to merge
	 */
	public Tripliser setMergeScope(Scope mergeScope) throws TripliserException {
		if (mergeScope == Scope.PROPERTY) throw new TripliserException("Scope PROPERTY is not a valid merge scope");
		this.mergeScope = mergeScope;
		return this;
	}

	/**
	 * Sets a single input <code>String</code>.
	 *
	 * This can be either a serialised input, or a reference to a data source.
	 * How inputs are handled is determined by the mapping XML file. Default
	 * behaviour is to treat all inputs as XML files.
	 *
	 * @param input The input as a <code>String</code> from which the triple data will be extracted by queries
	 */
	public Tripliser setInput(String input) throws TripliserException{
		checkInput(input, null);
		clearCurrentInputs();

		try {
			inputs.add(new ByteArrayInputStream(input.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/**
	 * Sets a single <code>InputStream</code>
	 *
	 * @param inputStream The input as an <code>InputStream</code> from which the triple data will be extracted by queries
	 */
	public Tripliser setInputStream(InputStream inputStream) throws TripliserException {
		checkInputStream(inputStream, null);
		clearCurrentInputs();

		inputs.add(inputStream);

		return this;
	}

	/**
	 * Sets a multiple <code>InputStream</code>s
	 *
	 * @param inputStreams The inputs as a list of  <code>InputStream</code>s from which the triple data will be extracted by queries
	 */
	public Tripliser setInputStreams(List<InputStream> inputStreams) throws TripliserException {
		int index = 1;
		for (InputStream inputStream : inputStreams) checkInputStream(inputStream, index++);
		clearCurrentInputs();

		inputs = inputStreams;

		return this;
	}

	/**
	 * Sets multiple input <code>String</code>s
	 *
	 * @param inputStrings The inputs as a list of <code>String</code>s from which the triple data will be extracted by queries
	 */
	public Tripliser setInputs(List<String> inputStrings) throws TripliserException {
		clearCurrentInputs();

		int index = 1;
		for (String input : inputStrings) {
			checkInput(input, index++);
			try {
				inputs.add(new ByteArrayInputStream(input.getBytes("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}

		return this;
	}

	/**
	 * Provides multiple <code>InputStream</code>s as input in an <code>Iterator</code>.
	 *
	 * This allows for more efficient loading of input data.
	 *
	 * @param inputIterator The <code>Iterator</code> providing each <code>InputStream</code>
	 */
	public Tripliser setInputIterator(Iterator<InputStream> inputIterator) {
		clearCurrentInputs();

		this.inputIterator = inputIterator;
		return this;
	}


	private void clearCurrentInputs() {
		inputs.clear();
		inputIterator = null;
	}

	/**
	 * Sets a map of supporting inputs
	 *
	 * @param supportingInputs A map of supporting inputs, each with a <code>String<code> key
	 * 			     to allow them to be referenced in the mapping file
	 */
	public Tripliser setSupportingInputs(Map<String, String> supportingInputs) throws TripliserException {
		this.supportingInputs = new HashMap<String, InputStream>();
		for (String name : supportingInputs.keySet()) {
			try {
				String supportingInput = supportingInputs.get(name);

				if (supportingInput == null) throw new TripliserException("Supporting input '" + name + "' is null");
				if (StringUtils.isBlank(supportingInput)) throw new TripliserException("Supporting input '" + name + "' is empty");

				this.supportingInputs.put(name, new ByteArrayInputStream(supportingInput.getBytes("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}

		return this;
	}

	/**
	 * Sets a map of supporting <code>InputStream</code>s
	 *
	 * @param supportingInputs A map of supporting inputs, each with a <code>String<code> key
	 * 			     to allow them to be referenced in the mapping file
	 */
	public Tripliser setSupportingInputStreams(Map<String, InputStream> supportingInputs) throws TripliserException {
		for (String name : supportingInputs.keySet()) {
			InputStream supportingInput = supportingInputs.get(name);

			if (supportingInput == null) throw new TripliserException("Supporting input stream '" + name + "' is null");
			try {
				if (supportingInput.available() == 0) throw new TripliserException("Supporting input stream '" + name + "' is empty");
			} catch (IOException e) {
				throw new TripliserException("Error when verifying supporting input stream '" + name + "'");
			}
		}

		this.supportingInputs = supportingInputs;

		return this;
	}

	/**
	 * Set the default mime type used to generate triple graphs. Determines the output of
	 * <code>generateRdf()</code>.
	 *
	 * Allowable mime types:
	 * <ul>
	 * 	<li>text/rdf+n3</li>
	 * 	<li>application/x-turtle</li>
	 * 	<li>text/plain</li>
	 * 	<li>application/rdf+xml+abbr*</li>
	 * 	<li>application/rdf+xml</li>
	 * </ul>
	 *
	 * *This is not a W3C valid mime type, but is used to produce abbreviated RDF XML.
	 *
	 * @param mimeType The mime type to produce.
	 */
	public Tripliser setMimeType(String mimeType) throws TripliserException {
		String jenaLang = Constants.MIME_TYPE_TO_JENA_LANG.get(mimeType);

		if (jenaLang == null) throw new TripliserException("Mime type " + mimeType + " is unsupported");

		this.mimeType = mimeType;
		return this;
	}

	private GraphContext getGraphContext() throws TripliserException {
		return graphContextFactory.createGraphContext(mapping, supportingInputs);
	}

	private void applyDefaultMergeScopeIfNull(Scope defaultMergeScope) {
		if (mergeScope == null) mergeScope = defaultMergeScope;
	}

	private void checkInput(String input, Integer index) throws TripliserException {
		String indexString = index == null ? "" : " " + index;
		if (input == null) throw new TripliserException("Supplied input" + indexString  + " is null");
		if (StringUtils.isBlank(input)) throw new TripliserException("Supplied input" + indexString + " is empty");
	}

	private void checkInputStream(InputStream inputStream, Integer index)
			throws TripliserException {
		String indexString = index == null ? "" : " " + index;
		if (inputStream == null) throw new TripliserException("Supplied input stream" + indexString  + " is null");
		try {
			if (inputStream.available() == 0) throw new TripliserException("Supplied input stream" + indexString  + " is empty");
		} catch (IOException e) {
			throw new TripliserException("Error when verifying input stream" + indexString);
		}
	}

}
