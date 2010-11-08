package module.signature.util.exporter;

import module.signature.metadata.SignatureMetaData;

public interface SignatureExporter<T extends SignatureMetaData> {

    public String export(T sign) throws ExporterException;

    public T rebuild(String content) throws ExporterException;
}
