package module.signature.presentationTier.renderers;

import module.signature.domain.Signable;
import pt.ist.fenixWebFramework.renderers.OutputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlApplet;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

/**
 * The default output renderer for the signature applet
 * 
 * @author Diogo Figueiredo
 */
public class AppletRenderer extends OutputRenderer {

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
		Signable objectId = (Signable) object;

		/*
		 * String url = getLink(); String fullUrl = url + "&" +
		 * GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME + "=" +
		 * GenericChecksumRewriter.calculateChecksum(url);
		 */

		HtmlApplet applet = new HtmlApplet();

		applet.setCode(getCode());
		applet.setArchive(getArchive());
		applet.setWidth(getWidth());
		applet.setHeight(getHeight());

		applet.setSignContentURL(getSignContentURL());
		applet.setServerURL(getServerURL());

		return applet;
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
