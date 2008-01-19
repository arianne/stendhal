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

import java.awt.Rectangle;

import tiled.core.Map;
import tiled.core.MapLayer;

/**
 * indicates the type of map change.
 * 
 * @author mtotz
 */
public class MapChangedEvent {
	/** the changed map. */
	private Map map;
	private Type type;
	private Rectangle modifiedRegion;
	private MapLayer layer;

	public MapChangedEvent(Map map, Type type) {
		this(map, type, null);
	}

	public MapChangedEvent(Map map, Type type, Rectangle modifiedRegion) {
		this(map, type, modifiedRegion, null);
	}

	public MapChangedEvent(Map map, Type type, Rectangle modifiedRegion, MapLayer layer) {
		this.map = map;
		this.type = type;
		this.modifiedRegion = modifiedRegion;
		this.layer = layer;
	}

	/** the map. */
	public Map getMap() {
		return map;
	}

	/** type of change. */
	public Type getType() {
		return type;
	}

	/**
	 * @return the modifiedRegion
	 */
	public Rectangle getModifiedRegion() {
		return modifiedRegion;
	}

	/**
	 * @return the layer
	 */
	public MapLayer getLayer() {
		return layer;
	}

	/** all change types. */
	public static enum Type {
		/** name / filename / path changed .*/
		NAME,
		/** layers removed/added/set/moved up/down. */
		LAYERS,
		/** brushes added/removed .*/
		BRUSHES,
		/** size of the map changed (and with it, all layers) .*/
		SIZE,
		/** tileset set/added/removed .*/
		TILESETS,
		/** content (tiles) changed .*/
		TILES,
		/** other properties changed (tile size etc.) .*/
		PROPERTIES;
	}
}
