package module.signature.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import module.signature.domain.SignatureIntention;
import module.signature.domain.SignatureIntentionMulti;
import module.signature.exception.SignatureMetaDataInvalidException;

@XmlRootElement(name = "multi")
public class SignatureMetaDataMulti extends SignatureMetaData<SignatureIntentionMulti> {

    @XmlElementRefs({ @XmlElementRef(type = SignatureProcessMetaData.class),
	    @XmlElementRef(type = SignatureMetaDataWorkflowLog.class), @XmlElementRef(type = SignatureMetaDataActivityLog.class) })
    private List<SignatureMetaData> list;

    public SignatureMetaDataMulti() {
	super();
    }

    public SignatureMetaDataMulti(SignatureIntentionMulti multi) {
	super(multi);

	list = new ArrayList<SignatureMetaData>();

	for (SignatureIntention childSignature : multi.getSignatureIntentions()) {
	    addChild(childSignature.getMetaData());
	}
    }

    @Override
    public void checkData(SignatureIntentionMulti multi) throws SignatureMetaDataInvalidException {
	assertEquals(getList().size(), multi.getSignatureIntentionsCount());

	int verified = 0;
	for (SignatureIntention signature : multi.getSignatureIntentions()) {
	    for (SignatureMetaData childMetaData : getList()) {
		if (signature.getExternalId().equals(childMetaData.getIdentification())) {
		    childMetaData.checkData(signature.getSignObject());
		    verified++;
		    break;
		}
	    }
	}

	if (verified < multi.getSignatureIntentionsCount()) {
	    throw new SignatureMetaDataInvalidException("Not all metadata could be found");
	}
    }

    public void addChild(SignatureMetaData metaData) {
	list.add(metaData);
    }

    public List<SignatureMetaData> getList() {
	return list;
    }

}
