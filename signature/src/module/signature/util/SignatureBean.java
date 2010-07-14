package module.signature.util;

import java.io.Serializable;

import module.signature.domain.SignatureIntention;
import module.signature.domain.SignatureIntentionObject;
import pt.ist.fenixWebFramework.services.Service;

public class SignatureBean implements Serializable {

    private final Signable signObject;
    private SignatureIntention signature;

    public SignatureBean(Signable signObject) {
	this.signObject = signObject;
	create();
    }

    @Service
    private void create() {
	this.signature = new SignatureIntentionObject(signObject);
    }

    public String getSignID() {
	return signature.getExternalId();
    }

    public String getTokenIn() {
	return signature.getTokenIn();
    }

    public String getTokenOut() {
	return signature.getTokenOut();
    }
}
