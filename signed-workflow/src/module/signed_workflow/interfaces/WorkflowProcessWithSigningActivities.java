/*
 * @(#)WorkflowProcessWithSigningActivities.java
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
package module.signed_workflow.interfaces;

import java.util.List;

import module.signed_workflow.domain.ActivitySignatureData;
import module.signed_workflow.domain.WorkflowProcessSignatureData;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;
import myorg.domain.User;

/**
 * 
 * This interface should be used by all of the {@link WorkflowProcess} that have
 * Activities which have activities that generate Signatures
 * 
 * @author João Antunes
 * 
 */
public interface WorkflowProcessWithSigningActivities {

    /**
     * 
     * @param pendingUser
     *            the user that will be set as a pending one on the newly
     *            created SignatureData or null if none is to be set
     * @param activityClass
     *            the class of the {@link WorkflowActivity} that should provide
     *            an hint for the Process to know who to delegate the creation
     *            of the SignatureData to
     * @return a newly created ActivitySignatureData with a Document to be
     *         signed already generated
     */
    public abstract ActivitySignatureData createActivitySignatureData(User pendingUser, Class activityClass);

    public abstract List<WorkflowProcessSignatureData> getPendingOrFinishedAssociatedSignatureDatas();

}
