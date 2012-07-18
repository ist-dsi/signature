/*
 * @(#)ActivitySignatureDataBean.java
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

import java.util.Random;

import module.signature.domain.data.SignatureData;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

/**
 * 
 *         Class used to retrieve the data from the various data sources. This
 *         is the class that will be marshalled and unmarshalled into XML
 * 
 * @author João Antunes
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
