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

package tiled.core;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * MultilayerPlane makes up the core functionality of both Maps and Brushes.
 * This class handles the order of layers as a group.
 */
public class MultilayerPlane implements Iterable<MapLayer> {
	private List<MapLayer> layers;
	protected int widthInTiles;
	protected int heightInTiles;

	/**
	 * Default constructor.
	 */
	public MultilayerPlane() {
		layers = new ArrayList<MapLayer>();
	}

	/**
	 * Construct a MultilayerPlane to the specified dimensions.
	 * 
	 * @param width
	 * @param height
	 */
	public MultilayerPlane(int width, int height) {
		this();
		widthInTiles = width;
		heightInTiles = height;
	}

	/**
	 * Returns the total number of layers.
	 * 
	 * @return the size of the layer vector
	 */
	public int getTotalLayers() {
		return layers.size();
	}

	/**
	 * Returns a <code>Rectangle</code> representing the maximum bounds in
	 * tiles.
	 * 
	 * @return a Rectangle
	 */
	public Rectangle getBounds() {
		return new Rectangle(0, 0, widthInTiles, heightInTiles);
	}

	/**
	 * Adds a layer to the map.
	 * 
	 * @param l
	 *            The {@link MapLayer} to add
	 * @return the layer passed to the function
	 */
	public MapLayer addLayer(MapLayer l) {
		layers.add(l);
		return l;
	}

	/**
	 * Adds the MapLayer <code>l</code> after the MapLayer <code>after</code>.
	 * 
	 * @param l
	 *            the layer to add
	 * @param after
	 *            specifies the layer to add <code>l</code> after
	 */
	public void addLayerAfter(MapLayer l, MapLayer after) {
		layers.add(layers.indexOf(after) + 1, l);
	}

	/**
	 * Add a layer at the specified index, which should be a valid.
	 * 
	 * @param index
	 *            the position at which to add the layer
	 * @param layer
	 *            the layer to add
	 */
	public void addLayer(int index, MapLayer layer) {
		layers.add(index, layer);
	}

	/**
	 * Adds all the layers in a given java.util.Collection.
	 * 
	 * @param c
	 *            a collection of layers to add
	 */
	public void addAllLayers(Collection<MapLayer> c) {
		layers.addAll(c);
	}

	/**
	 * Removes the layer at the specified index. Layers above this layer will
	 * move down to fill the gap.
	 * 
	 * @param index
	 *            the index of the layer to be removed
	 * @return the layer that was removed from the list
	 */
	public MapLayer removeLayer(int index) {
		return layers.remove(index);
	}

	/**
	 * Removes all layers from the plane.
	 */
	public void removeAllLayers() {
		layers.clear();
	}

	/**
	 * Returns the layer at the specified index.
	 * 
	 * @param index
	 *            the index of the layer to return
	 * @return the layer at the specified index, or null if the index is out of
	 *         bounds
	 */
	public MapLayer getLayer(int index) {
		// if (index >= 0 && index < layers.size())
		return layers.get(index);

		// return null;
	}

	/**
	 * Returns a list with the layers.
	 * 
	 * @return a list with the layers.
	 */
	public List<MapLayer> getLayerList() {
		return new ArrayList<MapLayer>(layers);
	}

	/**
	 * Sets the layer vector to the given list.
	 * 
	 * @param layers
	 *            the new set of layers
	 */
	public void setLayers(List<MapLayer> layers) {
		this.layers = new ArrayList<MapLayer>(layers);
	}

	/**
	 * Moves the layer at <code>index</code> up one in the vector.
	 * 
	 * @param index
	 *            the index of the layer to swap up
	 * @throws Exception
	 */
	public void swapLayerUp(int index) throws Exception {
		if (index + 1 == layers.size()) {
			throw new Exception("Can't swap up when already at the top.");
		}

		MapLayer hold = layers.get(index + 1);
		layers.set(index + 1, getLayer(index));
		layers.set(index, hold);
	}

	/**
	 * Moves the layer at <code>index</code> down one in the vector.
	 * 
	 * @param index
	 *            the index of the layer to swap down
	 * @throws Exception
	 */
	public void swapLayerDown(int index) throws Exception {
		if (index - 1 < 0) {
			throw new Exception("Can't swap down when already at the bottom.");
		}

		MapLayer hold = layers.get(index - 1);
		layers.set(index - 1, getLayer(index));
		layers.set(index, hold);
	}

	/**
	 * Merges the layer at <code>index</code> with the layer below it.
	 * 
	 * @see tiled.core.MapLayer#mergeOnto
	 * @param index
	 *            the index of the layer to merge down
	 * @throws Exception
	 */
	public void mergeLayerDown(int index) throws Exception {
		if (index - 1 < 0) {
			throw new Exception("Can't merge down bottom layer.");
		}

		getLayer(index).mergeOnto(getLayer(index - 1));
		removeLayer(index);
	}

	/**
	 * Gets a listIterator of all layers.
	 * 
	 * @return a listIterator
	 */
	public ListIterator<MapLayer> iterator() {
		return layers.listIterator();
	}

	/**
	 * Resizes this plane. The (dx, dy) pair determines where the original plane
	 * should be positioned on the new area. It only affects layers with
	 * dimensions equal to that of the MutlilayerPlane.
	 * 
	 * @see MapLayer#resize
	 * @param width
	 *            The new width of the map.
	 * @param height
	 *            The new height of the map.
	 * @param dx
	 *            The shift in x direction in tiles.
	 * @param dy
	 *            The shift in y direction in tiles.
	 */
	public void resize(int width, int height, int dx, int dy) {
		ListIterator<MapLayer> itr = iterator();

		while (itr.hasNext()) {
			MapLayer l = itr.next();
			if (l.getBounds().equals(getBounds())) {
				l.resize(width, height, dx, dy);
			}
		}

		widthInTiles = width;
		heightInTiles = height;
	}
}
