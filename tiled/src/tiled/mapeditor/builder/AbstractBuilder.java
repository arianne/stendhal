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

package tiled.mapeditor.builder;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

import javax.swing.undo.UndoManager;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.mapeditor.brush.Brush;
import tiled.mapeditor.undo.MapLayerEdit;

/**
 * Default builder base implementation. Takes care of handling the brush and map
 * storage.
 * 
 * @author mtotz
 */
public abstract class AbstractBuilder implements Builder {
	/** the map. */
	protected Map map;
	/** the brush. */
	protected Brush brush;
	/** the last point where the builder was called. */
	protected Point lastPoint;
	/** The undo manager to use. */
	protected UndoManager undoManager;
	/** the current edit. */
	private MapLayerEdit edit;
	/** is builder started? */
	protected boolean started;
	/** the layer where to start drawing (for multilayer brushes). */
	protected int startLayer;
	/** cached affected layers list for undo/redo feature. */
	private List<MapLayer> affectedLayers;

	/**
	 * Creates a new builder. The map/brush/startLayer must be set before using
	 * the builder
	 */
	public AbstractBuilder() {
		this.started = false;
	}

	/**
	 * creates a new builder.
	 * 
	 * @param map
	 *            the map
	 * @param brush
	 *            the brush
	 */
	public AbstractBuilder(Map map, Brush brush, int startLayer) {
		this();
		this.map = map;
		this.brush = brush;
		this.startLayer = startLayer;
	}

	/**
	 * sets the map.
	 * 
	 * @param map
	 *            the map
	 */
	public void setMap(Map map) {
		this.map = map;
		propertyChanged();
	}

	/**
	 * sets the brush.
	 * 
	 * @param brush
	 *            the brush
	 */
	public void setBrush(Brush brush) {
		this.brush = brush;
		propertyChanged();
	}

	/**
	 * Sets the UndoableEditSupport. <code>null</code> disables undo support
	 * 
	 * @param undoManager
	 *            The UndoableEditSupport
	 */
	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
		propertyChanged();
	}

	/**
	 * Returns true when the builder is started.
	 * 
	 * @return true when the builder is started
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * sets layer where the builder should start paining.
	 * 
	 * @param startLayer
	 *            the layer where to start painting
	 */
	public void setStartLayer(int startLayer) {
		this.startLayer = startLayer;
		propertyChanged();
	}

	/**
	 * Ensures that the builder is started. Calls startBuilder when is is not.
	 * 
	 * @param tile
	 *            the tile
	 */
	protected void ensureStarted(Point tile) {
		if (!started) {
			startBuilder(tile);
		}
	}

	/**
	 * Ensures that the builder is stopped. calls finishBuilder when is is not.
	 * 
	 * @param tile
	 *            the tile
	 */
	protected void ensureStopped(Point tile) {
		if (started && lastPoint != null) {
			finishBuilder(tile);
		}
	}

	/** updates the last drawn point. */
	protected void updateLastPoint(Point tile) {
		if (tile == null) {
			lastPoint = null;
		} else {
			lastPoint = new Point(tile);
		}
	}

	/**
	 * Called when one of the properties (map/brush/startLayer) was changed.
	 * Implementing classes may override this method to update cached states or
	 * stop/finish the building process.
	 */
	protected void propertyChanged() {
		// no default implementation
	}

	/**
	 * Starts this builder. By now it caches the current map state if undo-
	 * support is enabled. Should be called before any drawings occur.
	 */
	protected void start(MapLayer currentLayer) {
		if (undoManager != null) {
			edit = new MapLayerEdit(map);
			MapLayer[] layers = brush.getAffectedLayers();
			if (layers.length == 0) {
				layers = new MapLayer[1];
				layers[0] = currentLayer;
			}

			affectedLayers = Arrays.asList(layers);

			edit.start(affectedLayers);
		}
	}

	/**
	 * Stops this builder. By now it saves the modified map state if undo-
	 * support is enabled. Should be called after the last drawing operation
	 */
	protected void finish() {
		if (undoManager != null && edit != null) {
			edit.setPresentationName("undo");
			edit.end(affectedLayers);
			undoManager.addEdit(edit);
			edit = null;
		}
	}

	/**
	 * Starts the building process. Either the user has clicked the tile (then
	 * there is an imediate finishBuilder()) or he started a drag (expect some
	 * moveBuilder() and a finishBuilder()).
	 * 
	 * The implemeting class should set the <i>isStarted</i> flag to <i>true</i>
	 * and update the last drawn point (updateLastPoint())
	 * 
	 * @param tile
	 *            the tile where to start the builder
	 * @return modified region in tile coordinate space (may be null)
	 */
	public abstract Rectangle startBuilder(Point tile);

	/**
	 * Extends the building process to the given tile.
	 * 
	 * The implemeting class should update the last drawn point
	 * (updateLastPoint())
	 * 
	 * @param tile
	 *            the tile where to extend the building process to
	 * @return modified region in tile coordinate space (may be null)
	 */
	public abstract Rectangle moveBuilder(Point tile);

	/**
	 * Stops and finished the building process.
	 * 
	 * The implemeting class should set the <i>isStarted</i> flag to <i>false</i>.
	 * and set the last drawn point to null (updateLastPoint())
	 * 
	 * @param tile
	 *            the tile where to start the builder
	 * @return modified region in tile coordinate space (may be null)
	 */
	public abstract Rectangle finishBuilder(Point tile);

	/**
	 * Returns the bounds of the builder. This is mainly to show the user a
	 * modification cursor.
	 * 
	 * @return the bounds
	 */
	public abstract Rectangle getBounds();
}
