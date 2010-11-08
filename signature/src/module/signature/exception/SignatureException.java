package module.signature.exception;

public class SignatureException extends Exception {

    public SignatureException() {
    }

    public SignatureException(Exception ex) {
	super(ex);
    }

    public SignatureException(String message) {
	super(message);
    }
}
