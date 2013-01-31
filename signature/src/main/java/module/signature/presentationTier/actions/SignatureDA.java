/*
 * @(#)SignatureDA.java
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.signature.domain.Signature;
import module.signature.domain.SignatureFile;
import module.signature.domain.data.MultipleObjectSignatureDataAggregator;
import module.signature.domain.data.ObjectSignatureData;
import module.signature.domain.data.SignatureData;
import module.signature.domain.data.SignatureFormat;
import module.signature.exception.SignatureException;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.bennu.core.presentationTier.actions.ContextBaseAction;
import pt.ist.bennu.core.util.BundleUtil;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

@Mapping(path = "/signature")
/**
 * 
 * @author Diogo Figueiredo
 * @author João Antunes
 * @author Luis Cruz
 * 
 */
public class SignatureDA extends ContextBaseAction {

	//    protected SignatureQueue getSignatureQueue(HttpServletRequest request) {
	//	return getDomainObject(request, "OID");
	//    }

	//    protected SignatureIntention getSignatureIntention(HttpServletRequest request) {
	//	return getDomainObject(request, "objectId");
	//    }

	protected SignatureData getSignatureDataFromRequest(HttpServletRequest request) {
		return getDomainObject(request, "signatureDataOID");
	}

	protected Signature getSignatureFromRequest(HttpServletRequest request) {
		return getDomainObject(request, "signatureOID");
	}

	//    public ActionForward configure(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	//	    final HttpServletResponse response) {
	//
	//	List<SignatureQueue> queues = new ArrayList<SignatureQueue>();
	//
	//	for (User u : MyOrg.getInstance().getUser()) {
	//	    System.out.println(u.getUsername());
	//	    if (u.hasSignatureQueue()) {
	//		queues.add(u.getSignatureQueue());
	//	    }
	//	}
	//
	//	request.setAttribute("queues", queues);
	//
	//	return forward(request, "/signature/configure.jsp");
	//    }

	public ActionForward concludedSignatures(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response) {

		List<SignatureData> signedSignatureDatas = new ArrayList<SignatureData>();
		for (Signature signature : UserView.getCurrentUser().getSignedSignatures()) {
			SignatureData signatureData = signature.getSignatureData();
			if (signatureData instanceof MultipleObjectSignatureDataAggregator) {
				for (SignatureData signatureDataAggregated : ((MultipleObjectSignatureDataAggregator) signatureData)
						.getSignatureObjects()) {
					signedSignatureDatas.add(signatureDataAggregated);
				}
			} else {
				signedSignatureDatas.add(signatureData);
			}
		}

		request.setAttribute("signedSignatureDatas", signedSignatureDatas);

		return forward(request, "/signature/signedSignatures.jsp");
	}

