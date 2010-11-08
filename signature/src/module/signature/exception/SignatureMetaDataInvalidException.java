package module.signature.exception;

/**
 * This exception is used when for some reason the signature is not valid in
 * terms of time The most common reason is if tokens are not valid or the
 * expiration date/time has passed
 * 
 * @author Diogo Figueiredo
 * 
 */
public class SignatureMetaDataInvalidException extends SignatureException {

    public SignatureMetaDataInvalidException() {
    }

    public SignatureMetaDataInvalidException(Exception ex) {
	super(ex);
    }

    public SignatureMetaDataInvalidException(String message) {
	super(message);
    }
}
