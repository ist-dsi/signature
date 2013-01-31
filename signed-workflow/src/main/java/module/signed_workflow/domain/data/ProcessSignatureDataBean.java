/*
 * @(#)ProcessSignatureDataBean.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: João Antunes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Digital Signature Workflow Integration Module.
 *
 *   The Digital Signature Workflow Integration Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Digital Signature Workflow Integration Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Digital Signature Workflow Integration Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.signed_workflow.domain.data;

import module.signature.domain.data.GenericSourceOfInfoForSignatureDataBean;
import module.signature.domain.data.SignatureData;
import module.workflow.domain.WorkflowProcess;

/**
 * Generic SignatureDataBean for a WorkflowProcess {@link WorkflowProcess}
 * 
 * @author João Antunes
 * 
 */
public abstract class ProcessSignatureDataBean<P extends WorkflowProcess> extends GenericSourceOfInfoForSignatureDataBean {

	private final WorkflowProcess workflowProcess;

	public ProcessSignatureDataBean(SignatureData signatureData, WorkflowProcess process) {
		super(signatureData);
		this.workflowProcess = process;
	}

	public WorkflowProcess getWorkflowProcess() {
		return workflowProcess;
	}

	/**
	 * default serial verson
	 */
	private static final long serialVersionUID = 1L;

}
