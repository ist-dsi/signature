package module.signature.metadata;

import javax.xml.bind.annotation.XmlRootElement;

import module.signature.exception.SignatureMetaDataInvalidException;
import module.workflow.domain.ActivityLog;
import module.workflow.domain.WorkflowLog;

@XmlRootElement
public class SignatureMetaDataActivityLog extends SignatureMetaDataWorkflowLog<ActivityLog> {

    private String operationName;

    public SignatureMetaDataActivityLog() {
	super();
    }

    public SignatureMetaDataActivityLog(ActivityLog log) {
	super(log);
    }

    @Override
    public void checkData(WorkflowLog log) throws SignatureMetaDataInvalidException {
	super.checkData(log);

	// assertEquals(getOperationName(), log.get)
    }

    public String getOperationName() {
	return operationName;
    }

    public void setOperationName(String operationName) {
	this.operationName = operationName;
    }

}
