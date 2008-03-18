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

package tiled.mapeditor.widget;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import tiled.core.Map;
import tiled.core.TileGroup;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.brush.Brush;
import tiled.mapeditor.brush.MultiTileBrush;
import tiled.mapeditor.brush.ShapeBrush;
import tiled.mapeditor.brush.TileGroupBrush;
import tiled.mapeditor.util.MapChangeListener;
import tiled.mapeditor.util.MapChangedEvent;

/**
 * Brush Menu.
 * 
 * @author mtotz
 */
public class BrushMenu extends JPanel implements MapChangeListener {
	private static final long serialVersionUID = 1L;
	/** the map. */
	private Map map;
	/** the mapeditor instance. */
	private MapEditor mapEditor;
	/** list of default brushes. */
	private List<BrushWrapper> defaultBrushes;
	/** dropdown list. */
	private JComboBox combobox;
	/** the brush selected before the delete brush was selected .*/
	private BrushWrapper oldBrush;

	public BrushMenu(MapEditor mapEditor) {
		this.mapEditor = mapEditor;

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		combobox = new JComboBox();
		add(combobox);

		defaultBrushes = new ArrayList<BrushWrapper>();
		defaultBrushes.add(new BrushWrapper(new MultiTileBrush()));
		defaultBrushes.add(new BrushWrapper(ShapeBrush.makeRectBrush(1, 1)));
		defaultBrushes.add(new BrushWrapper(ShapeBrush.makeRectBrush(2, 2)));
		defaultBrushes.add(new BrushWrapper(ShapeBrush.makeRectBrush(4, 4)));
		defaultBrushes.add(new BrushWrapper(ShapeBrush.makeRectBrush(8, 8)));
	}

	@Override
	public Dimension getPreferredSize() {
		return combobox.getPreferredSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	/** sets the map. */
	public void setMap(Map map) {
		this.map = map;
		if (map != null) {
			map.addMapChangeListener(MapChangedEvent.Type.BRUSHES, this);
			updateBrushes();
		}
	}

	/** refreshes the brush list once the map changes. */
	public void mapChanged(MapChangedEvent e) {
		if (e.getType() != MapChangedEvent.Type.BRUSHES) {
			return;
		}

		updateBrushes();
	}

	/** updates the brush list. */
	private void updateBrushes() {
		List<TileGroup> groupList = map.getUserBrushes();

		List<BrushWrapper> brushes = new ArrayList<BrushWrapper>();
		brushes.addAll(defaultBrushes);

		for (TileGroup group : groupList) {
			brushes.add(new BrushWrapper(new TileGroupBrush(group)));
		}

		combobox.setModel(new DefaultComboBoxModel(brushes.toArray(new BrushWrapper[brushes.size()])));
		combobox.setSelectedIndex(1);
		combobox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object o = BrushMenu.this.combobox.getSelectedItem();
				if (o instanceof BrushWrapper) {
					BrushMenu.this.mapEditor.setBrush(((BrushWrapper) o).brush);
				}
			}
		});
	}

	/** selects the first shape brush (when another is currently active). */
	public void selectDefaultDeleteBrush() {
		BrushWrapper brushWrapper = (BrushWrapper) combobox.getSelectedItem();
		if (!(brushWrapper.brush instanceof ShapeBrush)) {
			// select the first ShapeBrush, it is the second default one
			combobox.setSelectedItem(defaultBrushes.get(1));
			oldBrush = brushWrapper;
		}
	}

	/** selects the first shape brush (when another is currently active). */
	public void unselectDefaultDeleteBrush() {
		if (oldBrush != null) {
			combobox.setSelectedItem(oldBrush);
		}
	}

	/** wraps a brush to create a nice toString() (for the selection box). */
	private class BrushWrapper {
		public Brush brush;

		public BrushWrapper(Brush brush) {
			this.brush = brush;
		}

		/**  */
		@Override
		public String toString() {
			return brush.getName();
		}

	}
}
