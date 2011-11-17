/**
 * 
 */
package module.signed_workflow.interfaces;

import java.util.List;

import module.signed_workflow.domain.ActivitySignatureData;
import module.signed_workflow.domain.WorkflowProcessSignatureData;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;
import myorg.domain.User;

/**
 * This interface should be used by all of the {@link WorkflowProcess} that have
 * Activities which have activities that generate Signatures
 * 
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public interface WorkflowProcessWithSigningActivities {

    /**
     * 
     * @param pendingUser
     *            the user that will be set as a pending one on the newly
     *            created SignatureData or null if none is to be set
     * @param activityClass
     *            the class of the {@link WorkflowActivity} that should provide
     *            an hint for the Process to know who to delegate the creation
     *            of the SignatureData to
     * @return a newly created ActivitySignatureData with a Document to be
     *         signed already generated
     */
    public abstract ActivitySignatureData createActivitySignatureData(User pendingUser, Class activityClass);

    public abstract List<WorkflowProcessSignatureData> getPendingOrFinishedAssociatedSignatureDatas();

}
