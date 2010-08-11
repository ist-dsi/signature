package module.signature.metadata;

import module.signature.util.exporter.SignatureExporter;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;

public class SignatureMetaDataActivity extends SignatureMetaData<WorkflowActivity> {

    private String activityName;
    private String description;

    public SignatureMetaDataActivity(WorkflowProcess process, WorkflowActivity activity) {
	super(activity);
    }

    @Override
    protected void transverse(WorkflowActivity activity) {
	this.activityName = activity.getName();
	this.description = activity.getLocalizedName();
    }

    @Override
    public void accept(SignatureExporter signatureExporter) {
	signatureExporter.addParent("process", getActivityName());
	signatureExporter.addParent("description", getDescription());
    }

    public String getActivityName() {
	return activityName;
    }

    public String getDescription() {
	return description;
    }
}
