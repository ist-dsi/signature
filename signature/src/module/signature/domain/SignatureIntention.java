package module.signature.domain;

import module.signature.metadata.SignatureMetaData;
import module.signature.util.Signable;
import module.signature.util.exporter.ExporterException;
import module.signature.util.exporter.SignatureExporter;
import myorg.applicationTier.Authenticate.UserView;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

abstract public class SignatureIntention extends SignatureIntention_Base {

    final static int EXPIRE_MINUTES = 10;

    public SignatureIntention() {
	super();

	setSignatureSystem(SignatureSystem.getInstance());
	setUser(UserView.getCurrentUser());

	setTokenInUsed(false);
	setTokenOutUsed(false);
	setExpire(new DateTime().plusMinutes(EXPIRE_MINUTES));

	generateTokens();
    }

    abstract public <T extends Signable> T getSignObject();

    abstract public SignatureMetaData getMetaData();

    abstract protected void finalizeSignature();

    abstract protected void setRelation(SignatureIntention signature);

    @Service
    final public void sealSignature(UploadedFile file0, UploadedFile file1) {
	SignatureRepository.getRepository().addSignature(this, file0, file1);

	finalizeSignature();
	setRelation(this);
	setSealedDateTime(new DateTime());
    }

    public String getSignatureContent() {
	if (getSignatureFile() != null) {
	    return new String(getSignatureFile().getContent());
	}

	return "";
    }

    private void generateTokens() {
	setTokenIn(RandomStringUtils.randomAlphanumeric(32));
	setTokenOut(RandomStringUtils.randomAlphanumeric(32));
    }

    public void getContentToSign(SignatureExporter signatureExporter) {
	try {
	    signatureExporter.export(getMetaData());
	} catch (ExporterException e) {
	    e.printStackTrace();
	}
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
	return getSealedDateTime() != null;
    }

    @Service
    public void delete() {
	removeMyOrg();
	removeSignatureFile();
	removeSignatureQueue();
	removeSignatureSystem();
	removeUser();
	removeWorkflowLog();
	removeWorkflowProcess();
	deleteDomainObject();
    }
}
