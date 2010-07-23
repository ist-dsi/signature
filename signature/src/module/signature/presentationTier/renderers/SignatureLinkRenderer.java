package module.signature.presentationTier.renderers;

import module.signature.domain.SignatureIntention;
import pt.ist.fenixWebFramework.renderers.OutputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlLink;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

/**
 * The default output renderer for the signature link
 * 
 * @author Diogo Figueiredo
 */
public class SignatureLinkRenderer extends OutputRenderer {

    private String bundle;

    public String getBundle() {
	return this.bundle;
    }

    public void setBundle(String bundle) {
	this.bundle = bundle;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
	return new Layout() {

	    @Override
	    public HtmlComponent createComponent(Object object, Class type) {
		SignatureIntention signIntention = (SignatureIntention) object;

		HtmlLink preLink = new HtmlLink();
		preLink.setUrl("/signatureAction.do?method=createSignature&objectId=" + signIntention.getExternalId());

		String signatureLink = GenericChecksumRewriter.injectChecksumInUrl("", preLink.calculateUrl());

		HtmlBlockContainer container = new HtmlBlockContainer();

		HtmlBlockContainer signLink = new HtmlBlockContainer();
		signLink.setId("signLink");
		signLink.addChild(new HtmlText("Assinar Digitalmente"));
		signLink.setStyle("margin: 5px 0; height: 22px; padding-top: 10px; padding-left: 36px; background:url(http://demo.rockettheme.com/mar10/images/stories/demo/tabs/lock.png) no-repeat left center");

		HtmlBlockContainer signWindow = new HtmlBlockContainer();
		signWindow.setId("signWindow");
		signWindow.addChild(new HtmlText("<iframe style=\"width: 820px; height: 710px\" src=\"" + signatureLink
			+ "\"></iframe>", false));

		container.addChild(signLink);
		container.addChild(signWindow);
		container
			.addChild(new HtmlText(
				"<script type=\"text/javascript\">"
					+ "$(function() { $(\"#signWindow\").dialog({ autoOpen: false, title: 'Assinatura Digital', width: 820, height: 710, resizable: false, draggable: false }); });"
					+ "$(\"#signLink\").click(function() { $(\"#signWindow\").dialog('open');});"
					+ "</script>", false));

		return container;
	    }
	};
    }
}
