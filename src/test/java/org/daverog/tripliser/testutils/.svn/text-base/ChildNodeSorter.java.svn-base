package org.daverog.tripliser.testutils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.sun.org.apache.xerces.internal.util.DOMUtil;

@SuppressWarnings("restriction")
public class ChildNodeSorter extends DOMUtil {

	/**
	 * Sorts the children of the given node upto the specified depth if
	 * available
	 * 
	 * @param node
	 *            - node whose children will be sorted
	 * @param descending
	 *            - true for sorting in descending order
	 * @param depth
	 *            - depth upto which to sort in DOM
	 * @param comparator
	 *            - comparator used to sort, if null a default NodeName
	 *            comparator is used.
	 */
	public static void sortRdfChildNodes(Node node, boolean descending, int depth,
			Comparator<Node> comparator) {

		List<Node> nodes = new ArrayList<Node>();
		NodeList childNodeList = node.getChildNodes();
		if (depth > 0 && childNodeList.getLength() > 0) {
			for (int i = 0; i < childNodeList.getLength(); i++) {
				Node tNode = childNodeList.item(i);
				sortRdfChildNodes(tNode, descending, depth - 1, comparator);
				// Remove empty text nodes
				if ((!(tNode instanceof Text))
						|| (tNode instanceof Text && ((Text) tNode)
								.getTextContent().trim().length() > 1)) {
					nodes.add(tNode);
				}
			}
			Comparator<Node> comp = (comparator != null) ? comparator
					: new Comparator<Node>() {
						public int compare(Node node1, Node node2) {
							if (node1.getNodeName().equals(node2.getNodeName())) {
								Node rdfResource1 = node1.getAttributes().getNamedItem("rdf:resource");
								Node rdfResource2 = node2.getAttributes().getNamedItem("rdf:resource");
								
								if (rdfResource1 == null || rdfResource1.getNodeValue().equals(rdfResource2.getNodeValue())) {							
									return node1.getTextContent().compareTo(node2.getTextContent());
								}
								
								return rdfResource1.getNodeValue().compareTo(rdfResource2.getNodeValue());
							}
							
							return node1.getNodeName().compareTo(
									node2.getNodeName());
						}
					};
			if (descending) {
				// if descending is true, get the reverse ordered comparator
				Collections.sort(nodes, Collections.reverseOrder(comp));
			} else {
				Collections.sort(nodes, comp);
			}
			for (Iterator<Node> iter = nodes.iterator(); iter.hasNext();) {
				Node element = iter.next();
				node.removeChild(element); 
				node.appendChild(element);
			}
		}

	}

}
