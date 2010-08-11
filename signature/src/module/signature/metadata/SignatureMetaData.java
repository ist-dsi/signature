package module.signature.metadata;

import module.signature.util.exporter.SignatureExporter;

public abstract class SignatureMetaData<T> {

    public SignatureMetaData(T t) {
	transverse(t);
    }

    abstract protected void transverse(T t);

    abstract public void accept(SignatureExporter signatureExporter);

}
