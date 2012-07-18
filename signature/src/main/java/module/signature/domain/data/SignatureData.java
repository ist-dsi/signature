/*
 * @(#)SignatureData.java
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

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import jvstm.cps.ConsistencyPredicate;
import module.signature.domain.Signature;
import module.signature.domain.SignatureDataFile;
import module.signature.exception.SignatureException;
import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.bennu.core.util.Base64;

import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.services.Service;

/**
 * 
 * @author João Antunes
 * 
 */
public abstract class SignatureData extends SignatureData_Base {

    private String auxiliarySignatureContentString;

    /**
     * The SignatureData must always have a relation with the user, sinalizing
     * it is a pending signature, or it has a valid, finished Signature object
     * associated
     * 
     * @return this method does not actually check for the validity of the
     *         Signature object, but returns true case the conditions above are
     *         respected, or false otherwise
     */
    @ConsistencyPredicate
    public boolean checkConsistency() {
	//	return ((hasSignature() && !hasUserToSignPendingSignature()) || (hasUserToSignPendingSignature() && !hasSignature()));
	return true;
    }

    public SignatureData() {
	super();
    }

    public void init(SignatureDataFormat signatureDataFormat, User pendingUser) {
	setSignatureDataFormat(signatureDataFormat);
	setCreationDateTime(new DateTime());
	generateAndPersistSignatureDataDocument();
	setSignatureId(getGenericSourceOfInfoForSignatureDataBean().getSignatureId());
	setSignatureDescription(getGenericSourceOfInfoForSignatureDataBean().getDescription());
	setSignatureIntention(getGenericSourceOfInfoForSignatureDataBean().getIntention());
	setUserToSignPendingSignature(pendingUser);
    }

    private GenericSourceOfInfoForSignatureDataBean genericSourceOfInfoBean;

    public GenericSourceOfInfoForSignatureDataBean getGenericSourceOfInfoForSignatureDataBean() {
	return genericSourceOfInfoBean;
    }

    public String getOriginalSignatureDataContentType() {
	return super.getSignatureDataOriginalContentFile().getContentType();
    }

    protected SignatureDataFile getOriginalSignatureDataFile() {
	return super.getSignatureDataOriginalContentFile();
    }

    @Override
    @Deprecated
    public SignatureDataFile getSignatureDataOriginalContentFile() {
	throw new DomainException("dont.use.directly.this.method.outside.of.this.class");
    }

    @Override
    public boolean hasSignatureDataOriginalContentFile() {
	return getOriginalSignatureDataFile() != null;
    }

    /**
     * It deletes the document to be signed, if and only if, we have no
     * signature associated with it
     */
    protected void deleteOriginalSignatureDataFile() {
	if (hasSignature())
	    throw new DomainException("cant.delete.signature.document.if.it.has.an.associated.signature");
	SignatureDataFile signatureDataFile = super.getSignatureDataOriginalContentFile();
	removeSignatureDataOriginalContentFile();
	signatureDataFile.delete();

    }

    public byte[] getOriginalSignatureDataContent() {
	return super.getSignatureDataOriginalContentFile().getContent();
    }

    abstract public byte[] getContentToSign();

    final public String getBase64EncodedContentToSign() {
	return Base64.encodeBytes(getContentToSign());
    }

    /**
     * @return the byte content that is sent as a response to the ajax request
     *         which is made for the server. Usually this should return the
     *         bytes of an HTML, although, I guess one may adapt things to be
     *         able to return a pdf or something of the like which the browser
     *         can render
     */
    abstract public byte[] getBrowserRenderableContentToSign();

    /**
     * establishes a bidirectional realtion between this object and the
     * {@link #genericSourceOfInfoBean}. And extracts its intention and
     * description
     * 
     * @param dataBean
     *            the {@link GenericSourceOfInfoForSignatureDataBean} to attach
     *            with this signature
     */
    public void attachGenericSourceOfInfoForSignatureDataBean(GenericSourceOfInfoForSignatureDataBean dataBean) {
	this.genericSourceOfInfoBean = dataBean;
	setSignatureDescription(this.genericSourceOfInfoBean.getDescription());
	setSignatureIntention(this.genericSourceOfInfoBean.getIntention());

    }

    abstract public void validateAccessToSignatureData(Set<User> usersRequestingSignatureData);

    /**
     * 
     * This method basicly does all that is needed to generate a Signature,
     * specifically the following is done: - Validates the received signature by
     * also comparing the original and the received document's content; -
     * Executes preSignatureGenerationMethod(); - Validates the format of the
     * XaDES and creates the signature object; - Executes the
     * postSignatureGenerationMethod(); - If this is not an aggregator, it
     * checks to see if it has an {@link MultipleObjectSignatureDataAggregator}
     * associated, if it does, removes it
     * 
     * @param signatureFormat
     *            the {@link SignatureFormat} to use
     * @param signer
     *            the {@link User} that signed the document
     * @param permittedUsers
     *            the list of users that can sign this Data
     * @return A Signature instance that represents the signature
     */
    @Service
    public final Signature generateSignature(SignatureFormat signatureFormat, User signer, Set<User> permittedUsers) {
	getSignatureDataFormat().validateSignature(this, permittedUsers, null, true);
	preSignatureGenerationMethod();
	try {
	    Signature sigToReturn = new Signature(signatureFormat, Base64.decode(getAuxiliarySignatureContentString()), this,
		    signer);
	    //if this is a MultipleObjectSignatureDataAggregator thus an aggregation, let's remove the pending from the
	    //composing objects (já fazia uma refactorização e um override deste método no MultipleSignautreData...)
	    if (this instanceof MultipleObjectSignatureDataAggregator) {
		for (ObjectSignatureData sigData : ((MultipleObjectSignatureDataAggregator) this).getSignatureObjects()) {
		    sigData.removeUserToSignPendingSignature();
		}
	    }
	    postSignatureGenerationMethod();
	    //let's clean the eventual MultipleObjectSignatureDataAggregator that might have been associated with this object
	    if (this instanceof ObjectSignatureData && !(this instanceof MultipleObjectSignatureDataAggregator)) {
		MultipleObjectSignatureDataAggregator aggregator = ((ObjectSignatureData) this)
			.getMultipleObjectSignatureDataObject();
		if (aggregator != null) {
		    aggregator.deleteUnusedAggregator(this);
		}
	    }
	    return sigToReturn;
	} catch (IOException e) {
	    throw new SignatureException("io.exception.ocurred", e);
	}

    }

    public byte[] getSentContentToBeSigned() {
	validateAccessToSignatureData(Collections.singleton(UserView.getCurrentUser()));
	return getOriginalSignatureDataContent();
    }

    public String getAuxiliarySignatureContentString() {
	return auxiliarySignatureContentString;
    }

    public void setAuxiliarySignatureContentString(String auxiliarySignatureContentString) {
	this.auxiliarySignatureContentString = auxiliarySignatureContentString;
    }

    protected abstract void preSignatureGenerationMethod();

    protected abstract void postSignatureGenerationMethod();

    public abstract void generateAndPersistSignatureDataDocument();

    public abstract void deleteOrphanSignatureData();

}
