/**
 * 
 */
package module.signed_workflow.domain.workflow.artifacts;

import module.signature.domain.Signature;
import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;

/**
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public class SignatureActivityInformation<P extends WorkflowProcess> extends ActivityInformation<P> {

    private final Signature signature;

    public SignatureActivityInformation(P process,
	    WorkflowActivity<? extends WorkflowProcess, ? extends ActivityInformation> activity, Signature signature) {
	super(process, activity);
	this.signature = signature;
    }

    public Signature getSignature() {
	return signature;
    }

    @Override
    public boolean hasAllneededInfo() {
	return signature != null;
    }

    /**
     * Default serial version
     */
    private static final long serialVersionUID = 1L;

}
