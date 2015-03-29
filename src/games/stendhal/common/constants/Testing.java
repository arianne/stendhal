/***************************************************************************
 *                   (C) Copyright 2003-2015 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.constants;

/**
 * Constants defined for testing purposes.
 * 
 * @author AntumDeluge
 */
public class Testing {
	/** Testing outfit system property */
	public static final String OUTFITS = "testing.outfits";
	
	/** Testing combat system property */
	public static final String COMBAT = "testing.combat";
	
	public static Boolean enabled(String property) {
		return (System.getProperty(property) != null);
	}
}
