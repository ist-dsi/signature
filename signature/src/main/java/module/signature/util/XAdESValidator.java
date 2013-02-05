/*
 * @(#)XAdESValidator.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: Diogo Figueiredo
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Digital Signature Module.
 *
 *   The Digital Signature Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Signature Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Signature Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.signature.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXParameters;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import module.signature.exception.SignatureDataException;
import module.signature.exception.SignatureException;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.cms.CMSSignedData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.bennu.core.util.Base64;

/**
 * 
 * @author João Antunes
 * 
 */
public class XAdESValidator {

    private static String xadesNS = "http://uri.etsi.org/01903/v1.4.1#";
    public static String schemaXmldSigCoreFilename = "xmldsig-core-schema.xsd";
    public static String schemaXAdESv132Filename = "XAdES.xsd";
    public static String schemaXAdESv141Filename = "XAdESv141.xsd";
    public static SchemaFactory schemaFactory;
    private static Schema schemaXSD;
    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(XAdESValidator.class);
    private static KeyStore cartaoCidadaoKeyStore;
    private static X509CertificateHolder tsaCert;
    private static ContentVerifierProvider contentVerifierProvider;

    static {
        //xml-security library initialization
        org.apache.xml.security.Init.init();
        loadXAdESSchemas();
        loadNeededCerts();
    }

    private static void loadXAdESSchemas() {
        if (schemaFactory == null) {
            schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        }
        Source[] sources = new StreamSource[3];
        sources[0] = new StreamSource(XAdESValidator.class.getResourceAsStream("/resources/" + schemaXmldSigCoreFilename));
        sources[1] = new StreamSource(XAdESValidator.class.getResourceAsStream("/resources/" + schemaXAdESv132Filename));
        sources[2] = new StreamSource(XAdESValidator.class.getResourceAsStream("/resources/" + schemaXAdESv141Filename));
        try {
            schemaXSD = schemaFactory.newSchema(sources);
        } catch (SAXException e) {
            e.printStackTrace();
            //joantune: TODO probably should do something differente here!!
            throw new DomainException("error.loading.XADES.scheme", e);
        }

    }

