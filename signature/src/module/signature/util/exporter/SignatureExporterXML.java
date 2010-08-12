package module.signature.util.exporter;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import module.signature.metadata.SignatureMetaData;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignatureExporterXML implements SignatureExporter {

    DocumentBuilder docBuilder;
    Document doc;
    Element root;

    public SignatureExporterXML() {
	try {
	    DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
	    docBuilder = dbfac.newDocumentBuilder();
	    doc = docBuilder.newDocument();

	    root = doc.createElement("root");
	    doc.appendChild(root);
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public String export() throws ExporterException {
	try {
	    TransformerFactory transfac = TransformerFactory.newInstance();
	    Transformer trans = transfac.newTransformer();
	    trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    trans.setOutputProperty(OutputKeys.INDENT, "yes");

	    // create string from xml tree
	    StringWriter sw = new StringWriter();
	    StreamResult result = new StreamResult(sw);
	    DOMSource source = new DOMSource(doc);
	    trans.transform(source, result);

	    String xmlString = sw.toString();

	    return xmlString;
	} catch (TransformerException e) {
	    throw new ExporterException(e);
	}
    }

    @Override
    public void addParent(String prefix, String id) {
	Element child = doc.createElement("parent");

	child.setAttribute("id", id);
	child.setTextContent(prefix);
	root.appendChild(child);
    }

    @Override
    public void addItem(String s) {
	Element child = doc.createElement("item");
	child.setTextContent(s);
	root.appendChild(child);
    }

    @Override
    public void addItem(SignatureMetaData signable) {
	signable.accept(this);
    }
}
