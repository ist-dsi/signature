/*
 * @(#)ObjectSignatureData.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: Diogo Figueiredo
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Digital Signature Module.
 *
 *   The Digital Signature Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Signature Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Signature Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.signature.domain.data;

import java.util.Collections;

import module.signature.domain.Signature;
import module.signature.domain.SignatureDataFile;
import module.signature.exception.SignatureDataException;

import org.joda.time.DateTime;

import pt.ist.bennu.core.applicationTier.Authenticate.UserView;

/**
 * 
 * @author João Antunes
 * 
 */
public abstract class ObjectSignatureData extends ObjectSignatureData_Base {

    protected ObjectSignatureData() {
        super();
    }

    @Override
    final public Signature getSignature() {
        Signature sigToReturn = super.getSignature();
        if (sigToReturn != null) {
            return sigToReturn;
        }
        if (hasMultipleObjectSignatureDataObject()) {
            return getMultipleObjectSignatureDataObject().getSignature();
        } else {
            return super.getSignature();
        }
    }

    @Override
    final public boolean hasSignature() {
        if (getSignature() != null) {
            return true;
        }
        return false;
    }

    @Override
    public final void generateAndPersistSignatureDataDocument() {
        if (hasSignature()) {
            throw new SignatureDataException("error.cant.update.original.content.of.already.signed.document");
        }
        //if we already have some content, let's remove the previous.
        if (super.getOriginalSignatureDataFile() != null) {
            SignatureDataFile signatureFile = getOriginalSignatureDataFile();
            removeSignatureDataOriginalContentFile();
            signatureFile.delete();

        }
        fillSourceOfInfoBean(new DateTime(), null);
        SignatureDataFile signatureFileToReturn = new SignatureDataFile();
        SignatureDataFormat signatureDataFormat = getSignatureDataFormat();
        signatureDataFormat.setDataSourceObject(getGenericSourceOfInfoForSignatureDataBean());
        signatureFileToReturn.setContent(signatureDataFormat.getSignatureDataContent());
        signatureFileToReturn.setContentType(signatureDataFormat.getContentType());
        signatureFileToReturn.setFilename(getGenericSourceOfInfoForSignatureDataBean().getSignatureId() + "_ORIGINAL."
                + signatureDataFormat.getFileNameExtension());
        this.setSignatureDataOriginalContentFile(signatureFileToReturn);

        return;
    }

    @Override
    public final byte[] getContentToSign() {
        //we should already have the content associated with this SignatureData on the
        //SignatureFile which is associated with it.

        //if this is a pending signature, it will return the already generated content.
        validateAccessToSignatureData(Collections.singleton(UserView.getCurrentUser()));
        if (hasSignatureDataOriginalContentFile()) {
            return getOriginalSignatureDataContent();
        }
        generateAndPersistSignatureDataDocument();
        return getOriginalSignatureDataContent();
    }

    /**
     * Method used to fill the Bean that will be used to generate the document
     * to be signed;
     * 
     * @param instant
     *            a {@link DateTime} with the instant to which the document
     *            should be generated
     * @param signatureId
     *            the {@link SignatureData#getSignatureId()} id that should be
     *            unique, or null to generate a new id
     */
    public abstract void fillSourceOfInfoBean(DateTime instant, String signatureId);

}
