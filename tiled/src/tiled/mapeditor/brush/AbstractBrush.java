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

package tiled.mapeditor.brush;

import java.util.ArrayList;
import java.util.List;

import tiled.core.MultilayerPlane;
import tiled.core.StatefulTile;

public abstract class AbstractBrush extends MultilayerPlane implements Brush {
	protected List<StatefulTile> selectedTiles;

	public AbstractBrush() {
		selectedTiles = new ArrayList<StatefulTile>();
	}

	/** Sets the currently selected Tiles. */
	public void setTiles(List<StatefulTile> selectedTiles) {
		this.selectedTiles.clear();
		if (selectedTiles != null) {
			this.selectedTiles.addAll(selectedTiles);
		}
	}

	/** returns the used tiles. */
	public List<StatefulTile> getTiles() {
		return selectedTiles;
	}

}
