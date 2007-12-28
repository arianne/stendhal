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
 *  Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.plugins;

import java.util.List;

/**
 * The mother of plugins for tiled.
 * 
 * @author mtotz
 */
public interface TiledPlugin {
	/** returns the description of the plugin. */
	String getPluginDescription();

	/**
	 * Sets the list where the plugin can store all messages it wants to tell
	 * the user.
	 */
	void setMessageList(List<String> errorList);
}
