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

import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import tiled.core.Map;
import tiled.core.MapLayer;

public class MapLayerStateEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = 8288536334620982325L;

	private Map map;
	private List<MapLayer> layersBefore;
	private List<MapLayer> layersAfter;
	private String name;

	public MapLayerStateEdit(Map m, List<MapLayer> before, List<MapLayer> after, String name) {
		this.map = m;
		this.layersBefore = before;
		this.layersAfter = after;
		this.name = name;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		map.setLayers(layersBefore);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		map.setLayers(layersAfter);
	}

	@Override
	public String getPresentationName() {
		return name;
	}
}
