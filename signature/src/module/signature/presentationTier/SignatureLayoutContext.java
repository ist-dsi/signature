package module.signature.presentationTier;

import myorg.presentationTier.Context;

import org.apache.struts.action.ActionForward;

public class SignatureLayoutContext extends Context {

    @Override
    public ActionForward forward(String forward) {
	return new ActionForward(forward);
    }

    public ActionForward forward() {
	return forward("");
    }
}
