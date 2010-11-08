package module.signature.domain;

import module.signature.util.Signable;
import module.signature.util.SignableObject;

public abstract class SignatureIntentionObject extends SignatureIntentionObject_Base {

    protected SignatureIntentionObject() {
	super();
    }

    protected void init(SignableObject signable) {
	setSignObjectId(signable.getIdentification());
    }

    @Override
    public <T extends Signable> T getSignObject() {
	return (T) fromExternalId(getSignObjectId());
    }
}
