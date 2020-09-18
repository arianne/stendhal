/***************************************************************************
 *                    (C) Copyright 2018-2020 - Arianne                    *
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
 * Constants for occasions such as Christmas & Mine Town Weeks.
 */
public class Occasion {
	// Christmas time
	public static final Boolean CHRISTMAS = System.getProperty("stendhal.christmas") != null;

	// Easter
	public static final Boolean EASTER = System.getProperty("stendhal.easter") != null;

	// Halloween/Mine Town Weeks
	public static final Boolean MINETOWN = System.getProperty("stendhal.minetown") != null;

	public static final boolean MINETOWN_CONSTRUCTION = System.getProperty("stendhal.minetownconstruction") != null;;
}
