/*
 * @(#)SignatureExporterXML.java
 *
 * Copyright 2010 Instituto Superior Tecnico
 * Founding Authors: Diogo Figueiredo
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Digital Signature Module.
 *
 *   The Digital Signature Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Signature Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Signature Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.signature.util.exporter;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Diogo Figueiredo
 * @author João Antunes
 * 
 */
public class SignatureExporterXML {

	private final Set<Class> metaDataClasses = new HashSet<Class>();

	public SignatureExporterXML() {
	}

	public SignatureExporterXML(Set<Class> classesToUse) {
		if (classesToUse != null) {
			metaDataClasses.addAll(classesToUse);
		}
	}

	/*
	 * public String export(SignatureIntention signature, SignatureMetaData
	 * metaData) throws ExporterException {
	 * 
	 * try { metaDataClasses.add(metaData.getClass());
	 * 
	 * Class[] clazzes = new Class[metaDataClasses.size()];
	 * 
	 * int i = 0; for (Class clazz : metaDataClasses) { clazzes[i] = clazz; i++;
	 * }
	 * 
	 * JAXBContext context = JAXBContext.newInstance(clazzes);
	 * 
	 * Marshaller m = context.createMarshaller();
	 * m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	 * 
	 * StringWriter writer = new StringWriter(); m.marshal(metaData, writer);
	 * 
	 * String xml = writer.toString();
	 * 
	 * System.out.println("XML Generated: ----------------");
	 * System.out.println(xml); System.out.println("-----");
	 * 
	 * return xml;
	 * 
	 * } catch (JAXBException ex) { throw new ExporterException(ex); } }
	 * 
	 * public SignatureMetaData rebuild(String content) throws ExporterException
	 * { try { JAXBContext context =
	 * JAXBContext.newInstance(SignatureMetaDataRoot.class); Unmarshaller m =
	 * context.createUnmarshaller();
	 * 
	 * StringReader reader = new StringReader(content);
	 * 
	 * return (SignatureMetaData) m.unmarshal(reader); } catch (JAXBException
	 * ex) { throw new ExporterException(ex); } }
	 * 
	 * // private void generateSchema(JAXBContext context, SignatureMetaData //
	 * signatureMetaData) throws ExporterException { // final File schemaFile =
	 * new File("generated" + File.separator + "schema", //
	 * signatureMetaData.getIdentification()); // // try { //
	 * context.generateSchema(new SchemaOutputResolver() { // @Override //
	 * public Result createOutput(String namespaceUri, String schemaName) throws
	 * // IOException { // final StreamResult schemaResult = new
	 * StreamResult(schemaFile); // return schemaResult; // } // }); // //
	 * System.out.println("------- Schema generated in: " + //
	 * schemaFile.getAbsolutePath()); // } catch (IOException ex) { // throw new
	 * ExporterException(ex); // } // }
	 */
}
