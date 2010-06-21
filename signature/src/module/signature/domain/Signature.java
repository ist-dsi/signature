package module.signature.domain;

import module.signature.util.Signable;
import myorg.domain.MyOrg;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

public class Signature extends Signature_Base {

    final static int EXPIRE_MINUTES = 10;

    public Signature(Signable signObject) {
	super();

	setMyOrg(MyOrg.getInstance());
	setSignObjectID(signObject.getSignID());

	setTokenInUsed(false);
	setTokenOutUsed(false);
	setExpire(new DateTime().plusMinutes(EXPIRE_MINUTES));

	generateTokens();
    }

    private void generateTokens() {
	setTokenIn(RandomStringUtils.randomAlphanumeric(32));
	setTokenOut(RandomStringUtils.randomAlphanumeric(32));
    }

    public Signable getSignObject() {
	return (Signable) fromExternalId(getSignObjectID());
    }

    public String getContentToSign() {
	return getSignObject().getContentToSign();
    }

    public void finalizeSignature(UploadedFile file0, UploadedFile file1) {
	getSignObject().receiveSignature(file0, file1);

	setTimestamp(new DateTime()); // set signature date
    }

    public void verifyTokenIn(String tokenIn) {
	if (getTokenInUsed() || !getTokenIn().equals(tokenIn)) {
	    throw new UnsupportedOperationException();
	}

	setTokenInUsed(true);
    }

    public void verifyTokenOut(String tokenOut) {
	if (getTokenOutUsed() || !getTokenOut().equals(tokenOut)) {
	    throw new UnsupportedOperationException();
	}

	setTokenOutUsed(true);
    }
}
