/*
 * @(#)SignatureAction.java
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
package module.signature.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.signature.domain.data.SignatureData;
import pt.ist.bennu.core.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;

/**
 * 
 * @author Diogo Figueiredo
 * @author João Antunes
 * @author Luis Cruz
 * 
 */
@Mapping(path = "/signatureAction")
public class SignatureAction extends ContextBaseAction {

    public ActionForward getSignatureContent(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response)
    {
	
	//	final SignatureData signatureDataObject = getSignatureDataObject(request);
	//	//TODO in the future it might be a good idea to transmit the content XORed against some password or something to add an extra layer of sec.
	//	//and it might make sense to make that encryption at the signaturedataformat class level and not here
	//	
	//		byte[] signatureDataContent = signatureDataObject.getContentToSign();
	//	
	//	try {
	//	    OutputStream outputStream = response.getOutputStream();
	//	    response.setContentType("text/plain");
	//	    response.setContentLength(signatureDataContent.length);
	//	    
	//	    outputStream.write(signatureDataContent);
	//	    outputStream.flush();
	//	    outputStream.close();
	//	    
	//	} catch (IOException e) {
	//	    e.printStackTrace();
	//	    throw new DomainException();
	//	}
	//	    
	return null;
	//	
	//	
    }

    private SignatureData getSignatureDataObject(HttpServletRequest request) {
	return getDomainObject(request, "contentId");
    }

}
