package module.signature.domain.data.interfaces;

import javax.xml.transform.Source;

/**
 * Interface to signal the signature system and the user to make sure that the
 * object that implements this interface is convertible to XML by castor
 * introspection i.e. using it's getters to serialize it
 * 
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public interface ConvertibleToXMLAndXHTML {

    public Source xsltSource();

}
