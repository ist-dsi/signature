/*
 * @(#)XMLTransformer.java
 *
 * Copyright 2011 Instituto Superior Tecnico
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
package module.signature.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * 
 * This class has the purpose of testing the XSLT that is used in the Signature
 * module.
 * 
 * Seen that there are very noticeable differences between for instance,
 * Firefox's interpretation of XSLT and the one used by the module (at least
 * once, things worked in Firefox but the module wasn't producing the expected
 * XHTML) this module serves the purpose of given a XML created for instance
 * with {@link CreateExampleSignatureData} and the XSLT that is used by the
 * module, to actually test the creation of the XHTML without needing to go
 * through an activity that does it
 * 
 * @author João Antunes
 * 
 */
public class XMLTransformer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Wrong number of arguments. Got " + args.length + " arguments");
			usage();
		} else {
			//let's open the files;
			File xmlFile = new File(args[0]);
			File xsltFile = new File(args[1]);
			File destinationFile = new File(args[2]);

			//read the contents of the first and the second, and check if we can write on the third
			//check if we can read the first two and write to the third
			if (!xmlFile.canRead()) {
				System.out.println("Could not read the XML file at: " + args[0]);
			}
			if (!xsltFile.canRead()) {
				System.out.println("Could not read the XSLT file at: " + args[1]);
			}
			try {

				destinationFile.createNewFile();
				if (!destinationFile.canWrite()) {
					System.out.println("Could not write to the destination file at: " + args[2]);
				}
				if (!destinationFile.canWrite() || !xsltFile.canRead() || !xmlFile.canRead()) {
					usage();
					System.exit(-1);
					return;
				}
				System.out.println("Transforming " + args[0] + " into " + args[2] + " using " + args[1]);

				StreamSource xmlSourceFile = new StreamSource(xmlFile);
				StreamSource xsltSourceFile = new StreamSource(xsltFile);
				StreamResult destinationResultFile = new StreamResult(new FileOutputStream(destinationFile));

				TransformerFactory tFactory = TransformerFactory.newInstance();
				TransformerImpl transformer = (TransformerImpl) tFactory.newTransformer(xsltSourceFile);

				transformer.transform(xmlSourceFile, destinationResultFile);

				System.out.println("Transformed " + args[0] + " into " + args[2] + " using " + args[1]);

			} catch (TransformerConfigurationException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				usage();
			} catch (TransformerException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				usage();

			} catch (IOException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				usage();
			}

		}

	}

	private static void usage() {
		System.out.println("Please execute the app with the following parameters:");
		System.out.println("appName pathToXMLFile pathToXSLTFile destinationFile");
		System.out.println("\n**Note**: this will override any content that destinationFile might already have");

	}

}
