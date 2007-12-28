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

package tiled.mapeditor.builder;

import java.awt.Point;
import java.awt.Rectangle;

import tiled.core.Map;
import tiled.mapeditor.brush.Brush;

/**
 * Builders are the workhorses of map editing. Every draw to the map is done
 * through a builder. Builders are based on brushes. How they interpret these
 * brushes is their problem.
 * 
 * The builder is called each time a mouse click or draw occures on the map. It
 * can then simply place the the current brush on the map or do something more
 * sophisticated (like drawing streets/corridors/forests/houses).
 * 
 * @author mtotz
 */
public interface Builder {

	/**
	 * sets the map.
	 * 
	 * @param map
	 *            the map
	 */
	void setMap(Map map);

	/**
	 * sets the brush.
	 * 
	 * @param brush
	 *            the brush
	 */
	void setBrush(Brush brush);

	/**
	 * sets layer where the builder should start paining.
	 * 
	 * @param startLayer
	 *            the layer where to start painting
	 */
	void setStartLayer(int startLayer);

	/**
	 * Returns the bounds of the builder. This is mainly to show the user a
	 * modification cursor.
	 * 
	 * @return the bounds
	 */
	Rectangle getBounds();

	/**
	 * Starts the building process. Either the user has clicked the tile (then
	 * there is an imediate finishBuilder()) or he started a drag (expect some
	 * moveBuilder() and a finishBuilder()).
	 * 
	 * @param tile
	 *            the tile where to start the builder
	 * @return modified region in tile coordinate space (may be null)
	 */
	Rectangle startBuilder(Point tile);

	/**
	 * Extends the building process to the given tile.
	 * 
	 * @param tile
	 *            the tile where to extend the building process to
	 * @return modified region in tile coordinate space (may be null)
	 */
	Rectangle moveBuilder(Point tile);

	/**
	 * Stops and finished the building process.
	 * 
	 * @param tile
	 *            the tile where to stop the builder. May be <code>null</code>
	 * @return modified region in tile coordinate space (may be null)
	 */
	Rectangle finishBuilder(Point tile);

	/**
	 * Returns true when the builder is started.
	 * 
	 * @return true when the builder is started
	 */
	boolean isStarted();
}
