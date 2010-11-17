package module.signature.domain.task;

import module.signature.domain.SignatureIntention;
import module.signature.domain.SignatureIntentionMulti;
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
	long startTime = System.nanoTime();

	// clean temporary (non-multi) signatures
	for (SignatureIntention signature : SignatureSystem.getInstance().getSignatureIntentions()) {
	    if (!(signature instanceof SignatureIntentionMulti)) {
		signature.cancel();
	    }
	}

	// clean temporary (multi) signatures
	for (SignatureIntention signature : SignatureSystem.getInstance().getSignatureIntentions()) {
	    if (signature instanceof SignatureIntentionMulti) {
		((SignatureIntentionMulti) signature).cancel();
	    }
	}

	// clean all expired queues
	/*
	 * for (User u : MyOrg.getInstance().getUser()) { if
	 * (u.hasSignatureQueue()) { SignatureQueue queue =
	 * u.getSignatureQueue();
	 * 
	 * if (queue.getExpire() != null && queue.getExpire().isBeforeNow()) {
	 * queue.clear(); } } }
	 */

	long estimatedTime = (long) ((System.nanoTime() - startTime) / 1000000.0);
	System.out.println("[cron] done in " + estimatedTime + "ms");
    }
}
