package module.signature.domain;

import java.util.ArrayList;
import java.util.List;

import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.ModuleInitializer;
import myorg.domain.MyOrg;
import pt.ist.fenixWebFramework.services.Service;

public class SignatureSystem extends SignatureSystem_Base implements ModuleInitializer {

    public static SignatureSystem getInstance() {
	MyOrg myorg = MyOrg.getInstance();

	if (myorg.getSignatureSystem() == null) {
	    myorg.setSignatureSystem(new SignatureSystem());
	}

	return myorg.getSignatureSystem();
    }

    private SignatureSystem() {
	super();
    }

    public static boolean hasQueue() {
	List<String> queueUsers = new ArrayList<String>();
	queueUsers.add("ist12282");

	return queueUsers.contains(UserView.getCurrentUser().getUsername());
    }

    @Override
    @Service
    public void init(MyOrg root) {
	/*
	 * for (SignatureIntention s :
	 * MyOrg.getInstance().getSignatureIntentions()) { try {
	 * System.out.println("apagando: " + s); s.delete(); } catch (Error ex)
	 * { System.out.println(ex.getMessage()); } catch (Exception ex) {
	 * System.out.println(ex.getMessage()); } }
	 */
    }
}
