/**
 * 
 */
package module.signed_workflow.domain;

import java.io.Serializable;

import module.signature.domain.data.GenericSourceOfInfoForSignatureDataBean;
import module.workflow.domain.ProcessFile;
import myorg.util.BundleUtil;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Wrapper bean that represents a {@link ProcessFile}
 * 
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
@XStreamAlias("processFile")
public class ProcessFileSignatureDataBean implements Serializable {

    /**
     * default serial version
     */
    private static final long serialVersionUID = 1L;
    
    private final String displayName;

    private final String filename;

    @XStreamAsAttribute
    private final String contentType;

    private final String presentationName;

    private final String fileClassPresentableName;

    private final String hexSHA1MessageDigest;

    @XStreamAsAttribute
    private final boolean isDeleted;

     public ProcessFileSignatureDataBean(ProcessFile processFile) {
	this.displayName = GenericSourceOfInfoForSignatureDataBean.signalBlankString(processFile.getDisplayName());
	this.filename = GenericSourceOfInfoForSignatureDataBean.signalBlankString(processFile.getFilename());
	this.contentType = processFile.getContentType();
	this.presentationName = GenericSourceOfInfoForSignatureDataBean.signalBlankString(processFile.getPresentationName());
	this.fileClassPresentableName = GenericSourceOfInfoForSignatureDataBean.signalBlankString(BundleUtil
		.getLocalizedNamedFroClass(processFile.getClass()));
	
	this.hexSHA1MessageDigest = processFile.getHexSHA1MessageDigest();

	this.isDeleted = processFile.hasProcessWithDeleteFile();
	 
    }

}
