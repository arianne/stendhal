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

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import tiled.core.Map;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.actions.CreateMultiLayerBrushAction;
import tiled.mapeditor.actions.CreateSingleLayerBrushAction;
import tiled.mapeditor.actions.ZoomInAction;
import tiled.mapeditor.actions.ZoomOutAction;
import tiled.mapeditor.util.MapEventAdapter;

/**
 * The toolbar.
 * 
 * @author Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */
public class ToolBar extends JToolBar implements ActionListener {
	private static final long serialVersionUID = 1L;

	private MapEditor mapEditor;

	private AbstractButton moveButton;

	private AbstractButton paintButton;

	private AbstractButton eraseButton;

	private AbstractButton eyedButton;
	/** the dropdown list with all available brushes. */
	private BrushMenu brushMenu;
	/** create simple singlelayer brush. */
	private AbstractButton brushButton;
	/** create multilayer brush. */
	private AbstractButton brushExtButton;

	public ToolBar(MapEditor mapEditor, MapEventAdapter mapEventAdapter) {
		super(JToolBar.HORIZONTAL);

		this.mapEditor = mapEditor;

		Icon iconMove = MapEditor.loadIcon("resources/gimp-tool-move-22.png");
		Icon iconPaint = MapEditor.loadIcon("resources/gimp-tool-pencil-22.png");
		Icon iconErase = MapEditor.loadIcon("resources/gimp-tool-eraser-22.png");
		Icon iconEyed = MapEditor.loadIcon("resources/gimp-tool-color-picker-22.png");

		paintButton = createToggleButton(iconPaint, "paint", "Paint");
		eraseButton = createToggleButton(iconErase, "erase", "Erase");
		eyedButton = createToggleButton(iconEyed, "eyed", "Eye dropper");
		moveButton = createToggleButton(iconMove, "move", "Move layer");
		brushMenu = new BrushMenu(mapEditor);
		brushButton = new JButton(mapEditor.actionManager.getAction(CreateSingleLayerBrushAction.class));
		brushExtButton = new JButton(mapEditor.actionManager.getAction(CreateMultiLayerBrushAction.class));

		mapEventAdapter.addListener(moveButton);
		mapEventAdapter.addListener(paintButton);
		mapEventAdapter.addListener(eraseButton);
		mapEventAdapter.addListener(eyedButton);

		setFloatable(false);
		add(moveButton);
		add(paintButton);
		add(eraseButton);
		add(eyedButton);
		addSeparator();
		add(new TButton(mapEditor.actionManager.getAction(ZoomInAction.class)));
		add(new TButton(mapEditor.actionManager.getAction(ZoomOutAction.class)));
		addSeparator();

		add(brushMenu);
		addSeparator();
		add(brushButton);
		add(brushExtButton);

		addSeparator();
		add(new MemMonitor());

	}

	private AbstractButton createToggleButton(Icon icon, String command, String tt) {
		return createButton(icon, command, true, tt);
	}

	private AbstractButton createButton(Icon icon, String command, boolean toggleButton, String tooltipText) {
		AbstractButton button;
		if (toggleButton) {
			button = new JToggleButton("", icon);
		} else {
			button = new JButton("", icon);
		}
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setActionCommand(command);
		button.addActionListener(this);
		if (tooltipText != null) {
			button.setToolTipText(tooltipText);
		}
		return button;
	}

	/**
	 * @param state
	 */
	public void setButtonStates(int state) {
		// Select the matching button
		paintButton.setSelected(state == MapEditor.PS_PAINT);
		eraseButton.setSelected(state == MapEditor.PS_ERASE);
		eyedButton.setSelected(state == MapEditor.PS_EYED);
		moveButton.setSelected(state == MapEditor.PS_MOVE);
	}

	/** sets the map. */
	public void setMap(Map map) {
		brushMenu.setMap(map);
		boolean state = (map != null);
		brushButton.setEnabled(state);
		brushExtButton.setEnabled(state);
	}

	/** returns the brush menu. */
	public BrushMenu getBrushMenu() {
		return brushMenu;
	}

	/** action handler for the buttons. */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("erase")) {
			mapEditor.toggleDeleteTile(true);
		} else if (command.equals("paint")) {
			mapEditor.toggleDeleteTile(false);
		}
	}

}
