package module.signature.domain;

import module.signature.metadata.SignatureMetaData;
import module.signature.metadata.SignatureMetaDataActivity;
import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.ActivityLog;
import module.workflow.domain.WorkflowLog;
import module.workflow.domain.WorkflowProcess;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

public class SignatureIntentionActivity<P extends WorkflowProcess, AI extends ActivityInformation<P>> extends
	SignatureIntentionActivity_Base {

    @Service
    public static <P extends WorkflowProcess, AI extends ActivityInformation<P>> SignatureIntentionActivity<P, AI> factory(
	    P process, WorkflowActivity<P, AI> activity, AI ai) {
	return new SignatureIntentionActivity<P, AI>(process, activity, ai);
    }

    public SignatureIntentionActivity(P process, WorkflowActivity<P, AI> activity, AI ai) {
	super();

	init(process, activity, ai);
    }

    protected void init(P process, WorkflowActivity<P, AI> activity, AI ai) {
	setProcessId(process.getExternalId());
	setActivityId(activity.getName());

	setIdentification(getProcessId() + getActivityId());
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
    public WorkflowLog getSignObject() {
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
	ActivityLog activityLog = getActivity().execute(getActivityInformation());

	setIdentification(activityLog.getIdentification());
	setRelation(this);
    }

}
