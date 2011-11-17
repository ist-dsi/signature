package module.signature.exception;

import java.util.List;
import java.util.ResourceBundle;

import myorg.domain.exceptions.DomainException;

/**
 * Class used to signal an exception, and that can be used to render a message
 * to the user as well
 * 
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public class SignatureException extends DomainException {

    public SignatureException(String string, Throwable e) {
	super(string, e, ResourceBundle.getBundle("resources/SignatureResources"));
    }

    public SignatureException(String string) {
	super(string, ResourceBundle.getBundle("resources/SignatureResources"));
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
    public SignatureException(String string, boolean b, List<String> arguments) {
	//TODO
    }

}
