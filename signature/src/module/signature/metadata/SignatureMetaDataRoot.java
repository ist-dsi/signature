package module.signature.metadata;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import module.signature.domain.SignatureIntention;

@XmlRootElement(name = "signature")
public class SignatureMetaDataRoot extends SignatureMetaData<SignatureIntention> {

    @XmlElementRefs({ @XmlElementRef(type = SignatureMetaDataMulti.class), @XmlElementRef(type = SignatureMetaDataProcess.class),
	    @XmlElementRef(type = SignatureMetaDataWorkflowLog.class), @XmlElementRef(type = SignatureMetaDataActivityLog.class) })
    private SignatureMetaData item;

    public SignatureMetaDataRoot() {
	super();
    }

    public SignatureMetaDataRoot(SignatureIntention signature) {
	super(signature);

	setIdentification(signature.getExternalId());
	setItem(signature.getMetaData());
    }

    public SignatureMetaData getItem() {
	return item;
    }

    public void setItem(SignatureMetaData item) {
	this.item = item;
    }
}
