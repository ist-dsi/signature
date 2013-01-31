/*
 * @(#)MultipleObjectSignatureDataAggregator.java
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.util.i18n.Language;

/**
 * 
 * This class is used to make one signature of an aggregation of signatures each
 * that can reference a domain object.
 * 
 * @author João Antunes
 * 
 */
public class MultipleObjectSignatureDataAggregator extends MultipleObjectSignatureDataAggregator_Base {

	@Override
	/**
	 * Overriding the method to be more strict. The MultipleObjectSignatureDataAggregator must not exist as a pendingSignature
	 * but it must have a valid Signature associated
	 * @see module.signature.domain.data.SignatureData#checkIsPendingOrHasSignature()
	 */
	public boolean checkConsistency() {
		return (hasAnySignatureObjects());
	}

	/**
	 * This constructor is private to make sure that it isn't misused
	 */
	private MultipleObjectSignatureDataAggregator() {
		super();
		//generically we are making this a XML and XHTML object aggregator

	}

	public void init(List<ObjectSignatureData> signatureDataList) {
		if (signatureDataList.size() < 1) {
			throw new DomainException("invalid.use.of.MultipleObjectSignatureData", getBundle());
		}
		Class firstObjectClass = signatureDataList.get(0).getClass();
		for (ObjectSignatureData objectSignatureData : signatureDataList) {
			//making sure all of the object are from the same class, thus making sure we do not have a signature which has very distinct kind of processes associated
			if (!firstObjectClass.isInstance(objectSignatureData)) {
				throw new DomainException(
						"invalid.use.of.MultipleObjectSignatureData.only.objects.of.the.same.class.are.allowed", getBundle());
			}
			//let's also check to make sure that none of the given ObjectSignatureData objects given has already an associated Signature
			if (objectSignatureData.hasSignature()) {
				throw new DomainException(
						"invalid.use.of.MultipleObjectSignatureData.only.objects.without.existing.signature.are.allowed",
						getBundle());
			}

			addSignatureObjects(objectSignatureData);
		}
		//let's populate the info of this signature data
		fillInnerBeans();
		super.init(SignatureDataFormat.AGGREGATION_OF_XML_XHTML_AND_XSLT, null);
	}

	static public ResourceBundle getBundle() {
		return ResourceBundle.getBundle("resources/SignatureResources", Language.getLocale());
	}

	protected MultipleObjectSignatureDataAggregator(List<ObjectSignatureData> signatureDataList) {
		this();
		init(signatureDataList);

	}

	@Override
	@Deprecated
	public byte[] getBrowserRenderableContentToSign() {
		/*
		 * not sure about what should happen here, and if we need to implement
		 * this method as this is an aggregator, and it should not be needed to
		 * be shown in the interfaces
		 */
		return null;
	}

	public void fillInnerBeans() {
		ArrayList<GenericSourceOfInfoForSignatureDataBean> listOfBeans = new ArrayList<GenericSourceOfInfoForSignatureDataBean>();
		for (ObjectSignatureData objectSignatureData : getSignatureObjects()) {
			//let's fill each bean and retrieve it
			objectSignatureData.fillSourceOfInfoBean(objectSignatureData.getCreationDateTime(),
					objectSignatureData.getSignatureId());
			listOfBeans.add(objectSignatureData.getGenericSourceOfInfoForSignatureDataBean());
		}
		attachGenericSourceOfInfoForSignatureDataBean(new MultipleGenericSourceOfInfoAggregationBean(getSignatureObjects(), this));
	}

	@Override
	public void validateAccessToSignatureData(Set<User> usersRequestingSignatureData) {
		for (SignatureData signatureData : getSignatureObjects()) {
			signatureData.validateAccessToSignatureData(usersRequestingSignatureData);
		}

	}

	@Override
	protected void preSignatureGenerationMethod() {
		//let's execute all of the preSignatureGenerationMethod of all it's constituents
		for (SignatureData signatureData : getSignatureObjects()) {
			signatureData.preSignatureGenerationMethod();
		}
	}

