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
import java.util.LinkedList;
import marauroa.common.Log4J;
import marauroa.common.net.InputSerializer;

import org.apache.log4j.Logger;

/** This class stores the layers that make the floor and the buildings */

public class StaticGameLayers {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(StaticGameLayers.class);

	private TileStore nextTilesets;
	
	/** List of pair name, layer */
	private LinkedList<Pair<String, LayerRenderer>> layers;

	/** List of pair name, layer */
	private LinkedList<Pair<String, CollisionDetection>> collisions;

	/** Name of the layers set that we are rendering right now */
	private String area;

	/** true when the area has been changed */
	private boolean areaChanged;

	public StaticGameLayers() {
		layers = new LinkedList<Pair<String, LayerRenderer>>();
		collisions = new LinkedList<Pair<String, CollisionDetection>>();

		area = null;
		areaChanged = true;
	}

	/** Returns width in world units */
	public double getWidth() {
		double width = 0;
		for (Pair<String, LayerRenderer> p : layers) {
			if ((area != null) && p.first().contains(area)) {
				if (width < p.second().getWidth()) {
					width = p.second().getWidth();
				}
			}
		}
		return width;
	}

	/** Returns the height in world units */
	public double getHeight() {
		double height = 0;
		for (Pair<String, LayerRenderer> p : layers) {
			if ((area != null) && p.first().contains(area)) {
				if (height < p.second().getHeight()) {
					height = p.second().getHeight();
				}
			}
		}
		return height;
	}

	/** Add a new Layer to the set 
	 * @throws ClassNotFoundException */
	public void addLayer(String name, InputStream in) throws IOException, ClassNotFoundException {
		Log4J.startMethod(logger, "addLayer");
		try {
			if (name.endsWith("_collision")) {
				for (int i = 0; i < collisions.size(); i++) {
					if (collisions.get(i).first().compareTo(name) == 0) {
						/** Repeated layers should be ignored. */
						return;
					}
				}
				CollisionDetection collision = new CollisionDetection();
				collision.setCollisionData(LayerDefinition.decode(new InputSerializer(in)));
				collisions.add(new Pair<String, CollisionDetection>(name, collision));
			} else if (name.endsWith("_tilesets")) {
				nextTilesets=new TileStore();
				nextTilesets.addTilesets(new InputSerializer(in));
			} else if (name.endsWith("_map")) {

			} else {
				int i;
				for (i = 0; i < layers.size(); i++) {
					if (layers.get(i).first().compareTo(name) == 0) {
						/** Repeated layers should be ignored. */
						return;
					}
					if (layers.get(i).first().compareTo(name) >= 0) {
						break;
					}
				}
				LayerRenderer content = null;
				URL url = getClass().getClassLoader().getResource("data/layers/" + name + ".jpg");
				if (url != null) {
					content = new ImageRenderer(url);
				}
				if (content == null) {
					//TODO: XXX
					content = new TileRenderer(nextTilesets);
					((TileRenderer) content).setMapData(new InputSerializer(in));
				}
				layers.add(i, new Pair<String, LayerRenderer>(name, content));
			}
		} finally {
			Log4J.finishMethod(logger, "addLayer");
		}
	}

	public boolean collides(Rectangle2D shape) {
		for (Pair<String, CollisionDetection> p : collisions) {
			if ((area != null) && p.first().equals(area + "_collision")) {
				if (p.second().collides(shape)) {
					return true;
				}
			}
		}
		return false;
	}

	/** Removes all layers */
	public void clear() {
		Log4J.startMethod(logger, "clear");
		layers.clear();
		Log4J.finishMethod(logger, "clear");
	}

	/** Set the set of layers that is going to be rendered */
	public void setRPZoneLayersSet(String area) {
		Log4J.startMethod(logger, "setRPZoneLayersSet");
		// keep only the actual zone
		for (int i = 0; i < layers.size(); i++) {
			if (!layers.get(i).first().contains(area)) {
				layers.remove(i);
			}
		}
		for (int i = 0; i < collisions.size(); i++) {
			if (!collisions.get(i).first().equals(area + "_collision")) {
				collisions.remove(i);
			}
		}
		
		this.area = area;
		this.areaChanged = true;
		Log4J.finishMethod(logger, "setRPZoneLayersSet");
	}

	public String getRPZoneLayerSet() {
		return area;
	}

	public void draw(GameScreen screen, String layer) {
		for (Pair<String, LayerRenderer> p : layers) {
			if (p.first().equals(layer)) {
				p.second().draw(screen);
			}
		}
	}

	/** Render the choosen set of layers */
	public void draw(GameScreen screen) {
		for (Pair<String, LayerRenderer> p : layers) {
			if ((area != null) && p.first().contains(area)) {
				p.second().draw(screen);
			}
		}
	}

	/**
	 * 
	 * @return the CollisionDetection Layer for the current map
	 * 
	 */
	public CollisionDetection getCollisionDetection() {
		for (Pair<String, CollisionDetection> cdp : collisions) {
			if ((area != null) && cdp.first().equals(area + "_collision")) {
				return cdp.second();
			}
		}
		return null;
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
