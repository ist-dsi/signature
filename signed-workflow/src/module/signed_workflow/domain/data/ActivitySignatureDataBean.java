/**
 * 
 */
package module.signed_workflow.domain.data;

import java.util.Random;

import module.signature.domain.data.SignatureData;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

/**
 * 
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 *         Class used to retrieve the data from the various data sources. This
 *         is the class that will be marshalled and unmarshalled into XML
 * 
 */
public abstract class ActivitySignatureDataBean<Activity extends WorkflowActivity>
	extends ProcessSignatureDataBean<WorkflowProcess> {

    /**
     * Default serial version ID
     */
    private static final long serialVersionUID = 1L;
    private static Random randomNrForSigIdGeneration = new Random();
    private final Class<Activity> activity;

    private final DateTime dataSnapshotDate;

    protected ActivitySignatureDataBean(WorkflowProcess process, Class<Activity> activity, SignatureData signatureData) {
	super(signatureData, process);
	//default constructor private so that it isn't used outside of this file
	this.dataSnapshotDate = new DateTime();
	this.activity = activity;
    }


    public DateTime getDataSnapshotDate() {
	return dataSnapshotDate;
    }

    /**
     * 
     * @return the SignatureId, this id should be unique and will identify the
     *         signed document
     */
    @Override
    public String generateSignatureId() {
	DateTime currentDateTime = new DateTime();
	String signatureId = getWorkflowProcess().getProcessNumber() + "-" + activity.getSimpleName() + "-"
		+ currentDateTime.getYear() + "-" + currentDateTime.getMonthOfYear() + "-" + currentDateTime.getDayOfMonth()
		+ "_" + currentDateTime.getMillis() + "_" + randomNrForSigIdGeneration.nextInt(100000);
	signatureId = StringUtils.replaceChars(signatureId, ' ', '_');
	return signatureId;

    }




}
