package module.signature.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import module.signature.exception.SignatureMetaDataInvalidException;
import module.workflow.domain.WorkflowLog;
import module.workflow.domain.WorkflowProcess;

@XmlRootElement(name = "process")
public class SignatureProcessMetaData<T extends WorkflowProcess> extends SignatureMetaData<WorkflowProcess> {

    private String processNumber;
    private String creationDate;
    private String creationPerson;
    private String description;

    private List<SignatureMetaDataWorkflowLog> logs;

    public SignatureProcessMetaData() {
	super();
    }

    public SignatureProcessMetaData(WorkflowProcess process) {
	super(process);

	setLogs(new ArrayList<SignatureMetaDataWorkflowLog>());

	setProcessNumber(process.getProcessNumber());
	setCreationDate(process.getCreationDate().toString("yyyy-MM-dd"));
	setCreationPerson(process.getProcessCreator().getShortPresentationName());
	setDescription(process.getDescription());

	for (WorkflowLog log : process.getExecutionLogs()) {
	    getLogs().add(new SignatureMetaDataWorkflowLog(log));
	}
    }

    @Override
    public void checkData(WorkflowProcess process) throws SignatureMetaDataInvalidException {
	assertEquals(getProcessNumber(), process.getProcessNumber());
    }

    public List<SignatureMetaDataWorkflowLog> getLogs() {
	return logs;
    }

    public void setLogs(List<SignatureMetaDataWorkflowLog> logs) {
	this.logs = logs;
    }

    public String getProcessNumber() {
	return processNumber;
    }

    public void setProcessNumber(String processNumber) {
	this.processNumber = processNumber;
    }

    public String getCreationDate() {
	return creationDate;
    }

    public void setCreationDate(String creationDate) {
	this.creationDate = creationDate;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getCreationPerson() {
	return creationPerson;
    }

    public void setCreationPerson(String creationPerson) {
	this.creationPerson = creationPerson;
    }
}
