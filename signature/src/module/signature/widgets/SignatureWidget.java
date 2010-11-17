package module.signature.widgets;

import module.dashBoard.presentationTier.WidgetRequest;
import module.dashBoard.widgets.WidgetController;
import module.signature.domain.SignatureIntention;
import myorg.applicationTier.Authenticate.UserView;
import myorg.util.BundleUtil;
import myorg.util.ClassNameBundle;

@ClassNameBundle(bundle = "resources/SignatureResources", key = "widget.title.signatureWidget")
public class SignatureWidget extends WidgetController {

    @Override
    public void doView(WidgetRequest request) {

	int unsigned = 0;

	for (SignatureIntention signature : UserView.getCurrentUser().getSignatureIntentions()) {
	    if (!signature.isSealed()) {
		unsigned++;
	    }
	}

	request.setAttribute("signaturesCount", unsigned);
    }

    @Override
    public String getWidgetDescription() {
	return BundleUtil.getStringFromResourceBundle("resources/SignatureResources", "widget.description.signatureWidget");
    }

    @Override
    public boolean isHelpModeSupported() {
	return false;
    }

}
