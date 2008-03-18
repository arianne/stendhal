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

import java.awt.Point;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import tiled.core.MapLayer;

public class MoveLayerEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = 651941478234175133L;

	private MapLayer layer;
	private Point moveDist;

	public MoveLayerEdit(MapLayer layer, Point moveDist) {
		this.layer = layer;
		this.moveDist = moveDist;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		layer.translate(-moveDist.x, -moveDist.y);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		layer.translate(moveDist.x, moveDist.y);
	}

	@Override
	public String getPresentationName() {
		return "Move";
	}
}
