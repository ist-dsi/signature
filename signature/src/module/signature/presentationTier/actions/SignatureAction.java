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
import module.signature.util.exporter.ExporterException;
import module.signature.util.exporter.SignatureExporter;
import module.signature.util.exporter.SignatureExporterXML;
import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.User;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;

import pt.ist.fenixWebFramework.renderers.plugin.RenderersRequestProcessorImpl;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/signatureAction")
public class SignatureAction extends ContextBaseAction {

    boolean QUEUE_ACTIVE = false;
    boolean TOKENS_ACTIVE = false;

    protected SignatureQueue getQueue() {
	User user = UserView.getCurrentUser();
	return user.getSignatureQueue();
    }

    public ActionForward createSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final SignatureIntention signIntention = getSignatureIntention(request);

	if (QUEUE_ACTIVE) {

	    getQueue().push(signIntention);

	    return forward(request, "/signature/confirmSignatureToQueue.jsp");
	}

	request.setAttribute("signIntention", signIntention);

	return forward(request, "/signature/createSignature.jsp");
    }

    public ActionForward showQueue(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	if (QUEUE_ACTIVE) {

	    SignatureIntentionMulti signIntention = SignatureIntentionMulti.factory(getQueue());

	    request.setAttribute("signIntention", signIntention);
	}

	return forward(request, "/signature/showQueue.jsp");
    }

    public ActionForward clearQueue(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	if (QUEUE_ACTIVE) {

	    getQueue().clear();
	}

	return new ActionRedirect(request.getRequestURI());
    }

    public ActionForward viewSignature(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final SignatureIntention signatureIntention = getSignatureIntention(request);

	request.setAttribute("signIntention", signatureIntention);

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

	final SignatureIntention signatureIntention = getSignatureIntention(request);

	verifyTokenIn(signatureIntention, request);

	try {
	    SignatureExporter signExporter = new SignatureExporterXML();
	    signatureIntention.getContentToSign(signExporter);
	    String contentToSign = signExporter.export();

	    OutputStream outputStream = response.getOutputStream();
	    OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream, "UTF-8");

	    response.setContentType("text/plain");
	    response.setContentLength(contentToSign.getBytes().length);

	    streamWriter.write(contentToSign);
	    streamWriter.flush();
	    streamWriter.close();

	} catch (ExporterException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
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

	    System.out.println("--- Receive Signature");
	    streamWriter.write("--- Receive Signature");

	    UploadedFile uploadedFile0 = RenderersRequestProcessorImpl.getUploadedFile("file0");
	    UploadedFile uploadedFile1 = RenderersRequestProcessorImpl.getUploadedFile("file1");

	    System.out.println("file0:" + new String(uploadedFile0.getFileData()));
	    System.out.println("file1:" + new String(uploadedFile1.getFileData()));

	    signIntention.sealSignature(uploadedFile0, uploadedFile1);

	    streamWriter.write("--- Finish Signature");
	    System.out.println("--- Finish Signature");

	    streamWriter.flush();
	    streamWriter.close();

	} catch (IOException e1) {
	    e1.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	if (QUEUE_ACTIVE) {

	    getQueue().clear();
	}

	return null;
    }

    protected SignatureIntention getSignatureIntention(HttpServletRequest request) {
	return getDomainObject(request, "objectId");
    }

    protected void verifyTokenIn(SignatureIntention signature, HttpServletRequest request) {
	if (!TOKENS_ACTIVE) {
	    return;
	}

	String tokenIn = request.getParameter("token");
	signature.verifyTokenIn(tokenIn);
    }

    protected void verifyTokenOut(SignatureIntention signature, HttpServletRequest request) {
	if (!TOKENS_ACTIVE) {
	    return;
	}

	String tokenOut = request.getParameter("token");
	signature.verifyTokenOut(tokenOut);
    }
}
