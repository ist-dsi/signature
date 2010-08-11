package module.signature.util.exporter;

import module.signature.metadata.SignatureMetaData;

public interface SignatureExporter {

    public String export() throws ExporterException;

    public void addParent(String prefix, String id);

    public void addItem(SignatureMetaData signable);

    public void addItem(String s);
}
