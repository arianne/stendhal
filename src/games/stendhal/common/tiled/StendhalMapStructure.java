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
package games.stendhal.common.tiled;


import java.util.LinkedList;
import java.util.List;

/**
 * This is the map format that our client uses.
 *
 * @author miguel
 *
 */
public class StendhalMapStructure {
	/** Width of the map. */
	int width;

	/** Height of the map. */
	int height;

	/** List of tilesets that this map contains. */
	List<TileSetDefinition> tilesets;

	/** List of layers this map contains. */
	List<LayerDefinition> layers;

	/**
	 * Constructor.
	 *
	 * @param w
	 *            the width of the map
	 * @param h
	 *            the height of the map.
	 */
	public StendhalMapStructure(final int w, final int h) {
		width = w;
		height = h;
		tilesets = new LinkedList<TileSetDefinition>();
		layers = new LinkedList<LayerDefinition>();
	}

	/**
	 * Adds a new tileset to the map.
	 *
	 * @param set
	 *            new tileset
	 */
	public void addTileset(final TileSetDefinition set) {
		tilesets.add(set);
	}

	/**
	 * Adds a new layer to the map.
	 *
	 * @param layer
	 *            new layer
	 */
	public void addLayer(final LayerDefinition layer) {
		layer.setMap(this);
		layers.add(layer);
	}

	/**
	 * Returns a list of the tilesets this map contains.
	 *
	 * @return a list of the tilesets this map contains.
	 */
	public List<TileSetDefinition> getTilesets() {
		return tilesets;
	}

	/**
	 * Returns a list of the layers this map contains.
	 *
	 * @return a list of the layers this map contains.
	 */
	public List<LayerDefinition> getLayers() {
		return layers;
	}

	/**
	 * Return true if the layer with given name exists.
	 *
	 * @param layername
	 *            the layer name
	 * @return true if it exists.
	 */
	public boolean hasLayer(final String layername) {
		return getLayer(layername) != null;
	}

	/**
	 * Returns the layer whose name is layer name or null.
	 *
	 * @param layername
	 *            the layer name
	 * @return the layer object or null if it doesnt' exists
	 */
	public LayerDefinition getLayer(final String layername) {
		for (final LayerDefinition layer : layers) {
			if (layername.equals(layer.getName())) {
				return layer;
			}
		}

		return null;
	}

	/**
	 * Build all layers data.
	 */
	public void build() {
		for (final LayerDefinition layer : layers) {
			layer.build();
		}
	}

	/**
	 * gets the width
	 *
	 * @return width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * gets the height
	 *
	 * @return height
	 */
	public int getHeight() {
		return height;
	}
}
