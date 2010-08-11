package module.signature.metadata;

import module.signature.util.exporter.SignatureExporter;
import module.workflow.domain.WorkflowLog;

public class SignatureMetaDataLog extends SignatureMetaData<WorkflowLog> {

    private String description;

    public SignatureMetaDataLog(WorkflowLog log) {
	super(log);
    }

    @Override
    public void accept(SignatureExporter signatureExporter) {
	signatureExporter.addItem(getDescription());
    }

    @Override
    protected void transverse(WorkflowLog log) {
	description = log.getDescription();
    }

    public String getDescription() {
	return description;
    }
}
