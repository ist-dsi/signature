package module.signature.util.exporter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import module.signature.metadata.SignatureMetaData;

public class SignatureExporterXML implements SignatureExporter {

    public SignatureExporterXML() {
    }

    @Override
    public String export(SignatureMetaData sign) throws ExporterException {

	try {
	    JAXBContext context = JAXBContext.newInstance(sign.getClass());

	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	    StringWriter result = new StringWriter();
	    m.marshal(sign, result);

	    return result.toString();
	} catch (JAXBException ex) {
	    throw new ExporterException(ex);
	}
    }

    private void generateSchema(JAXBContext context, SignatureMetaData signatureMetaData) throws ExporterException {
	final File schemaFile = new File("generated" + File.separator + "schema", signatureMetaData.getIdentification());

	try {
	    context.generateSchema(new SchemaOutputResolver() {
		@Override
		public Result createOutput(String namespaceUri, String schemaName) throws IOException {
		    final StreamResult schemaResult = new StreamResult(schemaFile);
		    return schemaResult;
		}
	    });

	    System.out.println("------- Schema generated in: " + schemaFile.getAbsolutePath());
	} catch (IOException ex) {
	    throw new ExporterException(ex);
	}
    }
}
