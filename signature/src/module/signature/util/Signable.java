package module.signature.util;

import pt.ist.fenixWebFramework.servlets.commons.UploadedFile;

public interface Signable {

    public String getSignID();

    public String getContentToSign();

    public void receiveSignature(UploadedFile file0, UploadedFile file1);

}
