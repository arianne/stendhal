/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import games.stendhal.client.gui.j2d.Blend;
import games.stendhal.common.CollisionDetection;
import games.stendhal.common.MathHelper;
import games.stendhal.tools.tiled.LayerDefinition;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marauroa.common.game.RPObject;
import marauroa.common.net.InputSerializer;

/**
 * Layer data of a zone.
 */
public class Zone {
	/** Name of the zone. */
	private final String name;
	/** Renderers for normal layers. */
	private final Map<String, LayerRenderer> layers = new HashMap<String, LayerRenderer>();
	/** Global current zone information. */
	private final ZoneInfo zoneInfo = ZoneInfo.get();
	/** Collision layer. */
	private CollisionDetection collision;
	/** Protection layer. */
	private CollisionDetection protection;
	/** Tilesets. */
	private TileStore tileset;
	/**
	 * <code>true</code>, if the zone has been succesfully validated since the
	 * last change, <code>false</code> otherwise.
	 */
	private volatile boolean isValid;
	/**
	 * If <code>true</code>, the zone needs a data layer added before it can be
	 * validated.
	 */
	private boolean requireData;
	/**
	 * Update property of the zone. <code>false</code> usually, but
	 * <code>true</code> when the zone is an update (such as
	 * changed colors) to the current zone. 
	 */
	private boolean update;
	
	/**
	 * Create a new zone.
	 * 
	 * @param name zone name
	 */
	Zone(String name) {
		this.name = name;
	}
	
	/**
	 * Check if the zone is an update to another zone, rather than one where
	 * the player has just moved to.
	 * 
	 * @return <code>true</code>, if the zone is an update, <code>false</code>
	 *	otherwise
	 */
	boolean isUpdate() {
		return update;
	}
	
	/**
	 * Set the update property of the zone. Zone data that is a color update
	 * should be prepared in a background thread, as far as possible, to avoid
	 * pausing the client. For normal zone changes, the update status should be
	 * <code>false</code>.
	 * 
	 * @param update <code>false</code> for normal zone changes. 
	 * 	<code>true</code> when the zone is an update to the current zone
	 */
	void setUpdate(boolean update) {
		this.update = update;
	}
	
	/**
	 * Call, if the zone requires a data layer. Calling this must happen
	 * <b>before</b> the said data layer is added.
	 */
	void requireDataLayer() {
		requireData = true;
	}
	
