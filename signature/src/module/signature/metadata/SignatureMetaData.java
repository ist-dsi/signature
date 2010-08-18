package module.signature.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "generic")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class SignatureMetaData<T> {

    @XmlAttribute(name = "id", required = true)
    private String identification;

    public SignatureMetaData() {
    }

    public SignatureMetaData(T t) {
    }

    public String getIdentification() {
	return identification;
    }

    public void setIdentification(String identification) {
	this.identification = identification;
    }
}
