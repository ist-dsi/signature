package module.signature.domain;

import module.signature.metadata.SignatureMetaData;
import module.signature.metadata.SignatureMetaDataWorkflowLog;
import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.ActivityLog;
import module.workflow.domain.WorkflowProcess;
import pt.ist.fenixWebFramework.services.Service;

public class SignatureIntentionActivity<P extends WorkflowProcess, AI extends ActivityInformation<P>> extends
	SignatureIntentionActivity_Base {

    @Service
    public static <P extends WorkflowProcess, AI extends ActivityInformation<P>> SignatureIntentionActivity<P, AI> factory(
	    ActivityLog log, AI ai) {
	return new SignatureIntentionActivity<P, AI>(log, ai);
    }

    public SignatureIntentionActivity(ActivityLog log, AI ai) {
	super();

	init(log, ai);
    }

    protected void init(ActivityLog log, AI ai) {
	super.init(log);
    }

    @Override
    public ActivityLog getSignObject() {
	return fromExternalId(getIdentification());
    }

    protected P getProcess() {
	return (P) getSignObject().getProcess();
    }

    protected WorkflowActivity<P, AI> getActivity() {
	return getProcess().getActivity(getSignObject().getOperation());
    }

    @SuppressWarnings("unchecked")
    protected AI getActivityInformation() {
	return (AI) getActivity().getActivityInformation(getProcess());
    }

    @Override
    public SignatureMetaData getMetaData() {
	return new SignatureMetaDataWorkflowLog(getSignObject(), getProcess());
    }

    @Override
    protected void setRelation(SignatureIntention signature) {
	getSignObject().setSignature(signature);
    }

    @Override
    protected void finalizeSignature() {
	getActivity().execute(getActivityInformation(), getSignObject());
	getSignObject().updateWhenOperationWasRan();
    }

}
