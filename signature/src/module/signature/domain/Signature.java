package module.signature.domain;

import module.signature.domain.data.SignatureData;
import module.signature.domain.data.SignatureFormat;
import module.signature.exception.SignatureException;
import myorg.domain.User;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

public class Signature extends Signature_Base {
    
    public  Signature() {
        super();
    }

    /**
     * 
     * @param signatureFormat
     * @param signatureContent
     *            the content of the signature
     * @throws SignatureException
     *             if the signature wasn't valid or if something else went wrong
     */
    public Signature(SignatureFormat signatureFormat, byte[] signatureContent, SignatureData signatureData, User userWhoSigned)
	    throws SignatureException {
	if (!signatureFormat.validateSignature(signatureContent))
	    throw new SignatureException("could.not.create.signature.as.its.invalid");
	setSignatureFormat(signatureFormat);
	setSignatureData(signatureData);
	setSignedUser(userWhoSigned);
	SignatureFile signatureFile = new SignatureFile();
	signatureFile.setContent(signatureContent);
	signatureFile.setContentType(signatureFormat.getContentType());
	signatureFile.setFilename(StringUtils.replaceChars(signatureData.getSignatureId(), "\\/", "-") + ".xades.xml");
	signatureFile.setDisplayName("Ficheiro de assinatura: " + getSignatureData().getSignatureDescription());

	setPersistedSignature(signatureFile);
	setCreatedDateTime(new DateTime());
	//remove it as a pending signature
	signatureData.removeUserToSignPendingSignature();

    }

    public byte[] getContent() {
	return getPersistedSignature().getContent();
    }

    public boolean isValid() {
	// TODO Auto-generated method stub
	return true;
    }
    
}
