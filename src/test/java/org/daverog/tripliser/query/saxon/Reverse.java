package org.daverog.tripliser.query.saxon;

import org.apache.commons.lang.StringUtils;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.SingletonIterator;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

public class Reverse extends ExtensionFunctionDefinition {

	@Override
	public ExtensionFunctionCall makeCallExpression() {
		return new ExtensionFunctionCall() {

			@Override
			public SequenceIterator call(SequenceIterator[] arguments, XPathContext xPathContext)
					throws XPathException {
				String value = arguments[0].next().getStringValue();
				return SingletonIterator.makeIterator(new StringValue(StringUtils.reverse(value)));
			}
		};
	}

	@Override
	public SequenceType getResultType(SequenceType[] suppliedArguments) {
		return SequenceType.SINGLE_STRING;
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("func", "http://my-site.com/functions", "reverse");
	}

	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] {SequenceType.SINGLE_STRING};
	}

}