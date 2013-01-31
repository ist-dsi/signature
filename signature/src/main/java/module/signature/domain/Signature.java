/*
 * @(#)Signature.java
 *
 * Copyright 2010 Instituto Superior Tecnico
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
package module.signature.domain;

import module.signature.domain.data.SignatureData;
import module.signature.domain.data.SignatureFormat;
import module.signature.exception.SignatureException;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.User;

/**
 * 
 * @author Diogo Figueiredo
 * @author João Antunes
 * 
 */
public class Signature extends Signature_Base {

	public Signature() {
		super();
	}

	/**
	 * 
	 * @param signatureFormat
	 * @param signatureContent
	 *            the content of the signature
	 * @throws SignatureException
	 *             if the signature wasn't valid or if something else went wrong
	 */
	public Signature(SignatureFormat signatureFormat, byte[] signatureContent, SignatureData signatureData, User userWhoSigned)
			throws SignatureException {
		if (!signatureFormat.validateSignature(signatureContent)) {
			throw new SignatureException("could.not.create.signature.as.its.invalid");
		}
		setSignatureFormat(signatureFormat);
		setSignatureData(signatureData);
		setSignedUser(userWhoSigned);
		SignatureFile signatureFile = new SignatureFile();
		signatureFile.setContent(signatureContent);
		signatureFile.setContentType(signatureFormat.getContentType());
		signatureFile.setFilename(StringUtils.replaceChars(signatureData.getSignatureId(), "\\/", "-") + ".xades.xml");
		signatureFile.setDisplayName("Ficheiro de assinatura: " + getSignatureData().getSignatureDescription());

		setPersistedSignature(signatureFile);
		setCreatedDateTime(new DateTime());
		//remove it as a pending signature
		signatureData.removeUserToSignPendingSignature();

	}

	public byte[] getContent() {
		return getPersistedSignature().getContent();
	}

	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

}
