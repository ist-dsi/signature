/*
 * @(#)ProcessFileConverter.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: João Antunes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Digital Signature Workflow Integration Module.
 *
 *   The Digital Signature Workflow Integration Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Digital Signature Workflow Integration Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Digital Signature Workflow Integration Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.signed_workflow.xmlconverters;

import module.workflow.domain.ProcessFile;
import module.workflow.domain.WorkflowProcess;
import pt.ist.bennu.core.util.BundleUtil;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * 
 * @author João Antunes
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
