package module.signature.domain;

import java.io.FileNotFoundException;
import java.io.IOException;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

public class SignatureRepository extends SignatureRepository_Base {

    public static SignatureRepository getRepository() {
	SignatureSystem system = SignatureSystem.getInstance();

	if (system.getSignatureRepository() == null) {
	    system.setSignatureRepository(new SignatureRepository());
	}

	return system.getSignatureRepository();
    }

    private SignatureRepository() {
	super();
    }

    @Service
    public void addSignature(SignatureIntention signIntention, UploadedFile uploadedFile0, UploadedFile uploadedFile1) {
	try {
	    SignatureFile file0 = new SignatureFile(this, signIntention, uploadedFile0.getName(), uploadedFile0.getName(),
		    uploadedFile0.getFileData());

	    SignatureFile file1 = new SignatureFile(this, signIntention, uploadedFile1.getName(), uploadedFile1.getName(),
		    uploadedFile1.getFileData());

	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
