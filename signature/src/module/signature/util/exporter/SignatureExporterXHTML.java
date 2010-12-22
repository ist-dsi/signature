package module.signature.util.exporter;

import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.xalan.transformer.TransformerImpl;

public class SignatureExporterXHTML {

    public SignatureExporterXHTML() {
    }

    public String export(Source xmlContent, Source xslt) throws ExporterException {

	try {
	    String result = "";

	    TransformerFactory tFactory = TransformerFactory.newInstance();
	    Transformer transformer = tFactory.newTransformer(xslt);

	    if (transformer instanceof TransformerImpl) {
		TransformerImpl transformerImpl = (TransformerImpl) transformer;

		StringWriter output = new StringWriter();
		transformer.transform(xmlContent, new StreamResult(output));

		// we need to take out the xml declaration. it will be added
		// later in the header
		result = output.toString();
		CharSequence cs = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		result = result.replace(cs, "");

		System.out.println("XHTML Generated: ----------------");
		System.out.println(result);
		System.out.println("-----");

		return result;
	    }

	    throw new ExporterException();

	} catch (TransformerConfigurationException ex) {
	    throw new ExporterException(ex);
	} catch (TransformerException ex) {
	    throw new ExporterException(ex);
	}
    }

}
