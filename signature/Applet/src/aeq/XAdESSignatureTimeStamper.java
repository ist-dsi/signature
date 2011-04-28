/**
 * Instituto Superior Técnico - 2009
 * XAdES Standalone SignatureTimeStamper
 * @author  Daniel Almeida - daniel.almeida@ist.utl.pt
 */
package aeq;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sun.security.timestamp.HttpTimestamper;
import sun.security.timestamp.TSRequest;
import sun.security.timestamp.TSResponse;

public class XAdESSignatureTimeStamper {

    public static void main(String args[]) {
        //System.out.println("XAdES SignatureTimeStamper");
        //System.out.println("2009 Daniel Almeida - daniel.almeida@ist.utl.pt");

        if (args.length != 2) {
            System.out.println("Usage: XAdESSignatureTimeStamper XAdESSignatureToTimeStamp.xml TimeStampAuthorityURL");
            return;
        }

        XAdESSignatureTimeStamper xts = new XAdESSignatureTimeStamper();
        xts.signatureTimeStamp(args[0],args[1]);
        
    }

    public void signatureTimeStamp(String fileTimeStampFilename, String tsa) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(new FileInputStream(fileTimeStampFilename));

            // XAdES, append UnsignedProperties
            NodeList nlQualifyingProperties = doc.getElementsByTagName("QualifyingProperties");
            if (nlQualifyingProperties.getLength() == 0) {
                System.out.println("ERROR");
                return;
            }

            // UnsignedProperties
            Element elUnsignedProperties;
            NodeList nlUnsignedProperties = doc.getElementsByTagName("UnsignedProperties");
            if (nlUnsignedProperties.getLength() == 0) {
                elUnsignedProperties = doc.createElement("UnsignedProperties");
            }else{
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
            HttpTimestamper httpTS = new HttpTimestamper(tsa);
            byte[] digest = sha1.digest(elSignatureValue.getTextContent().getBytes("UTF-8"));

            TSRequest tsQuery = new TSRequest(digest, "SHA-1");
            TSResponse tsResponse = httpTS.generateTimestamp(tsQuery);

            if(tsResponse.getStatusCode() == TSResponse.GRANTED){

                // SignatureTimeStamp
                Element elSignatureTimeStamp = doc.createElement("SignatureTimeStamp");
               
                //EncapsulatedTimeStamp
                Element elEncapsulatedTimeStamp = doc.createElement("EncapsulatedTimeStamp");
                elEncapsulatedTimeStamp.setTextContent(Base64.encodeBytes(tsResponse.getEncodedToken()));
                elSignatureTimeStamp.appendChild(elEncapsulatedTimeStamp);
                
                elUnsignedSignatureProperties.appendChild(elSignatureTimeStamp);
                elUnsignedProperties.appendChild(elUnsignedSignatureProperties);
                nlQualifyingProperties.item(0).appendChild(elUnsignedProperties);
            }else{
              System.out.println(tsResponse.getStatusCode()); //ERROR
              return;
            }

            // Transformer
            Transformer trans = TransformerFactory.newInstance().newTransformer();

            trans.transform(
                    new DOMSource(doc),
                    new StreamResult(new FileOutputStream(fileTimeStampFilename)));

            System.out.println("SUCCESS");
            
        }catch (TransformerException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESSignatureTimeStamper.class.getName()).log(Level.SEVERE, null, ex);
        }catch (NoSuchAlgorithmException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESSignatureTimeStamper.class.getName()).log(Level.SEVERE, null, ex);
        }catch (ParserConfigurationException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESSignatureTimeStamper.class.getName()).log(Level.SEVERE, null, ex);
        }catch (SAXException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESSignatureTimeStamper.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESSignatureTimeStamper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
