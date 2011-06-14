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


    public static void debugln(String message) {
	if (debugMode) {
	    System.out.println(message);
	}
    }

}
