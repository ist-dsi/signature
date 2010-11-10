package module.signature.widgets;

import module.dashBoard.presentationTier.WidgetRequest;
import module.dashBoard.widgets.WidgetController;
import module.signature.domain.SignatureSystem;
import myorg.util.BundleUtil;
import myorg.util.ClassNameBundle;

@ClassNameBundle(bundle = "resources/SignatureResources", key = "widget.title.signatureWidget")
public class SignatureWidget extends WidgetController {

    @Override
    public void doView(WidgetRequest request) {

	if (SignatureSystem.hasQueue()) {
	    request.setAttribute("queue", SignatureSystem.getInstance().getQueue());
	}
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
