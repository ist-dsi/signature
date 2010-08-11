package module.signature.domain;

import module.signature.metadata.SignatureMetaData;
import module.signature.metadata.SignatureMetaDataMulti;
import module.signature.util.Signable;
import module.signature.util.exporter.SignatureExporter;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

public class SignatureIntentionMulti extends SignatureIntentionMulti_Base {

    @Service
    public static SignatureIntentionMulti factory(SignatureQueue queue) {
	return new SignatureIntentionMulti(queue);
    }

    protected SignatureIntentionMulti(SignatureQueue queue) {
	super();

	init(queue);
    }

    protected void init(SignatureQueue queue) {
	for (SignatureIntention signIntention : queue.getSignatureIntentions()) {
	    addSignatureIntentions(signIntention);
	}
    }

    @Override
    public <T extends Signable> T getSignObject() {
	return null;
    }

    @Override
    public SignatureMetaData getMetaData() {
	return new SignatureMetaDataMulti(getSignatureIntentions());
    }

    @Override
    protected void setRelation(SignatureIntention signature) {
	for (SignatureIntention signIntention : getSignatureIntentions()) {
	    signIntention.setRelation(this);
	}
    }

    @Override
    public void getContentToSign(SignatureExporter signatureExporter) {
	for (SignatureIntention signIntention : getSignatureIntentions()) {
	    signIntention.getContentToSign(signatureExporter);
	}
    }

    @Override
    protected void finalizeSignature(UploadedFile file0, UploadedFile file1) {
	for (SignatureIntention signIntention : getSignatureIntentions()) {
	    signIntention.sealSignature(file0, file1);
	}

	setRelation(this);
    }

}
