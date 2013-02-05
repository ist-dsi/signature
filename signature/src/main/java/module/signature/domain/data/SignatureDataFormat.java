/*
 * @(#)SignatureDataFormat.java
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
package module.signature.domain.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Set;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import module.signature.domain.data.interfaces.ConvertibleToXMLAndXHTML;
import module.signature.exception.SignatureDataException;
import module.signature.util.XAdESValidator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.DomainException;

/**
 * 
 * @author João Antunes
 * 
 */
public enum SignatureDataFormat {
    XML_XHTML_AND_XSLT {

        /*
         * Now we do not need these delimeters anymore as the XML content is
         * inside the first span tag in the XHTML But it's not harmful to keep
         * them
         */
        public final static String BEGIN_XML_FILE = "------- XML START -------\n";
        public final static String END_XML_FILE = "------- XML END -------\n";
        public final static String BEGIN_XHTML_FILE = "------- XHTML START -------\n";
        public final static String END_XHTML_FILE = "------- XHTML END -------\n";

        private GenericSourceOfInfoForSignatureDataBean convertibleToXmlAndXHTMLObject;

        /**
         * 
         * @param includeXMLContent
         *            if true, it will put the XML content outside of the
         *            generated XHTML content. Note that this should not be
         *            needed as now the XML is inserted within the XHTML even if
         *            this parameter is set to false
         * @return the XHTML content or the XML + XHTML content delimited with
         *         the delimiters above {@link #BEGIN_XHTML_FILE} {@link #BEGIN_XML_FILE} {@link #END_XHTML_FILE}
         *         {@link #END_XML_FILE}
         */
        public byte[] getSignatureDataContent(boolean includeXMLContent) {
            //serialize the object into XML
            if (convertibleToXmlAndXHTMLObject == null) {
                throw new DomainException("incorrect.state.calling.getSignatureDataContent.source.is.null");
            }
            ByteArrayOutputStream byteArrayXmlOutputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream resultArrayOutputStream = new ByteArrayOutputStream();
            OutputStreamWriter outputStreamWriter = null;
            try {
                //this does not make sense here anymore, it makes more sense in the methods that retrieve the data (as the document
                //might be persisted, and thus might not be always generated when shown to the user
                //		convertibleToXmlAndXHTMLObject.validateLoggedInUserCanSeeSigData();

                outputStreamWriter = new OutputStreamWriter(byteArrayXmlOutputStream, "UTF-8");

                //let's get the XML:
                XStream xStream = new XStream();
                xStream.autodetectAnnotations(true);
                xStream.toXML(convertibleToXmlAndXHTMLObject, outputStreamWriter);

                //close the outputStreamWriter
                outputStreamWriter.flush();
                outputStreamWriter.close();

                logger.debug("---------------- BEGIN XML Generated: ----------------\n" + byteArrayXmlOutputStream.toString()
                        + "\n---------------- END XML Generated ----------------");

                ByteArrayInputStream byteArrayXmlInputStream = new ByteArrayInputStream(byteArrayXmlOutputStream.toByteArray());

                StreamSource xmlStreamSource = new StreamSource(byteArrayXmlInputStream);

                TransformerFactory tFactory = TransformerFactory.newInstance();
                Transformer transformer = tFactory.newTransformer(convertibleToXmlAndXHTMLObject.xsltSource());
                TransformerImpl transformerImpl = (TransformerImpl) transformer;
                StringWriter output = new StringWriter();
                transformerImpl.transform(xmlStreamSource, new StreamResult(output));

                String result = output.toString();
                //TODO remove these DEBUG prints:
                //		System.out.println("---------------- BEGIN XHTML Generated: ----------------");
                //		System.out.println(result);
                //		System.out.println("---------------- END XHTML Generated ----------------");

                //let's make a new outputStreamWriter to write all of the content
                outputStreamWriter = new OutputStreamWriter(resultArrayOutputStream);

                //let's signal the init of the XML file
                if (includeXMLContent) {
                    outputStreamWriter.append(BEGIN_XML_FILE);
                    outputStreamWriter.write(byteArrayXmlOutputStream.toString());
                    //the end
                    outputStreamWriter.append(END_XML_FILE);
                    //let's signal also the XHTML when writing it
                    outputStreamWriter.append(BEGIN_XHTML_FILE);
                }
                outputStreamWriter.append(result);
                if (includeXMLContent) {
                    outputStreamWriter.append(END_XHTML_FILE);
                }
                outputStreamWriter.flush();

                //TODO ?! clean up the house in between the operations making sure that the space is freed

                return resultArrayOutputStream.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
                throw new SignatureDataException(e);
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
                throw new SignatureDataException(e);
            } catch (TransformerException e) {
                e.printStackTrace();
                throw new SignatureDataException(e);
            } finally {
                if (outputStreamWriter != null) {
                    try {
                        outputStreamWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new SignatureDataException(e);
                    }
                }
            }
        }

        @Override
        public void setDataSourceObject(Object object) {
            this.convertibleToXmlAndXHTMLObject = (GenericSourceOfInfoForSignatureDataBean) object;
        }

        @Override
        public byte[] getBrowserRenderableContentToSign() {
            return getSignatureDataContent(false);
        }

        @Override
        public byte[] getSignatureDataContent() {
            /*
             * by default we should call the method below with the argument as
             * false. Please see the getSignatureDataContent(boolean) javadoc
             * for the reason why
             */
            return getSignatureDataContent(false);
        }

        @Override
        public void validateSignature(SignatureData signatureDataInfoSource, Set<User> permittedUsers, Set<User> excludedUsers,
                boolean mustIncludeAllPermittedUsers) throws SignatureDataException {
            XAdESValidator.validateSentAndReceivedContent(signatureDataInfoSource.getAuxiliarySignatureContentString(),
                    signatureDataInfoSource.getSentContentToBeSigned(), permittedUsers, excludedUsers,
                    mustIncludeAllPermittedUsers);

        }

        @Override
        public String getFileNameExtension() {
            return ".xhtml";
        }

        @Override
        public String getContentType() {
            return "application/xhtml+xml";
        }
    },
    AGGREGATION_OF_XML_XHTML_AND_XSLT {

        public final static String BEGIN_XML_FILE = "------- XML START -------\n";
        public final static String END_XML_FILE = "------- XML END -------\n";
        public final static String BEGIN_XHTML_FILE = "------- XHTML START -------\n";
        public final static String END_XHTML_FILE = "------- XHTML END -------\n";

        public final static String BODY_START_TAG = "<body>";
        public final static String BODY_END_TAG = "</body>";

        private MultipleGenericSourceOfInfoAggregationBean multipleAggregationBean;

        @Override
        public byte[] getSignatureDataContent() {
            /*
             * by default we should call the method below with the argument as
             * false. Please see the getSignatureDataContent(boolean) javadoc
             * for the reason why
             */
            return getSignatureDataContent(false);
        }

        public byte[] getSignatureDataContent(boolean includeXMLContent) {
            if (multipleAggregationBean == null) {
                throw new DomainException("incorrect.state.calling.getSignatureDataContent.source.is.null");
            }

            MultipleGenericSourceOfInfoAggregationBean tipoCena = multipleAggregationBean;
            ByteArrayOutputStream byteArrayXmlOutputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream resultArrayOutputStream = new ByteArrayOutputStream();
            OutputStreamWriter outputResultStreamWriter;
            outputResultStreamWriter = new OutputStreamWriter(resultArrayOutputStream);
            try {
                OutputStreamWriter outputXmlStreamWriter = new OutputStreamWriter(byteArrayXmlOutputStream, "UTF-8");
                /*
                 * each of the composing objects should be serialized into XML
                 * and its XHTML content extracted only if we don't already have
                 * a persisted version of the generated document, which we
                 * should have
                 */

                //but we should only need to use one transformer as the xslt of each bean should be alike

                //so, let's get the already persisted original data, if none is found, we throw an exception

                for (ObjectSignatureData objectSignatureData : multipleAggregationBean.getObjectSignatureDatas()) {
                    //so for now we will make some sanity checks to make sure that the ObjectSignatureData
                    //data that we retrieve is of the XML_XHTML_AND_XSLT type

                    if (!objectSignatureData.getSignatureDataFormat().equals(SignatureDataFormat.XML_XHTML_AND_XSLT)) {
                        throw new SignatureDataException("cannot.aggregate.documents.of.different.data.format.types");
                    }
                    //so now let's just retrieve the already generated XHTML and process it
                    String result = new String(objectSignatureData.getOriginalSignatureDataContent(), Charset.forName("UTF-8"));

                    //strip the content of the header and the <body> tag
                    //TODO make this case insensitive, just in case the body tag has uppercases
                    result = StringUtils.substringBetween(result, BODY_START_TAG, BODY_END_TAG);

                    //now we should add the link for the anchor and the link

                    String divWithId = "<a name=\"" + objectSignatureData.getSignatureId() + "\"/>\n";

                    String linkForTheTop = "<a href=\"#top\">Ir para o topo</a>\n";

                    result = divWithId + linkForTheTop + result;

                    //let's write the result
                    outputResultStreamWriter.append(result);
                    outputResultStreamWriter.flush();
                }

                TransformerFactory tFactory = TransformerFactory.newInstance();

                /*
                 * Commented as it can be useful in the future (generation of
                 * the XHTML by using the bean)
                 */
                //		Transformer transformer = tFactory.newTransformer(((ConvertibleToXMLAndXHTML) multipleAggregationBean
                //			.getSignatureDataBeans().get(0)).xsltSource());

                //		for (GenericSourceOfInfoForSignatureDataBean infoBean : multipleAggregationBean.getSignatureDataBeans()) {
                //
                //		    //let's get the XML:
                //		    XStream xStream = new XStream();
                //		    xStream.autodetectAnnotations(true);
                //		    xStream.toXML(infoBean, outputXmlStreamWriter);
                //
                //		    //close the outputStreamWriter
                //		    outputXmlStreamWriter.flush();
                //
                //		    //TODO remove these debug prints:
                //		    //		    System.out.println("---------------- BEGIN XML Generated: ----------------");
                //		    //		    System.out.println(byteArrayXmlOutputStream.toString());
                //		    //		    System.out.println("---------------- END XML Generated ----------------");
                //
                //		    ByteArrayInputStream byteArrayXmlInputStream = new ByteArrayInputStream(
                //			    byteArrayXmlOutputStream.toByteArray());
                //
                //		    StreamSource xmlStreamSource = new StreamSource(byteArrayXmlInputStream);
                //
                //		    StringWriter output = new StringWriter();
                //		    transformer.transform(xmlStreamSource, new StreamResult(output));
                //
                //		    String result = output.toString();
                //
                //		    //trim the result to exclude everything else but the body
                //
                //		    //TODO make this case insensitive, just in case the body tag has uppercases
                //		    result = StringUtils.substringBetween(result, BODY_START_TAG, BODY_END_TAG);
                //
                //		    //now we should add the link for the anchor and the link
                //
                //		    String divWithId = "<a name=\"" + infoBean.getSignatureId() + "\"/>\n";
                //
                //		    String linkForTheTop = "<a href=\"#top\">Ir para o topo</a>\n";
                //
                //		    result = divWithId + linkForTheTop + result;
                //
                //		    //TODO remove these DEBUG prints:
                //		    //		System.out.println("---------------- BEGIN XHTML Generated: ----------------");
                //		    //		System.out.println(result);
                //		    //		System.out.println("---------------- END XHTML Generated ----------------");
                //
                //		    //let's make a new outputStreamWriter to write all of the content
                //
                //		    outputResultStreamWriter.append(result);
                //		    outputResultStreamWriter.flush();
                //		    byteArrayXmlOutputStream.reset();
                //
                //		}
                /*
                 * Commented as it can be useful in the future (generation of
                 * the XHTML by using the bean)
                 */

                //generate this bean's XML

                //let's get the XML:
                XStream xStream = new XStream();
                //		Class[] types = (Class[]) ArrayUtils.addAll(multipleAggregationBean.getClass().getClasses(),
                //			multipleAggregationBean
                //			.getAggregatedSignatures().get(0).getClass().getClasses());
                //		types = (Class[]) ArrayUtils.add(types, ObjectSignatureData.GenericSourceOfInfoBean.class);

                //		xStream.processAnnotations(types);
                xStream.autodetectAnnotations(true);
                xStream.toXML(multipleAggregationBean, outputXmlStreamWriter);

                //close the outputStreamWriter
                outputXmlStreamWriter.flush();
                outputXmlStreamWriter.close();

                logger.debug("---------------- BEGIN XML Generated: ----------------\n" + byteArrayXmlOutputStream.toString()
                        + "\n---------------- END XML Generated ----------------");

                ByteArrayInputStream byteArrayXmlInputStream = new ByteArrayInputStream(byteArrayXmlOutputStream.toByteArray());

                //now we are transforming the XML in XHTML based on the multipleBean XML

                StreamSource xmlStreamSource = new StreamSource(byteArrayXmlInputStream);

                StringWriter output = new StringWriter();
                Transformer transformer =
                        tFactory.newTransformer(((ConvertibleToXMLAndXHTML) multipleAggregationBean).xsltSource());
                transformer.transform(xmlStreamSource, new StreamResult(output));

                String result = output.toString();

                //currently we have the multiple XHTML with the links and without the XHTML head and the body tags in the resultArrayOutputStream
                //, the XML of this AggregatorBean in the byteArrayXmlOutputStream and a very basic XHTML in the result, which must be injected with the
                //resultArrayOutputStream. Let's inject it here.

                result = StringUtils.replace(result, "#CONTENT", resultArrayOutputStream.toString("UTF-8"));
                //discard the string as we already injected it
                resultArrayOutputStream.reset();

                //so now result has the XHTML, we just need to add the XML outside of the XHTML if this is the case

                if (includeXMLContent) {
                    outputResultStreamWriter.append(BEGIN_XML_FILE);
                    //now here we should append the XML of the Bean that has the List of Beans
                    outputResultStreamWriter.append(byteArrayXmlOutputStream.toString("UTF-8"));
                    outputResultStreamWriter.append(END_XML_FILE);
                    outputResultStreamWriter.append(BEGIN_XHTML_FILE);
                }

                outputResultStreamWriter.append(result);

                if (includeXMLContent) {
                    outputResultStreamWriter.append(END_XHTML_FILE);
                }
                outputResultStreamWriter.flush();
                outputResultStreamWriter.close();

                logger.debug("---------------- Generated file: ----------------\n" + resultArrayOutputStream.toString("UTF-8")
                        + "\n---------------- END of Generated file ----------------");

                return resultArrayOutputStream.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
                throw new DomainException();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
                throw new DomainException();
            } catch (TransformerException e) {
                e.printStackTrace();
                throw new DomainException();
            }
        }

        @Override
        public void setDataSourceObject(Object object) {
            multipleAggregationBean = (MultipleGenericSourceOfInfoAggregationBean) object;

        }

        @Override
        public byte[] getBrowserRenderableContentToSign() {
            // TODO Auto-generated method stub
            //not sure if this is supposed to be called... for now it's as is
            return null;
        }

        @Override
        public void validateSignature(SignatureData signatureDataSourceOfInfo, Set<User> permittedUsers, Set<User> excludedUsers,
                boolean mustIncludeAllPermittedUsers) throws SignatureDataException {

            XAdESValidator.validateSentAndReceivedContent(signatureDataSourceOfInfo.getAuxiliarySignatureContentString(),
                    signatureDataSourceOfInfo.getSentContentToBeSigned(), permittedUsers, excludedUsers,
                    mustIncludeAllPermittedUsers);

        }

        @Override
        public String getFileNameExtension() {
            return "multiple.xhtml";
        }

        @Override
        public String getContentType() {
            return "application/xhtml+xml";
        }
    };

