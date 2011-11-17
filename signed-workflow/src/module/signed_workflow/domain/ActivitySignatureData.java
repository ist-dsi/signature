package module.signed_workflow.domain;

import java.util.Set;

import module.signature.domain.data.SignatureDataFormat;
import module.signature.exception.SignatureDataException;
import module.signed_workflow.interfaces.SigningProcessActivity;
import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;
import myorg.domain.User;

public abstract class ActivitySignatureData extends ActivitySignatureData_Base {

    protected ActivitySignatureData() {
	super();
    }

    protected ActivitySignatureData(WorkflowProcess workflowProcess,
	    WorkflowActivity<WorkflowProcess, ActivityInformation<WorkflowProcess>> activity) {
	super();
    }

    public void init(SignatureDataFormat signatureDataFormat, WorkflowProcess workflowProcess,
 Class<?> activityClass,
	    User pendingUser) {
	if (!SigningProcessActivity.class.isAssignableFrom(activityClass)) {
	    throw new SignatureDataException("illegal.class.parsed.as.argument.to.AcitivitySignatureData.constructor");
	}
	setActivityClass(activityClass);
	super.init(signatureDataFormat, workflowProcess, pendingUser);
    }

    @Override
    public void validateAccessToSignatureData(Set<User> usersRequestingSignatureData) {
	WorkflowProcess process = getWorkflowProcess();
	for (User user : usersRequestingSignatureData) {
	    //by default let's check if the process is accessible by the user who made the request
	    if (!process.isAccessible(user))
		throw new SignatureDataException("user.cannot.access.signature.data");
	    //let's validate with the specific rules that can be found with the associated
	    //activity
	    try {
		((SigningProcessActivity) getActivityClass().newInstance()).validateSignatureDataCanBeShown(user, process);
	    } catch (InstantiationException e) {
		e.printStackTrace();
		throw new SignatureDataException("validation.exception", e);
	    } catch (IllegalAccessException e) {
		e.printStackTrace();
		throw new SignatureDataException("validation.exception", e);
	    }
	}

    }

}
