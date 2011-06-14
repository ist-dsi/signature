package module.signed_workflow.domain;

import java.util.ArrayList;
import java.util.List;

import module.workflow.domain.WorkflowProcess;

public abstract class WorkflowProcessSignatureData extends WorkflowProcessSignatureData_Base {
    
    public  WorkflowProcessSignatureData() {
        super();
    }
    
    public static List<WorkflowProcessSignatureData> getSignatureData(WorkflowProcess process) {
	return getSpecificWorkflowSignatureData(process, WorkflowProcessSignatureData.class.getClass());

    }

    /**
     * 
     * @param process
     *            the WorkflowProcess to retrieve the signature data objects
     *            {@link WorkflowProcessSignatureData}
     * @param clazz
     *            a class of either a {@link WorkflowProcessSignatureData} or
     *            any of its subclasses or null to filter the result
     * @return
     */
    protected static List<WorkflowProcessSignatureData> getSpecificWorkflowSignatureData(WorkflowProcess process, Class clazz) {
	if (clazz == null)
	    return process.getSignatureData();
	ArrayList<WorkflowProcessSignatureData> listSigDatas = new ArrayList<WorkflowProcessSignatureData>();
	if (WorkflowProcessSignatureData.class.getClass().isAssignableFrom(clazz)) {
	    for (WorkflowProcessSignatureData signatureData : process.getSignatureData()) {
		if (signatureData.getClass().equals(clazz)) {
		    listSigDatas.add(signatureData);
		}
	    }
	}
	return listSigDatas;
    }
}
