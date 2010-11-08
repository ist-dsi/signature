package module.signature.domain;

import module.signature.exception.SignatureException;
import module.signature.exception.SignatureExpiredException;
import module.signature.exception.SignatureMetaDataInvalidException;
import module.signature.exception.SignatureNotSealedException;
import module.signature.metadata.SignatureMetaData;
import module.signature.metadata.SignatureMetaDataRoot;
import module.signature.util.Signable;
import module.signature.util.exporter.ExporterException;
import module.signature.util.exporter.SignatureExporter;
import myorg.applicationTier.Authenticate.UserView;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

abstract public class SignatureIntention extends SignatureIntention_Base implements Signable {

    private final int INTENTION_MINUTES_LIMIT = 5;

    public SignatureIntention() {
	super();

	setSignatureSystem(SignatureSystem.getInstance());
	setUser(UserView.getCurrentUser());
	setActivated(false);

	setTokenInUsed(false);
	setTokenOutUsed(false);
	setExpire(new DateTime().plusMinutes(INTENTION_MINUTES_LIMIT));

	generateTokens();
    }

    @Override
    final public String getIdentification() {
	return getExternalId();
    }

    abstract public <T extends Signable> T getSignObject();

    /**
     * Returns the specific metadata for the current signature profile. It is
     * the responsability of the signature to return the correct metadata
     * implementation
     * 
     * @return {@link SignatureMetaData}
     */
    abstract public <T extends SignatureMetaData> T getMetaData();

    abstract protected void setRelation(SignatureIntention signature);

    abstract protected void finalizeSignature();

    /**
     * - Closes the signature setting up the signature date/time and calling own
     * finalize method. - Saves the content of the signature to the file
     * repository. - Sets the relations between the object and the signature
     * 
     * @param file0
     * @param file1
     */
    @Service
    final public void seal(UploadedFile file0, UploadedFile file1) {
	SignatureRepository.getRepository().addSignature(this, file0, file1);

	finalizeSignature();
	setRelation(this);
	setSealedDateTime(new DateTime());

	// clean user queue
	SignatureSystem.getInstance().clearQueue();
    }

    /**
     * Returns true if the signature is valid or false otherwise. Reasons to a
     * non-valid signature are: data was changed, data corruption, ..
     * 
     * Validation is based on the digital signature algorithm
     * 
     * @see aeq.jar package
     * 
     * @return {@link Boolean}
     */
    final public boolean isValid() {
	return SignatureSystem.validateSignature(this);
    }

    /**
     * Rebuilds the specific metadata from the previously saved content
     * 
     * @return {@link SignatureMetaData}
     */
    protected SignatureMetaDataRoot rebuildMetaData() throws SignatureException {
	return SignatureSystem.rebuildMetaData(this);
    }

    /**
     * This method will reconstruct the data in the signature and will verify if
     * the data matches the current database state
     * 
     * @throws SignatureException
     *             Any error will throw an exception
     */
    @SuppressWarnings("unchecked")
    final public void checkData() throws SignatureException {
	rebuildMetaData().checkData(this);
    }

    /**
     * 
     */

    public String getSignatureContent() throws SignatureNotSealedException {
	if (getSignatureFile() == null) {
	    throw new SignatureNotSealedException();
	}

	return new String(getSignatureFile().getContent());
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
    public void verifyTokenIn(String tokenIn) throws SignatureExpiredException {
	if (getTokenInUsed() || !getTokenIn().equals(tokenIn)) {
	    throw new SignatureExpiredException();
	}

	setTokenInUsed(true);
    }

    @Service
    public void verifyTokenOut(String tokenOut) throws SignatureExpiredException {
	if (getTokenOutUsed() || !getTokenOut().equals(tokenOut)) {
	    throw new SignatureExpiredException();
	}

	setTokenOutUsed(true);
    }

    public boolean isSealed() {
	return getSealedDateTime() != null;
    }

    public boolean isPending() {
	return getActivated() && !isSealed();
    }

    @Service
    public void cancel() {
	if (isSealed()) {
	    return;
	}

	if (getExpire().isBeforeNow()) {
	    delete();
	}
    }

    @Service
    public void delete() {
	if (isSealed()) {
	    return;
	}

	removeSignatureSystem();
	removeSignatureQueue();
	removeSignatureFile();
	removeUser();
	removeWorkflowLog();
	removeWorkflowProcess();
	deleteDomainObject();
    }

    protected void assertEquals(Object obj1, Object obj2) throws SignatureMetaDataInvalidException {
	if (!obj1.equals(obj2)) {
	    throw new SignatureMetaDataInvalidException();
	}
    }

}
