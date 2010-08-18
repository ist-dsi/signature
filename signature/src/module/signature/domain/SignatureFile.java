package module.signature.domain;

public class SignatureFile extends SignatureFile_Base {

    public SignatureFile(SignatureRepository repository, SignatureIntention signature, String displayName, String filename,
	    byte[] content) {
	super();

	setRepository(repository);
	setSignature(signature);

	init(displayName, filename, content);
    }

}
