/*
 * @(#)WorkflowLogSignatureDataBean.java
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
package module.signed_workflow.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import module.workflow.domain.WorkflowLog;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import pt.ist.bennu.core.domain.User;

/**
 * 
 * @author João Antunes
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
		executorDateStringRepresentation =
				executorDate.toString(DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss").withLocale(new Locale("pt")));
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
