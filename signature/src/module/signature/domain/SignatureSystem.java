package module.signature.domain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.stream.StreamSource;

import module.dashBoard.WidgetRegister;
import module.signature.exception.SignatureException;
import module.signature.metadata.SignatureMetaData;
import module.signature.metadata.SignatureMetaDataRoot;
import module.signature.util.exporter.ExporterException;
import module.signature.util.exporter.SignatureExporterDocument;
import module.signature.util.exporter.SignatureExporterXHTML;
import module.signature.util.exporter.SignatureExporterXML;
import module.signature.widgets.SignatureWidget;
import myorg.domain.ModuleInitializer;
import myorg.domain.MyOrg;
import aeq.XAdESValidator;

public class SignatureSystem extends SignatureSystem_Base implements ModuleInitializer {

    /**
     * this allows to have dynamic xml generation; metadata root classe will
     * register here which metadata classes are used
     */
    private ThreadLocal<HashSet<Class>> metaDataClazzes;

    public static SignatureSystem getInstance() {
	MyOrg myorg = MyOrg.getInstance();

	if (myorg.getSignatureSystem() == null) {
	    myorg.setSignatureSystem(new SignatureSystem());
	}

	return myorg.getSignatureSystem();
    }

    private SignatureSystem() {
	super();
    }

    public String generateSignature(SignatureIntention signature) throws ExporterException {
	return generateDocument(generatePartial(signature));
    }

    public String generateSignature(SignatureIntentionMulti signature) throws ExporterException {
	StringBuffer buffer = new StringBuffer();

	int i = 1;
	int count = signature.getSignatureIntentions().size();

	for (SignatureIntention signatureIntention : signature.getSignatureIntentions()) {
	    // hack to get the contents from the previously generated signatures
	    // we extract the content of the <body> tags
	    // concat all into a new xhtml page :)

	    String content = signatureIntention.getContent();

	    String startMatch = "</head><body>";
	    int indexStartMatch = content.indexOf(startMatch);

	    if (indexStartMatch >= 0) {
		content = content.substring(indexStartMatch + startMatch.length());
	    }

	    String endMatch = "</body>";
	    int indexEndMatch = content.indexOf(endMatch);

	    if (indexEndMatch >= 0) {
		content = content.substring(0, indexEndMatch);
	    }

	    // now we had a little pagination
	    // all simple anchors

	    buffer.append("<a name=\"page" + i + "\"></a>");
	    buffer.append("<p class=\"text-center\">");
	    buffer.append("<b>Assinatura " + i + "/" + count + "</b> -- ");

	    if (i > 1) {
		buffer.append("<a href=\"#page" + (i - 1) + "\">« Anterior</a> | ");
	    }

	    if (i < count) {
		buffer.append("<a href=\"#page" + (i + 1) + "\">Próxima »</a>");
	    }

	    buffer.append("</p>");

	    buffer.append(content);

	    i++;
	}

	String document = generateDocument(buffer.toString());

	return document;
    }

    protected String generateDocument(String xhtml) throws ExporterException {
	SignatureExporterDocument documentBuilder = new SignatureExporterDocument();
	String result = documentBuilder.export(xhtml);

	return result;
    }

    protected String generatePartial(SignatureIntention signature) throws ExporterException {
	try {
	    if (metaDataClazzes == null) {
		synchronized (this) {
		    if (metaDataClazzes == null) {
			metaDataClazzes = new ThreadLocal<HashSet<Class>>();
		    }
		}
	    }

	    metaDataClazzes.set(new HashSet<Class>());

	    SignatureMetaData metaData = signature.getMetaData();
	    SignatureMetaDataRoot rootMetaData = new SignatureMetaDataRoot(metaData);

	    SignatureExporterXML xmlExporter = new SignatureExporterXML(metaDataClazzes.get());
	    SignatureExporterXHTML xhtmlExporter = new SignatureExporterXHTML();

	    String xml = xmlExporter.export(signature, rootMetaData);
	    String xhtml = xhtmlExporter.export(new StreamSource(new StringReader(xml)), signature.getTransformationTemplate());

	    return xhtml;

	} finally {
	    metaDataClazzes.set(null);
	}
    }

    public static SignatureMetaData rebuildMetaData(SignatureIntention signature) throws SignatureException {
	throw new UnsupportedOperationException();
	/*
	 * if (!signature.isSealed()) { throw new SignatureNotSealedException();
	 * }
	 * 
	 * SignatureExporterXML converter = new SignatureExporterXML();
	 * 
	 * return converter.rebuild(signature.getSignatureContent());
	 */
    }

    public static boolean validateSignature(SignatureIntention signature) {
	try {
	    File tempFile = File.createTempFile("toVerify", ".tmp");
	    tempFile.deleteOnExit();

	    BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
	    out.write(new String(signature.getSignatureFile().getContent()));
	    out.close();

	    XAdESValidator xv = new XAdESValidator(new String[] { "-sig", "-xsd", "-val" });

	    if (xv.validateXMLSignature(tempFile.getAbsolutePath())) {
		System.out.println("SUCCESS"); // should throw exception
	    } else {
		System.out.println("ERROR");
	    }

	    tempFile.delete();

	    return true;
	} catch (IOException ex) {
	    ex.printStackTrace();
	}

	return false;
    }

    public List<SignatureIntention> getSignaturesByIdentification(String identification) {
	List<SignatureIntention> result = new ArrayList<SignatureIntention>();

	for (SignatureIntention signature : getSignatureIntentions()) {
	    if (identification.equals(signature.getSignObjectId())) {
		result.add(signature);
	    }
	}

	return result;
    }

    public void registerMetaData(Class clazz) {
	getMetaDataClazzes().add(clazz);
    }

    public Set<Class> getMetaDataClazzes() {
	return metaDataClazzes.get();
    }

    @Override
    public void init(MyOrg root) {
	WidgetRegister.registerWidget(SignatureWidget.class);
    }

}
