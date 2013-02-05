/*
 * @(#)MultipleGenericSourceOfInfoAggregationBean.java
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import module.signature.domain.data.interfaces.ConvertibleToXMLAndXHTML;

import org.joda.time.DateTime;

import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.exceptions.DomainException;

/**
 * 
 * @author João Antunes
 * 
 */
public class MultipleGenericSourceOfInfoAggregationBean extends GenericSourceOfInfoForSignatureDataBean implements
        ConvertibleToXMLAndXHTML {

    private static Random randomNrForSigIdGeneration = new Random();
    /**
     * Version 1 - equivalent to Bennu Signature system v0.1 in FENIX-JIRA
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Field used to make sure that the various SignatureDataBeans are in the
     * XML, it's value should never be used directly but always from the
     * objectSignatureDatas
     */
    @XStreamImplicit(itemFieldName = "aggregatedSignature")
    private final List<GenericSourceOfInfoForSignatureDataBean> signatureDataBeans;

    /*
     * TODO commented the omit to see if we really need the signatureDataBeans
     * or not. We can probably implement a Converter to make sure that we don't
     * need the signatureDataBeans i.e. getting the actual persisted object and
     * it's fields.. but probably won't happen in the first version
     */
    //    @XStreamOmitField
    private final List<ObjectSignatureData> objectSignatureDatas;

    MultipleGenericSourceOfInfoAggregationBean(List<ObjectSignatureData> objectSignatureDatas, SignatureData signatureData) {
        super(signatureData);
        //let's get the beans of the associated objectSignatureDatas
        validateListOfObjectSignatureDatas(objectSignatureDatas);
        this.objectSignatureDatas = objectSignatureDatas;
        //let's extract the beans from each of the ObjectSignatureData
        this.signatureDataBeans = new ArrayList<GenericSourceOfInfoForSignatureDataBean>();
        for (ObjectSignatureData objectSignatureData : this.getObjectSignatureDatas()) {
            //do note that the genericSourceOfInfoForSignatureDataBean might be null as they are not persistent!
            if (objectSignatureData.getGenericSourceOfInfoForSignatureDataBean() == null) {
                //if that's the case we should fill them again
                objectSignatureData.fillSourceOfInfoBean(objectSignatureData.getCreationDateTime(),
                        objectSignatureData.getSignatureId());
            }
            this.signatureDataBeans.add(objectSignatureData.getGenericSourceOfInfoForSignatureDataBean());
        }
        validateListOfBeans(this.signatureDataBeans);
        generateDescriptionIntentionAndIdStrings();
    }

    /**
     * 
     * @param listOfBeans
     *            the ArrayList of {@link GenericSourceOfInfoForSignatureDataBean} wich contains
     *            the data upon which one will construct the multiple signatures
     * @throws DomainException
     *             if the listOfBeans isn't valid - that is, it has beans of
     *             different classes
     */
    private void validateListOfBeans(List<GenericSourceOfInfoForSignatureDataBean> listOfBeans) throws DomainException {
        if (listOfBeans == null || listOfBeans.isEmpty()) {
            throw new DomainException("invalid.use.of.MultipleObjectSignatureData.only.objects.of.the.same.class.are.allowed",
                    MultipleObjectSignatureDataAggregator.getBundle());
        }
        Class classOfOneListOfBeans = listOfBeans.get(0).getClass();
        for (GenericSourceOfInfoForSignatureDataBean genericSourceOfInfoForSignatureDataBean : listOfBeans) {
            if (!classOfOneListOfBeans.isInstance(genericSourceOfInfoForSignatureDataBean)) {
                throw new DomainException(
                        "invalid.use.of.MultipleObjectSignatureData.only.objects.of.the.same.class.are.allowed",
                        MultipleObjectSignatureDataAggregator.getBundle());
            }
        }

    }

    private void validateListOfObjectSignatureDatas(List<ObjectSignatureData> objectSignatureDatas) {
        if (objectSignatureDatas == null || objectSignatureDatas.isEmpty()) {
            throw new DomainException("invalid.use.of.MultipleObjectSignatureData.only.objects.of.the.same.class.are.allowed",
                    MultipleObjectSignatureDataAggregator.getBundle());
        }
        Class classOfOneListOfBeans = objectSignatureDatas.get(0).getClass();
        for (ObjectSignatureData objectSignatureData : objectSignatureDatas) {
            if (!classOfOneListOfBeans.isInstance(objectSignatureData)) {
                throw new DomainException(
                        "invalid.use.of.MultipleObjectSignatureData.only.objects.of.the.same.class.are.allowed",
                        MultipleObjectSignatureDataAggregator.getBundle());
            }
        }
    }

    @Override
    public String generateIntentionString() {

        //TODO acrescentar o nome do tipo de assinatura através de uma convenção e também a acção
        return "... múltiplas assinaturas de ...";
    }

    @Override
    public String generateDescriptionString() {
        //TODO acrescentar o nome do tipo de assinatura através de uma convenção
        return "Assinatura múltipla de assinaturas de ...";
    }

    @Override
    public String generateSignatureId() {
        DateTime currentDateTime = new DateTime();
        String signatureId =
                "MultipleSignature_" + signatureDataBeans.get(0).getClass().getSimpleName() + "_" + currentDateTime.getMillis()
                        + "_" + randomNrForSigIdGeneration.nextInt(100000);

        return signatureId;
    }

    @Override
    public Source xsltSource() {
        return new StreamSource(getClass().getResourceAsStream("/resources/xslts/signatureAggregations.xslt"));
    }

    @Override
    public SignatureDataFormat getSignatureDataFormat() {
        return SignatureDataFormat.AGGREGATION_OF_XML_XHTML_AND_XSLT;
    }

    public byte[] getSentContentToBeSigned() {
        for (ObjectSignatureData objectSignatureData : objectSignatureDatas) {
            objectSignatureData.validateAccessToSignatureData(Collections.singleton(UserView.getCurrentUser()));
        }
        SignatureDataFormat signatureDataFormat = getSignatureDataFormat();
        signatureDataFormat.setDataSourceObject(this);
        return signatureDataFormat.getSignatureDataContent();
    }

    //    @Override
    //    public SignatureData createSignatureData() {
    //	if (getSignatureData() != null)
    //	    return getSignatureData();
    //	else {
    //	    return new MultipleObjectSignatureDataAggregator(this.getObjectSignatureDatas(), getAuxiliarySignatureContentString());
    //	}
    //    }

    public List<ObjectSignatureData> getObjectSignatureDatas() {
        return objectSignatureDatas;
    }

}
