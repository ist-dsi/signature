package module.signature.metadata;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import module.workflow.domain.WorkflowLog;
import module.workflow.domain.WorkflowProcess;

@XmlRootElement(name = "log")
@XmlType(propOrder = { "processId", "description" })
public class SignatureMetaDataWorkflowLog extends SignatureMetaData<WorkflowLog> {

    private String processId;
    private String description;

    public SignatureMetaDataWorkflowLog() {
	super();
    }

    public SignatureMetaDataWorkflowLog(WorkflowLog log, WorkflowProcess process) {
	super(log);

	setIdentification(log.getIdentification());
	setProcessId(process.getExternalId());
	setDescription(log.getDescription());
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getProcessId() {
	return processId;
    }

    public void setProcessId(String processId) {
	this.processId = processId;
    }
}
