package module.signature.metadata;

import javax.xml.bind.annotation.XmlRootElement;

import module.signature.exception.SignatureMetaDataInvalidException;
import module.workflow.domain.WorkflowLog;

@XmlRootElement(name = "log")
public class SignatureMetaDataWorkflowLog<T extends WorkflowLog> extends SignatureMetaData<WorkflowLog> {

    private String processId;
    private String description;
    private String activityExecutorId;
    private String activityExecutor;
    private String whenOperationWasRan;

    public SignatureMetaDataWorkflowLog() {
	super();
    }

    public SignatureMetaDataWorkflowLog(WorkflowLog log) {
	super(log);

	setProcessId(log.getProcess().getExternalId());
	setDescription(log.getDescription());
	setActivityExecutorId(log.getActivityExecutor().getExternalId());
	setActivityExecutor(log.getActivityExecutor().getPresentationName());
	setWhenOperationWasRan(log.getWhenOperationWasRan().toString("yyyy-MM-dd kk:mm"));
    }

    @Override
    public void checkData(WorkflowLog log) throws SignatureMetaDataInvalidException {
	assertEquals(getProcessId(), log.getProcess().getExternalId());
	assertEquals(getDescription(), log.getDescription());
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getProcessId() {
	return processId;
    }

    public void setProcessId(String processId) {
	this.processId = processId;
    }

    public String getActivityExecutorId() {
	return activityExecutorId;
    }

    public void setActivityExecutorId(String activityExecutorId) {
	this.activityExecutorId = activityExecutorId;
    }

    public String getActivityExecutor() {
	return activityExecutor;
    }

    public void setActivityExecutor(String activityExecutor) {
	this.activityExecutor = activityExecutor;
    }

    public String getWhenOperationWasRan() {
	return whenOperationWasRan;
    }

    public void setWhenOperationWasRan(String whenOperationWasRan) {
	this.whenOperationWasRan = whenOperationWasRan;
    }

}
