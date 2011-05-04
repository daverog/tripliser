package org.daverog.tripliser.query.saxon;

import org.daverog.tripliser.query.Node;

import net.sf.saxon.s9api.XdmNode;

public class XdmNodeAdapter implements Node {

	private XdmNode node;
	
	public XdmNodeAdapter(XdmNode node) {
		this.node = node;
	}

	@Override
	public String getStringValue() {
		return node.getStringValue();
	}

	@Override
	public Object getNode() {
		return node;
	}

}
