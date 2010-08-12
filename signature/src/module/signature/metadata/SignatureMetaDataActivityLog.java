package module.signature.metadata;

import javax.xml.bind.annotation.XmlRootElement;

import module.workflow.domain.WorkflowLog;

@XmlRootElement(name = "activityLog")
public class SignatureMetaDataActivityLog extends SignatureMetaData<WorkflowLog> {

    private String description;

    public SignatureMetaDataActivityLog(WorkflowLog log) {
	super(log);
    }

    @Override
    protected void transverse(WorkflowLog log) {
	description = log.getDescription();
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

}
