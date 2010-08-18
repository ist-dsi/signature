package module.signature.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import module.workflow.domain.ActivityLog;
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

	setIdentification(process.getIdentification());
	setProcessNumber(process.getProcessNumber());

	setLogs(new ArrayList<SignatureMetaDataWorkflowLog>());

	for (WorkflowLog log : process.getExecutionLogs()) {
	    getLogs().add(addLog(log, process));
	}
    }

    private SignatureMetaDataWorkflowLog addLog(WorkflowLog log, WorkflowProcess process) {
	return new SignatureMetaDataWorkflowLog(log, process);
    }

    private SignatureMetaDataWorkflowLog addLog(ActivityLog log, WorkflowProcess process) {
	return new SignatureMetaDataActivityLog(log, process);
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

}
