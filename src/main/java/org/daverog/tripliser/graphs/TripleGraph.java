package org.daverog.tripliser.graphs;

import static org.daverog.tripliser.Constants.MIME_TYPE_TO_JENA_LANG;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.report.TripliserReport;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.JenaException;

public abstract class TripleGraph {

	private String name;
	private Model model;
	protected Map<String, String> tags;

	TripleGraph(Model model, String name){
		this.model = model;
		this.name = name;
		tags = new HashMap<String, String>();
	}

	/**
	 * @see #toString(String, String)
	 */
	public String toString(String mimeType) throws TripliserException {
		return toString(mimeType, null);
	}

	/**
	 * Serialise the triple graph to a String
	 *
	 * @param mimeType The MimeType of the required String
	 * @param relativeUri A relative URI to apply to serialised URIs
	 * @return The serialised RDF
	 */
	public String toString(String mimeType, String relativeUri) throws TripliserException {
		String lang = getLanguage(mimeType);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try{
			getModel().write(baos, lang, relativeUri);
			return baos.toString("UTF-8");
		} catch(JenaException e) {
			throw new TripliserException("Failed to create serialization in language " + lang, e, this);
		} catch(UnsupportedEncodingException e) {
			throw new RuntimeException("Unsupported encoding UTF-8", e);
		}
	}

	/**
	 * Serialise the triple graph into an OutputStream
	 *
	 * @param outputStream The stream to serialise into
	 * @param mimeType The MimeType of the required String
	 * @param relativeUri A relative URI to apply to serialised URIs
	 */
	public void write(OutputStream outputStream, String mimeType, String relativeUri) throws TripliserException {
		String lang = getLanguage(mimeType);

		try{
			getModel().write(outputStream, lang);
		} catch(JenaException e) {
			throw new TripliserException("Failed to create serialization in language " + lang, e, this);
		}
	}

	private String getLanguage(String mimeType) throws TripliserException {
		String lang = MIME_TYPE_TO_JENA_LANG.get(mimeType);

		if (lang == null) throw new TripliserException("Mime type " + mimeType + " is not supported", this);
		return lang;
	}

	/**
	 * The internal Jena Model object
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * The name of the graph, as applied using the
	 * name attribute in the mapping file. Otherwise the
	 * name will be an automatically provided value.
	 */
	public String getName() {
		return name;
	}

	/**
	 * The report indicating the success of
	 * the conversion process
	 */
	public abstract TripliserReport getReport();

	/**
	 * Obtain the value of a tag applied to the graph via a <tag> element in the mapping file
	 *
	 * @param name The tag name
	 * @return The tag value for this graph
	 */
	public String getTagValue(String name) {
		return tags.get(name);
	}

	/**
	 * An iteration over the available tag names
	 */
	public Iterator<String> getTagNames() {
		return tags.keySet().iterator();
	}

}
