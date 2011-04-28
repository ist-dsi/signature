/**
 * Instituto Superior Técnico - 2009
 * XAdES Standalone CounterSigner
 * @author  Daniel Almeida - daniel.almeida@ist.utl.pt
 */
package aeq;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XAdESCounterSigner {

    public static void main(String args[]) {
        //System.out.println("XAdES Counter Signer");
        //System.out.println("2009 Daniel Almeida - daniel.almeida@ist.utl.pt");

        if (args.length != 4) {
            System.out.println("Usage: XAdESCounterSigner XAdESSignatureToCounterSign.xml pkcs12File pkcs12Password pkcs12KeyPassword");
            return;
        }

        XAdESCounterSigner xcs = new XAdESCounterSigner();
        xcs.counterSign(args[0],args[1],args[2],args[3]);
        
    }

    public void counterSign(String fileCounterSignFilename, String pkcs12File, String pkcs12Password, String pkcs12KeyPassword) {

        try {
            // XML Signature Factory
            String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
            Provider provider = (Provider) Class.forName(providerName).newInstance();
            XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM", provider);

            // Reference
            Reference counterSignatureRef = factory.newReference("#idSignatureValue", factory.newDigestMethod(DigestMethod.SHA256, null));

            // Signed Info
            SignedInfo si = factory.newSignedInfo(factory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec) null), factory.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(counterSignatureRef));

            // Load PKCS12 keys / Certificate
            KeyStore ks = KeyStore.getInstance("PKCS12");
            FileInputStream pkcs12Stream = new FileInputStream(pkcs12File);
            ks.load(pkcs12Stream,pkcs12Password.toCharArray()); //PKCS12 password
            pkcs12Stream.close();

            String alias = (String)ks.aliases().nextElement();

            Certificate[] chain = ks.getCertificateChain(alias);
            X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

            KeyPair kp = new KeyPair(cert.getPublicKey(),(PrivateKey) ks.getKey(alias, pkcs12KeyPassword.toCharArray()));

            //KeyInfo X509Data
            KeyInfoFactory kif = factory.getKeyInfoFactory();
            List x509content = new ArrayList();
            KeyValue kv = kif.newKeyValue(kp.getPublic());
            x509content.add(kv);
            x509content.add(kif.newX509Data(Collections.singletonList(cert.getSubjectDN().toString())));
            x509content.add(kif.newX509Data(Collections.singletonList(chain[0])));

            KeyInfo ki =  kif.newKeyInfo(x509content);

            DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(new FileInputStream(fileCounterSignFilename));

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

            // CounterSignature
            Element elCounterSignature = doc.createElement("CounterSignature");
            elUnsignedSignatureProperties.appendChild(elCounterSignature);

            elUnsignedProperties.appendChild(elUnsignedSignatureProperties);
            nlQualifyingProperties.item(0).appendChild(elUnsignedProperties);

            DOMSignContext dsc = new DOMSignContext(kp.getPrivate(), elCounterSignature);

            XMLSignature signature = factory.newXMLSignature(si, ki);
            signature.sign(dsc);

            // Transformer
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            //trans.setOutputProperty(OutputKeys.INDENT, "yes");
            //trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            trans.transform(
                    new DOMSource(doc),
                    new StreamResult(new FileOutputStream(fileCounterSignFilename.replace(".sig.xml", ".cssig.xml"))));

            System.out.println("SUCCESS");

        } catch (UnrecoverableKeyException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESCounterSigner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESCounterSigner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESCounterSigner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESCounterSigner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MarshalException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESCounterSigner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLSignatureException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESCounterSigner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESCounterSigner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESCounterSigner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESCounterSigner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESCounterSigner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESCounterSigner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESCounterSigner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESCounterSigner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESCounterSigner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("ERROR");
            Logger.getLogger(XAdESCounterSigner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
