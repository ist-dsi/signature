/**
 * 
 */
package module.signature.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import module.signature.domain.data.SignatureData;
import module.signed_workflow.interfaces.SigningActivityInformation;
import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;
import myorg.domain.User;
import myorg.domain.VirtualHost;
import myorg.domain.exceptions.DomainException;
import myorg.domain.scheduler.WriteCustomTask;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.AcquisitionProcess;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.activities.commons.Authorize;
import pt.ist.fenixframework.pstm.Transaction;

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

    public final Class processClassToUse = AcquisitionProcess.class;
    public final Class activityClassToUse = Authorize.class;
    public final String userToUseUsername = "ist12282";
    public final File fileToWriteXmlTo = new File("/tmp/output.xml");
    public final File fileToWriteXHTMLTo = new File("/tmp/output.xhtml");

    public final static String BEGIN_XML_FILE = "------- XML START -------\n";
    public final static String END_XML_FILE = "------- XML END -------\n";
    public final static String BEGIN_XHTML_FILE = "------- XHTML START -------\n";
    public final static String END_XHTML_FILE = "------- XHTML END -------\n";

    @Override
    protected void doService() {

	VirtualHost.setVirtualHostForThread("dot.ist.utl.pt");
	if (VirtualHost.getVirtualHostForThread() == null)
	    throw new Error("Virtual host not found - i.e. 'vai pentear macacos' by Susana Fernandes");
	final User userToUse = User.findByUsername(userToUseUsername);
	try {
	    WorkflowProcess processToUse = null;
	    WorkflowActivity<WorkflowProcess, ActivityInformation<WorkflowProcess>> workflowActivity = (WorkflowActivity<WorkflowProcess, ActivityInformation<WorkflowProcess>>) activityClassToUse
		    .newInstance();
	    //let's iterate through the PaymentProcesses and find one suitable
	    for (Object object : WorkflowProcess.getAllProcesses(processClassToUse)) {
		WorkflowProcess process = (WorkflowProcess) object;
		if (workflowActivity.isActive(process, userToUse)) {
		    processToUse = process;
		    out.println("Using process: " + process.getDescription() + " number: " + process.getProcessNumber());
		    break;
		}
	    }

	    if (processToUse == null)
		throw new DomainException("could.not.find.suitable.process");

	    //so now, let's create the data to be used

	    SigningActivityInformation activityInformation = (SigningActivityInformation) workflowActivity
		    .getActivityInformation(processToUse);
	    SignatureData signatureData = activityInformation.getAuthorizationSignatureData();

	    //getting the content

	    byte[] contentToSign = signatureData.getContentToSign();

	    //separating the data and writing it 

	    String contentString = new String(contentToSign, Charset.forName("UTF-8"));
	    //let's extract the XML from the content
	    //let's remove the content which isn't pure XML:
	    int startIndexOfXMLContent = contentString.indexOf(BEGIN_XML_FILE) + BEGIN_XML_FILE.length();
	    int endIndexOfXMLContent = contentString.indexOf(END_XML_FILE);
	    String xmlContentString = contentString.substring(startIndexOfXMLContent, endIndexOfXMLContent);

	    FileOutputStream fos = new FileOutputStream(fileToWriteXmlTo);

	    fos.write(xmlContentString.getBytes(Charset.forName("UTF-8")));
	    fos.flush();
	    fos.close();
	    out.println("Wrote the XML content!");

	    //now let's abort the transaction, so that we do not inadvertibly create the SignatureData object

	    Transaction.abort();

	    out.println("Aborted the transaction!");

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
	    VirtualHost.releaseVirtualHostFromThread();
	}

    }
}
