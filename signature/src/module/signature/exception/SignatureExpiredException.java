package module.signature.exception;

/**
 * This exception is used when for some reason the signature is not valid in
 * terms of time The most common reason is if tokens are not valid or the
 * expiration date/time has passed
 * 
 * @author Diogo Figueiredo
 * 
 */
public class SignatureExpiredException extends SignatureException {

    public SignatureExpiredException() {
    }

    public SignatureExpiredException(Exception ex) {
	super(ex);
    }

}
