package module.signature.metadata;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import module.signature.domain.SignatureIntention;
import module.signature.exception.SignatureMetaDataInvalidException;

@XmlRootElement(name = "signature")
public class SignatureMetaDataRoot extends SignatureMetaData<SignatureIntention> {

    @XmlElementRefs({ @XmlElementRef(type = SignatureMetaDataMulti.class), @XmlElementRef(type = SignatureMetaDataProcess.class),
	    @XmlElementRef(type = SignatureMetaDataWorkflowLog.class), @XmlElementRef(type = SignatureMetaDataActivityLog.class) })
    private SignatureMetaData childMetaData;

    public SignatureMetaDataRoot() {
	super();
    }

    public SignatureMetaDataRoot(SignatureIntention signature) {
	super(signature);

	setChildMetaData(signature.getMetaData());
    }

    @Override
    public void checkData(SignatureIntention signature) throws SignatureMetaDataInvalidException {
	getChildMetaData().checkData(signature.getSignObject());
    }

    public SignatureMetaData getChildMetaData() {
	return childMetaData;
    }

    public void setChildMetaData(SignatureMetaData item) {
	this.childMetaData = item;
    }
}
