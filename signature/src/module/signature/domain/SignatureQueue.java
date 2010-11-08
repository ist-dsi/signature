package module.signature.domain;

import module.signature.util.Signable;

import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.services.Service;

public class SignatureQueue extends SignatureQueue_Base {

    private final int QUEUE_MINUTES_LIMIT = 120;

    public SignatureQueue() {
	super();
    }

    @Service
    public <T extends Signable> void push(SignatureIntention signIntention) {
	if (getExpire() == null) {
	    setExpire(new DateTime().plusMinutes(QUEUE_MINUTES_LIMIT));
	}

	for (SignatureIntention signIntentionIterator : getSignatureIntentions()) {
	    if (signIntentionIterator.getIdentification().equals(signIntention.getIdentification())) {
		return;
	    }
	}

	addSignatureIntentions(signIntention);

	// synchronize queue expire time with signature expire
	signIntention.setExpire(getExpire());
    }

    @Service
    public void clear() {
	for (SignatureIntention signature : getSignatureIntentions()) {
	    signature.cancel();
	}

	setExpire(null);
	getSignatureIntentions().clear();
    }

    @Service
    public void delete() {
	clear();
	removeUser();
	super.deleteDomainObject();
    }
}
