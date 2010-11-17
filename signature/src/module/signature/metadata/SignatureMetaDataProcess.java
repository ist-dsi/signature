package module.signature.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import module.signature.exception.SignatureMetaDataInvalidException;
import module.workflow.domain.WorkflowLog;
import module.workflow.domain.WorkflowProcess;

import org.joda.time.DateTime;

@XmlRootElement(name = "process")
@XmlAccessorType(XmlAccessType.FIELD)
public class SignatureMetaDataProcess extends SignatureMetaData<WorkflowProcess> {

    private String processNumber;
    private DateTime creationDate;
    private String description;

    private List<SignatureMetaDataWorkflowLog> logs;

    public SignatureMetaDataProcess() {
	super();
    }

    public SignatureMetaDataProcess(WorkflowProcess process) {
	super(process);

	logs = new ArrayList<SignatureMetaDataWorkflowLog>();

	setProcessNumber(process.getProcessNumber());
	setCreationDate(process.getCreationDate());
	setDescription(process.getDescription());

	for (WorkflowLog log : process.getExecutionLogs()) {
	    SignatureMetaDataWorkflowLog metadataLog = new SignatureMetaDataWorkflowLog(log);
	    metadataLog.setProcessId(process.getIdentification());
	    metadataLog.setDescription(log.getDescription());

	    addLog(metadataLog);
	}
    }

    @Override
    public void checkData(WorkflowProcess process) throws SignatureMetaDataInvalidException {
	assertEquals(getProcessNumber(), process.getProcessNumber());
    }

    public List<SignatureMetaDataWorkflowLog> getLogs() {
	return logs;
    }

    public void addLog(SignatureMetaDataWorkflowLog metaDataLog) {
	logs.add(metaDataLog);
    }

    public String getProcessNumber() {
	return processNumber;
    }

    public void setProcessNumber(String processNumber) {
	this.processNumber = processNumber;
    }

    public DateTime getCreationDate() {
	return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
	this.creationDate = creationDate;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

}
