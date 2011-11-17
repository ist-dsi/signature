/**
 * 
 */
package module.signature.exception;

import java.util.List;

import module.signature.domain.data.SignatureData;

/**
 * Class used to present to the user an error that ocurred with the
 * {@link SignatureData}
 * 
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public class SignatureDataException extends SignatureException {

    public SignatureDataException(String string, Throwable e) {
	super(string, e);
    }

    public SignatureDataException(String string) {
	super(string);
    }

    /**
     * 
     * @param string
     * @param b
     *            if b is set to true, this will generate a log message with a
     *            special format to be interpreted by a script which will
     *            deliver a notification
     * @param arguments
     *            the arguments for the notification, or null
     */
    public SignatureDataException(String string, boolean b, List<String> arguments) {
	//TODO
	super(string, b, arguments);
    }

    public SignatureDataException(Throwable e) {
	super("unspecified.message.got.exception", e);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
