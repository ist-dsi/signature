package module.signature.util;

import java.io.Serializable;

import module.signature.domain.Signature;

import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.services.Service;

public class SignatureBean implements Serializable {

    private final Signable signObject;
    private Signature signature;

    public SignatureBean(Signable signObject) {
	this.signObject = signObject;
	create();
    }

    @Service
    private void create() {
	this.signature = new Signature(signObject);
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

    public DateTime getTimestamp() {
	return signature.getTimestamp();
    }
}
