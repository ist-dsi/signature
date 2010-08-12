package module.signature.metadata;


public abstract class SignatureMetaData<T> {

    public SignatureMetaData(T t) {
	transverse(t);
    }

    abstract protected void transverse(T t);

}