    private static void loadNeededCerts() {

        try {
            InputStream keyStoreIS = XAdESValidator.class.getResourceAsStream("/resources/certs/cc-keystore");
            cartaoCidadaoKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            cartaoCidadaoKeyStore.load(keyStoreIS, "123456".toCharArray());

            InputStream tsaCertIS = XAdESValidator.class.getResourceAsStream("/resources/certs/tsaCert.cer");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (tsaCertIS.available() != 0) {
                //not the fastest way to do it.. but who cares 
                baos.write(tsaCertIS.read());
            }
            tsaCert = new X509CertificateHolder(baos.toByteArray());

        } catch (KeyStoreException e) {
            logger.error("Error loading the needed certificates", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error loading the needed certificates", e);
        } catch (CertificateException e) {
            logger.error("Error loading the needed certificates", e);
        } catch (IOException e) {
            logger.error("Error loading the needed certificates", e);
        }
    }

    /*
     * joantune: by default we'll set all of the check's below to true as we
     * want to check for everything
     */
    public static boolean checkSchema = true; // -xsd
    public static boolean checkSignature = true; // -sig
    public static boolean checkRevocation = true; // -rev
    public static boolean checkValidity = true; // -val

    /**
     * @author joantune
     * 
     *         Makes sure that the document which was signed is equal to the one
     *         that was sent. It also checks to see if the signers are the
     *         correct persons or not NOTE: It does note validate the validity
     *         of the signature itself, only that what was sent was the same
     *         that was receveived.
     * 
     *         To validate, see:
     * 
     * @see XAdESValidator#validateXMLSignature(byte[])
     * @param receivedContent
     *            the received signature
     * @param originalContent
     *            the byte array of what was sent to the signer
     * @param usersPermitted
     *            Users that are permitted to be on the signature
     * @param usersExcluded
     *            Users which must not be on the list of signers, or null/empty
     *            list if none is excluded
     * @param allUsersPermittedShouldBeThere
     *            if true, all of the users of <i>usersPermitted</i> must be on
     *            the list of signers
     * @throws SignatureDataException
     *             if any error has been observed, thus the validation fails
     */
    public static void validateSentAndReceivedContent(String receivedContent, byte[] originalContent, Set<User> usersPermitted,
            Set<User> usersExcluded, boolean allUsersPermittedShouldBeThere) throws SignatureDataException {
        //let's validate the content. That is, what we received against what we sent and make sure it hasn't changed

        if (schemaXSD == null) {
            loadXAdESSchemas();
        }
        //we know that we are receiving a XaDES-T signature
        boolean validSignature = false;
        try {

            //let's extract the signatureContent and compare it against what we sent

            //let's decode and interpret the XML file

            //making the objects to interpret the XML document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder parser = dbf.newDocumentBuilder();

            ErrorHandler eh = new ErrorHandler() {

                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    throw new UnsupportedOperationException("Not supported yet.", exception);
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    throw new UnsupportedOperationException("Not supported yet.", exception);
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw new UnsupportedOperationException("Not supported yet.", exception);
                }
            };

            //let's decode the document

            byte[] signatureDecoded = Base64.decode(receivedContent);
            ByteArrayInputStream bais = new ByteArrayInputStream(signatureDecoded);

            // let's parse the document
            parser.setErrorHandler(eh);
            Document document = parser.parse(bais);

            NodeList fileContentNodeList = document.getElementsByTagName("FileContent");

            //Even if we have more than one signature, we should only have one file content!! if not, let's throw an exception here
            if (fileContentNodeList.getLength() > 1) {
                throw new SignatureDataException("too.many.file.content.nodes.malformed.signature.document", true, null);
            }
            if (fileContentNodeList.getLength() < 1) {
                throw new SignatureDataException("no.file.content.nodes.in.received.signature", true, null);
            }

            Node fileContentNode = fileContentNodeList.item(0).getFirstChild();

            //now finally, we can compare the content of this node with the one that we generated
            //debug lines:

            byte[] receivedDecodedByteContent = Base64.decode(fileContentNode.getNodeValue());

            //ok, so let's parse this again to strings and then we can better compare them and maybe know exactly why they are different

            String originalEncodedContent = Base64.encodeBytes(originalContent);

            String originalDecodedContent = new String(Base64.decode(originalEncodedContent), Charset.forName("UTF-8"));
            String receivedDecodedContent = new String(receivedDecodedByteContent, Charset.forName("UTF-8"));
            //now let's
            //make sure the signature is from the right person
            //TODO uncomment the following line:
            //	    validateSigner(document, usersPermitted, usersExcluded, allUsersPermittedShouldBeThere);

            if (!StringUtils.equals(StringUtils.trimToEmpty(originalDecodedContent),
                    StringUtils.trimToEmpty(receivedDecodedContent))) {
                //	    }
                throw new SignatureDataException("signature.content.sent.and.received.are.different");
            } else {
                validSignature = true;
            }

            //TODO FENIX-196 assert if one should be notified of these errors
        } catch (IOException e1) {
            //	    e1.printStackTrace();
            throw new SignatureDataException("error.decoding.base64.sig", e1);
        } catch (SAXException e) {
            //	    e.printStackTrace();
            throw new SignatureDataException("error.parsing.received.signature.file", e);
        } catch (ParserConfigurationException e) {
            //	    e.printStackTrace();
            throw new SignatureDataException("error.parsing.received.signature.file.parser.configuration", e);
        }

        if (!validSignature) {
            throw new SignatureDataException("invalid.signature.content");
        }

    }

    private static void validateSigner(Document document, Set<User> usersPermitted, Set<User> usersExcluded,
            boolean allUsersPermittedShouldBeThere) throws SignatureDataException {

        if (!allUsersPermittedShouldBeThere || ((usersExcluded != null) && !usersExcluded.isEmpty())) {
            //TODO implement it when needed
            throw new DomainException("method.not.yet.implemented");
        }
        final String ID_NR_PREFIX = "OID.2.5.4.5=BI";
        ArrayList<String> usersPermittedIdNumbers = new ArrayList<String>();
        for (User user : usersPermitted) {
            usersPermittedIdNumbers.add(user.getPerson().getRemotePerson().getDocumentIdNumber());
        }
        //let's extract each signature
        // XMLDSIG
        NodeList nlSignature = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        //DEBUG 
        System.out.println("Got " + nlSignature.getLength() + " signatures");
        if (nlSignature.getLength() < 1) {
            throw new SignatureException("could.not.find.a.signature.in.incoming.data", true, null);
        }

        HashSet<String> usersFoundIdNumbers = new HashSet<String>();
        for (int i = 0; i < nlSignature.getLength(); i++) {
            //for each signature, let's extract the ID number of who did it
            Element signature = (Element) nlSignature.item(i);
            try {
                XMLSignature xmlSig = new XMLSignature(signature, null);
                KeyInfo ki = xmlSig.getKeyInfo();
                String certificateIDNr = ki.getX509Certificate().getSubjectX500Principal().getName("RFC1779");
                certificateIDNr = certificateIDNr.substring(certificateIDNr.indexOf(ID_NR_PREFIX) + ID_NR_PREFIX.length());
                //let's take out the virgul and the last character, which is a control one
                certificateIDNr = certificateIDNr.substring(0, certificateIDNr.indexOf(',') - 1);
                usersFoundIdNumbers.add(certificateIDNr);
            } catch (XMLSignatureException e) {
                e.printStackTrace();
                throw new SignatureDataException("signature.error.XMLSignatureExceptionError", e);
            } catch (XMLSecurityException e) {
                throw new SignatureDataException("signature.error.XMLSecurityException", e);
            }
        }

        //now let's validate the extracted info
        if (allUsersPermittedShouldBeThere && usersFoundIdNumbers.containsAll(usersPermittedIdNumbers)) {
            return;
            //TODO TODO URGENT uncomment the next two lines (just made possible to be able to test it!!)
        } else {
            throw new SignatureDataException("wrong.document.signer");
        }

        //TODO the rest of the use cases aren't implemented ATM

    }

