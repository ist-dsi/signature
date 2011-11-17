/**
 * 
 */
package module.signed_workflow.interfaces;

import module.workflow.domain.WorkflowProcess;
import myorg.domain.User;

/**
 * Interface used to mark all of the activities which are part of a Signature
 * proccess
 * 
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public interface SigningProcessActivity {
    
    public void validateSignatureDataCanBeShown(User userWhoMadeTheRequest, WorkflowProcess process);

}
