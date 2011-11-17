/**
 * 
 */
package module.signed_workflow.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import module.workflow.domain.WorkflowLog;
import myorg.domain.User;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public class WorkflowLogSignatureDataBean implements Serializable {

    /**
     * The default Serial version
     */
    private static final long serialVersionUID = 1L;
    private final String executorNameAndIstId;
    private final DateTime executorDate;
    private final String executorDateStringRepresentation;
    private final String description;

    public WorkflowLogSignatureDataBean(WorkflowLog workflowLog) {
	description = workflowLog.getDescription();
	User executorUser = workflowLog.getActivityExecutor();
	executorNameAndIstId = executorUser.getPerson().getName() + " (" + executorUser.getUsername() + ")";
	executorDate = workflowLog.getWhenOperationWasRan();
	executorDateStringRepresentation = executorDate.toString(DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss").withLocale(
		new Locale("pt")));
    }

    public static List<WorkflowLogSignatureDataBean> createLogBeans(List<WorkflowLog> executionLogs) {
	List<WorkflowLogSignatureDataBean> dataBeans = new ArrayList<WorkflowLogSignatureDataBean>();
	for (WorkflowLog workflowLog : executionLogs) {
	    WorkflowLogSignatureDataBean workflowLogSignatureDataBean = new WorkflowLogSignatureDataBean(workflowLog);
	    dataBeans.add(workflowLogSignatureDataBean);
	}
	return dataBeans;
    }

    public String getDescription() {
	return description;
    }

    public DateTime getExecutorDate() {
	return executorDate;
    }

    public String getExecutorNameAndIstId() {
	return executorNameAndIstId;
    }

    public String getExecutorDateStringRepresentation() {
	return executorDateStringRepresentation;
    }

}
