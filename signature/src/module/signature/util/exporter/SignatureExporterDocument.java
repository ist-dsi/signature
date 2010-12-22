package module.signature.util.exporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SignatureExporterDocument {

    public SignatureExporterDocument() {
    }

    public String export(String xhtmlContent) throws ExporterException {

	try {
	    // construct the document

	    StringBuilder builder = new StringBuilder();

	    // the header
	    BufferedReader br = new BufferedReader(new InputStreamReader(getClass()
		    .getResourceAsStream("/templates/header.xhtml")));

	    String line;
	    while ((line = br.readLine()) != null) {
		builder.append(line);
	    }

	    br.close();

	    // the body
	    builder.append(xhtmlContent);

	    // the footer
	    br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/templates/footer.xhtml")));

	    while ((line = br.readLine()) != null) {
		builder.append(line);
	    }

	    System.out.println("Final Document: ----------------");
	    System.out.println(builder.toString());
	    System.out.println("-----");

	    return builder.toString();

	} catch (IOException ex) {
	    throw new ExporterException(ex);
	}
    }
}
