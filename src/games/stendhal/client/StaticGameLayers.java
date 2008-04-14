/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client;

import games.stendhal.client.entity.User;
import games.stendhal.common.CollisionDetection;
import games.stendhal.tools.tiled.LayerDefinition;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import marauroa.common.net.InputSerializer;

import org.apache.log4j.Logger;

/** This class stores the layers that make the floor and the buildings. */

public class StaticGameLayers {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StaticGameLayers.class);

	/**
	 * Area collision maps.
	 */
	private Map<String, CollisionDetection> collisions;

	/**
	 * The current collision map.
	 */
	private CollisionDetection collision;

	/**
	 * Named layers.
	 */
	private Map<String, LayerRenderer> layers;

	/**
	 * Area tilesets.
	 */
	private Map<String, TileStore> tilesets;

	/**
	 * The current area height.
	 */
	private double height;

	/**
	 * The current area width.
	 */
	private double width;

	/** Name of the layers set that we are rendering right now. */
	private String area;

	/** true when the area has been changed. */
	private boolean areaChanged;

	/**
	 * Whether the internal state is valid.
	 */
	private boolean isValid;

	public StaticGameLayers() {
		collisions = new HashMap<String, CollisionDetection>();
		layers = new HashMap<String, LayerRenderer>();
		tilesets = new HashMap<String, TileStore>();

		height = 0.0;
		width = 0.0;
		area = null;
		areaChanged = true;
		isValid = true;
	}

	/** @return width in world units. */
	public double getWidth() {
		validate();

		return width;
	}

	/** @return the height in world units */
	public double getHeight() {
		validate();

		return height;
	}

	/**
	 * Add a new Layer to the set.
	 * @param area 
	 * @param layer 
	 * @param in 
	 * @throws IOException 
	 * 
	 * @throws ClassNotFoundException
	 */
	public void addLayer(String area, String layer, InputStream in)
			throws IOException, ClassNotFoundException {
		String name = getLayerKey(area, layer);

		logger.debug("Layer name: " + name);

		if (layer.equals("collision")) {
			/*
			 * Add a collision layer.
			 */
			if (collisions.containsKey(area)) {
				// Repeated layers should be ignored.
				return;
			}

			CollisionDetection collisionTemp = new CollisionDetection();
			collisionTemp.setCollisionData(LayerDefinition.decode(in));

			collisions.put(area, collisionTemp);
		} else if (layer.equals("tilesets")) {
			/*
			 * Add tileset
			 */
			TileStore tileset = new TileStore();
			tileset.addTilesets(new InputSerializer(in));

			tilesets.put(area, tileset);
		} else if (layer.endsWith("_map")) {
			/*
			 * It is the minimap image for this zone.
			 */
		} else {
			/*
			 * It is a tile layer.
			 */
			if (layers.containsKey(name)) {
				// Repeated layers should be ignored.
				return;
			}

			LayerRenderer content = null;

			URL url = getClass().getClassLoader().getResource(
					"data/layers/" + area + "/" + layer + ".jpg");

			if (url != null) {
				content = new ImageRenderer(url);
			}

			if (content == null) {
				content = new TileRenderer();
				((TileRenderer) content).setMapData(in);
			}

			layers.put(name, content);
		}

		invalidate();
	}

	public boolean collides(Rectangle2D shape) {
		validate();

		User user = User.get();

		if (collision != null && (user == null || !User.get().isGhostMode())) {
			return collision.collides(shape);
		}

		return false;
	}

	/** Removes all layers. */
	public void clear() {
		layers.clear();
		tilesets.clear();
		collision = null;
		area = null;
	}

	/**
	 * Set the name of the area to be rendered.
	 */
	public void setAreaName(String area) {
		logger.info("Area: " + area);

		this.area = area;
		this.areaChanged = true;
		invalidate();
	}

	/**
	 * Invalidate any cached settings.
	 */
	public void invalidate() {
		isValid = false;
	}

	protected void validate() {
		if (isValid) {
			return;
		}

		if (area == null) {
			height = 0.0;
			width = 0.0;
			collision = null;

			isValid = true;
			return;
		}

		/*
		 * Set collision map
		 */
		collision = collisions.get(area);

		if (collision != null) {
			collisions.put(area, collision);
		}

		/*
		 * Get maximum layer size. Assign tileset to layers.
		 */
		TileStore tileset = tilesets.get(area);
		height = 0.0;
		width = 0.0;

		String prefix = area + ".";

		for (Map.Entry<String, LayerRenderer> entry : layers.entrySet()) {
			if (entry.getKey().startsWith(prefix)) {
				LayerRenderer lr = entry.getValue();

				lr.setTileset(tileset);
				height = Math.max(height, lr.getHeight());
				width = Math.max(width, lr.getWidth());
			}
		}

		isValid = true;
	}

	public void draw(IGameScreen screen, String area, String layer, int x,
			int y, int width, int height) {
		validate();

		LayerRenderer lr = getLayer(area, layer);

		if (lr != null) {
			lr.draw(screen, x, y, width, height);
		}
	}

	/**
	 * 
	 * @return the CollisionDetection Layer for the current map
	 * 
	 */
	public CollisionDetection getCollisionDetection() {
		validate();

		return collision;
	}

	/**
	 * 
	 * @return the current area/map
	 * 
	 */
	public String getArea() {
		return area;
	}

	/**
	 * Get a layer renderer.
	 * 
	 * @return A layer renderer, or <code>null</code>,
	 */
	public LayerRenderer getLayer(String area, String layer) {
		return layers.get(getLayerKey(area, layer));
	}

	/**
	 * Make a map "key" from an area/layer name.
	 * 
	 * 
	 * TODO: Make the key an object with area/layer fields and replace ugly code
	 * that uses startsWith('area.').
	 */
	protected String getLayerKey(final String area, final String layer) {
		return area + "." + layer;
	}

	/**
	 * @return true if the area has changed since the last
	 */
	public boolean changedArea() {
		return areaChanged;
	}

	/**
	 * resets the areaChanged flag.
	 */
	public void resetChangedArea() {
		areaChanged = false;
	}
}