	public ActionForward pendingSignatures(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) {

		String filterBy = (String) getAttribute(request, "filterBy");
		if (filterBy == null) {
			filterBy = "";
		}

		HashMap<String, String> filters = new HashMap<String, String>();
		List<SignatureData> pending = new ArrayList<SignatureData>();

		//let's filter the signatures to be put into the pending list depending on the received filters and create the filters

		for (SignatureData signatureData : UserView.getCurrentUser().getPendingSignatures()) {
			if (!(signatureData instanceof MultipleObjectSignatureDataAggregator)) {
				Class<? extends SignatureData> signatureClass = signatureData.getClass();
				filters.put(signatureClass.getName(), BundleUtil.getLocalizedNamedFroClass(signatureClass));
				try {
					if (filterBy.equals("") || signatureClass.isAssignableFrom(Class.forName(filterBy))) {
						pending.add(signatureData);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		request.setAttribute("pending", pending);
		request.setAttribute("filters", filters);
		request.setAttribute("filterBy", filterBy);

		return forward(request, "/signature/pendingSignatures.jsp");
	}

	/*
	 * public ActionForward viewSignature(final ActionMapping mapping, final
	 * ActionForm form, final HttpServletRequest request, final
	 * HttpServletResponse response) {
	 * 
	 * final SignatureIntention signatureIntention =
	 * getSignatureDataFromRequest(request);
	 * 
	 * request.setAttribute("signIntention", signatureIntention);
	 * 
	 * return forward(request, "/signature/viewSignature.jsp"); }
	 */
	public ActionForward multiSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) {
		final String[] signatureIds = request.getParameterValues("signatureIds");

		if (signatureIds == null) {
			return pendingSignatures(mapping, form, request, response);
		}
		MultipleObjectSignatureDataAggregator multipleObjectSignatureDataAggregator = null;
		try {
			//extracting the ids from the request
			final List<ObjectSignatureData> signatures = new ArrayList<ObjectSignatureData>();
			for (final String signatureId : signatureIds) {
				final SignatureData signatureData = AbstractDomainObject.fromExternalId(signatureId);
				signatures.add((ObjectSignatureData) signatureData);
			}
			if (signatures.size() == 1) {
				//if we only have one, let's invoke the process of creating just one signature
				return createSignature(request, signatures.get(0));
			}
			multipleObjectSignatureDataAggregator =
					MultipleObjectSignatureDataAggregator.getOrCreateAggregatorInstance(signatures);
		} catch (DomainException e) {
			addLocalizedMessage(request, e.getLocalizedMessage());
			RenderUtils.invalidateViewState();
			return pendingSignatures(mapping, form, request, response);
		}

		return createMultipleSignature(request, multipleObjectSignatureDataAggregator);
	}

	private ActionForward createMultipleSignature(HttpServletRequest request,
			MultipleObjectSignatureDataAggregator multipleObjectSignatureDataAggregator) {
		if (multipleObjectSignatureDataAggregator == null) {
			throw new DomainException("error.multiple.signature.found.empty");
		}
		request.setAttribute("signData", multipleObjectSignatureDataAggregator);

		//let's make sure before forwarding the user to the createSignature, that he can see this content;

		return forward(request, "/signature/createSignature.jsp");
	}

	public ActionForward viewSignatureContent(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response) {

		final SignatureData signatureData = getSignatureDataFromRequest(request);
		try {
			response.getOutputStream().write(signatureData.getBrowserRenderableContentToSign());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ActionForward downloadSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) {

		final SignatureData signatureData = getSignatureDataFromRequest(request);
		try {
			//let's get the file
			Signature signature = signatureData.getSignature();
			SignatureFile signatureFile = signature.getPersistedSignature();
			return download(response, signatureFile.getFilename(), signatureFile.getStream(), signatureFile.getContentType());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	//    public ActionForward viewSignaturePdf(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	//	    final HttpServletResponse response) {
	//
	//	final SignatureIntention signatureIntention = getSignatureIntention2(request);
	//
	//	try {
	//	    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	//	    Document doc = builder.parse(new StringBufferInputStream(signatureIntention.getContent()));
	//
	//	    response.getOutputStream().write("dfd".getBytes());
	//	} catch (IOException e) {
	//	    e.printStackTrace();
	//	} catch (SAXException e) {
	//	    e.printStackTrace();
	//	} catch (ParserConfigurationException e) {
	//	    e.printStackTrace();
	//	}
	//
	//	return null;
	//    }

	public ActionForward createSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) {

		final SignatureData signIntention = getSignatureDataFromRequest(request);

		return createSignature(request, signIntention);
	}

	public ActionForward createSignature(final HttpServletRequest request, final SignatureData signIntention) {

		request.setAttribute("signData", signIntention);

		return forward(request, "/signature/createSignature.jsp");
	}

	public ActionForward submitSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) {

		//let's process the signature. TODO FENIX-188
		SignatureData signatureData = getRenderedObject("signatureData");

		Set<User> permittedUsers = Collections.singleton(UserView.getCurrentUser());
		User currentUser = UserView.getCurrentUser();

		//validate the signatureData
		try {
			if (signatureData == null) {
				throw new SignatureException("could.not.find.content");
			}

			signatureData.generateSignature(SignatureFormat.XAdEST, currentUser, permittedUsers);
		} catch (DomainException domainEx) {
			addLocalizedMessage(request, domainEx.getLocalizedMessage());
			RenderUtils.invalidateViewState();
			if (signatureData == null) {
				return pendingSignatures(mapping, form, request, response);
			}
			return createSignature(request, signatureData);
		}
		addLocalizedSuccessMessage(request, "signature.success");
		return pendingSignatures(mapping, form, request, response);

	}

}
