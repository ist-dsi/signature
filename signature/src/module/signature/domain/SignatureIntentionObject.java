package module.signature.domain;

import module.signature.util.SignableObject;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

public class SignatureIntentionObject extends SignatureIntentionObject_Base {

    SignableObject signableObject;

    @Service
    public static SignatureIntentionObject factory(SignableObject signObject) {
	return new SignatureIntentionObject(signObject);
    }

    public SignatureIntentionObject(SignableObject signObject) {
	super();

	this.signableObject = signObject;
    }

    @Override
    public String getIdentification() {
	return signableObject.getIdentification();
    }

    @Override
    public SignableObject getSignObject() {
	return (SignableObject) fromExternalId(getIdentification());
    }

    @Override
    public void finalizeSignature(UploadedFile file0, UploadedFile file1) {
	SignableObject signable = getSignObject();
	signable.receiveSignature(file0, file1);

	new Signature(signable);
    }
}
