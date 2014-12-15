package com.temenos.demo;

import static org.junit.Assert.assertNotNull;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.abdera.model.Content;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * Help parse and form OData atom request / responses.
 *
 * @author aphethean
 *
 */
public class AtomHelper {

	protected final static String MS_ODATA = "http://schemas.microsoft.com/ado/2007/08/dataservices";
	protected final static String MS_ODATA_METADATA = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata";
	protected final static String MS_ODATA_RELATED = "http://schemas.microsoft.com/ado/2007/08/dataservices/related";	

	/**
	 * Obtain the specified element from the atom entry. 
	 * @param entry
	 * @param fqElementName fully qualified property name (e.g. mycomplextype1.subcomplextype1.myproperty)
	 */
	public Element getEntryElement(Entry entry, String fqElementName) {
		Pattern elementPattern = Pattern.compile("element\\((\\d+?)\\)");
		Element content = entry.getFirstChild(new QName("http://www.w3.org/2005/Atom", "content"));
		assertNotNull(content);
		Element properties = content.getFirstChild(new QName(MS_ODATA_METADATA, "properties"));
		assertNotNull(properties);
		String[] tokens = fqElementName.split("\\.");
		Element element = properties.getFirstChild(new QName(MS_ODATA, tokens[0]));
		assertNotNull(element);
		for(int i=1; i < tokens.length; i++) {
			Matcher m = elementPattern.matcher(tokens[i]);
			if(m.find()) {
				element = element.getFirstChild(new QName(MS_ODATA, "element"));
				int elementIndex = Integer.valueOf(m.group(1)).intValue();
				while(elementIndex > 0) {		//This is an element(i) token so parse element i
					element = element.getNextSibling(new QName(MS_ODATA, "element"));
					elementIndex--;
				}
			}
			else {
				element = element.getFirstChild(new QName(MS_ODATA, tokens[i]));
			}
			assertNotNull(element);
		}
		return element;
	}

	/**
	 * Populate an Atom entry with the specified property values. 
	 * @param entry
	 * @param replaceValues Map of value index by the fully qualified property name (e.g. mycomplextype1.subcomplextype1.myproperty)
	 */
	public void populateEntryProperties(Entry entry, Map<String, String> replaceValues) {
		Pattern elementPattern = Pattern.compile("element\\((\\d+?)\\)");
		String content = entry.getContent();
		try {
			//Convert to entry content to a dom object to allow us to add new element
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
			docFactory.setIgnoringElementContentWhitespace(true);
			docFactory.setIgnoringComments(true);
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(content));
			org.w3c.dom.Document doc = docBuilder.parse(is);
			for(String testValue : replaceValues.keySet()) {
				String[] testValueTokens = testValue.split("\\.");
				Node element = getNode(doc, testValueTokens[0]);
				removeWhitespaceNodes(element);		//Remove whitespaces
				assert(element != null);
				for(int i=1; i < testValueTokens.length; i++) {
					String token = testValueTokens[i];
					Matcher m = elementPattern.matcher(token);
					if(m.find()) {
						Node parentElement = element;
						element = getChildElement(element);
						
						//This is an element(i) token so parse element i
						int elementIndex = Integer.valueOf(m.group(1)).intValue();
						while(elementIndex > 0) {
							Node nextElement = element.getNextSibling();
							if(nextElement == null) {
								//Sibling does not exist so add a copy of the previous one
								nextElement = element.cloneNode(true);
								parentElement.appendChild(nextElement);							
							}
							element = nextElement;
							elementIndex--;
						}
					}
					else {
						element = getChildNode(element, token);
					}
					assert(element != null);
				}
				String replacementValue = replaceValues.get(testValue);
				if(replacementValue != null) {
					element.setTextContent(replaceValues.get(testValue));
					Node nullAttrib = element.getAttributes().getNamedItem("m:null");
					if (nullAttrib != null) {
						((org.w3c.dom.Element) element).removeAttribute(nullAttrib.getNodeName());
					}
				}
			}

			//Convert dom document to Abdera entry content
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			entry.setContent(writer.toString(), Content.Type.XML);
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	/*
	 * Return the specified element in the DOM document
	 * @param doc
	 * @param name
	 * @return first element
	 */
	private Node getNode(org.w3c.dom.Document doc, String name) {
		NodeList nodes = doc.getElementsByTagNameNS(MS_ODATA, name);
		assert(nodes != null);
		assert(nodes.getLength() > 0) : "Failed to find [" + name + "]";
		Node node = nodes.item(0);
		assert(node != null);
		return node;
	}

	private Node getChildElement(Node node) throws Exception {
		return getChildNode(node, "element");
	}
	
	/*
	 * Return the first child element in the DOM document
	 * @param doc
	 * @param name
	 * @return first element
	 */
	private Node getChildNode(Node node, String name) throws Exception {
		NodeList nodes = node.getChildNodes();
		for(int i=0; i < nodes.getLength(); i++) {
			Node childNode = nodes.item(i);
			if(childNode.getLocalName().equals(name) || name.equals("element") && childNode.getLocalName().startsWith("element(")) {
				return childNode;
			}
		}
		throw new Exception("Failed to find child node [" + name + "] on node [" + node.getNodeName() + "].");
	}

	/*
	 * Remove whitespace nodes 
	 * @param e node
	 */
	private void removeWhitespaceNodes(Node e) {
		NodeList children = e.getChildNodes();
		for (int i = children.getLength() - 1; i >= 0; i--) {
			Node child = children.item(i);
			if (child instanceof Text && ((Text) child).getData().trim().length() == 0) {
				e.removeChild(child);
			}
			else if (child instanceof Node || child instanceof org.w3c.dom.Element) {
				removeWhitespaceNodes(child);
			}
		}
	}

}
