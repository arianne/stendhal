/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 *  
 *  modified for Stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */

package tiled.plugins;

import javax.swing.filechooser.FileFilter;

/**
 * A plugin that reads or saves files.
 * 
 * @author mtotz
 */
public interface IOPlugin extends TiledPlugin {
	/**
	 * Returns a list of FileFilters. This list is used in the FileSelection
	 * Dialogs.
	 */
	FileFilter[] getFilters();

}
