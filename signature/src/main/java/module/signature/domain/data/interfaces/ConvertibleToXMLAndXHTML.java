/*
 * @(#)ConvertibleToXMLAndXHTML.java
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

import javax.xml.transform.Source;

/**
 * Interface to signal the signature system and the user to make sure that the
 * object that implements this interface is convertible to XML by castor
 * introspection i.e. using it's getters to serialize it
 * 
 * @author João Antunes
 * 
 */
public interface ConvertibleToXMLAndXHTML {

    public Source xsltSource();

}
