/**
 * Instituto Superior TÃ©cnico - 2009
 * XAdES Standalone Signer
 * @author  Daniel Almeida - daniel.almeida@ist.utl.pt
 */
package aeq;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.activation.MimeType;
import javax.swing.JOptionPane;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import sun.misc.BASE64Encoder;

public class XAdESSigner {

    private static final int BUFFER_SIZE = 1024 * 20;
    private static CartaoCidadaoPKCS11 cartaoCidadaoPKCS11;
    private KeyStore keyStore;
    private Certificado cert;
    private static String xadesNS = "http://uri.etsi.org/01903/v1.3.2#";

    // standalone application
    public static void main(String[] args) {

	System.out.println("This is the altered version of the XAdESSigner and will not run stand-alone ATM");
	return;
	/*
	 * if (args.length != 5) { System.out.println(
	 * "Usage: XAdESSigner FileToSign XMLFilename Role FileDescription Commitment"
	 * ); return; }
	 * 
	 * try {
	 * 
	 * XAdESSigner signer = new XAdESSigner(); signer.sign(args[0], args[1],
	 * args[2], args[3], args[4]);
	 * 
	 * } catch (Exception ex) {
	 * Logger.getLogger(XAdESSigner.class.getName()).log(Level.SEVERE, null,
	 * ex); }
	 */
    }

    public XAdESSigner() throws Exception {

	System.out.println("CartÃ£o de CidadÃ£o - XAdES Signer");
	System.out.println("2009 Daniel Almeida - daniel.almeida@ist.utl.pt");

	keystoreInit();

    }

    public Certificado getCertificado() {
	return this.cert;
    }

    // inicia a keystore
    public void keystoreInit() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, Exception {

	keyStore = CartaoCidadaoPKCS11.getPKCS11Store();

	if (Certificado.containsCitizenCertificate(keyStore)) {
	    String aliasToUse = Certificado.getAliasToUse(keyStore);
	    cert = new Certificado(keyStore, aliasToUse);
	    cert.printCertInfo();
	} else {
	    JOptionPane.showMessageDialog(MainWindow.getInstance(),
		    "Não foi possível ler o certificado correcto do Cartão de Cidadão", "Erro", JOptionPane.ERROR_MESSAGE);
	}

    }

