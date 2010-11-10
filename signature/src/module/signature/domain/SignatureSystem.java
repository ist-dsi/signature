package module.signature.domain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import module.dashBoard.WidgetRegister;
import module.signature.exception.SignatureException;
import module.signature.exception.SignatureNotSealedException;
import module.signature.metadata.SignatureMetaDataRoot;
import module.signature.util.exporter.ExporterException;
import module.signature.util.exporter.SignatureExporter;
import module.signature.util.exporter.SignatureExporterXML;
import module.signature.widgets.SignatureWidget;
import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.ModuleInitializer;
import myorg.domain.MyOrg;
import myorg.domain.User;
import aeq.XAdESValidator;

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
	return UserView.getCurrentUser().hasSignatureQueue();
    }

    public SignatureQueue getQueue() {
	User user = UserView.getCurrentUser();

	if (hasQueue()) {
	    return user.getSignatureQueue();
	}

	return null;
    }

    public void clearQueue() {
	User user = UserView.getCurrentUser();

	if (hasQueue()) {
	    user.getSignatureQueue().clear();
	}
    }

    public List<SignatureIntention> getSignatures() {
	List<SignatureIntention> sealed = new ArrayList<SignatureIntention>();

	for (SignatureIntention signature : UserView.getCurrentUser().getSignatureIntentions()) {
	    if (signature.isSealed()) {
		sealed.add(signature);
	    }
	}

	return sealed;
    }

    public static String exportSignature(SignatureIntention signature) throws ExporterException {
	SignatureExporter<SignatureMetaDataRoot> converter = new SignatureExporterXML<SignatureMetaDataRoot>();
	SignatureMetaDataRoot signRoot = new SignatureMetaDataRoot(signature);

	return converter.export(signRoot);
    }

    public static SignatureMetaDataRoot rebuildMetaData(SignatureIntention signature) throws SignatureException {
	if (!signature.isSealed()) {
	    throw new SignatureNotSealedException();
	}

	SignatureExporter<SignatureMetaDataRoot> converter = new SignatureExporterXML<SignatureMetaDataRoot>();

	return converter.rebuild(signature.getSignatureContent());
    }

    public static boolean validateSignature(SignatureIntention signature) {
	try {
	    File tempFile = File.createTempFile("toVerify", ".tmp");
	    tempFile.deleteOnExit();

	    BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
	    out.write(new String(signature.getSignatureFile().getContent()));
	    out.close();

	    XAdESValidator xv = new XAdESValidator(new String[] { "-sig", "-xsd", "-val" });

	    if (xv.validateXMLSignature(tempFile.getAbsolutePath())) {
		System.out.println("SUCCESS"); // should throw exception
	    } else {
		System.out.println("ERROR");
	    }

	    tempFile.delete();

	    return true;
	} catch (IOException ex) {
	    ex.printStackTrace();
	}

	return false;
    }

    public List<SignatureIntention> getSignaturesByIdentification(String identification) {
	List<SignatureIntention> result = new ArrayList<SignatureIntention>();

	for (SignatureIntention signature : getSignatureIntentions()) {
	    if (identification.equals(signature.getSignObjectId())) {
		result.add(signature);
	    }
	}

	return result;
    }

    @Override
    public void init(MyOrg root) {
	WidgetRegister.registerWidget(SignatureWidget.class);
    }

}
