package module.signature.presentationTier.renderers;

import module.signature.domain.SignatureIntention;
import module.signature.domain.SignatureQueue;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

/**
 * The output renderer for the Queue signature applet
 * 
 * @author Diogo Figueiredo
 */
public class SignatureQueueRenderer extends SignatureRenderer {

    private SignatureQueue signatureQueue;

    @Override
    protected String generateContentUrl(Object object) {
	StringBuilder builder = new StringBuilder();

	for (SignatureIntention signItem : signatureQueue.getSignatureIntentions()) {
	    builder.append(super.generateContentUrl(signItem) + ",");
	}

	return builder.toString();
    }

    @Override
    protected String generateServerUrl(Object object) {
	StringBuilder builder = new StringBuilder();

	for (SignatureIntention signItem : signatureQueue.getSignatureIntentions()) {
	    builder.append(super.generateServerUrl(signItem) + ",");
	}

	return builder.toString();
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
	signatureQueue = (SignatureQueue) object;

	return super.getLayout(object, type);
    }
}
