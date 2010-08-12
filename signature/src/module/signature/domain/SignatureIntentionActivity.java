package module.signature.domain;

import module.signature.metadata.SignatureMetaData;
import module.signature.metadata.SignatureMetaDataActivity;
import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.ActivityLog;
import module.workflow.domain.WorkflowProcess;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

public class SignatureIntentionActivity<P extends WorkflowProcess, AI extends ActivityInformation<P>> extends
	SignatureIntentionActivity_Base {

    @Service
    public static <P extends WorkflowProcess, AI extends ActivityInformation<P>> SignatureIntentionActivity<P, AI> factory(
	    ActivityLog log, P process, WorkflowActivity<P, AI> activity, AI ai) {
	return new SignatureIntentionActivity<P, AI>(log, process, activity, ai);
    }

    public SignatureIntentionActivity(ActivityLog log, P process, WorkflowActivity<P, AI> activity, AI ai) {
	super();

	init(log, process, activity, ai);
    }

    protected void init(ActivityLog log, P process, WorkflowActivity<P, AI> activity, AI ai) {
	setIdentification(log.getIdentification());

	setProcessId(process.getExternalId());
	setActivityId(activity.getName());
    }

    protected P getProcess() {
	return (P) ((WorkflowProcess) fromExternalId(getProcessId()));
    }

    protected WorkflowActivity<P, AI> getActivity() {
	return getProcess().getActivity(getActivityId());
    }

    @SuppressWarnings("unchecked")
    protected AI getActivityInformation() {
	return (AI) getActivity().getActivityInformation(getProcess());
    }

    @Override
    public ActivityLog getSignObject() {
	return fromExternalId(getIdentification());
    }

    @Override
    protected void setRelation(SignatureIntention signature) {
	getSignObject().setSignature(signature);
    }

    @Override
    public SignatureMetaData getMetaData() {
	return new SignatureMetaDataActivity(getProcess(), getActivity());
    }

    @Override
    protected void finalizeSignature(UploadedFile file0, UploadedFile file1) {
	getActivity().execute(getActivityInformation(), getSignObject());

	setRelation(this);
    }

}
