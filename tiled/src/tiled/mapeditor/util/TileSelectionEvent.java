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

package tiled.mapeditor.util;

import java.util.EventObject;
import java.util.List;

import tiled.core.StatefulTile;

public class TileSelectionEvent extends EventObject {
	private static final long serialVersionUID = 4689788849006324950L;

	/** list of selected tiles. */
	private List<StatefulTile> tiles;

	public TileSelectionEvent(Object source, List<StatefulTile> tiles) {
		super(source);
		this.tiles = tiles;
	}

	public List<StatefulTile> getTiles() {
		return tiles;
	}
}
