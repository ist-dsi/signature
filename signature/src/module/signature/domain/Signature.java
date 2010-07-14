package module.signature.domain;

import module.signature.util.Signable;
import myorg.domain.MyOrg;

import org.joda.time.DateTime;

public class Signature extends Signature_Base {

    public Signature(Signable signObject) {
	super();

	setMyOrg(MyOrg.getInstance());
	setIdentification(signObject.getIdentification());
	setTimestamp(new DateTime());
    }

    public String getContentSigned() {
	return null;
    }
}
