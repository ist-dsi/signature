/**
 * 
 */
package module.signature.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.signature.domain.data.SignatureData;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;

/**
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
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
