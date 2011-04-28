/**
 * Instituto Superior Técnico - 2009
 * Certificado de Assinatura Electrónica Qualificada (Cartão de Cidadão)
 * @author  Daniel Almeida - daniel.almeida@ist.utl.pt
 */
package aeq;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.x500.X500Principal;

public class Certificado {

    private static final String secondaryAliasHint = "cn=ec de assinatura digital qualificada do cartão de cidadão";
    static String alias = "CITIZEN SIGNATURE CERTIFICATE";
    private static KeyStore loadedStore;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final X509Certificate x509cert;
    Certificate[] chain;

    private final String issuer;         
    private String issuerCN;       
    private String subject;       
    private String subjectCN;      
    private final String expirationDate;
    private final String serialNumber;

    static Pattern pa = Pattern.compile("CN=([^,]*)($|,)");
    
    public Certificado(KeyStore store, String aliasToUse) throws KeyStoreException, CertificateException, Exception {
        loadedStore = store;
	alias = aliasToUse;
        
        Certificate cert = loadedStore.getCertificate(alias);
	if (cert == null) {
	    //let's use the secondary alias to retrieve the actual certificate to use
	    cert = loadedStore.getCertificate(retrieveSecondaryCompleteAlias(loadedStore));
	}

	// verifica se o certificado é do tipo X.509
        if(!(cert instanceof X509Certificate)){
            throw new java.security.cert.CertificateException("Certificate entry should be an X509Certificate");
        }
        
        x509cert = (X509Certificate) cert;
        
        X500Principal subjPrinc = x509cert.getSubjectX500Principal();
        X500Principal issuerPrinc = x509cert.getIssuerX500Principal();

        this.issuer = issuerPrinc.toString();
        this.subject = subjPrinc.toString();

        this.serialNumber = x509cert.getSerialNumber().toString();

        // Match CN's
        Matcher m1 = pa.matcher(issuer);
        boolean matchFound1 = m1.find();
        if(matchFound1)
          this.issuerCN = m1.group(1);

        this.subject = subjPrinc.toString();
        Matcher m2 = pa.matcher(subject);
        boolean matchFound2 = m2.find();
        if(matchFound2)
          this.subjectCN = m2.group(1);

        this.expirationDate = new SimpleDateFormat().format(x509cert.getNotAfter());

        try{
          
          // chave privada
          privateKey = (PrivateKey) loadedStore.getKey(alias, null);
          publicKey = cert.getPublicKey();
          chain = loadedStore.getCertificateChain(alias);
	    if (chain == null) {
		chain = loadedStore.getCertificateChain(retrieveSecondaryCompleteAlias(loadedStore));
	    }

        }catch(Exception e){
            throw e;
        }

    }

    public static boolean containsCitizenCertificate(KeyStore keystore) throws KeyStoreException {
	if (keystore.isKeyEntry(alias)) {
	    return true;
	} else {
	    //let's iterate through all of the existing certificates
	    return retrieveSecondaryCompleteAlias(keystore) != null;
	}
    }

    private static String retrieveSecondaryCompleteAlias(KeyStore keystore) throws KeyStoreException
    {
	 for (Enumeration<String> alias = keystore.aliases(); alias.hasMoreElements();) {
		String currentAlias = alias.nextElement();
	    if (currentAlias.contains(secondaryAliasHint))
		    return currentAlias;
	    }
	return null;
    }

    public String getSerialNumber(){return this.serialNumber;}
    public String getIssuer(){return this.issuer;}
    public String getIssuerCN(){return this.issuerCN;}
    public String getSubject(){return this.subject;}
    public String getSubjectCN(){return this.subjectCN;}
    public String getExpirationDate(){return this.expirationDate;}
    public X509Certificate getX509Cert(){return this.x509cert;}
    public PrivateKey getPrivateKey(){return this.privateKey;}
    public PublicKey getPublicKey(){return this.publicKey;}

    
    public Provider getProvider(){
        return loadedStore.getProvider();
    }
    public KeyStore getLoadedKeyStore(){
        return loadedStore;
    }
    
    public Certificate[] getCertificateChain() throws java.security.KeyStoreException{
        return chain;
    }
    
    public void printCertInfo(){
      
      System.out.println("(*) Certificado: ");
      System.out.println("    Subject: " + subject + "\n");
      System.out.println("    Issuer: " + issuer + "\n");
      System.out.println("    Expiration Date: " + expirationDate + "\n");
      System.out.println("    Public Key: " + publicKey + "\n");
      System.out.println("    Serial Number: " + serialNumber + "\n");

//      for (int i = 0; i < chain.length; i++) {
//        System.out.println("    Certification Chain: " + chain[i].toString() + "\n");
//      }
      
    }

    public static String getAliasToUse(KeyStore keyStore) throws KeyStoreException {
	if (keyStore.isKeyEntry(alias))
	    return alias;
	else
	    return retrieveSecondaryCompleteAlias(keyStore);
    }
    
}
