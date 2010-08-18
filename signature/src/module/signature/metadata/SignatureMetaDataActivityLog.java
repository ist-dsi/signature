package module.signature.metadata;

import javax.xml.bind.annotation.XmlRootElement;

import module.workflow.domain.ActivityLog;
import module.workflow.domain.WorkflowProcess;

@XmlRootElement
public class SignatureMetaDataActivityLog extends SignatureMetaDataWorkflowLog {

    private String operationName;

    public SignatureMetaDataActivityLog() {
	super();
    }

    public SignatureMetaDataActivityLog(ActivityLog log, WorkflowProcess process) {
	super(log, process);
    }

    public String getOperationName() {
	return operationName;
    }

    public void setOperationName(String operationName) {
	this.operationName = operationName;
    }

}
