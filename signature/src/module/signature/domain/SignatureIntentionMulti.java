package module.signature.domain;

import java.util.Set;

import module.signature.metadata.SignatureMetaDataMulti;
import module.signature.util.exporter.ExporterException;
import pt.ist.fenixWebFramework.services.Service;

public class SignatureIntentionMulti extends SignatureIntentionMulti_Base {

    @Service
    public static SignatureIntentionMulti factory(Set<SignatureIntention> signatures) {
	return new SignatureIntentionMulti(signatures);
    }

    protected SignatureIntentionMulti(Set<SignatureIntention> signatures) {
	super();

	init(signatures);
    }

    protected void init(Set<SignatureIntention> signatures) {
	for (SignatureIntention signIntention : signatures) {
	    if (signIntention.getActivated()) {
		addSignatureIntentions(signIntention);
	    }
	}

	super.init();
    }

    @Override
    protected void generateContent() throws ExporterException {
	setContent(SignatureSystem.getInstance().generateSignature(this));
    }

    @Override
    public SignatureIntentionMulti getSignObject() {
	return this;
    }

    @Override
    public SignatureMetaDataMulti getMetaData() {
	return new SignatureMetaDataMulti(this);
    }

    @Override
    protected void setRelation(SignatureIntention signature) {
	for (SignatureIntention signIntention : getSignatureIntentions()) {
	    signIntention.setRelation(this);
	}
    }

    @Override
    protected void finalizeSignature() {
	for (SignatureIntention signIntention : getSignatureIntentions()) {
	    signIntention.finalizeSignature();
	}
    }

    @Override
    public String getSignatureDescription() {
	return "Multi";
    }

    @Override
    public String getType() {
	return "Várias Assinaturas";
    }
}
