/*
 * @(#)SignatureDataException.java
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
package module.signature.exception;

import java.util.List;

import module.signature.domain.data.SignatureData;

/**
 * Class used to present to the user an error that ocurred with the {@link SignatureData}
 * 
 * @author João Antunes
 * 
 */
public class SignatureDataException extends SignatureException {

	public SignatureDataException(String string, Throwable e) {
		super(string, e);
	}

	public SignatureDataException(String string) {
		super(string);
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
	public SignatureDataException(String string, boolean b, List<String> arguments) {
		//TODO
		super(string, b, arguments);
	}

	public SignatureDataException(Throwable e) {
		super("unspecified.message.got.exception", e);
	}

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

}
