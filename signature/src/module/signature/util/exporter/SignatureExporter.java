package module.signature.util.exporter;

import module.signature.metadata.SignatureMetaData;

public interface SignatureExporter {

    public String export(SignatureMetaData sign) throws ExporterException;

}
