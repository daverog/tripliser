package org.daverog.tripliser.mapping.model;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.core.PersistenceException;
import org.simpleframework.xml.core.Validate;

public class PropertyMapping extends ValueMapping implements GenericMapping {

	@Attribute
	private String name;

	@Attribute(required = false)
	private Boolean alternative;

	@ElementList(required=false, inline=true, entry="property")
	private List<PropertyMapping> propertyMappings;

	private PropertyMapping parent;

	public String getName() {
		return name;
	}

	public List<PropertyMapping> getPropertyMappings() {
		for (PropertyMapping propertyMapping : propertyMappings) {
			propertyMapping.setParent(this);
		}

		return propertyMappings;
	}

	private void setParent(PropertyMapping parent) {
		this.parent = parent;
	}

	public PropertyMapping getParent() {
		return parent;
	}

	public boolean hasPropertyMappings() {
		return propertyMappings != null && !propertyMappings.isEmpty();
	}

	public Boolean isAlternative() {
		return alternative;
	}

	@Validate
	public void validate() throws PersistenceException {
		if (value != null && query != null) throw new PersistenceException("A property mapping can only provide either a query or a value attribute");
		if (dataType != null && isResource()) throw new PersistenceException("A resource cannot provide a dataType attribute");
		if (hasPropertyMappings()) {
			if (value != null) throw new PersistenceException("A blank node mapping cannot provide a value attribute");
			if (resource != null && resource == false) throw new PersistenceException("A blank node mapping cannot have a resource attribute set to false");
		} else {
			if (query == null && value == null) throw new PersistenceException("A property mapping must provide a query or value attribute");
		}
	}

}
