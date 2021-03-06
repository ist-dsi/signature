/*
 * @(#)WorkflowProcessSignatureData.java
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

import java.util.ArrayList;
import java.util.List;

import module.signature.domain.data.MultipleObjectSignatureDataAggregator;
import module.signature.domain.data.SignatureData;
import module.signature.domain.data.SignatureDataFormat;
import module.workflow.domain.WorkflowProcess;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.predicates.Predicate;

/**
 * 
 * @author João Antunes
 * 
 */
public abstract class WorkflowProcessSignatureData extends WorkflowProcessSignatureData_Base {

    protected WorkflowProcessSignatureData() {
        super();
    }

    public WorkflowProcessSignatureData(SignatureDataFormat signatureDataFormat, WorkflowProcess workflowProcess, User pendingUser) {
        super();
        init(signatureDataFormat, getWorkflowProcess(), pendingUser);
    }

    public static List<WorkflowProcessSignatureData> getSignatureData(WorkflowProcess process) {
        return getSpecificWorkflowSignatureData(process, WorkflowProcessSignatureData.class, null);

    }

    public static List<WorkflowProcessSignatureData> getPendingOrFinishedSignatureData(WorkflowProcess process) {
        return getSpecificWorkflowSignatureData(process, WorkflowProcessSignatureData.class, new Predicate<SignatureData>() {

            @Override
            public boolean eval(SignatureData signatureData) {
                if (signatureData.hasSignature() || signatureData.hasUserToSignPendingSignature()) {
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    public void init(SignatureDataFormat signatureDataFormat, WorkflowProcess workflowProcess, User pendingUser) {
        setWorkflowProcess(workflowProcess);
        setProcessId(workflowProcess.getProcessNumber());
        super.init(signatureDataFormat, pendingUser);
    }

    /**
     * 
     * @param process
     *            the WorkflowProcess to retrieve the signature data objects {@link WorkflowProcessSignatureData}
     * @param clazz
     *            a class of either a {@link WorkflowProcessSignatureData} or
     *            any of its subclasses or null to filter the result
     * @return
     */
    protected static List<WorkflowProcessSignatureData> getSpecificWorkflowSignatureData(WorkflowProcess process, Class clazz,
            Predicate<SignatureData> predicate) {
        if (clazz == null) {
            return process.getSignatureData();
        }
        ArrayList<WorkflowProcessSignatureData> listSigDatas = new ArrayList<WorkflowProcessSignatureData>();
        if (WorkflowProcessSignatureData.class.isAssignableFrom(clazz)) {
            for (WorkflowProcessSignatureData signatureData : process.getSignatureData()) {
                if (clazz.isAssignableFrom(signatureData.getClass())) {
                    if (predicate != null) {
                        if (predicate.eval(signatureData)) {
                            listSigDatas.add(signatureData);
                        }

                    } else {
                        listSigDatas.add(signatureData);

                    }
                }
            }
        }
        return listSigDatas;
    }

    /**
     * Deletes this object if this is an Orphan SignatureData i.e. if it doesn't
     * have any kind of connection with a Signature and is just a pending one,
     * otherwise it will throw an exception
     */
    @Service
    @Override
    public void deleteOrphanSignatureData() {
        if (getSignature() != null) {
            throw new DomainException("trying.to.delete.a.SignatureData.with.a.Signature");
        }

        removeUserToSignPendingSignature();
        removeWorkflowProcess();
        deleteDomainObject();
    }

    @Service
    public static void removeOrphanSignatureDatasFromList(List<WorkflowProcessSignatureData> listSignatureDatas) {
        //let's get rid of all the orphan SignatureData objects that might exist
        boolean deletedOneActivityAlready = false;
        if (listSignatureDatas != null && (!listSignatureDatas.isEmpty())) {
            for (WorkflowProcessSignatureData workflowProcessSignatureData : listSignatureDatas) {
                if (!workflowProcessSignatureData.hasSignature() && deletedOneActivityAlready) {
                    /*
                     * if we were gonna delete more than one SignatureData, then
                     * the system is inconsistent or the method wasn't working,
                     * so let's throw an exception
                     */
                    throw new DomainException("it.was.gonna.delete.more.than.one.signature.data");
                }
                if (!workflowProcessSignatureData.hasSignature()) {
                    deletedOneActivityAlready = true;
                    //let's check if it is part of a MultipleSignatureDataAggregator, if it is, delete it as well
                    MultipleObjectSignatureDataAggregator objectSignatureDataAggregator =
                            workflowProcessSignatureData.getMultipleObjectSignatureDataObject();
                    if (objectSignatureDataAggregator != null && !objectSignatureDataAggregator.hasSignature()) {
                        objectSignatureDataAggregator.deleteOrphanSignatureData();
                    }
                    workflowProcessSignatureData.deleteOrphanSignatureData();
                }
            }
        }
    }

}
