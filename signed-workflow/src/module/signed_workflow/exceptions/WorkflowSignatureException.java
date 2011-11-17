/**
 * 
 */
package module.signed_workflow.exceptions;

import java.util.ResourceBundle;

import myorg.domain.exceptions.DomainException;

/**
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public class WorkflowSignatureException extends DomainException {

    public WorkflowSignatureException(String string) {
	super(string, ResourceBundle.getBundle("resources/Signed-workflowResources"));
    }

    public WorkflowSignatureException(String string, Exception e) {
	super(string, e, ResourceBundle.getBundle("resources/Signed-workflowResources"));
    }

    /**
     * default version ID
     */
    private static final long serialVersionUID = 1L;

}
