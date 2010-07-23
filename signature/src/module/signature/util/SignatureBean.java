package module.signature.util;

import java.io.Serializable;

public class SignatureBean implements Serializable {

    private static final long serialVersionUID = 7034592773819075266L;

    private final Signable signObject;

    public SignatureBean(Signable signObject) {
	this.signObject = signObject;
    }

    public String getIdentification() {
	return signObject.getIdentification();
    }
}
