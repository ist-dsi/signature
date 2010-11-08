package module.signature.exception;

public class SignatureNotSealedException extends SignatureException {

    public SignatureNotSealedException() {
    }

    public SignatureNotSealedException(Exception ex) {
	super(ex);
    }

}