    // assina um documento
    public byte[] sign(byte[] contentToSign, String signatureId, String role, String fileDescription, String commitment,
	    MimeType mimeType)
	    throws CertificateEncodingException, SAXException, KeyStoreException, ClassNotFoundException, InstantiationException,
	    IllegalAccessException, ParserConfigurationException, FileNotFoundException, IOException, NoSuchAlgorithmException,
	    InvalidAlgorithmParameterException, KeyException, MarshalException, XMLSignatureException,
	    TransformerConfigurationException, TransformerException {

//	String signatureFileFilename = signedFileFilename.concat(".sig.xml");

//	System.out.println("(*) Ficheiro a assinar: " + signedFileFilename);
//	System.out.println("(*) Ficheiro de assinatura: " + signatureFileFilename);

	// XML Signature Factory
	String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
	Provider provider = (Provider) Class.forName(providerName).newInstance();
	XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM", provider);

	// Document Builder Factory
	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	documentBuilderFactory.setNamespaceAware(true); // XML namespace

	// Novo Documento XML
	Document doc = documentBuilderFactory.newDocumentBuilder().newDocument();

	// InclusÃ£o do ficheiro a assinar
	Element signedFileObject = doc.createElement("SignedFile");
	Element signedFileContentObject = doc.createElement("FileContent");
	signedFileContentObject.setAttribute("Id", signatureId);

	// ConversÃ£o para Base64
	System.out.println("(*) A converter ficheiro para Base64 (RFC4648)...");
//	File signedFile = new File(signedFileFilename);
//	FileInputStream signedFileStream = new FileInputStream(signedFile);
	BASE64Encoder encoder = new BASE64Encoder();
	signedFileContentObject.setTextContent(encoder.encode(contentToSign));
	// BREAK LINES AT 76 CHARS object.setTextContent(Base64.encodeBytes(signedFileContent,Base64.DO_BREAK_LINES));      
	signedFileObject.appendChild(signedFileContentObject);
	doc.appendChild(signedFileObject);

	System.out.println("(*) A criar extensÃµes XAdES...");
	// *** XAdES ***
	// Propriedades qualificantes
	Element elQualifProp = doc.createElementNS(xadesNS, "QualifyingProperties");
	elQualifProp.setAttributeNS(null, "Target", "idSignature");
	elQualifProp.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", xadesNS);

	// Propriedades qualificantes assinadas
	Element elSignedSignatureProperties = doc.createElement("SignedSignatureProperties");
	Element elSignedProperties = doc.createElement("SignedProperties");
	elSignedProperties.setAttribute("Id", "idSignedProperties");

	// Signing Time
	Element elTime = doc.createElement("SigningTime");
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	String date = simpleDateFormat.format(new Date());
	StringBuilder xmlDate = new StringBuilder(date);
	xmlDate.insert(22, ':');
	elTime.appendChild(doc.createTextNode(xmlDate.toString()));
	elSignedSignatureProperties.appendChild(elTime);

	// Signing Certificate
	Element elSigningCertificate = doc.createElement("SigningCertificate");

	Element elCert = doc.createElement("Cert");
	Element elCertDigest = doc.createElement("CertDigest");

	Element elDigestMethod = doc.createElement("ds:DigestMethod");
	//elDigestMethod.setAttribute("Algorithm", "http://www.w3.org/2000/09/xmldsig#sha1");
	elDigestMethod.setAttribute("Algorithm", "http://www.w3.org/2000/09/xmldsig#sha256");
	elCertDigest.appendChild(elDigestMethod);

	Element elDigestValue = doc.createElement("ds:DigestValue");
	//MessageDigest sha1 = MessageDigest.getInstance("SHA1");
	//elDigestValue.setTextContent(Base64.encodeBytes(sha1.digest(cert.getX509Cert().getEncoded())));
	MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
	elDigestValue.setTextContent(encoder.encode(sha256.digest(cert.getX509Cert().getEncoded())));

	elCertDigest.appendChild(elDigestValue);

	Element elIssuerSerial = doc.createElement("IssuerSerial");
	Element elX509IssuerName = doc.createElement("ds:X509IssuerName");
	elX509IssuerName.setTextContent(cert.getIssuer());
	elIssuerSerial.appendChild(elX509IssuerName);

	Element elX509SerialNumber = doc.createElement("ds:X509SerialNumber");
	elX509SerialNumber.setTextContent(cert.getSerialNumber());
	elIssuerSerial.appendChild(elX509SerialNumber);

	elCert.appendChild(elCertDigest);
	elCert.appendChild(elIssuerSerial);
	elSigningCertificate.appendChild(elCert);
	elSignedSignatureProperties.appendChild(elSigningCertificate);

	//SignatureProductionPlace - Not Implemented

	//Signer SignerRole
	Element elSignerRole = doc.createElement("SignerRole");
	Element elClaimedRoles = doc.createElement("ClaimedRoles");
	Element elClaimedRole = doc.createElement("ClaimedRole");
	elClaimedRole.setTextContent(role);

	elClaimedRoles.appendChild(elClaimedRole);
	elSignerRole.appendChild(elClaimedRoles);
	elSignedSignatureProperties.appendChild(elSignerRole);

	//SignedDataObjectProperties
	Element elSignedDataObjectProperties = doc.createElement("SignedDataObjectProperties");
	Element elDataObjectFormat = doc.createElement("DataObjectFormat");
	elDataObjectFormat.setAttribute("ObjectReference", "#" + signatureId);

	Element elDescription = doc.createElement("Description");
	elDescription.setTextContent(fileDescription);
	elDataObjectFormat.appendChild(elDescription);

	Element elMimeType = doc.createElement("MimeType");
	elMimeType.setTextContent(mimeType.toString());
	elDataObjectFormat.appendChild(elMimeType);

	Element elEncoding = doc.createElement("Encoding");
	elEncoding.setTextContent("base64");
	elDataObjectFormat.appendChild(elEncoding);

	elSignedDataObjectProperties.appendChild(elDataObjectFormat);

	//CommitmentTypeIndication
	Element elCommitmentTypeIndication = doc.createElement("CommitmentTypeIndication");

	//CommitmentTypeId
	Element elCommitmentTypeId = doc.createElement("CommitmentTypeId");
	Element elIdentifier = doc.createElement("Identifier");
	//elIdentifier.setAttribute("Qualifier", "OIDAsURI");
	elCommitmentTypeId.appendChild(elIdentifier);
	elCommitmentTypeIndication.appendChild(elCommitmentTypeId);

	Element elAllSignedDataObjects = doc.createElement("AllSignedDataObjects");
	elCommitmentTypeIndication.appendChild(elAllSignedDataObjects);

	Element elCommitmentTypeQualifiers = doc.createElement("CommitmentTypeQualifiers");
	Element elCommitmentTypeQualifier = doc.createElement("CommitmentTypeQualifier");
	elCommitmentTypeQualifier.setTextContent(commitment);
	elCommitmentTypeQualifiers.appendChild(elCommitmentTypeQualifier);
	elCommitmentTypeIndication.appendChild(elCommitmentTypeQualifiers);

	elSignedDataObjectProperties.appendChild(elCommitmentTypeIndication);

	elSignedProperties.appendChild(elSignedSignatureProperties);
	elSignedProperties.appendChild(elSignedDataObjectProperties);

	elQualifProp.appendChild(elSignedProperties);
	XMLObject xades = factory.newXMLObject(Collections.singletonList(new DOMStructure(elQualifProp)), "idObject", null, null);

	System.out.println("(*) A gerar KeyInfo e X509data...");
	KeyInfoFactory kif = factory.getKeyInfoFactory();

	//KeyInfo X509Data
	List x509content = new ArrayList();
	KeyValue kv = kif.newKeyValue(cert.getPublicKey());
	x509content.add(kv);
	x509content.add(kif.newX509Data(Collections.singletonList(cert.getSubject())));
	x509content.add(kif.newX509Data(Collections.singletonList(cert.getCertificateChain()[0])));

	KeyInfo ki = kif.newKeyInfo(x509content);

	DOMSignContext dsc = new DOMSignContext(cert.getPrivateKey(), doc.getFirstChild());
	dsc.setProperty("org.jcp.xml.dsig.internal.dom.SignatureProvider", cert.getProvider());
	dsc.putNamespacePrefix(XMLSignature.XMLNS, "ds");

	CanonicalizationMethod canonicalizationMethod = factory.newCanonicalizationMethod(
		CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec) null);
	//DigestMethod referenceDigestMethod = factory.newDigestMethod(DigestMethod.SHA1, null);
	DigestMethod referenceDigestMethod = factory.newDigestMethod(DigestMethod.SHA256, null);

