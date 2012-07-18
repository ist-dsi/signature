/*
 * @(#)SignatureLayoutContext.java
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
package module.signature.presentationTier;

import pt.ist.bennu.core.presentationTier.Context;

import org.apache.struts.action.ActionForward;

/**
 * 
 * @author Diogo Figueiredo
 * 
 */
public class SignatureLayoutContext extends Context {

    @Override
    public ActionForward forward(String forward) {
	return new ActionForward(forward);
    }

    public ActionForward forward() {
	return forward("");
    }
}