	/**
	 * Add a layer.
	 * 
	 * @param layer layer name
	 * @param in Stream for reading the layer data
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	void addLayer(String layer, InputStream in) throws IOException, ClassNotFoundException {
		if (layer.equals("collision")) {
			/*
			 * Add a collision layer.
			 */
			collision = new CollisionDetection();
			collision.setCollisionData(LayerDefinition.decode(in));
		} else if (layer.equals("protection")) {
			/*
			 * Add protection
			 */
			protection = new CollisionDetection();
			protection.setCollisionData(LayerDefinition.decode(in));
		} else if (layer.equals("tilesets")) {
			/*
			 * Add tileset
			 */
			TileStore store = new TileStore();
			store.addTilesets(new InputSerializer(in));
			tileset = store;
		} else if (layer.equals("data_map")) {
			// Zone attributes
			RPObject obj = new RPObject();
			obj.readObject(new InputSerializer(in));

			String colorMode = obj.get("color_method");
			if ("multiply".equals(colorMode)) {
				zoneInfo.setColorMethod(Blend.Multiply);
			} else if ("screen".equals(colorMode)) {
				zoneInfo.setColorMethod(Blend.Screen);
			}
			String color = obj.get("color");
			if (color != null) {
				// Keep working, but use an obviously broken color if parsing
				// the value fails.
				zoneInfo.setZoneColor(MathHelper.parseIntDefault(color, 0x00ff00));
			} else {
				// Ensure there's no old color left. That can happen in the
				// morning on a daylight colored zone.
				zoneInfo.setColorMethod(null);
			}
			// OK to try validating after this
			requireData = false;
		} else {
			/*
			 * It is a tile layer.
			 */
			TileRenderer content = new TileRenderer();
			content.setMapData(in);
			layers.put(layer, content);
		}
		isValid = false;
	}
	
	/**
	 * Get the name of the zone.
	 * 
	 * @return zone name
	 */
	String getName() {
		return name;
	}
	
	/**
	 * Get the zone width.
	 * 
	 * @return zone width, or 0 if the zone is not ready enough to return the
	 * 	real width 
	 */
	double getWidth() {
		if (!isValid) {
			return 0.0;
		}
		return collision.getWidth();
	}
	
	/**
	 * Get the zone height.
	 * 
	 * @return zone height, or 0 if the zone is not ready enough to return the
	 * 	real height
	 */
	double getHeight() {
		if (!isValid) {
			return 0.0;
		}
		return collision.getHeight();
	}
	
	/**
	 * Check if a shape collides within the zone.
	 * 
	 * @param shape
	 * @return <code>true</code>, if the shape overlaps the static zone
	 *	collision, <code>false</code> otherwise
	 */
	boolean collides(final Rectangle2D shape) {
		if (collision != null) {
			return collision.collides(shape);
		}
		return false;
	}
	
	/**
	 * Draw a layer.
	 * 
	 * @param g graphics
	 * @param layer layer name
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	void draw(Graphics g, final String layer, final int x, final int y,
			final int width, final int height) {
		if (!isValid) {
			return;
		}

		final LayerRenderer lr = layers.get(layer);
		if (lr != null) {
			lr.draw(g, x, y, width, height);
		}
	}

	/**
	 * Get the collision map.
	 * 
	 * @return collision
	 */
	CollisionDetection getCollision() {
		return collision;
	}
	
	/**
	 * Get the protection map.
	 * 
	 * @return protection.
	 */
	CollisionDetection getProtection() {
		return protection;
	}
	
	/**
	 * Get a composite representation of multiple tile layers.
	 * 
	 * @param compositeName name to be used for the composite for caching
	 * @param layerNames names of the layers making up the composite starting
	 * from the bottom
	 * @return layer corresponding to all sub layers or <code>null</code> if
	 * 	they can not be merged
	 */
	LayerRenderer getMerged(String compositeName, String ... layerNames) {
		LayerRenderer r = layers.get(compositeName);
		if (r == null) {
			List<TileRenderer> subLayers = new ArrayList<TileRenderer>(layerNames.length);
			for (int i = 0; i < layerNames.length; i++) {
				LayerRenderer subLayer = layers.get(layerNames[i]);
				if (subLayer instanceof TileRenderer) {
					subLayers.add((TileRenderer) subLayer);
				} else {
					// Can't merge
					return null;
				}
			}
			// Make sure the sub layers have their tiles defined before passing
			// them to CompositeLayerRenderer
			if (!isValid) {
				return null;
			}
			
			// The partial sublayers won't be needed for anything anymore, and
			// they can be dropped to save some memory
			for (String layer : layerNames) {
				layers.remove(layer);
			}

			r = new CompositeLayerRenderer(subLayers);
			layers.put(compositeName, r);
		}
		return r;
	}
	
	/**
	 * Try validating the zone.
	 * 
	 * @return <code>true</code>, if the zone has been successfully validated,
	 * 	<code>false</code> otherwise.
	 */
	boolean validate() {
		if (isValid) {
			return true;
		}

		// Tilesets are always required. Also fail validation until required
		// data_map has been added
		if (tileset == null || requireData) {
			return false;
		}
		// Collision is always required
		if (collision == null) {
			return false;
		}
		if (!tileset.validate(zoneInfo.getZoneColor(), zoneInfo.getColorMethod())) {
			return false;
		}

		for (final LayerRenderer lr : layers.values()) {
				lr.setTileset(tileset);
		}

		isValid = true;
		return true;
	}
}
