package module.signature.domain;

import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

public class SignatureIntentionActivity<P extends WorkflowProcess, AI extends ActivityInformation<P>> extends
	SignatureIntentionActivity_Base {

    private final WorkflowProcess process;
    private final WorkflowActivity<P, AI> activity;
    private final AI ai;

    @Service
    public static <P extends WorkflowProcess, AI extends ActivityInformation<P>> SignatureIntentionActivity<P, AI> factory(
	    WorkflowProcess process, WorkflowActivity<P, AI> activity, AI ai) {
	return new SignatureIntentionActivity<P, AI>(process, activity, ai);
    }

    public SignatureIntentionActivity(WorkflowProcess process, WorkflowActivity<P, AI> activity, AI ai) {
	super();

	this.process = process;
	this.activity = activity;
	this.ai = ai;
    }

    @Override
    public String getIdentification() {
	return activity.getName();
    }

    @Override
    public WorkflowActivity<P, AI> getSignObject() {
	return activity;
    }

    @Override
    public void finalizeSignature(UploadedFile file0, UploadedFile file1) {
	activity.execute(ai);
    }
}
