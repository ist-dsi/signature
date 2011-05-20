package aeq;

import java.applet.AppletContext;

import javax.swing.JApplet;

public class Applet extends JApplet {

    public static boolean debugMode = true;

    @Override
    public void init() {
	super.init();

	AppletContext appletContext = this.getAppletContext();

	try {
	    String signContentURL = getParameter("signContentURL");
	    String serverURL = getParameter("serverURL");
	    String redirectURL = getParameter("redirectURL");
	    Applet.debugln("Running " + System.getProperty("sun.arch.data.model") + "bit version of JVM");

	    MainWindow window = new MainWindow(appletContext, signContentURL, serverURL, redirectURL);
            this.setContentPane(window);
	    window.setVisible(true);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    public void main(String[] args) {
	String signContentURL = "http://joantune-workstation:8080/bennu-signature";
	String serverURL = "http://joantune-workstation:8080/bennu-signature";
	String redirectURL = "http://joantune-workstation:8080/bennu-signature";

	AppletContext appletContext = this.getAppletContext();
	MainWindow window = new MainWindow(appletContext, signContentURL, serverURL, redirectURL);
	window.setVisible(true);

    }

    public static void debugln(String message) {
	if (debugMode) {
	    System.out.println(message);
	}
    }

}
