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
 * Gathers all Debug constants in one place.
 *
 * @author mtotz
 */
public class Debug {
	/** if this is enable tileset are loaded on demand. */
	public static final boolean VERY_FAST_CLIENT_START = true;

	/** enables cycling through the panel textures by clicking at the title bar. */
	public static final boolean CYCLE_PANEL_TEXTURES = false;

	/** version. */
	// Note: This line is updated by build.xml using a regexp so be sure to adjust it in case you modify this line.
	public static final String VERSION = "1.41";

	/** pre release suffix */
	// Note: This line is updated by build.xml using a regexp so be sure to adjust it in case you modify this line.
	public static final String PRE_RELEASE_VERSION = null;

	/**
	 * Log list sizes to find the memory leak. It must be somewhere...
	 */
	public static final boolean SHOW_LIST_SIZES = false;

	/**
	 * it shows entity's view area
	 */
	public static final boolean SHOW_ENTITY_VIEW_AREA = false;
}
