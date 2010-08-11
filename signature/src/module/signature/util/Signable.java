package module.signature.util;

import module.signature.util.exporter.SignatureExporter;
import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

public interface Signable {

    public String getIdentification();

    public void getContentToSign(SignatureExporter signatureExporter);

    public void receiveSignature(UploadedFile file0, UploadedFile file1);

}
