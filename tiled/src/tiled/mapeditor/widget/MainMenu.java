/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package tiled.mapeditor.widget;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;
import tiled.mapeditor.util.MapEventAdapter;

/**
 * The menu bar
 * @author Matthias Totz <mtotz@users.sourceforge.net>
 */
public class MainMenu extends JMenuBar
{
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

  // view menu
  private JCheckBoxMenuItem cursorMenuItem;
  private JCheckBoxMenuItem coordinatesMenuItem;
  private JCheckBoxMenuItem gridMenuItem;
  private JCheckBoxMenuItem boundaryMenuItem;

  /**
   * 
   */
  public MainMenu(MapEditor mapEditor, MapEventAdapter mapEventAdapter)
  {
    super();
    this.mapEditor = mapEditor;

    JMenuItem save        = createMenuItem("Save", null, "Save current map", "control S");
    JMenuItem saveAs      = createMenuItem("Save as...", null,"Save current map as new file", "control shift S");
    JMenuItem saveAsImage = createMenuItem("Save as Image...", null, "Save current map as an image", "control shift I");
    JMenuItem close       = createMenuItem("Close", null, "Close this map", "control W");
    JMenuItem print       = createMenuItem("Print...", null, "Print the map", "control P");
    
    recentMenu = new JMenu("Open Recent");
    
    mapEventAdapter.addListener(save);
    mapEventAdapter.addListener(saveAs);
    mapEventAdapter.addListener(saveAsImage);
    mapEventAdapter.addListener(close);
    mapEventAdapter.addListener(print);
    
    JMenu fileMenu = new JMenu("File");
    fileMenu.add(createMenuItem("New...", null, "Start a new map", "control N"));
    fileMenu.add(createMenuItem("Open...", null, "Open a map", "control O"));
    fileMenu.add(recentMenu);
    fileMenu.add(save);
    fileMenu.add(saveAs);
    fileMenu.add(saveAsImage);
    // TODO: Re-add print menuitem when printing is functional
    //fileMenu.addSeparator();
    //fileMenu.add(print);
    //mapEventAdapter.addListener(print);
    fileMenu.addSeparator();
    fileMenu.add(close);
    fileMenu.add(createMenuItem("Exit", null, "Exit the map editor",
            "control Q"));
    
    undoMenuItem = new TMenuItem(mapEditor.undoAction);
    redoMenuItem = new TMenuItem(mapEditor.redoAction);
    undoMenuItem.setEnabled(false);
    redoMenuItem.setEnabled(false);
    
    TMenuItem copyMenuItem = new TMenuItem(mapEditor.copyAction);
    TMenuItem cutMenuItem = new TMenuItem(mapEditor.cutAction);
    TMenuItem pasteMenuItem = new TMenuItem(mapEditor.pasteAction);
    copyMenuItem.setEnabled(false);
    cutMenuItem.setEnabled(false);
    pasteMenuItem.setEnabled(false);
    
    JMenu transformSub = new JMenu("Transform");
    transformSub.add(new TMenuItem(mapEditor.rot90Action, true));
    transformSub.add(new TMenuItem(mapEditor.rot180Action, true));
    transformSub.add(new TMenuItem(mapEditor.rot270Action, true));
    transformSub.addSeparator();
    transformSub.add(new TMenuItem(mapEditor.flipHorAction, true));
    transformSub.add(new TMenuItem(mapEditor.flipVerAction, true));
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
    editMenu.add(createMenuItem("Brush...", null, "Configure the brush", "control B"));
    
    mapEventAdapter.addListener(undoMenuItem);
    mapEventAdapter.addListener(redoMenuItem);
    mapEventAdapter.addListener(copyMenuItem);
    mapEventAdapter.addListener(cutMenuItem);
    mapEventAdapter.addListener(pasteMenuItem);
    
    
    JMenu mapMenu = new JMenu("Map");
    mapMenu.add(createMenuItem("Resize", null, "Modify map dimensions"));
    mapMenu.add(createMenuItem("Search", null,
            "Search for/Replace tiles"));
    mapMenu.addSeparator();
    mapMenu.add(createMenuItem("Properties", null, "Map properties"));
    mapEventAdapter.addListener(mapMenu);
    
    
    JMenuItem layerAdd = createMenuItem("Add Layer", null, "Add a layer");
    layerClone = createMenuItem("Duplicate Layer", null, "Duplicate current layer");
    layerDel = createMenuItem("Delete Layer", null, "Delete current layer");
    layerUp = createMenuItem("Move Layer Up", null, "Move layer up one in layer stack", "shift PAGE_UP");
    layerDown = createMenuItem("Move Layer Down", null, "Move layer down one in layer stack", "shift PAGE_DOWN");
    layerMerge = createMenuItem("Merge Down", null, "Merge current layer onto next lower", "shift control M");
    layerMergeAll = createMenuItem("Merge All", null, "Merge all layers");
    JMenuItem layerProperties = createMenuItem("Layer Properties", null,
        "Current layer properties");
    
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
    tilesetMenu.add(createMenuItem("New Tileset...", null,
            "Add a new internal tileset"));
    tilesetMenu.add(createMenuItem("Import Tileset...", null,
            "Import an external tileset"));
    tilesetMenu.addSeparator();
    tilesetMenu.add(createMenuItem("Tileset Manager", null,
            "Open the tileset manager"));
    
    
    /*
    objectMenu = new JMenu("Objects");
    objectMenu.add(createMenuItem("Add Object", null, "Add an object"));
    mapEventAdapter.addListener(objectMenu);
    
    JMenu modifySub = new JMenu("Modify");
    modifySub.add(createMenuItem("Expand Selection", null, ""));
    modifySub.add(createMenuItem("Contract Selection", null, ""));
    */
    
    JMenu selectMenu = new JMenu("Select");
    selectMenu.add(new TMenuItem(mapEditor.selectAllAction, true));
    selectMenu.add(new TMenuItem(mapEditor.cancelSelectionAction, true));
    selectMenu.add(new TMenuItem(mapEditor.inverseAction, true));
    //selectMenu.addSeparator();
    //selectMenu.add(modifySub);
    
    
    gridMenuItem = new JCheckBoxMenuItem("Show Grid");
    gridMenuItem.addActionListener(mapEditor);
    gridMenuItem.setToolTipText("Toggle grid");
    gridMenuItem.setAccelerator(KeyStroke.getKeyStroke("control G"));
    
    cursorMenuItem = new JCheckBoxMenuItem("Highlight Cursor");
    cursorMenuItem.setSelected(mapEditor.configuration.keyHasValue("tiled.cursorhighlight", 1));
    cursorMenuItem.addActionListener(mapEditor);
    cursorMenuItem.setToolTipText("Toggle highlighting on-map cursor position");
    
    boundaryMenuItem = new JCheckBoxMenuItem("Show Boundaries");
    boundaryMenuItem.addActionListener(mapEditor);
    boundaryMenuItem.setToolTipText("Toggle layer boundaries");
    boundaryMenuItem.setAccelerator(KeyStroke.getKeyStroke("control E"));
    
    coordinatesMenuItem = new JCheckBoxMenuItem("Show Coordinates");
    coordinatesMenuItem.addActionListener(mapEditor);
    coordinatesMenuItem.setToolTipText("Toggle tile coordinates");
    
    JMenu viewMenu = new JMenu("View");
    viewMenu.add(new TMenuItem(mapEditor.zoomInAction));
    viewMenu.add(new TMenuItem(mapEditor.zoomOutAction));
    viewMenu.add(new TMenuItem(mapEditor.zoomNormalAction));
    viewMenu.addSeparator();
    viewMenu.add(gridMenuItem);
    viewMenu.add(cursorMenuItem);
    // TODO: Enable when boudary drawing code finished.
    //viewMenu.add(boundaryMenuItem);
    viewMenu.add(coordinatesMenuItem);
    
    mapEventAdapter.addListener(layerMenu);
    mapEventAdapter.addListener(tilesetMenu);
    mapEventAdapter.addListener(selectMenu);
    mapEventAdapter.addListener(viewMenu);
    
    JMenu helpMenu = new JMenu("Help");
    helpMenu.add(createMenuItem("About Plug-ins", null,
            "Show plugin window"));
    helpMenu.add(createMenuItem("About Tiled", null, "Show about window"));
    
    add(fileMenu);
    add(editMenu);
    add(selectMenu);
    add(viewMenu);
    add(mapMenu);
    add(layerMenu);
    add(tilesetMenu);
    //menuBar.add(objectMenu);
    add(helpMenu);
  }

