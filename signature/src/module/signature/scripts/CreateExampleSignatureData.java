/**
 * 
 */
package module.signature.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import module.signature.domain.data.MultipleObjectSignatureDataAggregator;
import module.signature.domain.data.ObjectSignatureData;
import module.signature.domain.data.SignatureData;
import module.signed_workflow.interfaces.WorkflowProcessWithSigningActivities;
import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;
import myorg.applicationTier.Authenticate;
import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.User;
import myorg.domain.VirtualHost;
import myorg.domain.exceptions.DomainException;
import myorg.domain.scheduler.WriteCustomTask;

import org.apache.commons.lang.StringUtils;

import pt.ist.expenditureTrackingSystem.domain.acquisitions.AcquisitionProcess;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.activities.commons.Authorize;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.simplified.SimplifiedProcedureProcess;

/**
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public class CreateExampleSignatureData extends WriteCustomTask {

    /*
     * (non-Javadoc)
     * 
     * @see myorg.domain.scheduler.WriteCustomTask#doService()
     */

    public final Class processClassToUse = SimplifiedProcedureProcess.class;
    public final Class activityClassToUse = Authorize.class;
    public final String userToUseUsername = "ist154457";
    public final File fileToWriteContentTo = new File("/tmp/output.xhtml");
    public final File fileToWriteXMLTo = new File("/tmp/output.xml");

    final boolean generateAMultipleSignature = true;
    public final String nrFirstProcessIdNrToUseInMultipleSig = "RD/2011/97";
    public final String nrSecondProcessIdNrToUseInMultipleSig = "RD/2011/247";

    /**
     * If this variable is set to a value different than zero, it tries to use
     * the given process id to find an WorkflowProcess instead of the first one
     * it encounters of the given class
     */
    public final String processIdNrToUse = "RD/2011/249";
    private final boolean debugModeOn = true;

    @Override
    protected void doService() {

	VirtualHost.setVirtualHostForThread("dot.ist-id.ist.utl.pt");
	if (VirtualHost.getVirtualHostForThread() == null)
	    throw new Error("Virtual host not found - i.e. 'vai pentear macacos' by Susana Fernandes");
	final User userToUse = User.findByUsername(userToUseUsername);
	try {
	    byte[] contentToSign = null;
	    UserView userView = Authenticate.authenticate(userToUse);
	    pt.ist.fenixWebFramework.security.UserView.setUser(userView);
	    WorkflowProcess processToUse = null;
	    WorkflowActivity<WorkflowProcess, ActivityInformation<WorkflowProcess>> workflowActivity = (WorkflowActivity<WorkflowProcess, ActivityInformation<WorkflowProcess>>) activityClassToUse
		    .newInstance();
	    ArrayList<WorkflowProcess> processesToUseInMultiple = new ArrayList<WorkflowProcess>();
	    //let's iterate through the PaymentProcesses and find one suitable
	    for (Object object : WorkflowProcess.getAllProcesses(processClassToUse)) {
		WorkflowProcess process = (WorkflowProcess) object;
		if (workflowActivity.isActive(process, userToUse)) {
		    if (generateAMultipleSignature) {
			//let's get the first and second proccesses
			AcquisitionProcess acquisitionProcess = (AcquisitionProcess) process;
			if (acquisitionProcess.getProcessNumber().equals(nrFirstProcessIdNrToUseInMultipleSig)
				|| acquisitionProcess.getProcessNumber().equals(nrSecondProcessIdNrToUseInMultipleSig)) {
			    processesToUseInMultiple.add(acquisitionProcess);
			}
		    } else if (!StringUtils.isEmpty(processIdNrToUse)) {
			if (process.getProcessNumber().equals(processIdNrToUse)) {
			    processToUse = process;
			    out.println("Found process: " + process.getDescription() + " number: " + process.getProcessNumber());
			    break;
			}
		    } else {
			processToUse = process;
			out.println("Using process: " + process.getDescription() + " number: " + process.getProcessNumber());
			break;
		    }
		}
	    }

	    if (processToUse == null && !generateAMultipleSignature)
		throw new DomainException("could.not.find.suitable.process");

	    if (generateAMultipleSignature && processesToUseInMultiple.size() < 2)
		throw new DomainException("could.not.find.all.needed.processes.for.multiple");

	    if (!generateAMultipleSignature) {
		//so now, let's create the data to be used

		//	    SigningActivityInformation activityInformation = (SigningActivityInformation) workflowActivity
		//		    .getActivityInformation(processToUse);
		//	    SignatureData signatureData = activityInformation.getAuthorizationSignatureData();

		//getting the content
		SignatureData signatureData = ((WorkflowProcessWithSigningActivities) processToUse).createActivitySignatureData(
			User.findByUsername(userToUseUsername), activityClassToUse);

		contentToSign = signatureData.getContentToSign();
	    }//if (!generateAMultipleSignature)
	    else {
		//it's the multipleSignature, let's write the XML and the XHTML output that we got
		List<ObjectSignatureData> signatureDatasToUse = new ArrayList<ObjectSignatureData>();
		for (WorkflowProcess process : processesToUseInMultiple) {
		    SignatureData signatureData = ((WorkflowProcessWithSigningActivities) process).createActivitySignatureData(
			    User.findByUsername(userToUseUsername), activityClassToUse);
		    signatureDatasToUse.add((ObjectSignatureData) signatureData);

		}
		SignatureData multipleSignatureData = MultipleObjectSignatureDataAggregator
			.getOrCreateAggregatorInstance(signatureDatasToUse);

		contentToSign = multipleSignatureData.getContentToSign();
	    }

		//let's get the original XML content to get the: signatureId; intentionString; descriptionString; roleString;
		//we can do that, because we will always have the first span of the document as a non displayed one with the start
		//of the id with the name AllContent_

		//now let's interpret it
		//TODO joantune: note, to do this accurately, use the tip at: http://stackoverflow.com/questions/484995/java-dom-get-the-xml-content-of-a-node 
		//which basicly tells you to use a XSLT to do the job, but, as a plus, it provides a pretty neat xml pretty printer to ident the output
		//		String xhtmlContentString = new String(contentToSign, Charset.forName("UTF-8"));
		//
		//		Document document = XMLResource.load(new StringReader(xhtmlContentString)).getDocument();
		//		NodeList allSpans = document.getElementsByTagName("span");
		//		Node nodeWithNeededElements = null;
		//
		//		FileOutputStream fosXml = new FileOutputStream(fileToWriteXMLTo);
		//		for (int i = 0; i < allSpans.getLength(); i++) {
		//		    //iterating through all spans and getting the first one whose id starts with AllContent
		//		    Node nodeBeingInspected = allSpans.item(i);
		//		    if (nodeBeingInspected.getAttributes() != null) {
		//			Node id = nodeBeingInspected.getAttributes().getNamedItem("id");
		//			if (id != null) {
		//			    if (id.getNodeValue() == null || id.getNodeValue().isEmpty()) {
		//				continue;
		//			    } else {
		//				//let's inspect it and see if we have what we came for
		//				if (id.getNodeValue().startsWith("AllContent")) {
		//				    debugln("Supposedly found the superNode! content: " + nodeBeingInspected.getTextContent());
		//
		//				    document.
		//				    //this is it, let's extract the needed things from here
		//				    //which is all the XML
		//				    fosXml.write(nodeBeingInspected.toString().getBytes(Charset.forName("UTF-8")));
		//
		//				}
		//			    }
		//			}
		//		    }
		//		}
		//
		//		fosXml.flush();
		//		fosXml.close();
		//		out.println("Wrote the XML content!");

		//separating the data and writing it 

		//TODO uncomment
		//	    String contentString = new String(contentToSign, Charset.forName("UTF-8"));
		//let's extract the XML from the content
		//let's remove the content which isn't pure XML:
		//	    int startIndexOfXMLContent = contentString.indexOf(BEGIN_XML_FILE) + BEGIN_XML_FILE.length();
		//	    int endIndexOfXMLContent = contentString.indexOf(END_XML_FILE);
		//	    String xmlContentString = contentString.substring(startIndexOfXMLContent, endIndexOfXMLContent);
		//
		FileOutputStream fos = new FileOutputStream(fileToWriteContentTo);
		//
		fos.write(contentToSign);
		fos.flush();
		fos.close();
		out.println("Wrote the content!");


	    //now let's abort the transaction, so that we do not inadvertibly create the SignatureData object

	    out.println("Will abort the transaction!");
	    throw new DomainException("reached the end. Aborted transaction on purpose");

	    //TODO write the XHTML file

	} catch (InstantiationException e) {
	    out.println("Got an exception " + e.getMessage());
	    e.printStackTrace();
	    throw new DomainException();
	} catch (IllegalAccessException e) {
	    out.println("Got an exception " + e.getMessage());
	    e.printStackTrace();
	    throw new DomainException();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    throw new DomainException();
	} catch (IOException e) {
	    out.println("Got an exception " + e.getMessage());
	    e.printStackTrace();
	    throw new DomainException();
	} finally {
	    pt.ist.fenixWebFramework.security.UserView.setUser(null);
	    VirtualHost.releaseVirtualHostFromThread();
	}

    }

    private void debugln(String string) {
	if (debugModeOn)
	    out.println("DEBUG: " + string);

    }
}
