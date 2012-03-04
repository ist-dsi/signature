/*
 * @(#)SignatureDataSourceOfInfoInterface.java
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
package module.signature.domain.data.interfaces;

import module.signature.domain.data.SignatureData;
import module.signature.domain.data.SignatureDataFormat;

/**
 * Interface used to mark the objects that can/should be used to generate the
 * Signature document, that is, the SignatureData and to transport the Signature
 * 
 * @author João Antunes
 * 
 */
public interface SignatureDataSourceOfInfoInterface {

    public SignatureDataFormat getSignatureDataFormat();

    /**
     * 
     * @return the {@link SignatureData}, if it exists, associated with this
     *         bean
     */
    public SignatureData getSignatureData();


    //    public SignatureData createSignatureData();


}
