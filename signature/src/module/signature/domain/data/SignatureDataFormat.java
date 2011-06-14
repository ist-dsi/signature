/**
 *  It contains representations of how the signature data is going to be constructed e.g. XAdES-BES, XAdES-X-L, etc.
 */
package module.signature.domain.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import module.signature.domain.data.interfaces.ConvertibleToXMLAndXHTML;
import myorg.domain.exceptions.DomainException;

import org.apache.xalan.transformer.TransformerImpl;

import com.thoughtworks.xstream.XStream;

/**
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public enum SignatureDataFormat {
    XML_XHTML_AND_XSLT {
	
	public final static String BEGIN_XML_FILE = "------- XML START -------\n";
	public final static String END_XML_FILE = "------- XML END -------\n";
	public final static String BEGIN_XHTML_FILE = "------- XHTML START -------\n";
	public final static String END_XHTML_FILE = "------- XHTML END -------\n";

	private ConvertibleToXMLAndXHTML convertibleToXmlAndXHTMLObject;
	@Override
	public byte[] getSignatureDataContent() {
	    //serialize the object into XML
	    if (convertibleToXmlAndXHTMLObject == null) {
		throw new DomainException("incorrect.state.calling.getSignatureDataContent.source.is.null");
	    }
	    ByteArrayOutputStream byteArrayXmlOutputStream = new ByteArrayOutputStream();
	    ByteArrayOutputStream resultArrayOutputStream = new ByteArrayOutputStream();
	    OutputStreamWriter outputStreamWriter;
	    try {
		outputStreamWriter = new OutputStreamWriter(byteArrayXmlOutputStream, "UTF-8");

		//let's get the XML:
		XStream xStream = new XStream();
		xStream.autodetectAnnotations(true);
		xStream.toXML(convertibleToXmlAndXHTMLObject, outputStreamWriter);

		//close the outputStreamWriter
		outputStreamWriter.flush();
		outputStreamWriter.close();

		//TODO remove these debug prints:
		//		System.out.println("---------------- BEGIN XML Generated: ----------------");
		//		System.out.println(byteArrayXmlOutputStream.toString());
		//		System.out.println("---------------- END XML Generated ----------------");
		

		ByteArrayInputStream byteArrayXmlInputStream = new ByteArrayInputStream(byteArrayXmlOutputStream.toByteArray());

		StreamSource xmlStreamSource = new StreamSource(byteArrayXmlInputStream);

		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(convertibleToXmlAndXHTMLObject.xsltSource());
		TransformerImpl transformerImpl = (TransformerImpl) transformer;
		StringWriter output = new StringWriter();
		transformer.transform(xmlStreamSource, new StreamResult(output));

		String result = output.toString();
		//TODO remove these DEBUG prints:
		//		System.out.println("---------------- BEGIN XHTML Generated: ----------------");
		//		System.out.println(result);
		//		System.out.println("---------------- END XHTML Generated ----------------");

		//let's make a new outputStreamWriter to write all of the content
		outputStreamWriter = new OutputStreamWriter(resultArrayOutputStream);

		//let's signal the init of the XML file
		outputStreamWriter.append(BEGIN_XML_FILE);
		outputStreamWriter.write(byteArrayXmlOutputStream.toString());
		//the end
		outputStreamWriter.append(END_XML_FILE);
		//let's signal also the XHTML when writing it
		outputStreamWriter.append(BEGIN_XHTML_FILE);
		outputStreamWriter.append(result);
		outputStreamWriter.append(END_XHTML_FILE);
		outputStreamWriter.flush();
		
		//TODO ?! clean up the house in between the operations making sure that the space is freed
		
		return resultArrayOutputStream.toByteArray();

	    } catch (IOException e) {
		e.printStackTrace();
		throw new DomainException();
	    } catch (TransformerConfigurationException e) {
		e.printStackTrace();
		throw new DomainException();
	    } catch (TransformerException e) {
		e.printStackTrace();
		throw new DomainException();
	    }
	}

	@Override
	public void setDataSourceObject(Object object) {
	    this.convertibleToXmlAndXHTMLObject = (ConvertibleToXMLAndXHTML) object;
	}
    };

    /**
     * @return the byte array of with the signature data to be signed
     */
    public abstract byte[] getSignatureDataContent();

    public abstract void setDataSourceObject(Object object);
}
