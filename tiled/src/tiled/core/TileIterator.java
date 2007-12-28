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

package tiled.core;

import java.lang.IllegalStateException;
import java.lang.UnsupportedOperationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class TileIterator implements Iterator<Tile> {
	private List<Tile> tiles;
	private int pos;

	public TileIterator(List<Tile> tiles) {
		this.tiles = tiles;
		pos = 0;
	}

	public boolean hasNext() {
		while (pos < tiles.size()) {
			if (tiles.get(pos) != null) {
				return true;
			}
			pos++;
		}
		return false;
	}

	public Tile next() throws NoSuchElementException {
		while (pos < tiles.size()) {
			Tile t = tiles.get(pos);
			pos++;
			if (t != null) {
				return t;
			}
		}
		throw new NoSuchElementException();
	}

	public void remove() throws UnsupportedOperationException, IllegalStateException {
		throw new UnsupportedOperationException();
	}
}
