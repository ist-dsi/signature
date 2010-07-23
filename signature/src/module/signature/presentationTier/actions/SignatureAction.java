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

import module.signature.domain.Queue;
import module.signature.domain.SignatureIntention;
import module.signature.util.Signable;
import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.User;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.renderers.plugin.RenderersRequestProcessorImpl;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/signatureAction")
public class SignatureAction<T extends Signable> extends ContextBaseAction {

    boolean QUEUE_ACTIVE = true;

    public ActionForward createSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final SignatureIntention<T> signIntention = getSignatureIntention(request);

	if (QUEUE_ACTIVE) {

	    User user = UserView.getCurrentUser();
	    Queue queue = user.getQueue();

	    queue.push(signIntention);

	    System.out.println("Queue: " + user.getQueue().getSignatureIntentionsCount() + " items");

	    return forward(request, "/signature/confirmSignatureToQueue.jsp");
	}

	request.setAttribute("signIntention", signIntention);

	return forward(request, "/signature/createSignature.jsp");
    }

    public ActionForward getLogsToSign(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final SignatureIntention<T> signatureIntention = getSignatureIntention(request);

	verifyTokenIn(signatureIntention, request);

	String contentToSign = signatureIntention.getContentToSign();

	try {

	    OutputStream outputStream = response.getOutputStream();
	    OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream, "UTF-8");

	    response.setContentType("text/plain");
	    response.setContentLength(contentToSign.getBytes().length);

	    streamWriter.write(contentToSign);
	    streamWriter.flush();
	    streamWriter.close();

	} catch (IOException e) {
	    e.printStackTrace();
	}

	return null;
    }

    public ActionForward receiveSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final SignatureIntention signature = getSignatureIntention(request);

	verifyTokenOut(signature, request);

	OutputStream outputStream;
	try {
	    // response stream
	    response.setContentType("text/plain");
	    outputStream = response.getOutputStream();
	    OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream, "UTF-8");

	    System.out.println("--- Receive Signature");
	    streamWriter.write("--- Receive Signature");

	    UploadedFile uploadedFile0 = RenderersRequestProcessorImpl.getUploadedFile("file0");
	    UploadedFile uploadedFile1 = RenderersRequestProcessorImpl.getUploadedFile("file1");

	    System.out.println("file0:" + new String(uploadedFile0.getFileData()));
	    System.out.println("file1:" + new String(uploadedFile1.getFileData()));

	    signature.finalizeSignature(uploadedFile0, uploadedFile1);

	    streamWriter.write("--- Finish Signature");
	    System.out.println("--- Finish Signature");

	    streamWriter.flush();
	    streamWriter.close();

	} catch (IOException e1) {
	    e1.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return null;
    }

    protected SignatureIntention<T> getSignatureIntention(HttpServletRequest request) {
	return getDomainObject(request, "objectId");
    }

    protected void verifyTokenIn(SignatureIntention<T> signature, HttpServletRequest request) {
	String tokenIn = request.getParameter("token");
	signature.verifyTokenIn(tokenIn);
    }

    protected void verifyTokenOut(SignatureIntention<T> signature, HttpServletRequest request) {
	String tokenOut = request.getParameter("token");
	signature.verifyTokenOut(tokenOut);
    }

}
