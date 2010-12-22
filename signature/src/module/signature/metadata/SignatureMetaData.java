package module.signature.metadata;

import javax.xml.bind.annotation.XmlAttribute;

import module.signature.domain.SignatureSystem;
import module.signature.exception.SignatureMetaDataInvalidException;
import module.signature.util.Signable;

public abstract class SignatureMetaData<T> {

    @XmlAttribute(name = "id")
    private String identification;

    /**
     * Constructor used to rebuild the data from the xml
     */
    public SignatureMetaData() {
	SignatureSystem.getInstance().registerMetaData(getClass());
    }

    public SignatureMetaData(T obj) {
	SignatureSystem.getInstance().registerMetaData(getClass());
    }

    public SignatureMetaData(Signable obj) {
	setIdentification(obj.getIdentification());
	SignatureSystem.getInstance().registerMetaData(getClass());
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

    final protected boolean notNull(Object object) {
	return object != null;
    }
}
