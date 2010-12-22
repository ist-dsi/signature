/*
 * @(#)SignatureAction.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: Diogo Figueiredo
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package module.signature.presentationTier.actions;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import module.signature.domain.SignatureIntention;
import module.signature.domain.SignatureIntentionMulti;
import module.signature.domain.SignatureQueue;
import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.MyOrg;
import myorg.domain.User;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

@Mapping(path = "/signature")
public class SignatureDA extends ContextBaseAction {

    protected SignatureQueue getSignatureQueue(HttpServletRequest request) {
	return getDomainObject(request, "OID");
    }

    protected SignatureIntention getSignatureIntention(HttpServletRequest request) {
	return getDomainObject(request, "objectId");
    }

    protected SignatureIntention getSignatureIntention2(HttpServletRequest request) {
	return getDomainObject(request, "OID");
    }

    public ActionForward configure(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	List<SignatureQueue> queues = new ArrayList<SignatureQueue>();

	for (User u : MyOrg.getInstance().getUser()) {
	    System.out.println(u.getUsername());
	    if (u.hasSignatureQueue()) {
		queues.add(u.getSignatureQueue());
	    }
	}

	request.setAttribute("queues", queues);

	return forward(request, "/signature/configure.jsp");
    }

    public ActionForward sealed(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	List<SignatureIntention> sealed = new ArrayList<SignatureIntention>();

	for (SignatureIntention signature : UserView.getCurrentUser().getSignatureIntentions()) {
	    if (signature.isSealed()) {
		sealed.add(signature);
	    }
	}

	request.setAttribute("sealed", sealed);

	return forward(request, "/signature/sealed.jsp");
    }

    public ActionForward history(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	String filterBy = (String) getAttribute(request, "filterBy");
	if (filterBy == null) {
	    filterBy = "";
	}

	HashMap<String, String> filters = new HashMap<String, String>();
	List<SignatureIntention> pending = new ArrayList<SignatureIntention>();

	for (SignatureIntention signature : UserView.getCurrentUser().getSignatureIntentions()) {
	    if (!signature.isSealed() && !(signature instanceof SignatureIntentionMulti)) {
		filters.put(signature.getClass().getName(), signature.getType());
		try {
		    if (filterBy.equals("") || signature.getClass().isAssignableFrom(Class.forName(filterBy))) {
			pending.add(signature);
		    }
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		}
	    }
	}

	request.setAttribute("pending", pending);
	request.setAttribute("filters", filters);
	request.setAttribute("filterBy", filterBy);

	return forward(request, "/signature/history.jsp");
    }

    public ActionForward viewSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final SignatureIntention signatureIntention = getSignatureIntention2(request);

	request.setAttribute("signIntention", signatureIntention);

	return forward(request, "/signature/viewSignature.jsp");
    }

    public ActionForward multiSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {
	final String[] signatureIds = request.getParameterValues("signatureIds");

	if (signatureIds == null) {
	    return history(mapping, form, request, response);
	}

	final Set<SignatureIntention> signatures = new HashSet<SignatureIntention>();
	for (final String signatureId : signatureIds) {
	    final SignatureIntention signatureIntention = AbstractDomainObject.fromExternalId(signatureId);
	    signatures.add(signatureIntention);
	}

	SignatureIntention multi = SignatureIntentionMulti.factory(signatures);

	return createSignature(request, multi);
    }

    public ActionForward viewSignatureContent(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) {

	final SignatureIntention signatureIntention = getSignatureIntention2(request);

	try {
	    response.getOutputStream().write(signatureIntention.getContent().getBytes());
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return null;
    }

    public ActionForward viewSignaturePdf(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final SignatureIntention signatureIntention = getSignatureIntention2(request);

	try {
	    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    Document doc = builder.parse(new StringBufferInputStream(signatureIntention.getContent()));

	    response.getOutputStream().write("dfd".getBytes());
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (SAXException e) {
	    e.printStackTrace();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}

	return null;
    }

    public ActionForward createSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final SignatureIntention signIntention = getSignatureIntention2(request);

	return createSignature(request, signIntention);
    }

    public ActionForward createSignature(final HttpServletRequest request, final SignatureIntention signIntention) {

	request.setAttribute("signIntention", signIntention);

	return forward(request, "/signature/createSignature.jsp");
    }

}
