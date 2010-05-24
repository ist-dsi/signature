package module.signature.domain;

public interface Signable {

    public String getContentToSign();

    public void receiveSignature();

}
