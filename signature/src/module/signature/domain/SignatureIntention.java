package module.signature.domain;

import module.signature.util.Signable;
import myorg.domain.MyOrg;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

abstract public class SignatureIntention extends SignatureIntention_Base {

    final static int EXPIRE_MINUTES = 10;

    public SignatureIntention() {
	super();
    }

    public SignatureIntention(Signable signObject) {
	super();

	init(signObject);
    }

    protected void init(Signable signObject) {
	setMyOrg(MyOrg.getInstance());
	setIdentification(signObject.getIdentification());

	setTokenInUsed(false);
	setTokenOutUsed(false);
	setExpire(new DateTime().plusMinutes(EXPIRE_MINUTES));

	generateTokens();
    }

    @Service
    final public void finalizeSignature(UploadedFile file0, UploadedFile file1) {
	Signable signable = getSignObject();
	signable.receiveSignature(file0, file1);

	new Signature(signable);
    }

    private void generateTokens() {
	setTokenIn(RandomStringUtils.randomAlphanumeric(32));
	setTokenOut(RandomStringUtils.randomAlphanumeric(32));
    }

    public Signable getSignObject() {
	return (Signable) fromExternalId(getIdentification());
    }

    public String getContentToSign() {
	return getSignObject().getContentToSign();
    }

    @Service
    public void verifyTokenIn(String tokenIn) {
	if (getTokenInUsed() || !getTokenIn().equals(tokenIn)) {
	    throw new UnsupportedOperationException();
	}

	setTokenInUsed(true);
    }

    @Service
    public void verifyTokenOut(String tokenOut) {
	if (getTokenOutUsed() || !getTokenOut().equals(tokenOut)) {
	    throw new UnsupportedOperationException();
	}

	setTokenOutUsed(true);
    }
}
