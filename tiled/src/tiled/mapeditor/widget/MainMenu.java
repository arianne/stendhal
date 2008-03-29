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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import tiled.core.MapLayer;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.actions.AddLayerAction;
import tiled.mapeditor.actions.CancelSelectionAction;
import tiled.mapeditor.actions.CloseAction;
import tiled.mapeditor.actions.CopyAction;
import tiled.mapeditor.actions.CutAction;
import tiled.mapeditor.actions.DelLayerAction;
import tiled.mapeditor.actions.DuplicateLayerAction;
import tiled.mapeditor.actions.ExitApplicationAction;
import tiled.mapeditor.actions.ImportTilesetAction;
import tiled.mapeditor.actions.InverseSelectionAction;
import tiled.mapeditor.actions.LayerPropertiesAction;
import tiled.mapeditor.actions.LayerTransformAction;
import tiled.mapeditor.actions.MapPropertiesAction;
import tiled.mapeditor.actions.MoveLayerDownAction;
import tiled.mapeditor.actions.MoveLayerUpAction;
import tiled.mapeditor.actions.NewMapAction;
import tiled.mapeditor.actions.NewTilesetAction;
import tiled.mapeditor.actions.OpenAction;
import tiled.mapeditor.actions.PasteAction;
import tiled.mapeditor.actions.RedoAction;
import tiled.mapeditor.actions.SaveAsImageAction;
import tiled.mapeditor.actions.SaveMapAction;
import tiled.mapeditor.actions.SelectAllAction;
import tiled.mapeditor.actions.TilesetManagerAction;
import tiled.mapeditor.actions.ToggleCoordinatesAction;
import tiled.mapeditor.actions.ToggleGridAction;
import tiled.mapeditor.actions.TreeTilesetChooserAction;
import tiled.mapeditor.actions.UndoAction;
import tiled.mapeditor.actions.ZoomInAction;
import tiled.mapeditor.actions.ZoomNormalAction;
import tiled.mapeditor.actions.ZoomOutAction;
import tiled.mapeditor.util.MapEventAdapter;

/**
 * The menu bar.
 * 
 * @author Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */
public class MainMenu extends JMenuBar implements ActionListener {
	private static final long serialVersionUID = 6139028013794826255L;

	private MapEditor mapEditor;

	// File Menu
	private JMenu recentMenu;

	// Edit menu
	private TMenuItem undoMenuItem;
	private TMenuItem redoMenuItem;

	// Layer menu
	private JMenuItem layerClone;
	private JMenuItem layerDel;
	private JMenuItem layerUp;
	private JMenuItem layerDown;
	private JMenuItem layerMerge;
	private JMenuItem layerMergeAll;

	/** creates the main menu. */
	public MainMenu(MapEditor mapEditor, MapEventAdapter mapEventAdapter) {
		super();
		this.mapEditor = mapEditor;

		JMenuItem save = new TMenuItem(mapEditor.actionManager.getAction(SaveMapAction.class, false));
		JMenuItem saveAs = new TMenuItem(mapEditor.actionManager.getAction(SaveMapAction.class, true));
		JMenuItem saveAsImage = new TMenuItem(mapEditor.actionManager.getAction(SaveAsImageAction.class));
		JMenuItem close = new TMenuItem(mapEditor.actionManager.getAction(CloseAction.class));

		recentMenu = new JMenu("Open Recent");

		mapEventAdapter.addListener(save);
		mapEventAdapter.addListener(saveAs);
		mapEventAdapter.addListener(saveAsImage);
		mapEventAdapter.addListener(close);

		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new TMenuItem(mapEditor.actionManager.getAction(NewMapAction.class)));
		fileMenu.add(new TMenuItem(mapEditor.actionManager.getAction(OpenAction.class)));
		fileMenu.add(recentMenu);
		fileMenu.add(save);
		fileMenu.add(saveAs);
		fileMenu.add(saveAsImage);
		fileMenu.addSeparator();
		fileMenu.add(close);
		fileMenu.add(new TMenuItem(mapEditor.actionManager.getAction(ExitApplicationAction.class)));

		undoMenuItem = new TMenuItem(mapEditor.actionManager.getAction(UndoAction.class));
		redoMenuItem = new TMenuItem(mapEditor.actionManager.getAction(RedoAction.class));
		undoMenuItem.setEnabled(false);
		redoMenuItem.setEnabled(false);

