package module.signature.metadata;

import java.util.ArrayList;
import java.util.List;

import module.signature.util.exporter.SignatureExporter;
import module.workflow.domain.WorkflowLog;
import module.workflow.domain.WorkflowProcess;

public class SignatureMetaDataProcess extends SignatureMetaData<WorkflowProcess> {

    private String processId;
    private List<SignatureMetaDataLog> logs;

    public SignatureMetaDataProcess(WorkflowProcess process) {
	super(process);
    }

    @Override
    protected void transverse(WorkflowProcess process) {
	processId = process.getExternalId();

	logs = new ArrayList<SignatureMetaDataLog>();

	for (WorkflowLog log : process.getExecutionLogs()) {
	    logs.add(new SignatureMetaDataLog(log));
	}
    }

    @Override
    public void accept(SignatureExporter signatureExporter) {
	signatureExporter.addParent("process", getProcessId());

	for (SignatureMetaDataLog log : getLogs()) {
	    signatureExporter.addItem(log);
	}
    }

    public String getProcessId() {
	return processId;
    }

    public List<SignatureMetaDataLog> getLogs() {
	return logs;
    }

}
