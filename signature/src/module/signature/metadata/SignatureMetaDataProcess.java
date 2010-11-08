package module.signature.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import module.signature.exception.SignatureMetaDataInvalidException;
import module.workflow.domain.WorkflowLog;
import module.workflow.domain.WorkflowProcess;

@XmlRootElement(name = "process")
@XmlType(propOrder = { "processNumber", "logs" })
@XmlAccessorType(XmlAccessType.FIELD)
public class SignatureMetaDataProcess extends SignatureMetaData<WorkflowProcess> {

    private String processNumber;

    private List<SignatureMetaDataWorkflowLog> logs;

    public SignatureMetaDataProcess() {
	super();
    }

    public SignatureMetaDataProcess(WorkflowProcess process) {
	super(process);

	logs = new ArrayList<SignatureMetaDataWorkflowLog>();

	setProcessNumber(process.getProcessNumber());

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

}