		TMenuItem copyMenuItem = new TMenuItem(mapEditor.actionManager.getAction(CopyAction.class));
		TMenuItem cutMenuItem = new TMenuItem(mapEditor.actionManager.getAction(CutAction.class));
		TMenuItem pasteMenuItem = new TMenuItem(mapEditor.actionManager.getAction(PasteAction.class));
		copyMenuItem.setEnabled(false);
		cutMenuItem.setEnabled(false);
		pasteMenuItem.setEnabled(false);

		JMenu transformSub = new JMenu("Transform");
		transformSub.add(new TMenuItem(
				mapEditor.actionManager.getAction(LayerTransformAction.class, MapLayer.ROTATE_90), true));
		transformSub.add(new TMenuItem(mapEditor.actionManager.getAction(LayerTransformAction.class,
				MapLayer.ROTATE_180), true));
		transformSub.add(new TMenuItem(mapEditor.actionManager.getAction(LayerTransformAction.class,
				MapLayer.ROTATE_270), true));
		transformSub.addSeparator();
		transformSub.add(new TMenuItem(mapEditor.actionManager.getAction(LayerTransformAction.class,
				MapLayer.MIRROR_HORIZONTAL), true));
		transformSub.add(new TMenuItem(mapEditor.actionManager.getAction(LayerTransformAction.class,
				MapLayer.MIRROR_VERTICAL), true));
		mapEventAdapter.addListener(transformSub);

		JMenu editMenu = new JMenu("Edit");
		editMenu.add(undoMenuItem);
		editMenu.add(redoMenuItem);
		editMenu.addSeparator();
		editMenu.add(copyMenuItem);
		editMenu.add(cutMenuItem);
		editMenu.add(pasteMenuItem);
		editMenu.addSeparator();
		editMenu.add(transformSub);
		editMenu.addSeparator();
		editMenu.add(createMenuItem("Preferences...", null, "Configure options of the editor", null));

		mapEventAdapter.addListener(undoMenuItem);
		mapEventAdapter.addListener(redoMenuItem);
		mapEventAdapter.addListener(copyMenuItem);
		mapEventAdapter.addListener(cutMenuItem);
		mapEventAdapter.addListener(pasteMenuItem);

		JMenu mapMenu = new JMenu("Map");
		mapMenu.add(createMenuItem("Resize", null, "Modify map dimensions"));
		mapMenu.add(createMenuItem("Search", null, "Search for/Replace tiles"));
		mapMenu.addSeparator();
		mapMenu.add(new TMenuItem(mapEditor.actionManager.getAction(MapPropertiesAction.class)));
		mapEventAdapter.addListener(mapMenu);

		JMenuItem layerAdd = new TMenuItem(mapEditor.actionManager.getAction(AddLayerAction.class));
		layerClone = new TMenuItem(mapEditor.actionManager.getAction(DuplicateLayerAction.class));
		layerDel = new TMenuItem(mapEditor.actionManager.getAction(DelLayerAction.class));
		layerUp = new TMenuItem(mapEditor.actionManager.getAction(MoveLayerUpAction.class));
		layerDown = new TMenuItem(mapEditor.actionManager.getAction(MoveLayerDownAction.class));
		layerMerge = createMenuItemWithThisAsActionListener("Merge Down", null, "Merge current layer onto next lower",
				"shift control M");
		layerMergeAll = createMenuItemWithThisAsActionListener("Merge All", null, "Merge all layers", null);
		JMenuItem layerProperties = new TMenuItem(mapEditor.actionManager.getAction(LayerPropertiesAction.class));

		mapEventAdapter.addListener(layerAdd);

		JMenu layerMenu = new JMenu("Layer");
		layerMenu.add(layerAdd);
		layerMenu.add(layerClone);
		layerMenu.add(layerDel);
		layerMenu.addSeparator();
		layerMenu.add(layerUp);
		layerMenu.add(layerDown);
		layerMenu.addSeparator();
		layerMenu.add(layerMerge);
		layerMenu.add(layerMergeAll);
		layerMenu.addSeparator();
		layerMenu.add(layerProperties);

