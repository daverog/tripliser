package org.daverog.tripliser.mapping.model;

import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.List;

import org.daverog.tripliser.mapping.model.PropertyMapping;
import org.daverog.tripliser.mapping.model.ValueMapping;

public class ValueMappingBuilder {

	private String name;
	private String query;
	private String value;
	private String dataType;
	private boolean resource;
	private boolean required = true;
	private String prepend;
	private String append;
	private String input;
	private List<PropertyMapping> propertyMappings;
	private String validationRegex;

	public ValueMappingBuilder(){
		query = "query";
		resource = false;

		propertyMappings = new ArrayList<PropertyMapping>();
	}

	public ValueMappingBuilder name(String name) {
		this.name = name;
		return this;
	}

	public ValueMappingBuilder value(String value) {
		this.value = value;
		if (value != null) this.query = null;
		return this;
	}

	public ValueMappingBuilder query(String query) {
		this.query = query;
		if (query != null) this.value = null;
		return this;
	}

	public ValueMappingBuilder resource(boolean resource) {
		this.resource = resource;
		return this;
	}

	public ValueMappingBuilder dataType(String dataType) {
		this.dataType = dataType;
		return this;
	}

	public ValueMappingBuilder required(boolean required) {
		this.required = required;
		return this;
	}

	public ValueMappingBuilder prepend(String prepend) {
		this.prepend = prepend;
		return this;
	}

	public ValueMappingBuilder append(String append) {
		this.append = append;
		return this;
	}

	public ValueMappingBuilder input(String input) {
		this.input = input;
		return this;
	}

	public ValueMappingBuilder validationRegex(String validationRegex) {
		this.validationRegex = validationRegex;
		return this;
	}

	public ValueMappingBuilder addSubPropertyMapping(PropertyMapping propertyMapping) {
		propertyMappings.add(propertyMapping);
		return this;
	}

	public ValueMapping toValueMapping() {
		ValueMapping valueMapping = new ValueMapping();

		setValues(valueMapping);

		return valueMapping;
	}

	private void setValues(ValueMapping valueMapping) {
		setInternalState(valueMapping, "query", query);
		setInternalState(valueMapping, "value", value);
		setInternalState(valueMapping, "resource", resource);
		setInternalState(valueMapping, "dataType", dataType);
		setInternalState(valueMapping, "required", required);
		setInternalState(valueMapping, "prepend", prepend);
		setInternalState(valueMapping, "append", append);
		setInternalState(valueMapping, "input", input);
		setInternalState(valueMapping, "validationRegex", validationRegex);
		setInternalState(valueMapping, "name", name);
	}

	public PropertyMapping toPropertyMapping() {
		PropertyMapping valueMapping = new PropertyMapping();

		if (!propertyMappings.isEmpty()) setInternalState(valueMapping, "propertyMappings", propertyMappings);
		setValues(valueMapping);

		return valueMapping;
	}

}
