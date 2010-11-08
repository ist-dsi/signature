package module.signature.domain.task;

import module.signature.domain.SignatureIntention;
import module.signature.domain.SignatureSystem;

public class ClearExpiredSignatures extends ClearExpiredSignatures_Base {

    public ClearExpiredSignatures() {
	super();
    }

    @Override
    public String getLocalizedName() {
	return getClass().getName();
    }

    @Override
    public void executeTask() {
	System.out.println("[cron] " + getLocalizedName());

	for (SignatureIntention signature : SignatureSystem.getInstance().getSignatureIntentions()) {
	    signature.delete();
	}

	System.out.println("[cron] done");
    }

}
