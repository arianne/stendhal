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
 *  Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import tiled.core.MapLayer;
import tiled.mapeditor.MapEditor;

/**
 * Rotates or mirrors the current layer.
 * 
 * @author mtotz
 */
public class LayerTransformAction extends AbstractAction {
	private static final long serialVersionUID = 1727912057792605353L;

	private int transform;

	private MapEditor mapEditor;

	public LayerTransformAction(MapEditor mapEditor, int transform) {
		this.transform = transform;
		this.mapEditor = mapEditor;

		switch (transform) {
		case MapLayer.ROTATE_90:
			putValue(NAME, "Rotate 90 degrees CW");
			putValue(SHORT_DESCRIPTION, "Rotate layer 90 degrees clockwise");
			putValue(SMALL_ICON, MapEditor.loadIcon("resources/gimp-rotate-90-16.png"));
			break;
		case MapLayer.ROTATE_180:
			putValue(NAME, "Rotate 180 degrees CW");
			putValue(SHORT_DESCRIPTION, "Rotate layer 180 degrees clockwise");
			putValue(SMALL_ICON, MapEditor.loadIcon("resources/gimp-rotate-180-16.png"));
			break;
		case MapLayer.ROTATE_270:
			putValue(NAME, "Rotate 90 degrees CCW");
			putValue(SHORT_DESCRIPTION, "Rotate layer 90 degrees counterclockwise");
			putValue(SMALL_ICON, MapEditor.loadIcon("resources/gimp-rotate-270-16.png"));
			break;
		case MapLayer.MIRROR_VERTICAL:
			putValue(NAME, "Flip vertically");
			putValue(SHORT_DESCRIPTION, "Flip layer vertically");
			putValue(SMALL_ICON, MapEditor.loadIcon("resources/gimp-flip-vertical-16.png"));
			break;
		case MapLayer.MIRROR_HORIZONTAL:
			putValue(NAME, "Flip horizontally");
			putValue(SHORT_DESCRIPTION, "Flip layer horizontally");
			putValue(SMALL_ICON, MapEditor.loadIcon("resources/gimp-flip-horizontal-16.png"));
			break;
		}
	}

	/** constructor for the action manager. */
	public LayerTransformAction(MapEditor mapEditor, Object[] params) {
		// note: no checks. the only parameter should be an int :)
		this(mapEditor, ((Integer) params[0]).intValue());
	}

	public void actionPerformed(ActionEvent evt) {
		MapLayer currentLayer = mapEditor.getCurrentLayer();
		MapLayer layer = currentLayer;
		// MapLayerEdit transEdit;
		// transEdit = new MapLayerEdit(currentLayer,
		// mapEditor.createLayerCopy(currentLayer));

		switch (transform) {
		case MapLayer.ROTATE_90:
		case MapLayer.ROTATE_180:
		case MapLayer.ROTATE_270:
			// transEdit.setPresentationName("Rotate");
			layer.rotate(transform);
			// if(marqueeSelection != null) marqueeSelection.rotate(transform);
			break;
		case MapLayer.MIRROR_VERTICAL:
			// transEdit.setPresentationName("Vertical Flip");
			layer.mirror(MapLayer.MIRROR_VERTICAL);
			// if(marqueeSelection != null) marqueeSelection.mirror(transform);
			break;
		case MapLayer.MIRROR_HORIZONTAL:
			// transEdit.setPresentationName("Horizontal Flip");
			layer.mirror(MapLayer.MIRROR_HORIZONTAL);
			// if(marqueeSelection != null) marqueeSelection.mirror(transform);
			break;
		}

		// transEdit.end(mapEditor.createLayerCopy(currentLayer));
		// mapEditor.undoSupport.postEdit(transEdit);
		// mapView.repaint();
	}
}