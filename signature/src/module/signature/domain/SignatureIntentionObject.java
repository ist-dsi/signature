package module.signature.domain;

import module.signature.util.Signable;

public class SignatureIntentionObject extends SignatureIntentionObject_Base {

    public SignatureIntentionObject(Signable signObject) {
	super();

	init(signObject);
    }

}
