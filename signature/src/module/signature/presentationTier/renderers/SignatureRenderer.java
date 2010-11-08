package module.signature.presentationTier.renderers;

import javax.servlet.http.HttpServletRequest;

import module.signature.domain.SignatureIntention;
import pt.ist.fenixWebFramework.renderers.OutputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlApplet;
import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
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

    private String getBaseURL() {
	HttpServletRequest request = RenderersRequestProcessorImpl.getCurrentRequest();
	String scheme = request.getScheme(); // http
	String serverName = request.getServerName(); // hostname.com
	int serverPort = request.getServerPort(); // 80
	String contextPath = request.getContextPath(); // /mywebapp

	return scheme + "://" + serverName + ":" + serverPort + contextPath;
    }

    protected String generateContentUrl(Object object) {
	SignatureIntention signIntention = (SignatureIntention) object;

	return getBaseURL() + "/signatureAction.do?method=getLogsToSign&objectId=" + signIntention.getExternalId() + "&token="
		+ signIntention.getTokenIn();
    }

    protected String generateServerUrl(Object object) {
	SignatureIntention signIntention = (SignatureIntention) object;

	return getBaseURL() + "/signatureAction.do?method=receiveSignature&objectId=" + signIntention.getExternalId() + "&token="
		+ signIntention.getTokenOut();
    }

    protected String generateRedirectUrl(Object object) {
	SignatureIntention signIntention = (SignatureIntention) object;

	return getBaseURL() + "/signature.do?method=history";
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
	return new Layout() {

	    @Override
	    public HtmlComponent createComponent(Object object, Class type) {
		HtmlApplet applet = new HtmlApplet();

		applet.setCode(getCode());
		applet.setArchive(getArchive());
		applet.setWidth(getWidth());
		applet.setHeight(getHeight());

		applet.setProperty("signContentURL", generateContentUrl(object));
		applet.setProperty("serverURL", generateServerUrl(object));
		applet.setProperty("redirectURL", generateRedirectUrl(object));

		HtmlBlockContainer signWindow = new HtmlBlockContainer();
		signWindow.setId("signWindow");
		signWindow.addChild(applet);

		HtmlBlockContainer container = new HtmlBlockContainer();
		container.addChild(signWindow);

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