  private JMenuItem createMenuItem(String name, Icon icon, String tt)
  {
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
  
  private JMenuItem createMenuItem(String name, Icon icon, String tt, String keyStroke)
  {
    JMenuItem menuItem = createMenuItem(name, icon, tt);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(keyStroke));
    return menuItem;
  }

  /**
   * @param validSelection
   * @param notBottom
   * @param notTop
   */
  public void updateLayerOperations(boolean validSelection, boolean notBottom, boolean notTop, boolean enableMergeAll)
  {
    layerClone.setEnabled(validSelection);
    layerDel.setEnabled(validSelection);
    layerUp.setEnabled(notTop);
    layerDown.setEnabled(notBottom);
    layerMerge.setEnabled(notBottom);
    layerMergeAll.setEnabled(enableMergeAll);
  }

  public void setUndo(boolean enable, String undoText)
  {
    undoMenuItem.setText(undoText);
    undoMenuItem.setEnabled(enable);
  }

  public void setRedo(boolean enable, String redoText)
  {
    redoMenuItem.setText(redoText);
    redoMenuItem.setEnabled(enable);
  }
  
  public boolean isHighlightCursorSelected()
  {
    return cursorMenuItem.isSelected();
  }

  public void clearAllRecent()
  {
    recentMenu.removeAll();
  }

  public void addRecent(String name, String actionCmd)
  {
    JMenuItem recentOption = createMenuItem(name, null, null);
    recentOption.setActionCommand(actionCmd);
    recentMenu.add(recentOption);
  }

  public void setShowGrid(boolean mode)
  {
    gridMenuItem.setState(mode);
  }

  public void setShowCoordinates(boolean mode)
  {
    coordinatesMenuItem.setState(mode);
  }
  
  

}
