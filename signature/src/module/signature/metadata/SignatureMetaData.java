package module.signature.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import module.signature.exception.SignatureMetaDataInvalidException;
import module.signature.util.Signable;

@XmlRootElement(name = "generic")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class SignatureMetaData<T extends Signable> {

    @XmlAttribute(name = "id", required = true)
    private String identification;

    /**
     * Constructor used to rebuild the data from the xml
     */
    public SignatureMetaData() {

    }

    public SignatureMetaData(T obj) {
	setIdentification(obj.getIdentification());
    }

    private void setIdentification(String identification) {
	this.identification = identification;
    }

    public String getIdentification() {
	return identification;
    }

    abstract public void checkData(T x) throws SignatureMetaDataInvalidException;

    final protected void assertEquals(Object obj1, Object obj2) throws SignatureMetaDataInvalidException {
	if (!obj1.equals(obj2)) {
	    throw new SignatureMetaDataInvalidException();
	}
    }
}
