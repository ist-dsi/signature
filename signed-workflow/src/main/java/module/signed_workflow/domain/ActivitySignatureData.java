/*
 * @(#)ActivitySignatureData.java
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

import java.util.Set;

import module.signature.domain.data.SignatureDataFormat;
import module.signature.exception.SignatureDataException;
import module.signed_workflow.interfaces.SigningProcessActivity;
import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;
import pt.ist.bennu.core.domain.User;

/**
 * 
 * @author João Antunes
 * 
 */
public abstract class ActivitySignatureData extends ActivitySignatureData_Base {

    protected ActivitySignatureData() {
        super();
    }

    protected ActivitySignatureData(WorkflowProcess workflowProcess,
            WorkflowActivity<WorkflowProcess, ActivityInformation<WorkflowProcess>> activity) {
        super();
    }

    public void init(SignatureDataFormat signatureDataFormat, WorkflowProcess workflowProcess, Class<?> activityClass,
            User pendingUser) {
        if (!SigningProcessActivity.class.isAssignableFrom(activityClass)) {
            throw new SignatureDataException("illegal.class.parsed.as.argument.to.AcitivitySignatureData.constructor");
        }
        setActivityClass(activityClass);
        super.init(signatureDataFormat, workflowProcess, pendingUser);
    }

    @Override
    public void validateAccessToSignatureData(Set<User> usersRequestingSignatureData) {
        WorkflowProcess process = getWorkflowProcess();
        for (User user : usersRequestingSignatureData) {
            //by default let's check if the process is accessible by the user who made the request
            if (!process.isAccessible(user)) {
                throw new SignatureDataException("user.cannot.access.signature.data");
            }
            //let's validate with the specific rules that can be found with the associated
            //activity
            try {
                ((SigningProcessActivity) getActivityClass().newInstance()).validateSignatureDataCanBeShown(user, process);
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new SignatureDataException("validation.exception", e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new SignatureDataException("validation.exception", e);
            }
        }

    }

}
