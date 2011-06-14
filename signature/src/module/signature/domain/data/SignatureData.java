package module.signature.domain.data;

import sun.misc.BASE64Encoder;

public abstract class SignatureData extends SignatureData_Base {
    
    public  SignatureData() {
        super();
    }

    public abstract byte[] getContentToSign();
    
    final public String getBase64EncodedContentToSign() {
	BASE64Encoder base64Encoder = new BASE64Encoder();
	return base64Encoder.encode(getContentToSign());
    }

}