		JMenu tilesetMenu = new JMenu("Tilesets");
		tilesetMenu.add(new TMenuItem(mapEditor.actionManager.getAction(NewTilesetAction.class)));
		tilesetMenu.add(new TMenuItem(mapEditor.actionManager.getAction(ImportTilesetAction.class)));
		JCheckBoxMenuItem treeTilesetChooser = new JCheckBoxMenuItem(
				mapEditor.actionManager.getAction(TreeTilesetChooserAction.class));
		treeTilesetChooser.setSelected(true);
		tilesetMenu.add(treeTilesetChooser);
		tilesetMenu.addSeparator();
		tilesetMenu.add(new TMenuItem(mapEditor.actionManager.getAction(TilesetManagerAction.class)));

		JMenu selectMenu = new JMenu("Select");
		selectMenu.add(new TMenuItem(mapEditor.actionManager.getAction(SelectAllAction.class), true));
		selectMenu.add(new TMenuItem(mapEditor.actionManager.getAction(CancelSelectionAction.class), true));
		selectMenu.add(new TMenuItem(mapEditor.actionManager.getAction(InverseSelectionAction.class), true));

		JMenu viewMenu = new JMenu("View");
		viewMenu.add(new TMenuItem(mapEditor.actionManager.getAction(ZoomInAction.class)));
		viewMenu.add(new TMenuItem(mapEditor.actionManager.getAction(ZoomOutAction.class)));
		viewMenu.add(new TMenuItem(mapEditor.actionManager.getAction(ZoomNormalAction.class)));
		viewMenu.addSeparator();
		viewMenu.add(new JCheckBoxMenuItem(mapEditor.actionManager.getAction(ToggleGridAction.class)));
		viewMenu.add(new JCheckBoxMenuItem(mapEditor.actionManager.getAction(ToggleCoordinatesAction.class)));

		mapEventAdapter.addListener(layerMenu);
		mapEventAdapter.addListener(tilesetMenu);
		mapEventAdapter.addListener(selectMenu);
		mapEventAdapter.addListener(viewMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(createMenuItem("About Plug-ins", null, "Show plugin window"));
		helpMenu.add(createMenuItem("About", null, "Show about window"));

		add(fileMenu);
		add(editMenu);
		add(selectMenu);
		add(viewMenu);
		add(mapMenu);
		add(layerMenu);
		add(tilesetMenu);
		add(helpMenu);
	}

	private JMenuItem createMenuItem(String name, Icon icon, String tt) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.addActionListener(mapEditor);
		if (icon != null) {
			menuItem.setIcon(icon);
		}
		if (tt != null) {
			menuItem.setToolTipText(tt);
		}
		return menuItem;
	}

	private JMenuItem createMenuItemWithThisAsActionListener(String name, Icon icon, String tt, String keyStroke) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.addActionListener(this);
		if (icon != null) {
			menuItem.setIcon(icon);
		}
		if (tt != null) {
			menuItem.setToolTipText(tt);
		}
		if (keyStroke != null) {
			menuItem.setAccelerator(KeyStroke.getKeyStroke(keyStroke));
		}
		return menuItem;
	}

	private JMenuItem createMenuItem(String name, Icon icon, String tt, String keyStroke) {
		JMenuItem menuItem = createMenuItem(name, icon, tt);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(keyStroke));
		return menuItem;
	}

	/**
	 * @param validSelection
	 * @param notBottom
	 * @param notTop
	 */
	public void updateLayerOperations(boolean validSelection, boolean notBottom, boolean notTop, boolean enableMergeAll) {
		layerClone.setEnabled(validSelection);
		layerDel.setEnabled(validSelection);
		layerUp.setEnabled(notTop);
		layerDown.setEnabled(notBottom);
		layerMerge.setEnabled(notBottom);
		layerMergeAll.setEnabled(enableMergeAll);
	}

	public void setUndo(boolean enable, String undoText) {
		undoMenuItem.setText(undoText);
		undoMenuItem.setEnabled(enable);
	}

	public void setRedo(boolean enable, String redoText) {
		redoMenuItem.setText(redoText);
		redoMenuItem.setEnabled(enable);
	}

	public void clearAllRecent() {
		recentMenu.removeAll();
	}

	public void addRecent(String name, String actionCmd) {
		JMenuItem recentOption = createMenuItem(name, null, null);
		recentOption.setActionCommand(actionCmd);
		recentMenu.add(recentOption);
	}

	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();

		if (command.equals("Merge Down") || command.equals("Merge All")) {
			mapEditor.doLayerStateChange(event);
		}
	}

}
