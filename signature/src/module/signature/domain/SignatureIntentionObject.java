package module.signature.domain;

import module.signature.util.Signable;

public abstract class SignatureIntentionObject extends SignatureIntentionObject_Base {

    protected SignatureIntentionObject() {
	super();
    }

    @Override
    public <T extends Signable> T getSignObject() {
	return (T) fromExternalId(getSignObjectId());
    }
}
