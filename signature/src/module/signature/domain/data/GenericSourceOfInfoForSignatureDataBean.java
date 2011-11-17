package module.signature.domain.data;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import module.signature.domain.data.interfaces.ConvertibleToXMLAndXHTML;
import module.signature.domain.data.interfaces.SignatureDataSourceOfInfoInterface;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixWebFramework.services.Service;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * This is a generic bean that should be extended, and which is used as
 * intermediary to retrieve the content to be signed
 * 
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public abstract class GenericSourceOfInfoForSignatureDataBean implements Serializable, SignatureDataSourceOfInfoInterface,
	ConvertibleToXMLAndXHTML {
    /**
     * Version 1 of this bean. To note, changes on this class should be
     * retro-compatible with the previous under penalty of finding unexisting
     * discrepancies with currently signed objects
     */
    private static final long serialVersionUID = 1L;
    @XStreamAsAttribute
    private String intention;
    @XStreamAsAttribute
    private String description;

    @XStreamAsAttribute
    private String signatureId;

    private final SignatureData signatureData;


    public GenericSourceOfInfoForSignatureDataBean(SignatureData signatureData) {
	this.signatureData = signatureData;
    }

    @Service
    @PostConstruct
    public void generateDescriptionIntentionAndIdStrings() {
	this.description = generateDescriptionString();
	this.intention = generateIntentionString();
	this.signatureId = generateSignatureId();

    }

    public String getSignatureId() {
	return signatureId;
    }

    public abstract String generateIntentionString();

    public abstract String generateDescriptionString();

    public abstract String generateSignatureId();

    public String getDescription() {
	return description;
    }

    public String getIntention() {
	return intention;
    }

    @Override
    public SignatureData getSignatureData() {
	return signatureData;
    }

    /**
     * 
     * Method used to put "-" instead of "" for fields that are blank
     * 
     * @param originalString
     * @return "-" if the originalString is blank/empty or originalString
     *         otherwise
     */
    public static String signalBlankString(String originalString) {
	if (originalString == null || StringUtils.isBlank(originalString))
	    return "-";
	else
	    return originalString;

    }

}
