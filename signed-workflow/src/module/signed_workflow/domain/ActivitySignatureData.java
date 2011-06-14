package module.signed_workflow.domain;

import java.io.Serializable;

import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;
import myorg.util.FileUploadBean;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


public abstract class ActivitySignatureData extends ActivitySignatureData_Base {

    /**
     * 
     * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
     * 
     *         Class used to retrieve the data from the various data sources.
     *         This is the class that will be marshalled and unmarshalled into
     *         XML
     * 
     */
    public abstract class ActivitySignatureDataBean<Process extends WorkflowProcess>
	    implements Serializable {
	@XStreamAsAttribute
	private final String intention;
	@XStreamAsAttribute
	private final String description;

	private final Class<WorkflowActivity> activity;
	private final Process process;

	private final DateTime dataSnapshotDate;

	@XStreamAsAttribute
	private final String signatureId;


	protected ActivitySignatureDataBean(Process process, Class activity) {
	    //default constructor private so that it isn't used outside of this file
	    this.dataSnapshotDate = new DateTime();
	    this.description = generateDescriptionString();
	    this.intention = generateIntentionString();
	    this.activity = activity;
	    this.process = process;
	    this.signatureId = generateSignatureId();
	}

	public ActivitySignatureDataBean(WorkflowProcess workflowProcess) {
	    throw new Error("Please do not use this constructor directly, override it");
	}

	public DateTime getDataSnapshotDate() {
	    return dataSnapshotDate;
	}

	public final String getDescription() {
	    return generateDescriptionString();
	}

	public final String getIntention() {
	    return generateIntentionString();
	}

	public abstract String generateIntentionString();

	public abstract String generateDescriptionString();

	/**
	 * 
	 * @return the SignatureId, this id should be unique and will identify
	 *         the signed document
	 */
	public String generateSignatureId() {
	    String signatureId = process.getDescription() + "-" + activity.getName() + "-" + new DateTime().toString();
	    signatureId = StringUtils.replaceChars(signatureId, ' ', '_');
	    return signatureId;

	}

	public String getSignatureId() {
	    return signatureId;
	}


    }

    private FileUploadBean auxiliarySignatureContentBean;

    private String auxiliarySignatureContentString;

    public ActivitySignatureData(
ActivitySignatureDataBean<WorkflowProcess> wfSignatureDataBean) {
	super();
    }

    protected ActivitySignatureData() {

    }

    public void setAuxiliarySignatureContentBean(FileUploadBean auxiliarySignatureContentBean) {
	this.auxiliarySignatureContentBean = auxiliarySignatureContentBean;
    }

    public FileUploadBean getAuxiliarySignatureContentBean() {
	return auxiliarySignatureContentBean;
    }

    public void setAuxiliarySignatureContentString(String auxiliarySignatureContentString) {
	this.auxiliarySignatureContentString = auxiliarySignatureContentString;
    }

    public String getAuxiliarySignatureContentString() {
	return auxiliarySignatureContentString;
    }

}
