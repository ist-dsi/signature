/*
 * @(#)GenericSourceOfInfoForSignatureDataBean.java
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

import java.io.Serializable;

import javax.annotation.PostConstruct;

import module.signature.domain.data.interfaces.ConvertibleToXMLAndXHTML;
import module.signature.domain.data.interfaces.SignatureDataSourceOfInfoInterface;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixWebFramework.services.Service;

/**
 * This is a generic bean that should be extended, and which is used as
 * intermediary to retrieve the content to be signed
 * 
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
/**
 * 
 * @author João Antunes
 * 
 */
public abstract class GenericSourceOfInfoForSignatureDataBean implements Serializable, SignatureDataSourceOfInfoInterface,
		ConvertibleToXMLAndXHTML {
	/**
	 * Version 1 of this bean. To note, changes on this class should be
	 * retro-compatible with the previous under penalty of finding unexisting
	 * discrepancies with currently signed objects
	 */
	private static final long serialVersionUID = 1L;
	@XStreamAsAttribute
	private String intention;
	@XStreamAsAttribute
	private String description;

	@XStreamAsAttribute
	private String signatureId;

	private final SignatureData signatureData;

	public GenericSourceOfInfoForSignatureDataBean(SignatureData signatureData) {
		this.signatureData = signatureData;
	}

	@Service
	@PostConstruct
	public void generateDescriptionIntentionAndIdStrings() {
		this.description = generateDescriptionString();
		this.intention = generateIntentionString();
		this.signatureId = generateSignatureId();

	}

	public String getSignatureId() {
		return signatureId;
	}

	public abstract String generateIntentionString();

	public abstract String generateDescriptionString();

	public abstract String generateSignatureId();

	public String getDescription() {
		return description;
	}

	public String getIntention() {
		return intention;
	}

	@Override
	public SignatureData getSignatureData() {
		return signatureData;
	}

	/**
	 * 
	 * Method used to put "-" instead of "" for fields that are blank
	 * 
	 * @param originalString
	 * @return "-" if the originalString is blank/empty or originalString
	 *         otherwise
	 */
	public static String signalBlankString(String originalString) {
		if (originalString == null || StringUtils.isBlank(originalString)) {
			return "-";
		} else {
			return originalString;
		}

	}

}