    private static final Logger logger = Logger.getLogger(SignatureDataFormat.class);

    /**
     * @return the byte array of with the signature data to be signed
     */
    public abstract byte[] getSignatureDataContent();

    public abstract void setDataSourceObject(Object object);

    /**
     * 
     * This method checks for the validity of the signatureData, that is: . If
     * the signature content which was received is indeed equal to the one sent;
     * . If the signers of the received data are correct; . Existence of a valid
     * timestamp from a TSA, that is the timestamp is not in the future (more
     * than 5 mins) and is indeed valid; TODO FENIX-192
     * 
     * NOTE: This method does not validate the signature itself, as the
     * {@link SignatureData#generateSignature(SignatureFormat, User)} already
     * does that
     * 
     * @param signatureDataWithSignedDocument
     *            the signatureData to be validated, this one has bot the
     *            original and the already signed content
     * @param permittedUsers
     *            a Set of users which are explicitly permitted to be the
     *            signers of the signature, or null/empty set if any user is
     *            permitted
     * @param excludedUsers
     *            a
     * @throws SignatureDataException
     *             if the SignatureData doesn't validate
     */
    public abstract void validateSignature(SignatureData signatureDataWithSignedDocument, Set<User> permittedUsers,
            Set<User> excludedUsers, boolean mustIncludeAllPermittedUsers) throws SignatureDataException;

    public abstract byte[] getBrowserRenderableContentToSign();

    public abstract String getFileNameExtension();

    public abstract String getContentType();
}
