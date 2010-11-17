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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.signature.domain.SignatureIntention;
import module.signature.domain.SignatureQueue;
import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.MyOrg;
import myorg.domain.User;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;

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

    public ActionForward history(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	List<SignatureIntention> pending = new ArrayList<SignatureIntention>();
	List<SignatureIntention> sealed = new ArrayList<SignatureIntention>();

	for (SignatureIntention signature : UserView.getCurrentUser().getSignatureIntentions()) {
	    if (signature.isSealed()) {
		sealed.add(signature);
	    } else {
		pending.add(signature);
	    }
	}

	request.setAttribute("pending", pending);
	request.setAttribute("sealed", sealed);

	return forward(request, "/signature/history.jsp");
    }

    public ActionForward viewSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final SignatureIntention signatureIntention = getSignatureIntention2(request);

	request.setAttribute("signIntention", signatureIntention);

	return forward(request, "/signature/viewSignature.jsp");
    }

    public ActionForward createSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final SignatureIntention signIntention = getSignatureIntention2(request);

	request.setAttribute("signIntention", signIntention);

	return forward(request, "/signature/createSignature.jsp");
    }
}
