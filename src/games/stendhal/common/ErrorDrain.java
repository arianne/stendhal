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

	void setError(String error);
	boolean hasError();
	String getErrorString();
	
}
