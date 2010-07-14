package module.signature.util;

import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

public interface Signable {

    public String getIdentification();

    public String getContentToSign();

    public void receiveSignature(UploadedFile file0, UploadedFile file1);

}
