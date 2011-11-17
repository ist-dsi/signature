/**
 * 
 */
package module.signed_workflow.domain;

import java.io.Serializable;

import module.signed_workflow.domain.data.ActivitySignatureDataBean;
import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;

/**
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public abstract class SignedActivityInformation<P extends WorkflowProcess> extends ActivityInformation<WorkflowProcess> implements
	Serializable {

    private ActivitySignatureDataBean transientActivitySignatureBean;

    protected SignedActivityInformation(WorkflowProcess process,
	    WorkflowActivity<? extends WorkflowProcess, ? extends ActivityInformation> activity) {
	super(process, activity);
    }

    public ActivitySignatureDataBean getTransientActivitySignatureBean() {
	return transientActivitySignatureBean;
    }


}
