package module.signature.metadata;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import module.signature.exception.SignatureMetaDataInvalidException;
import module.workflow.domain.WorkflowLog;

@XmlRootElement(name = "log")
@XmlType(propOrder = { "processId", "description" })
public class SignatureMetaDataWorkflowLog<T extends WorkflowLog> extends SignatureMetaData<WorkflowLog> {

    private String processId;
    private String description;

    public SignatureMetaDataWorkflowLog() {
	super();
    }

    public SignatureMetaDataWorkflowLog(WorkflowLog log) {
	super(log);

	setProcessId(log.getProcess().getExternalId());
	setDescription(log.getDescription());
    }

    @Override
    public void checkData(WorkflowLog log) throws SignatureMetaDataInvalidException {
	assertEquals(getProcessId(), log.getProcess().getExternalId());
	assertEquals(getDescription(), log.getDescription());
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
