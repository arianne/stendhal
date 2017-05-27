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
 * Utility functions to handle item names.
 *
 * @author Martin Fuchs
 */
public class ItemTools {

	/**
     * Replace underscores in the given String by spaces.
     * This is used to replace underscore characters in compound item and creature names
     * after loading data from the database.
     *
     * @param name name of item
     * @return transformed String if name contained an underscore,
     * 			or unchanged String object
     * 			or null if name was null
     */
    public static String itemNameToDisplayName(final String name) {
    	if (name != null) {
    		if (name.indexOf('_') != -1) {
    			return name.replace('_', ' ');
    		}
    	}
    	return name;
    }

}
