package org.daverog.tripliser.value;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.mapping.model.ValueMapping;


public class ValueGenerator {

	public String getValue(String rawValue, ValueMapping valueMapping, GraphContext graphContext) throws ValueValidationException {
		if (StringUtils.isBlank(rawValue)) {
			throw new ValueValidationException("A value cannot be empty");
		}

		if (valueMapping.getValidationRegex() != null) {
			if (!rawValue.matches(valueMapping.getValidationRegex())){
				throw new ValueValidationException("The value '"+rawValue+"' does not match the regular expression '" + valueMapping.getValidationRegex() + "'");
			}
		}

		StringBuffer value = new StringBuffer();
		if (valueMapping.getPrepend() != null) {
			value.append(convertConstants(valueMapping.getPrepend(), graphContext));
		}

		if (!valueMapping.isXPath()) {
			value.append(convertConstants(rawValue, graphContext));
		} else {
			value.append(rawValue);
		}

		if (valueMapping.getAppend() != null) {
			value.append(convertConstants(valueMapping.getAppend(), graphContext));
		}

		return value.toString();
	}


	public String convertConstants(String text, GraphContext graphContext) {
		Iterator<String> constantNames = graphContext.getConstantNames();
		while (constantNames.hasNext()) {
			String name = constantNames.next();
			text = text.replaceAll("\\$\\{" + name + "\\}", graphContext.getConstant(name));
		}
		return text;
	}

}
