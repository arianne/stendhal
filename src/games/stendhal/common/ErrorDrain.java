/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common;

/**
 * ErrorDrain registers error messages while executing some algorithm like parsing command line texts.
 *
 * @author Martin Fuchs
 */
public interface ErrorDrain {

	/**
	 * sets an error message
	 *
	 * @param error error message
	 */
	public void setError(String error);

	/**
	 * did an error occur?
	 *
	 * @return true, if an error occurred; false otherwise
	 */
	public boolean hasError();

	/**
	 * gets the error message
	 *
	 * @return error message or <code>null</code>
	 */
	public String getErrorString();

}