	// ReferÃªncias da assinatura XML-DSIG
	System.out.println("(*) A criar referÃªncias...");
	List<Reference> references = new ArrayList<Reference>();

	// Self Document Reference
	Reference refSelf = factory.newReference("#" + signatureId, referenceDigestMethod,
		Collections.singletonList(canonicalizationMethod), null, null);
	references.add(refSelf);

	// XAdES Reference
	Reference refSignedProperties = factory.newReference("#idSignedProperties", referenceDigestMethod,
		Collections.singletonList(canonicalizationMethod), null, null);
	references.add(refSignedProperties);

	System.out.println("(*) A criar assinatura...");
	//Signed Info
	SignatureMethod signatureMethod = factory.newSignatureMethod(SignatureMethod.RSA_SHA1, null);

	//SignatureMethod signatureMethod = factory.newSignatureMethod("SHA256withRSA",null);
	SignedInfo si = factory.newSignedInfo(canonicalizationMethod, signatureMethod, references);
	XMLSignature signature = factory.newXMLSignature(si, ki, Collections.singletonList(xades), "idSignature",
		"idSignatureValue");

	System.out.println("(*) A assinar...");
	signature.sign(dsc);

	// GeraÃ§Ã£o do ficheiro XML
	System.out.println("(*) A gerar assinatura XML...");

	// Transformer
	Transformer trans = TransformerFactory.newInstance().newTransformer();
	//trans.setOutputProperty(OutputKeys.INDENT,"yes");
	//trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

	ByteArrayOutputStream outputByteStream = new ByteArrayOutputStream();
	trans.transform(new DOMSource(doc), new StreamResult(outputByteStream));
	return outputByteStream.toByteArray();

    }
}
