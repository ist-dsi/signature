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
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.signature.domain.SignatureIntention;
import module.signature.domain.SignatureIntentionMulti;
import module.signature.domain.SignatureQueue;
import module.signature.domain.SignatureSystem;
import module.signature.exception.SignatureExpiredException;
import module.signature.presentationTier.SignatureLayoutContext;
import module.signature.util.exporter.ExporterException;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.renderers.plugin.RenderersRequestProcessorImpl;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/signatureAction")
public class SignatureAction extends ContextBaseAction {

    private final boolean TOKENS_ACTIVE = true;

    public ActionForward createSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final SignatureIntention signIntention = getSignatureIntention(request);

	setContext(request, new SignatureLayoutContext());
	activateSignature(signIntention);

	if (SignatureSystem.hasQueue()) {

	    SignatureSystem.getInstance().getQueue().push(signIntention);

	    return forward(request, "/signature/confirmSignatureToQueue.jsp");
	}

	request.setAttribute("signIntention", signIntention);

	return forward(request, "/signature/createSignature.jsp");
    }

    @Service
    private void activateSignature(SignatureIntention signIntention) {
	signIntention.setActivated(true);
    }

    public ActionForward showQueue(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	if (SignatureSystem.hasQueue()) {
	    SignatureQueue queue = SignatureSystem.getInstance().getQueue();
	    if (queue.getSignatureIntentionsCount() > 0) {
		SignatureIntentionMulti signIntention = SignatureIntentionMulti.factory(queue);

		request.setAttribute("signIntention", signIntention);
	    }
	}

	return forward(request, "/signature/showQueue.jsp");
    }

    public ActionForward clean(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	for (SignatureIntention signature : SignatureSystem.getInstance().getSignatureIntentions()) {
	    signature.delete();
	}

	return forward(request, "/signature/showQueue.jsp");
    }

    // TODO remove this :)
    public ActionForward clearQueue(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	SignatureSystem.getInstance().clearQueue();

	request.setAttribute("signIntention", null);

	return forward(request, "/signature/showQueue.jsp");
    }

    public ActionForward checkSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final SignatureIntention signature = getSignatureIntention(request);

	SignatureSystem.validateSignature(signature);

	request.setAttribute("signIntention", signature);

	return forward(request, "/signature/viewSignature.jsp");
    }

    public ActionForward deleteSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final SignatureIntention signatureIntention = getSignatureIntention(request);

	request.setAttribute("signIntention", signatureIntention);

	signatureIntention.delete();

	return forward(request, "/signature/viewSignature.jsp");
    }

    public ActionForward getLogsToSign(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final SignatureIntention signature = getSignatureIntention(request);

	verifyTokenIn(signature, request);

	try {
	    String contentToSign = SignatureSystem.exportSignature(signature);

	    OutputStream outputStream = response.getOutputStream();
	    OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream, "UTF-8");

	    response.setContentType("text/plain");
	    response.setContentLength(contentToSign.getBytes().length);

	    streamWriter.write(contentToSign);
	    streamWriter.flush();
	    streamWriter.close();

	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ExporterException e) {
	    e.printStackTrace();
	} catch (NullPointerException ex) {
	    ex.printStackTrace();
	}

	return null;
    }

    public ActionForward receiveSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final SignatureIntention signIntention = getSignatureIntention(request);

	verifyTokenOut(signIntention, request);

	OutputStream outputStream;
	try {
	    // response stream
	    response.setContentType("text/plain");
	    outputStream = response.getOutputStream();
	    OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream, "UTF-8");

	    streamWriter.write("--- Receive Signature");

	    UploadedFile uploadedFile0 = RenderersRequestProcessorImpl.getUploadedFile("file0");
	    UploadedFile uploadedFile1 = RenderersRequestProcessorImpl.getUploadedFile("file1");

	    System.out.println("file0: ");
	    System.out.println("---" + new String(uploadedFile0.getFileData()) + "---");
	    System.out.println("file1: ");
	    System.out.println("---" + new String(uploadedFile1.getFileData()) + "---");

	    signIntention.seal(uploadedFile0, uploadedFile1);

	    streamWriter.write("--- Finish Signature");

	    streamWriter.flush();
	    streamWriter.close();

	} catch (IOException e1) {
	    e1.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return null;
    }

    protected SignatureQueue getSignatureQueue(HttpServletRequest request) {
	return getDomainObject(request, "OID");
    }

    protected SignatureIntention getSignatureIntention(HttpServletRequest request) {
	return getDomainObject(request, "objectId");
    }

    protected void verifyTokenIn(SignatureIntention signature, HttpServletRequest request) {
	if (!TOKENS_ACTIVE) {
	    return;
	}

	String tokenIn = request.getParameter("token");
	try {
	    signature.verifyTokenIn(tokenIn);
	} catch (SignatureExpiredException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    protected void verifyTokenOut(SignatureIntention signature, HttpServletRequest request) {
	if (!TOKENS_ACTIVE) {
	    return;
	}

	String tokenOut = request.getParameter("token");
	try {
	    signature.verifyTokenOut(tokenOut);
	} catch (SignatureExpiredException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
