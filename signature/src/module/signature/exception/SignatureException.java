/*
 * @(#)SignatureException.java
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
package module.signature.exception;

import java.util.List;
import java.util.ResourceBundle;

import myorg.domain.exceptions.DomainException;

/**
 * Class used to signal an exception, and that can be used to render a message
 * to the user as well
 * 
 * @author Diogo Figueiredo
 * @author João Antunes
 * 
 */
public class SignatureException extends DomainException {

    public SignatureException(String string, Throwable e) {
	super(string, e, ResourceBundle.getBundle("resources/SignatureResources"));
    }

    public SignatureException(String string) {
	super(string, ResourceBundle.getBundle("resources/SignatureResources"));
    }

    /**
     * 
     * @param string
     * @param b
     *            if b is set to true, this will generate a log message with a
     *            special format to be interpreted by a script which will
     *            deliver a notification
     * @param arguments
     *            the arguments for the notification, or null
     */
    public SignatureException(String string, boolean b, List<String> arguments) {
	//TODO
    }

}
