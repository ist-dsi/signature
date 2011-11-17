/**
 * 
 */
package module.signed_workflow.xmlconverters;

import module.workflow.domain.ProcessFile;
import module.workflow.domain.WorkflowProcess;
import myorg.util.BundleUtil;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public class ProcessFileConverter implements Converter {

    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
     */
    @Override
    public boolean canConvert(Class arg0) {
	return (arg0 != null && ProcessFile.class.isAssignableFrom(arg0));
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
     */
    @Override
    public void marshal(Object processFile, HierarchicalStreamWriter writer, MarshallingContext context) {
	ProcessFile file = (ProcessFile) processFile;
	writer.startNode("displayName");
	WorkflowProcess fileProcess = file.getProcess();
	if (fileProcess != null)
	{
	    writer.addAttribute("processNumber", fileProcess.getProcessNumber());
	    writer.addAttribute("processExternalId", fileProcess.getExternalId());
	    writer.addAttribute("processDescription", fileProcess.getDescription());
	}
	writer.setValue(file.getDisplayName());
	writer.endNode();
	
	writer.startNode("filename");
	writer.addAttribute("contentType", file.getContentType());
	writer.setValue(file.getFilename());
	writer.endNode();
	
	writer.startNode("presentationName");
	writer.addAttribute("fileClassPresentableName", BundleUtil.getLocalizedNamedFroClass(file.getClass()));
	writer.setValue(file.getPresentationName());
	writer.endNode();
//	writer.addAttribute(", arg1)
//	writer.startNode(")
	//	file.getProcess()

    }

    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
	// TODO Auto-generated method stub
	return null;
    }

}
