/**
 * Instituto Superior Técnico - 2009
 * XAdES Standalone Validator (Schema and Signature)
 * @author  Daniel Almeida - daniel.almeida@ist.utl.pt
 */
package aeq;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.XMLConstants;
import org.apache.xml.security.keys.KeyInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.apache.xml.security.signature.XMLSignature;

public class XAdESValidator {

    private static String xadesNS = "http://uri.etsi.org/01903/v1.3.2#";
    public static String schemaFilename = "XAdES.xsd";
    public static boolean checkSchema = false; // -xsd
    public static boolean checkSignature = false; // -sig
    public static boolean checkRevocation = false; // -rev
    public static boolean checkValidity = false; // -val

    public static void main(String args[]) {

        if (args.length < 2) {
            System.out.println("XAdESValidator");
            System.out.println("2009 Daniel Almeida - daniel.almeida@ist.utl.pt\n");
            System.out.println("Usage: XAdESValidator XMLtovalidate.xml options");
            System.out.println("Options:");
            System.out.println("  -xsd  Validate signatures against XAdES schema definition");
            System.out.println("  -sig  Validate XML signatures");
            System.out.println("  -rev  Check for revocated certificates while validating signatures");
            System.out.println("  -val  Check for certificate validity on the SigningTime");
            return;
        }

        XAdESValidator xv = new XAdESValidator(args);

        if (xv.validateXMLSignature(args[0])) {
            System.out.println("SUCCESS");
        } else {
            System.out.println("ERROR");
        }
    }

    public XAdESValidator(String args[]){
          for (int argc = 1; argc < args.length; argc += 1) {
            String option = args[argc];

            if (option.equalsIgnoreCase("-xsd")) {
                checkSchema = true;
            }
            if (option.equalsIgnoreCase("-sig")) {
                checkSignature = true;
            }
            if (option.equalsIgnoreCase("-rev")) {
                checkRevocation = true;
            }
            if (option.equalsIgnoreCase("-val")) {
                checkValidity = true;
            }
        }
    }

    public boolean validateXMLSignature(String fileToValidateFilename) {

        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // get the  xsd schema
            Schema schemaXSD = schemaFactory.newSchema(new File(schemaFilename));

            Validator validator = schemaXSD.newValidator();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder parser = dbf.newDocumentBuilder();

            ErrorHandler eh = new ErrorHandler() {

                public void warning(SAXParseException exception) throws SAXException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public void error(SAXParseException exception) throws SAXException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public void fatalError(SAXParseException exception) throws SAXException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };

            // parse the document
            File fileToValidate = new File(fileToValidateFilename);
            parser.setErrorHandler(eh);
            Document document = parser.parse(fileToValidate);

            // XAdES extension
            NodeList nlObject = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Object");
            // XMLDSIG
            NodeList nlSignature = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");

            if (checkSchema) {
                if (nlObject.getLength() < 1) {
                    return false;
                }
                if (nlSignature.getLength() < 1) {
                    return false;
                }

                // parse the XML DOM tree againts the XSD schema
                validator.validate(new DOMSource(nlSignature.item(0)));
            }

            if (checkSignature){
                // Validate Every Signature Element (including CounterSignatures)
                for(int i=0; i<nlSignature.getLength();i++){
              
                    Element signature = (Element) nlSignature.item(i);
                    String baseURI = fileToValidate.toURL().toString();
                    XMLSignature xmlSig = new XMLSignature(signature, baseURI);

                    KeyInfo ki = xmlSig.getKeyInfo();

                    // If signature contains X509Data
                    if (ki.containsX509Data()) {

                        NodeList nlSigningTime = signature.getElementsByTagNameNS(xadesNS, "SigningTime");
                        Date signingDate = null;
                        
                        if(nlSigningTime.item(0) != null){
                          StringBuilder xmlDate = new StringBuilder(nlSigningTime.item(0).getTextContent()).deleteCharAt(22);
                          SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                          signingDate = simpleDateFormat.parse(xmlDate.toString());
                        }

                        //verificação OCSP
                        if (checkRevocation) {
                            //keystore certs cc, raiz estado
                            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                            FileInputStream ksFile = new FileInputStream("cc-keystore");
                            ks.load(ksFile, "123456".toCharArray());

                            Security.setProperty("ocsp.enable", "true");
                            //System.setProperty("com.sun.security.enableCRLDP", "true");

                            CertificateFactory cf = CertificateFactory.getInstance("X.509");

                            CertPath certPath = cf.generateCertPath(Collections.singletonList(ki.getX509Certificate()));
                            TrustAnchor trustA = new TrustAnchor(ki.getX509Certificate(),null);
                            Set trustAnchors = Collections.singleton(trustA);

                            PKIXParameters params = new PKIXParameters(ks);
                            params.setRevocationEnabled(true);

                            // validar o estado na data da assinatura
                            if(nlSigningTime.item(0) != null){
                                params.setDate(signingDate);
                            }

                            try{
                              CertPathValidator cpValidator = CertPathValidator.getInstance("PKIX");
                              CertPathValidatorResult result = cpValidator.validate(certPath, params);
                            }catch(CertPathValidatorException ex){
                                return false;
                            }catch(InvalidAlgorithmParameterException ex){
                                return false;
                            }
                        }

                        // verifica a validade do certificado no momento da assinatura
                        if(checkValidity){

                          if(nlSigningTime.item(0) != null){ // continue if there is no SigningTime, if CounterSignature isn't XAdES
                              try{
                                ki.getX509Certificate().checkValidity(signingDate);
                              }catch(CertificateExpiredException ex){
                                return false;
                              }catch(CertificateNotYetValidException ex){
                                return false;
                              }
                          }
                        }

                        // validate against Certificate Public Key
                        boolean validSignature = xmlSig.checkSignatureValue(ki.getX509Certificate().getPublicKey());
                       
                        if (!validSignature) {
                            return false;
                        }
                    }
                    
                    // if signature includes KeyInfo KeyValue, also check against it
                    if(ki.containsKeyValue()){
                        boolean validSignature = xmlSig.checkSignatureValue(ki.getPublicKey());
                        if (!validSignature) {
                            return false;
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(XAdESValidator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XAdESValidator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (SAXException ex) {
            Logger.getLogger(XAdESValidator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (Exception ex) {
            Logger.getLogger(XAdESValidator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    //xml-security library initialization
    static {
        org.apache.xml.security.Init.init();
    }
}
