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

import games.stendhal.common.CollisionDetection;
import games.stendhal.common.Pair;
import games.stendhal.tools.tiled.LayerDefinition;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import marauroa.common.Log4J;
import marauroa.common.net.InputSerializer;

import org.apache.log4j.Logger;

/** This class stores the layers that make the floor and the buildings */

public class StaticGameLayers {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(StaticGameLayers.class);

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
	private double	height;

	/**
	 * The current area width.
	 */
	private double	width;

	/** Name of the layers set that we are rendering right now */
	private String area;

	/** true when the area has been changed */
	private boolean areaChanged;

	/**
	 * Whether the internal state is valid
	 */
	private boolean valid;

	public StaticGameLayers() {
		collisions = new HashMap<String, CollisionDetection>();
		layers = new HashMap<String, LayerRenderer>();
		tilesets = new HashMap<String, TileStore>();

		height = 0.0;
		width = 0.0;
		area = null;
		areaChanged = true;
		valid = true;
	}

	/** Returns width in world units */
	public double getWidth() {
		validate();

		return width;
	}

	/** Returns the height in world units */
	public double getHeight() {
		validate();

		return height;
	}

	/** Add a new Layer to the set 
	 * @throws ClassNotFoundException */
	public void addLayer(String name, InputStream in) throws IOException, ClassNotFoundException {
		Log4J.startMethod(logger, "addLayer");
		logger.info("Layer: "+name);
		try {
			if (name.endsWith("_collision")) {
				String area = name.substring(0, name.length() - 10);

				/*
				 * Add a collision layer.
				 */
				if(collisions.containsKey(area)) {
					// Repeated layers should be ignored.
					return;
				}

				CollisionDetection collision = new CollisionDetection();
				collision.setCollisionData(LayerDefinition.decode(new InputSerializer(in)));

				collisions.put(area, collision);
			} else if (name.endsWith("_tilesets")) {
				String area = name.substring(0, name.length() - 9);

				/*
				 * Add tileset
				 */
				TileStore tileset = new TileStore();
				tileset.addTilesets(new InputSerializer(in));

				tilesets.put(area, tileset);
			} else if (name.endsWith("_map")) {
				/*
				 * It is the minimap image for this zone.
				 */
			} else {
				/*
				 * It is a tile layer.
				 */
				if(layers.containsKey(name)) {
					// Repeated layers should be ignored.
					return;
				}

				LayerRenderer content = null;

				URL url = getClass().getClassLoader().getResource("data/layers/" + name + ".jpg");

				if (url != null) {
					content = new ImageRenderer(url);
				}

				if (content == null) {
					//TODO: XXX
					content = new TileRenderer();
					((TileRenderer) content).setMapData(new InputSerializer(in));
				}

				layers.put(name, content);
			}

			valid = false;
		} finally {
			Log4J.finishMethod(logger, "addLayer");
		}
	}

	public boolean collides(Rectangle2D shape) {
		validate();

		if(collision != null) {
			return collision.collides(shape);
		}

		return false;
	}

	/** Removes all layers */
	public void clear() {
		Log4J.startMethod(logger, "clear");
		layers.clear();
		tilesets.clear();
		collision = null;
		area = null;
		Log4J.finishMethod(logger, "clear");
	}

	/** Set the set of layers that is going to be rendered */
	public void setRPZoneLayersSet(String area) {
		Log4J.startMethod(logger, "setRPZoneLayersSet");

		logger.info("Area: "+area);

		this.area = area;
		this.areaChanged = true;
		valid = false;

		Log4J.finishMethod(logger, "setRPZoneLayersSet");
	}
	

	protected void validate() {
		if(valid == true) {
			return;
		}

		if(area == null) {
			height = 0.0;
			width = 0.0;
			collision = null;

			valid = true;
			return;
		}

		/*
		 * Set collision map
		 */
		collision = collisions.get(area);

		if(collision != null) {
			collisions.put(area, collision);
		}


		/*
		 * Get maximum layer size.
		 * Assign tileset to layers.
		 */
		TileStore tileset = tilesets.get(area);
		height = 0.0;
		width = 0.0;

		for(Map.Entry<String, LayerRenderer> entry : layers.entrySet()) {
			if(entry.getKey().startsWith(area)) {
				LayerRenderer lr = entry.getValue();

				lr.setTileset(tileset);
				height = Math.max(height, lr.getHeight());
				width = Math.max(width, lr.getWidth());
			}
		}

		valid = true;
	}


	public String getRPZoneLayerSet() {
		return area;
	}

	public void draw(GameScreen screen, String layer) {
		validate();

		LayerRenderer lr = layers.get(layer);

		if(lr != null) {
			lr.draw(screen);
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
	 * @return true if the area has changed since the last
	 */
	public boolean changedArea() {
		return areaChanged;
	}

	/**
	 * resets the areaChanged flag
	 */
	public void resetChangedArea() {
		areaChanged = false;
	}
}
