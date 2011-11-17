/**
 * 
 */
package module.signature.domain.data.interfaces;

import module.signature.domain.data.SignatureData;
import module.signature.domain.data.SignatureDataFormat;

/**
 * Interface used to mark the objects that can/should be used to generate the
 * Signature document, that is, the SignatureData and to transport the Signature
 * 
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public interface SignatureDataSourceOfInfoInterface {

    public SignatureDataFormat getSignatureDataFormat();

    /**
     * 
     * @return the {@link SignatureData}, if it exists, associated with this
     *         bean
     */
    public SignatureData getSignatureData();


    //    public SignatureData createSignatureData();


}
