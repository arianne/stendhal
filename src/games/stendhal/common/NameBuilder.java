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
 * NameBuilder to build a name separated by spaces.
 *
 * @author Martin Fuchs
 */
public class NameBuilder {

	protected StringBuilder buffer = new StringBuilder();

	protected boolean first = true;

	/**
	 * Append a string, separate by space if not empty.
	 *
	 * @param str
	 */
	public void append(final String str) {
		if ((str != null) && (str.length() > 0)) {
    		if (first) {
    			first = false;
    		} else {
    			buffer.append(' ');
    		}

    		buffer.append(str);
		}
	}

	/**
	 * Retun true if still empty.
	 *
	 * @return empty flag
	 */
	public boolean isEmpty() {
		return first;
	}

	@Override
	public String toString() {
		return buffer.toString();
	}

}
