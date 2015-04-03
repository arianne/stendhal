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
	/** General testing property */
	public static final boolean GENERAL =
			(System.getProperty("testing") != null);
	
	/** Debugging */
	public static final boolean DEBUG =
			(System.getProperty("DEBUG") != null);
	
	/** Testing actions system property */
	public static final boolean ACTIONS =
			(System.getProperty("testing.actions") != null);
	
	/** Testing combat system property */
	public static final boolean COMBAT =
			(System.getProperty("testing.combat") != null);
	
	/** Testing outfit system property */
	public static final boolean OUTFITS =
			(System.getProperty("testing.outfits") != null);
}
