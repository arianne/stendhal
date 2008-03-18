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

package tiled.mapeditor.undo;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import tiled.core.Map;
import tiled.core.MapLayer;

/** This class handles all layer modification. */
public class MapLayerEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = 1L;

	/** the map. */
	private Map editedMap;
	/** List of start layers during the edit. */
	private List<MapLayer> layerReference;
	/** the layers to be copied when undoing the changes. */
	private List<MapLayer> layerUndo;
	/** the layers to be copied when redoing the changes. */
	private List<MapLayer> layerRedo;
	/** displayName. */
	private String name;
	/** true when start() has been called, but end() not yet. */
	private boolean inProgress = false;

	/**
	 * Creates a new empty Edit. You have to call the start() and end() methods
	 * to finish the edit.
	 * 
	 * @param map
	 *            the map
	 */
	public MapLayerEdit(Map map) {
		editedMap = map;
	}

	/**
	 * Creates a new Edit. The Start-Layer is set to <code>before</code>. You
	 * still have to call the end() method to finish the edit.
	 * 
	 * @param map
	 *            the map
	 * @param before
	 *            the layers which will be modified. This is the state wich will
	 *            be restored upon undo.
	 */
	public MapLayerEdit(Map map, List<MapLayer> before) {
		this(map);
		start(before);
	}

	/**
	 * Creates a new Edit. The Start-Layer is set to <code>before</code>. You
	 * still have to call the end() method to finish the edit.
	 * 
	 * @param map
	 *            the map
	 * @param before
	 *            the layers which will be modified. This is the state wich will
	 *            be restored upon undo.
	 * @param after
	 *            the layers after they are modified. The order of the layer
	 *            <b>must</b> be the same as provided on start()
	 */
	public MapLayerEdit(Map map, List<MapLayer> before, List<MapLayer> after) {
		this(map);
		start(before);
		end(after);
	}

	/**
	 * Sets the start state of the layers. The layer list will be copied, so you
	 * don't have to do it yourself.
	 * 
	 * @param layers
	 *            the layers which will be modified
	 */
	public void start(List<MapLayer> layers) {
		// copy the layers
		layerReference = new ArrayList<MapLayer>();
		for (MapLayer layer : layers) {
			MapLayer copy = layer.getLayerCopy(layer.getBounds());
			layerReference.add(copy);
		}

		inProgress = true;
	}

	public void end(List<MapLayer> layers) {
		if (!inProgress) {
			throw new IllegalStateException("end called before start");
		}

		if (layerReference.size() != layers.size()) {
			throw new IllegalArgumentException("size of end-layers list must be the same as the start layers list "
					+ "(start size: " + layerReference.size() + " end size: " + layers.size());
		}

		layerUndo = new ArrayList<MapLayer>();
		layerRedo = new ArrayList<MapLayer>();

		// now get the changes
		for (int i = 0; i < layerReference.size(); i++) {
			// this is the changed part of the layer
			MapLayer diff = layerReference.get(i).createDiff(layers.get(i));
			// no need to keep unchanged layers
			Rectangle diffBounds = diff.getBounds();
			if (diffBounds.width > 0 && diffBounds.height > 0) {
				layerRedo.add(diff);

				// this is the original part of the layer
				MapLayer undo = layerReference.get(i).getLayerCopy(diff.getBounds());
				layerUndo.add(undo);
			}
		}
		layerReference.clear();
		layerReference = null;

	}

	public List<MapLayer> getStart() {
		return layerUndo;
	}

	/** Applies the undo-redo layers. */
	private void applyLayers(List<MapLayer> layerList) {
		for (MapLayer layer : layerList) {
			for (MapLayer mapLayer : editedMap.getLayerList()) {
				if (mapLayer.getName().equals(layer.getName())) {
					mapLayer.copyFrom(layer);
					break;
				}
			}
		}
	}

	/* inherited methods */
	@Override
	public void undo() throws CannotUndoException {
		applyLayers(layerUndo);
	}

	/** undo the last paint. */
	@Override
	public boolean canUndo() {
		return (layerUndo != null) && (layerUndo.size() > 0);
	}

	@Override
	public void redo() throws CannotRedoException {
		applyLayers(layerRedo);
	}

	/** redo the last paint. */
	@Override
	public boolean canRedo() {
		return (layerRedo != null) && (layerRedo.size() > 0);
	}

	@Override
	public void die() {
		layerUndo = null;
		layerRedo = null;
		inProgress = false;
	}

	@Override
	public boolean addEdit(UndoableEdit anEdit) {
		if (inProgress && anEdit.getClass() == this.getClass()) {
			// TODO: absorb the edit
			// return true;
		}
		return false;
	}

	public void setPresentationName(String s) {
		name = s;
	}

	@Override
	public String getPresentationName() {
		return name;
	}
}
