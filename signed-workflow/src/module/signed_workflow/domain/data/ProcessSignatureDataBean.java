/**
 * 
 */
package module.signed_workflow.domain.data;

import module.signature.domain.data.GenericSourceOfInfoForSignatureDataBean;
import module.signature.domain.data.SignatureData;
import module.workflow.domain.WorkflowProcess;

/**
 * Generic SignatureDataBean for a WorkflowProcess {@link WorkflowProcess}
 * 
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public abstract class ProcessSignatureDataBean<P extends WorkflowProcess> extends GenericSourceOfInfoForSignatureDataBean {

    private final WorkflowProcess workflowProcess;

    public ProcessSignatureDataBean(SignatureData signatureData, WorkflowProcess process) {
	super(signatureData);
	this.workflowProcess = process;
    }

    public WorkflowProcess getWorkflowProcess() {
	return workflowProcess;
    }
    /**
     * default serial verson
     */
    private static final long serialVersionUID = 1L;


}
