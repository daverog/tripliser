package org.daverog.tripliser.graphs;

import java.net.URISyntaxException;
import java.util.List;

import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.exception.IgnoredPropertyMappingException;
import org.daverog.tripliser.exception.IgnoredResourceMappingException;
import org.daverog.tripliser.exception.InvalidPropertyMappingException;
import org.daverog.tripliser.exception.InvalidResourceMappingException;
import org.daverog.tripliser.mapping.model.PropertyMapping;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.daverog.tripliser.mapping.model.ValueMapping;
import org.daverog.tripliser.query.Item;
import org.daverog.tripliser.query.Node;
import org.daverog.tripliser.query.QueryException;
import org.daverog.tripliser.query.QueryService;
import org.daverog.tripliser.query.Queryable;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReport;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.daverog.tripliser.value.ItemToStringConverter;


import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class MutableTripleGraph extends TripleGraph {

	private JenaModelService jenaModelService;
	private TripliserReporter tripliserReporter;
	private final ItemToStringConverter itemToStringConverter;
	private final QueryService queryService;

	public MutableTripleGraph(String name, Model model, TripliserReporter tripliserReporter, JenaModelService jenaModelService, QueryService queryService, ItemToStringConverter itemToStringConverter){
		super(model, name);
		this.jenaModelService = jenaModelService;
		this.tripliserReporter = tripliserReporter;
		this.queryService = queryService;
		this.itemToStringConverter = itemToStringConverter;
	}

	public void addPropertiesForResourceAndMapping(Queryable input, PropertyMapping propertyMapping, Resource resource, Node node, GraphContext graphContext) throws InvalidPropertyMappingException, IgnoredPropertyMappingException {
		List<Item> items;
		try {
			items = queryService.getItems(input, propertyMapping, node, graphContext);
		} catch (QueryException e) {
			throw new InvalidPropertyMappingException("Failed to retrieve mapped items", e);
		}

		boolean success = false;

		RDFDatatype dataType = null;

		if (!propertyMapping.isResource()) {
			try {
				if (propertyMapping.getDataType() != null) {
					RDFDatatype derivedType = TypeMapper.getInstance().getTypeByName(jenaModelService.convertToUri(getModel(), propertyMapping.getDataType(), graphContext));
					if (derivedType != null) {
						dataType = derivedType;
					} else {
						throw new InvalidPropertyMappingException("Datatype '" + propertyMapping.getDataType() + "' not found");
					}
				}
			} catch(URISyntaxException e) {
				throw new InvalidPropertyMappingException("The datatype '" + propertyMapping.getDataType() + "' is not a valid URI", e);
			}
		}

		if (propertyMapping.hasPropertyMappings()) {
			for (Item item : items) {
				Resource blankNode = getModel().createResource();
				resource.addProperty(jenaModelService.getProperty(getModel(), propertyMapping.getName(), graphContext), blankNode);
				List<PropertyMapping> subPropertyMappings = propertyMapping.getPropertyMappings();
				for(PropertyMapping subPropertyMapping : subPropertyMappings) {
					addPropertiesForResourceAndMapping(input, subPropertyMapping , blankNode, (Node) item, graphContext);
					success = true;
				}
			}
		}
		else{
			List<String> values = itemToStringConverter.convertItemsToStrings(items, propertyMapping, tripliserReporter, graphContext);
			for (String value : values) {
				try {
					if (dataType != null && !dataType.isValid(value)) {
						throw new DatatypeFormatException(value, dataType, "Invalid value format");
					}

					if (propertyMapping.isResource()) {
						resource.addProperty(jenaModelService.getProperty(getModel(), propertyMapping.getName(), graphContext),
								getModel().createResource(jenaModelService.convertToUri(getModel(), value, graphContext)));
					} else {
						resource.addProperty(jenaModelService.getProperty(getModel(), propertyMapping.getName(), graphContext), value, dataType);
					}
					success = true;
				} catch(URISyntaxException e) {
					tripliserReporter.addMessage(new ReportEntry(e, Status.WARNING, Scope.PROPERTY, propertyMapping));
				} catch(DatatypeFormatException e) {
					tripliserReporter.addMessage(new ReportEntry(e, Status.WARNING, Scope.PROPERTY, propertyMapping));
				}
			}
		}
		if (!success) {
			if (propertyMapping.isRequired()) throw new InvalidPropertyMappingException("A required property did not have any triples created");
			else throw new IgnoredPropertyMappingException("No triples created for property");
		}
	}

	public Resource createMainResource(Queryable input, ResourceMapping resourceMapping, Node inputNode, GraphContext graphContext) throws IgnoredResourceMappingException, InvalidResourceMappingException {
		try {
			ValueMapping valueMapping = resourceMapping.getAbout();
			List<Item> items = queryService.getItems(input, valueMapping, inputNode, graphContext);

			if (items.isEmpty() && valueMapping.isRequired()) {
				throw new InvalidResourceMappingException("Query for creating resource URI returned no results but resource URI mapping was required");
			}

			if (items.isEmpty() && !resourceMapping.isRequired()) {
				throw new IgnoredResourceMappingException("Query for creating resource URI returned no results but resource mapping was not required");
			}

			if (items.size() != 1) {
				throw new InvalidResourceMappingException(
						"The mapping '" + resourceMapping.getAbout().getName() + "' returned " +
							items.size() + " results where 1 was expected");
			}

			List<String> values = itemToStringConverter.convertItemsToStrings(items, valueMapping, tripliserReporter, graphContext);

			return getModel().createResource(values.get(0));

		} catch (QueryException e) {
			throw new InvalidResourceMappingException(e.getMessage(), e);
		}
	}

	public TripliserReporter getTripliserReporter() {
		return tripliserReporter;
	}

	@Override
	public TripliserReport getReport() {
		return tripliserReporter.getReport();
	}

	public void addTag(String name, String value) {
		tags.put(name, value);
	}

}
