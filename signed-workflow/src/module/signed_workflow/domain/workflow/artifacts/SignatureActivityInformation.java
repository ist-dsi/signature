/*
 * @(#)SignatureActivityInformation.java
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
package module.signed_workflow.domain.workflow.artifacts;

import module.signature.domain.Signature;
import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;

/**
 * 
 * @author João Antunes
 * 
 */
public class SignatureActivityInformation<P extends WorkflowProcess> extends ActivityInformation<P> {

    private final Signature signature;

    public SignatureActivityInformation(P process,
	    WorkflowActivity<? extends WorkflowProcess, ? extends ActivityInformation> activity, Signature signature) {
	super(process, activity);
	this.signature = signature;
    }

    public Signature getSignature() {
	return signature;
    }

    @Override
    public boolean hasAllneededInfo() {
	return signature != null;
    }

    /**
     * Default serial version
     */
    private static final long serialVersionUID = 1L;

}
