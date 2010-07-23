package module.signature.domain;

import module.signature.util.Signable;
import pt.ist.fenixWebFramework.services.Service;

public class Queue extends Queue_Base {

    public Queue() {
	super();
    }

    @Service
    public <T extends Signable> void push(SignatureIntention<T> signIntention) {
	addSignatureIntentions(signIntention);
    }

}