    @Deprecated
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

        //	if (xv.validateXMLSignature(args[0])) {
        //	    System.out.println("SUCCESS");
        //	} else {
        //	    System.out.println("ERROR");
        //	}
    }

    @Deprecated
    public XAdESValidator(String args[]) {
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

    public XAdESValidator() {
        //default constructor
    }

    /**
     * @author João Antunes - joao.antunes@tagus.ist.utl.pt
     * @param content
     *            the byte[] content which has the content of the signature to
     *            validate
     * @return true if valid, false otherwise
     */
    public boolean validateXMLSignature(byte[] content) {
        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        return validateXMLSignature(bais);
    }

    /**
     * @author joao.antunes@tagus.ist.utl.pt adapted it from {@link #validateXMLSignature(String)}
     * @param streamWithSignature
     *            the {@link InputStream} that has the signature content
     * @return true if it's valid, false otherwise
     */
    public boolean validateXMLSignature(InputStream streamWithSignature) {
        try {

            // get the  xsd schema

            Validator validator = schemaXSD.newValidator();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder parser = dbf.newDocumentBuilder();

            ErrorHandler eh = new ErrorHandler() {

                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    throw new UnsupportedOperationException("Not supported yet.", exception);
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    throw new UnsupportedOperationException("Not supported yet.", exception);
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw new UnsupportedOperationException("Not supported yet.", exception);
                }
            };

            // parse the document
            parser.setErrorHandler(eh);
            Document document = parser.parse(streamWithSignature);

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

            if (checkSignature) {
                // Validate Every Signature Element (including CounterSignatures)
                for (int i = 0; i < nlSignature.getLength(); i++) {

                    Element signature = (Element) nlSignature.item(i);
                    //		    String baseURI = fileToValidate.toURL().toString();
                    XMLSignature xmlSig = new XMLSignature(signature, null);

                    KeyInfo ki = xmlSig.getKeyInfo();

                    // If signature contains X509Data
                    if (ki.containsX509Data()) {

                        NodeList nlSigningTime = signature.getElementsByTagNameNS(xadesNS, "SigningTime");
                        Date signingDate = null;

                        if (nlSigningTime.item(0) != null) {
                            StringBuilder xmlDate = new StringBuilder(nlSigningTime.item(0).getTextContent()).deleteCharAt(22);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                            signingDate = simpleDateFormat.parse(xmlDate.toString());
                        }

                        //verificação OCSP
                        //TODO FENIX-189 joantune: na realidade acho que isto não verifica mesmo a revocação.. a não ser que a keystore indicada seja actualizada regularmente.
                        if (checkRevocation) {
                            //keystore certs cc, raiz estado

                            Security.setProperty("ocsp.enable", "true");
                            //System.setProperty("com.sun.security.enableCRLDP", "true");

                            CertificateFactory cf = CertificateFactory.getInstance("X.509");

                            CertPath certPath = cf.generateCertPath(Collections.singletonList(ki.getX509Certificate()));
                            //			    TrustAnchor trustA = new TrustAnchor(ki.getX509Certificate(), null);
                            //			    Set trustAnchors = Collections.singleton(trustA);

                            PKIXParameters params = new PKIXParameters(cartaoCidadaoKeyStore);
                            params.setRevocationEnabled(true);

                            // validar o estado na data da assinatura
                            if (nlSigningTime.item(0) != null) {
                                params.setDate(signingDate);
                            }

                            try {
                                CertPathValidator cpValidator = CertPathValidator.getInstance("PKIX");
                                CertPathValidatorResult result = cpValidator.validate(certPath, params);
                                //TODO FENIX-196 probably one would want to send a notification here
                            } catch (CertPathValidatorException ex) {
                                return false;
                            } catch (InvalidAlgorithmParameterException ex) {
                                return false;
                            }
                        }

                        // verifica a validade do certificado no momento da assinatura
                        if (checkValidity) {

                            if (nlSigningTime.item(0) != null) { // continue if there is no SigningTime, if CounterSignature isn't XAdES
                                try {
                                    ki.getX509Certificate().checkValidity(signingDate);
                                } catch (CertificateExpiredException ex) {
                                    return false;
                                } catch (CertificateNotYetValidException ex) {
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
                    if (ki.containsKeyValue()) {
                        boolean validSignature = xmlSig.checkSignatureValue(ki.getPublicKey());
                        if (!validSignature) {
                            return false;
                        }
                    }

                    //let's check the SignatureTimeStamp(s) joantune

                    NodeList signatureTimeStamps = signature.getElementsByTagNameNS("*", "SignatureTimeStamp");
                    Element signatureValue = null;
                    if (signatureTimeStamps.getLength() > 0) {
                        signatureValue = (Element) signature.getElementsByTagNameNS("*", "SignatureValue").item(0);
                    }
                    for (int j = 0; j < signatureTimeStamps.getLength(); j++) {
                        logger.debug("Found a SignatureTimeStamp");
                        Element signatureTimeStamp = (Element) signatureTimeStamps.item(j);
                        //for now we are ignoring the XMLTimeStamp element, let's iterate through all of the EncapsulatedTimeStamp that we find
                        NodeList encapsulatedTimeStamps = signatureTimeStamp.getElementsByTagNameNS("*", "EncapsulatedTimeStamp");
                        for (int k = 0; k < encapsulatedTimeStamps.getLength(); k++) {
                            logger.debug("Found an EncapsulatedTimeStamp");
                            Element encapsulatedTimeStamp = (Element) encapsulatedTimeStamps.item(k);
                            //let's check it
                            // note, we have the timestamptoken, not the whole response, that is, we don't have the status field

                            ASN1Sequence signedTimeStampToken =
                                    ASN1Sequence.getInstance(Base64.decode(encapsulatedTimeStamp.getTextContent()));

                            CMSSignedData cmsSignedData =
                                    new CMSSignedData(Base64.decode(encapsulatedTimeStamp.getTextContent()));

                            TimeStampToken timeStampToken = new TimeStampToken(cmsSignedData);

                            //let's construct the Request to make sure this is a valid response

                            //let's generate the digest
                            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                            byte[] digest = sha1.digest(signatureValue.getTextContent().getBytes("UTF-8"));

                            //let's make sure the digests are the same
                            if (!Arrays.equals(digest, timeStampToken.getTimeStampInfo().getMessageImprintDigest())) {
                                //TODO probably want to send an e-mail if this happens, as it's clearly a sign of tampering
                                //FENIX-196
                                logger.debug("Found a different digest in the timestamp!");
                                return false;
                            }

                            try {
                                //TODO for now we won't use the provided certificates that came with the TST
                                //				X509Store certificateStore = (X509Store) timeStampToken.getCertificates();
                                //				JcaDigestCalculatorProviderBuilder builder = new JcaDigestCalculatorProviderBuilder();
                                //				timeStampToken.validate(tsaCert, "BC");
                                //				timeStampToken.validate(new SignerInformationVerifier(new JcaContentVerifierProviderBuilder()
                                //					.build(tsaCert), builder.build()));
                                timeStampToken.validate(new SignerInformationVerifier(new JcaContentVerifierProviderBuilder()
                                        .build(tsaCert), new BcDigestCalculatorProvider()));
                                //let's just verify that the timestamp was done in the past :) - let's give a tolerance of 5 mins :)
                                Date currentDatePlus5Minutes = new Date();
                                //let's make it go 5 minutes ahead
                                currentDatePlus5Minutes.setMinutes(currentDatePlus5Minutes.getMinutes() + 5);
                                if (!timeStampToken.getTimeStampInfo().getGenTime().before(currentDatePlus5Minutes)) {
                                    //FENIX-196 probably we want to log this!
                                    //what the heck, timestamp is done in the future!! (clocks might be out of sync)
                                    logger.warn("Found a timestamp in the future!");
                                    return false;
                                }
                                logger.debug("Found a valid TimeStamp!");
                                //as we have no other timestamp elements in this signature, this means all is ok! :) 
                                //(point 5) of g.2.2.16.1.3 on the specs

                            } catch (TSPException exception) {
                                logger.debug("TimeStamp response did not validate", exception);
                                return false;
                            }

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

}
