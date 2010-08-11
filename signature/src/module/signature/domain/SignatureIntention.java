package module.signature.domain;

import module.signature.metadata.SignatureMetaData;
import module.signature.util.Signable;
import module.signature.util.exporter.SignatureExporter;
import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.MyOrg;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

abstract public class SignatureIntention extends SignatureIntention_Base {

    final static int EXPIRE_MINUTES = 10;

    public SignatureIntention() {
	super();

	setMyOrg(MyOrg.getInstance());
	setUser(UserView.getCurrentUser());

	setSealed(false);
	setTokenInUsed(false);
	setTokenOutUsed(false);
	setExpire(new DateTime().plusMinutes(EXPIRE_MINUTES));

	generateTokens();
    }

    abstract protected void finalizeSignature(UploadedFile file0, UploadedFile file1);

    abstract public <T extends Signable> T getSignObject();

    abstract public SignatureMetaData getMetaData();

    abstract protected void setRelation(SignatureIntention signature);

    @Service
    final public void sealSignature(UploadedFile file0, UploadedFile file1) {
	finalizeSignature(file0, file1);

	setSealedDateTime(new DateTime());
	setSealed(true);
    }

    public String getSignatureDocument() {
	return "uhhhh .pdf";
    }

    /**
     * not very interesting methods
     */

    private void generateTokens() {
	setTokenIn(RandomStringUtils.randomAlphanumeric(32));
	setTokenOut(RandomStringUtils.randomAlphanumeric(32));
    }

    public void getContentToSign(SignatureExporter signatureExporter) {
	getMetaData().accept(signatureExporter);
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

    public boolean isSealed() {
	return getSealed();
    }

    @Service
    public void delete() {
	removeMyOrg();
	removeUser();
	removeSignatureQueue();
	removeWorkflowProcess();
    }
}
