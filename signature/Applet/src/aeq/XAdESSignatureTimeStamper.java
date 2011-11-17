/**
 * Instituto Superior Técnico - 2009
 * XAdES Standalone SignatureTimeStamper
 * @author  Daniel Almeida - daniel.almeida@ist.utl.pt
 */
package aeq;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bouncycastle.tsp.TSPAlgorithms;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sun.misc.BASE64Encoder;

public class XAdESSignatureTimeStamper {

    private static final Logger logger = Logger.getLogger(XAdESSignatureTimeStamper.class.getName());

    public static void main(String args[]) {
	//System.out.println("XAdES SignatureTimeStamper");
	//System.out.println("2009 Daniel Almeida - daniel.almeida@ist.utl.pt");

	System.out.println("StandAlone TimeStamper disabled in this version");
	return;

	/*
	 * if (args.length != 2) { System.out.println(
	 * "Usage: XAdESSignatureTimeStamper XAdESSignatureToTimeStamp.xml TimeStampAuthorityURL"
	 * ); return; }
	 * 
	 * XAdESSignatureTimeStamper xts = new XAdESSignatureTimeStamper();
	 * xts.signatureTimeStamp(args[0],args[1]);
	 */

    }

    /**
     * 
     * @param signatureContent
     *            the content to be timestamped
     * @param tsa
     *            the TSA to use URL
     * @return an object array where the first position is the content and the
     *         second the {@link Date} representing the time which was provided
     *         by the TSA
     */
    public Object[] signatureTimeStamp(byte[] signatureContent, String tsa) {

	Object[] toReturn = new Object[2];
	try {
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    dbf.setNamespaceAware(true);
	    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(signatureContent);
	    Document doc = dbf.newDocumentBuilder().parse(byteArrayInputStream);

	    // XAdES, append UnsignedProperties
	    NodeList nlQualifyingProperties = doc.getElementsByTagName("QualifyingProperties");
	    if (nlQualifyingProperties.getLength() == 0) {
		System.out.println("ERROR");
		return null;
	    }

	    // UnsignedProperties
	    Element elUnsignedProperties;
	    NodeList nlUnsignedProperties = doc.getElementsByTagName("UnsignedProperties");
	    if (nlUnsignedProperties.getLength() == 0) {
		elUnsignedProperties = doc.createElement("UnsignedProperties");
	    } else {
		elUnsignedProperties = (Element) nlUnsignedProperties.item(0);
	    }

	    // UnsignedSignatureProperties
	    Element elUnsignedSignatureProperties;
	    NodeList nlUnsignedSignatureProperties = doc.getElementsByTagName("UnsignedSignatureProperties");
	    if (nlUnsignedSignatureProperties.getLength() == 0) {
		elUnsignedSignatureProperties = doc.createElement("UnsignedSignatureProperties");
	    } else {
		elUnsignedSignatureProperties = (Element) nlUnsignedSignatureProperties.item(0);
	    }

	    // Signature Value
	    NodeList nlSignatureValue = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "SignatureValue");
	    Element elSignatureValue = (Element) nlSignatureValue.item(0);

	    MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

	    //let's construct the request
	    TimeStampRequestGenerator reqGen = new TimeStampRequestGenerator();
	    reqGen.setCertReq(true);
	    TimeStampRequest timeStampRequest = reqGen.generate(TSPAlgorithms.SHA1,
		    sha1.digest(elSignatureValue.getTextContent().getBytes("UTF-8")));

	    DefaultHttpClient httpclient = new DefaultHttpClient();
	    ByteArrayEntity httpTSEntity = new ByteArrayEntity(timeStampRequest.getEncoded());
	    httpTSEntity.setContentType("application/timestamp-query");

	    HttpPost httpPostTS = new HttpPost(tsa);
	    httpPostTS.setEntity(httpTSEntity);

	    HttpResponse httpResponse = httpclient.execute(httpPostTS);

	    TimeStampResponse timeStampResponse = new TimeStampResponse(httpResponse.getEntity().getContent());

	    //            HttpTimestamper httpTS = new HttpTimestamper(tsa);
	    //            byte[] digest = sha1.digest(elSignatureValue.getTextContent().getBytes("UTF-8"));

	    //            TSRequest tsQuery = new TSRequest(digest, "SHA-1");
	    //	    tsQuery.requestCertificate(true);
	    //            TSResponse tsResponse = httpTS.generateTimestamp(tsQuery);

	    timeStampResponse.validate(timeStampRequest);

	    toReturn[1] = timeStampResponse.getTimeStampToken().getTimeStampInfo().getGenTime();

	    // SignatureTimeStamp
	    Element elSignatureTimeStamp = doc.createElement("SignatureTimeStamp");

	    //EncapsulatedTimeStamp
	    Element elEncapsulatedTimeStamp = doc.createElement("EncapsulatedTimeStamp");
	    BASE64Encoder encoder = new BASE64Encoder();

	    elEncapsulatedTimeStamp.setTextContent(encoder.encode(timeStampResponse.getTimeStampToken().getEncoded()));
	    elSignatureTimeStamp.appendChild(elEncapsulatedTimeStamp);

	    elUnsignedSignatureProperties.appendChild(elSignatureTimeStamp);
	    elUnsignedProperties.appendChild(elUnsignedSignatureProperties);
	    nlQualifyingProperties.item(0).appendChild(elUnsignedProperties);

	    // Transformer
	    Transformer trans = TransformerFactory.newInstance().newTransformer();

	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    trans.transform(new DOMSource(doc), new StreamResult(byteArrayOutputStream));

	    System.out.println("SUCCESS");

	    toReturn[0] = byteArrayOutputStream.toByteArray();

	    return toReturn;

	} catch (TransformerException ex) {
	    System.out.println("ERROR");
	    logger.log(Level.SEVERE, null, ex);
	} catch (NoSuchAlgorithmException ex) {
	    System.out.println("ERROR");
	    logger.log(Level.SEVERE, null, ex);
	} catch (ParserConfigurationException ex) {
	    System.out.println("ERROR");
	    logger.log(Level.SEVERE, null, ex);
	} catch (SAXException ex) {
	    System.out.println("ERROR");
	    logger.log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    System.out.println("ERROR");
	    logger.log(Level.SEVERE, null, ex);
	} catch (IllegalStateException e) {
	    System.out.println("ERROR");
	    logger.log(Level.SEVERE, null, e);
	} catch (TSPException e) {
	    System.out.println("ERROR");
	    logger.log(Level.SEVERE, null, e);
	}
	return null;
    }
}
