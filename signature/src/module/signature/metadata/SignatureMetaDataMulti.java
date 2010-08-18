package module.signature.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import module.signature.domain.SignatureIntention;
import module.signature.domain.SignatureIntentionMulti;

@XmlRootElement(name = "multi")
public class SignatureMetaDataMulti extends SignatureMetaData<SignatureIntentionMulti> {

    @XmlElementRefs({ @XmlElementRef(type = SignatureMetaDataProcess.class),
	    @XmlElementRef(type = SignatureMetaDataWorkflowLog.class), @XmlElementRef(type = SignatureMetaDataActivityLog.class) })
    private List<SignatureMetaData> list;

    public SignatureMetaDataMulti() {
	super();
    }

    public SignatureMetaDataMulti(SignatureIntentionMulti signature) {
	super(signature);

	setIdentification(signature.getIdentification());
	setList(new ArrayList<SignatureMetaData>());

	for (SignatureIntention si : signature.getSignatureIntentions()) {
	    getList().add(si.getMetaData());
	}
    }

    public List<SignatureMetaData> getList() {
	return list;
    }

    public void setList(List<SignatureMetaData> list) {
	this.list = list;
    }

}
