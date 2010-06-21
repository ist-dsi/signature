package module.signature.presentationTier.renderers;

import javax.servlet.http.HttpServletRequest;

import module.signature.util.SignatureBean;
import pt.ist.fenixWebFramework.renderers.OutputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlApplet;
import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.plugin.RenderersRequestProcessorImpl;

/**
 * The default output renderer for the signature applet
 * 
 * @author Diogo Figueiredo
 */
public class SignatureRenderer extends OutputRenderer {

    private String bundle;
    private String signContentURL;
    private String serverURL;

    private String code;
    private String archive;
    private int width;
    private int height;

    public String getBundle() {
	return this.bundle;
    }

    /**
     * Chooses the bundle in which the labels will be searched.
     * 
     * @property
     */
    public void setBundle(String bundle) {
	this.bundle = bundle;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
	return new Layout() {

	    @Override
	    public HtmlComponent createComponent(Object object, Class type) {
		SignatureBean signBean = (SignatureBean) object;

		HttpServletRequest request = RenderersRequestProcessorImpl.getCurrentRequest();
		String scheme = request.getScheme(); // http
		String serverName = request.getServerName(); // hostname.com
		int serverPort = request.getServerPort(); // 80
		String contextPath = request.getContextPath(); // /mywebapp

		String baseURL = scheme + "://" + serverName + ":" + serverPort + contextPath + "/signatureAction.do?";

		String defaultGetContentURL = baseURL + "method=getLogsToSign&objectId=" + signBean.getSignID();
		String defaultServerURL = baseURL + "method=receiveSignature&objectId=" + signBean.getSignID();

		HtmlApplet applet = new HtmlApplet();

		applet.setCode(getCode());
		applet.setArchive(getArchive());
		applet.setWidth(getWidth());
		applet.setHeight(getHeight());

		applet.setProperty("signContentURL", defaultGetContentURL);
		applet.setProperty("serverURL", defaultServerURL);
		applet.setProperty("tokenIn", signBean.getTokenIn());
		applet.setProperty("tokenOut", signBean.getTokenOut());

		/*
		 * <div> <div id="signLink">..</div> <div id="signWindow">
		 * ..applet..</div> <script>..</script>< /div>
		 */

		HtmlBlockContainer container = new HtmlBlockContainer();

		HtmlBlockContainer signLink = new HtmlBlockContainer();
		signLink.setId("signLink");
		signLink.addChild(new HtmlText("Assinar Digitalmente"));
		signLink
			.setStyle("margin: 5px 0; height: 22px; padding-top: 10px; padding-left: 36px; background:url(http://demo.rockettheme.com/mar10/images/stories/demo/tabs/lock.png) no-repeat left center");

		HtmlBlockContainer signWindow = new HtmlBlockContainer();
		signWindow.setId("signWindow");
		signWindow.addChild(applet);

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

    public void setSignContentURL(String link) {
	this.signContentURL = link;
    }

    public String getSignContentURL() {
	return signContentURL;
    }

    public void setServerURL(String link) {
	this.serverURL = link;
    }

    public String getServerURL() {
	return serverURL;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getArchive() {
	return archive;
    }

    public void setArchive(String archive) {
	this.archive = archive;
    }

    public int getWidth() {
	return width;
    }

    public void setWidth(int width) {
	this.width = width;
    }

    public int getHeight() {
	return height;
    }

    public void setHeight(int height) {
	this.height = height;
    }

}
