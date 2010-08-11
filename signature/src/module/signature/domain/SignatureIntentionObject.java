package module.signature.domain;

import module.signature.metadata.SignatureMetaData;
import module.signature.util.Signable;
import module.signature.util.SignableObject;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

public class SignatureIntentionObject extends SignatureIntentionObject_Base {

    protected SignatureIntentionObject() {
	super();
    }

    protected void init(SignableObject signable) {
	setIdentification(signable.getIdentification());
    }

    @Override
    protected void finalizeSignature(UploadedFile file0, UploadedFile file1) {
	SignableObject signable = getSignObject();
	signable.receiveSignature(file0, file1);

	setRelation(this);
    }

    @Override
    public <T extends Signable> T getSignObject() {
	return (T) fromExternalId(getIdentification());
    }

    /**
     * these need to be removed after making this class abstract
     */

    @Override
    protected void setRelation(SignatureIntention signature) {
    }

    @Override
    public SignatureMetaData getMetaData() {
	return null;
    }
}