	@Override
	protected void postSignatureGenerationMethod() {
		//let's execute all of the postSignatureGenerationMethod of all it's constituents
		for (SignatureData signatureData : getSignatureObjects()) {
			signatureData.postSignatureGenerationMethod();
		}
	}

	/**
	 * NOTE: It deletes any previous MultipleObjectSignatureDataAggregator
	 * instances which don't have the exact list of SignatureData objects.
	 * 
	 * @param signatures
	 *            the SignatureData objects that are aggregated with the use of
	 *            this instance of MultipleSignatureData
	 * @return a {@link MultipleObjectSignatureDataAggregator} object reused (if
	 *         one with the exact same SignatureData was found, or a newly
	 *         created with the given SignatureData objects)
	 */
	@Service
	public static MultipleObjectSignatureDataAggregator getOrCreateAggregatorInstance(List<ObjectSignatureData> signatures) {
		Set<MultipleObjectSignatureDataAggregator> possiblyDataAggregatorsToBeDeleted =
				new HashSet<MultipleObjectSignatureDataAggregator>();

		boolean createANewOneAndDeleteTheRest = false;

		MultipleObjectSignatureDataAggregator signatureDataAggregatorToReturn = null;

		for (ObjectSignatureData objectSignatureData : signatures) {
			if (objectSignatureData.getMultipleObjectSignatureDataObject() == null) {
				//ok, for now on we now that we have to create a new one and delete the rest
				createANewOneAndDeleteTheRest = true;
			} else {
				List<ObjectSignatureData> objectSignatureDatas =
						objectSignatureData.getMultipleObjectSignatureDataObject().getSignatureObjects();
				if (signatures.containsAll(objectSignatureDatas) && objectSignatureDatas.size() == signatures.size()) {
					//we found something we can reuse!
					signatureDataAggregatorToReturn = objectSignatureData.getMultipleObjectSignatureDataObject();
					break;
				}
				//if we got here, that means that we should delete what we found and keep track of it
				createANewOneAndDeleteTheRest = true;
				possiblyDataAggregatorsToBeDeleted.add(objectSignatureData.getMultipleObjectSignatureDataObject());
			}
		}

		if (!createANewOneAndDeleteTheRest) {
			return signatureDataAggregatorToReturn;
		}
		for (MultipleObjectSignatureDataAggregator dataAggregatorToDelete : possiblyDataAggregatorsToBeDeleted) {
			dataAggregatorToDelete.deleteUnusedAggregator(null);
		}
		return new MultipleObjectSignatureDataAggregator(signatures);
	}

	/**
	 * @param sigDataThatObsoletedThisAggregator
	 *            the SignatureData that obsolted this instance. This means that
	 *            the signature of that individual SignatureData instance is
	 *            what made this MultipleSignature obsolete, because we cannot
	 *            reuse it anymore (as any SignatureData object must have only
	 *            one Signature object) Deletes this MultipleObjectSignatureData
	 *            if it is unused
	 */
	protected void deleteUnusedAggregator(SignatureData sigDataThatObsoletedThisAggregator) {
		if (hasSignature()) {
			throw new DomainException(
					"trying.to.delete.a.multiple.signature.data.aggregator.which.already.has.a.signature.associated");
		}
		super.deleteOriginalSignatureDataFile();
		for (ObjectSignatureData signatureData : getSignatureObjects()) {
			if (sigDataThatObsoletedThisAggregator != null && !signatureData.equals(sigDataThatObsoletedThisAggregator)) {
				if (signatureData.hasSignature()) {
					throw new DomainException(
							"illegal.state.trying.to.delete.aggregator.with.containing.signature.data.with.signature");
				}
			}
			removeSignatureObjects(signatureData);
		}
		removeUserToSignPendingSignature();
		deleteDomainObject();
	}

	@Override
	@Service
	public void deleteOrphanSignatureData() {
		deleteUnusedAggregator(null);
	}

	@Override
	public void fillSourceOfInfoBean(DateTime instant, String signatureId) {
		//the arguments are irrelevant here, as they use the instants found inside
		fillInnerBeans();
	}

}
