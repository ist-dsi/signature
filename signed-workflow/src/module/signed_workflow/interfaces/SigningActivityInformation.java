/**
 * 
 */
package module.signed_workflow.interfaces;

import module.signature.domain.data.SignatureFormat;


/**
 * Interface used to signal that an Activity is in fact signed
 * 
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public interface SigningActivityInformation {

    public SignatureFormat getSignatureFormat();


}
