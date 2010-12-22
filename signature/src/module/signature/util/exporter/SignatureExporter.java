package module.signature.util.exporter;

import module.signature.domain.SignatureIntention;
import module.signature.metadata.SignatureMetaData;

public interface SignatureExporter {

    public String export(SignatureIntention signature, SignatureMetaData metaData) throws ExporterException;

    public SignatureMetaData rebuild(String content) throws ExporterException;
}
