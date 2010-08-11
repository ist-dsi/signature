package module.signature.domain;

import module.signature.util.Signable;
import pt.ist.fenixWebFramework.services.Service;

public class SignatureQueue extends SignatureQueue_Base {

    public SignatureQueue() {
	super();
    }

    @Service
    public <T extends Signable> void push(SignatureIntention signIntention) {
	for (SignatureIntention signIntentionIterator : getSignatureIntentions()) {
	    if (signIntentionIterator.getIdentification().equals(signIntention.getIdentification())) {
		return;
	    }
	}

	addSignatureIntentions(signIntention);
    }

    @Service
    public void clear() {
	getSignatureIntentions().clear();
    }
}
