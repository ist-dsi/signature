package module.signature.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import module.workflow.domain.WorkflowLog;
import module.workflow.domain.WorkflowProcess;

@XmlRootElement(name = "process")
@XmlType
public class SignatureMetaDataProcess extends SignatureMetaData<WorkflowProcess> {

    private String processId;
    private List<SignatureMetaDataActivityLog> logs;

    public SignatureMetaDataProcess(WorkflowProcess process) {
	super(process);
    }

    @Override
    protected void transverse(WorkflowProcess process) {
	processId = process.getExternalId();

	logs = new ArrayList<SignatureMetaDataActivityLog>();

	for (WorkflowLog log : process.getExecutionLogs()) {
	    logs.add(new SignatureMetaDataActivityLog(log));
	}
    }

    public String getProcessId() {
	return processId;
    }

    public void setProcessId(String processId) {
	this.processId = processId;
    }

    public List<SignatureMetaDataActivityLog> getLogs() {
	return logs;
    }

    public void setLogs(List<SignatureMetaDataActivityLog> logs) {
	this.logs = logs;
    }

}
