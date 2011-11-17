/**
 *  It contains representations of signature formats e.g. XAdES-BES, XAdES-X-L, etc.
 */
package module.signature.domain.data;

import java.io.InputStream;

import module.signature.util.XAdESValidator;

/**
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public enum SignatureFormat {
    XAdEST {
	
	private final XAdESValidator xAdESValidator = new XAdESValidator();

	@Override
	public boolean validateSignature(InputStream signatureContent) {
	    return xAdESValidator.validateXMLSignature(signatureContent);
	}

	@Override
	public boolean validateSignature(byte[] signatureContent) {
	    return xAdESValidator.validateXMLSignature(signatureContent);
	}

	@Override
	public String getContentType() {
	    return "application/xml-external-parsed-entity";
	}
    };
    
    public abstract boolean validateSignature(InputStream signatureContent);

    public abstract boolean validateSignature(byte[] signatureContent);

    public abstract String getContentType();

}
