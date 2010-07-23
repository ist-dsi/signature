package module.signature.domain;

import module.signature.util.Signable;
import myorg.domain.MyOrg;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

abstract public class SignatureIntention<T extends Signable> extends SignatureIntention_Base {

    final static int EXPIRE_MINUTES = 10;

    public SignatureIntention() {
	super();

	init();
    }

    protected void init() {
	setMyOrg(MyOrg.getInstance());

	setTokenInUsed(false);
	setTokenOutUsed(false);
	setExpire(new DateTime().plusMinutes(EXPIRE_MINUTES));

	generateTokens();
    }

    @Service
    abstract public void finalizeSignature(UploadedFile file0, UploadedFile file1);

    abstract public T getSignObject();

    private void generateTokens() {
	setTokenIn(RandomStringUtils.randomAlphanumeric(32));
	setTokenOut(RandomStringUtils.randomAlphanumeric(32));
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
