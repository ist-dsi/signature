package module.signature.util.exporter;

import module.signature.exception.SignatureException;

public class ExporterException extends SignatureException {
    public ExporterException(Exception ex) {
	super(ex);
    }
}
