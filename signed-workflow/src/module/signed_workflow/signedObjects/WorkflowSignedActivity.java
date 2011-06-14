/**
 *  Class which represents a signed activity. All of the signed activities must extend from this class
 */
package module.signed_workflow.signedObjects;

import module.signature.domain.data.ObjectSignatureData;
import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;

/**
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public abstract class WorkflowSignedActivity<SigData extends ObjectSignatureData, P extends WorkflowProcess, AI extends ActivityInformation<P>>
	extends WorkflowActivity<P, AI> {

}
