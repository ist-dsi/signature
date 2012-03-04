/*
 * @(#)ProcessFileSignatureDataBean.java
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
package module.signed_workflow.domain;

import java.io.Serializable;

import module.signature.domain.data.GenericSourceOfInfoForSignatureDataBean;
import module.workflow.domain.ProcessFile;
import myorg.util.BundleUtil;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Wrapper bean that represents a {@link ProcessFile}
 * 
 * @author João Antunes
 * 
 */
@XStreamAlias("processFile")
public class ProcessFileSignatureDataBean implements Serializable {

    /**
     * default serial version
     */
    private static final long serialVersionUID = 1L;
    
    private final String displayName;

    private final String filename;

    @XStreamAsAttribute
    private final String contentType;

    private final String presentationName;

    private final String fileClassPresentableName;

    private final String hexSHA1MessageDigest;

    @XStreamAsAttribute
    private final boolean isDeleted;

     public ProcessFileSignatureDataBean(ProcessFile processFile) {
	this.displayName = GenericSourceOfInfoForSignatureDataBean.signalBlankString(processFile.getDisplayName());
	this.filename = GenericSourceOfInfoForSignatureDataBean.signalBlankString(processFile.getFilename());
	this.contentType = processFile.getContentType();
	this.presentationName = GenericSourceOfInfoForSignatureDataBean.signalBlankString(processFile.getPresentationName());
	this.fileClassPresentableName = GenericSourceOfInfoForSignatureDataBean.signalBlankString(BundleUtil
		.getLocalizedNamedFroClass(processFile.getClass()));
	
	this.hexSHA1MessageDigest = processFile.getHexSHA1MessageDigest();

	this.isDeleted = processFile.hasProcessWithDeleteFile();
	 
    }

}
