package module.signature.metadata;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import module.signature.domain.SignatureIntention;
import module.signature.exception.SignatureMetaDataInvalidException;

import org.joda.time.DateTime;

@XmlRootElement(name = "signature")
public class SignatureMetaDataRoot extends SignatureMetaData<SignatureIntention> {

    private String date;
    private SignatureMetaData childMetaData;

    public SignatureMetaDataRoot() {
	super();
    }

    public SignatureMetaDataRoot(SignatureMetaData metaData) {
	DateTime date = new DateTime();
	setDate(date.toString("yyyy-MM-dd"));
	setChildMetaData(metaData);
    }

    @Override
    public void checkData(SignatureIntention signature) throws SignatureMetaDataInvalidException {
	getChildMetaData().checkData(signature.getSignObject());
    }

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    @XmlElementRef
    public SignatureMetaData getChildMetaData() {
	return childMetaData;
    }

    public void setChildMetaData(SignatureMetaData item) {
	this.childMetaData = item;
    }
}
