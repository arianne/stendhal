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
 *  modified for stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.mapeditor;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.UndoableEditSupport;

import tiled.core.*;
import tiled.view.test.MapView;
import tiled.view.test.Orthogonal;
import tiled.mapeditor.TilesetManager;
import tiled.mapeditor.actions.*;
import tiled.mapeditor.brush.*;
import tiled.mapeditor.dialog.*;
import tiled.mapeditor.plugin.PluginClassLoader;
import tiled.mapeditor.selection.SelectionLayer;
import tiled.mapeditor.util.*;
import tiled.mapeditor.widget.*;
import tiled.mapeditor.undo.*;
import tiled.util.TiledConfiguration;
import tiled.util.Util;
import tiled.io.MapHelper;
import tiled.io.MapReader;
import tiled.io.MapWriter;

/**
 * The main class for the Tiled Map Editor.
 */
public class MapEditor implements ActionListener, MouseListener,
    MouseMotionListener, MapChangeListener, ComponentListener
{
  // Constants and the like
  public static final int     PS_POINT         = 0;
  public static final int     PS_PAINT         = 1;
  public static final int     PS_ERASE         = 2;
  public static final int     PS_POUR          = 3;
  public static final int     PS_EYED          = 4;
  public static final int     PS_MARQUEE       = 5;
  public static final int     PS_MOVE          = 6;
  public static final int     PS_MOVEOBJ       = 7;

  /** current release version */
  public static final String  version          = "0.0.1";

  public Map                  currentMap;
  public MapView              mapView;
  public UndoStack           undoStack;
  public UndoableEditSupport undoSupport;
  private MapEventAdapter     mapEventAdapter;
  public PluginClassLoader   pluginLoader;
  public TiledConfiguration   configuration;

  int                         currentPointerState;
  // Tile currentTile;
  private List<Tile>          currentTiles;

  public int                  currentLayer     = -1;
  boolean                     bMouseIsDown     = false;
  SelectionLayer              cursorHighlight;
  Point                       mousePressLocation, mouseInitialPressLocation;
  Point                       moveDist;
  int                         mouseButton;
  public Brush                       currentBrush;
  public SelectionLayer              marqueeSelection = null;
  public MapLayer                    clipboardLayer   = null;

  // GUI components
  public MainMenu             mainMenu;
  public ToolBar              toolBar;
  public LayerEditPanel       layerEditPanel;
  public TilesetChooserTabbedPane tilePalettePanel;
  public MapEditPanel         mapEditPanel;

  JPanel                      mainPanel;

  JPanel                      statusBar;
  public JScrollPane          mapScrollPane;

  public JFrame                      appFrame;
  JLabel                      zoomLabel, tileCoordsLabel;

  TilePaletteDialog           tilePaletteDialog;
  AboutDialog                 aboutDialog;
  MapLayerEdit                paintEdit;

  // Actions
  public Action               zoomInAction;
  public Action               zoomOutAction;
  public Action               zoomNormalAction;
  public Action               undoAction;
  public Action               redoAction;
  public Action               rot90Action;
  public Action               rot180Action;
  public Action               rot270Action;
  public Action               flipHorAction;
  public Action               flipVerAction;
  public Action               copyAction;
  public Action               cutAction;
  public Action               pasteAction;
  public Action               selectAllAction;
  public Action               inverseAction;
  public Action               cancelSelectionAction;
  public Action               openMapAction;
  public Action               closeMapAction;
  public Action               saveMapAction;
  public Action               saveMapAsAction;
  public Action               saveMapAsImageAction;
  public Action               newMapAction;
  public Action               exitApplicationAction;
  public Action               newTilesetAction;
  public Action               importTilesetAction;
  public Action               tilesetManagerAction;
  public Action               mapPropertiesAction;
  public Action               layerPropertiesAction;

  public MapEditor()
  {
    // Get instance of configuration
    configuration = TiledConfiguration.getInstance();

    undoStack = new UndoStack();
    undoSupport = new UndoableEditSupport();
    undoSupport.addUndoableEditListener(new UndoAdapter());

    cursorHighlight = new SelectionLayer(1, 1);
    cursorHighlight.select(0, 0);
    cursorHighlight.setVisible(configuration.keyHasValue(
        "tiled.cursorhighlight", 1));

    mapEventAdapter = new MapEventAdapter();

    // Create a default brush
    ShapeBrush sb = new ShapeBrush();
    sb.makeQuadBrush(new Rectangle(0, 0, 1, 1));
    setBrush(sb);

    // Create the actions
    zoomInAction = new ZoomInAction(this);
    zoomOutAction = new ZoomOutAction(this);
    zoomNormalAction = new ZoomNormalAction(this);
    undoAction = new UndoAction(this);
    redoAction = new RedoAction(this);
    rot90Action = new LayerTransformAction(this, MapLayer.ROTATE_90);
    rot180Action = new LayerTransformAction(this, MapLayer.ROTATE_180);
    rot270Action = new LayerTransformAction(this, MapLayer.ROTATE_270);
    flipHorAction = new LayerTransformAction(this, MapLayer.MIRROR_HORIZONTAL);
    flipVerAction = new LayerTransformAction(this, MapLayer.MIRROR_VERTICAL);
    copyAction = new CopyAction(this);
    pasteAction = new PasteAction(this);
    cutAction = new CutAction(this);
    selectAllAction = new SelectAllAction(this);
    cancelSelectionAction = new CancelSelectionAction(this);
    inverseAction = new InverseSelectionAction(this);
    openMapAction = new OpenAction(this);
    closeMapAction = new CloseAction(this);
    saveMapAction = new SaveMapAction(this,false);
    saveMapAsAction = new SaveMapAction(this,true);
    saveMapAsImageAction = new SaveAsImageAction(this);
    newMapAction = new NewMapAction(this);
    exitApplicationAction = new ExitApplicationAction(this);
    newTilesetAction = new NewTilesetAction(this);
    importTilesetAction = new ImportTilesetAction(this);
    tilesetManagerAction = new TilesetManagerAction(this);
    mapPropertiesAction = new MapPropertiesAction(this);
    layerPropertiesAction = new LayerPropertiesAction(this);

    // Create our frame
    appFrame = new JFrame("Stendhal Mapeditor");
    appFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    appFrame.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent event)
      {
        exit();
      }
    });
    appFrame.setContentPane(createContentPane());
    // add the main menu
    mainMenu = new MainMenu(this, mapEventAdapter);
    appFrame.setJMenuBar(mainMenu);
    appFrame.setSize(600, 400);
    setCurrentMap(null);
    updateRecent(null);

    appFrame.setVisible(true);

    // Load plugins
    pluginLoader = PluginClassLoader.getInstance();
    try
    {
      pluginLoader.readPlugins(null, appFrame);
    } catch (Exception e)
    {
      e.printStackTrace();
      JOptionPane.showMessageDialog(appFrame, e.toString(), "Plugin loader",
          JOptionPane.WARNING_MESSAGE);
    }
    MapHelper.init(pluginLoader);
  }

  private JPanel createContentPane()
  {
    createStatusBar();
    // minimap needs the mapScrollPane
    mapScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    layerEditPanel = new LayerEditPanel(this, mapEventAdapter);

    mapEditPanel = new MapEditPanel(this);
    mapEditPanel.setMinimapPanel(layerEditPanel.getMiniMap());
    mapScrollPane.setViewportView(mapEditPanel);

    JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false,
        mapScrollPane, layerEditPanel);
    mainSplit.setResizeWeight(1.0);

    tilePalettePanel = new TilesetChooserTabbedPane(this);
    JSplitPane baseSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false,
        mainSplit, tilePalettePanel);
    baseSplit.setOneTouchExpandable(true);
    baseSplit.setResizeWeight(0.9);

    mainPanel = new JPanel(new BorderLayout());
    toolBar = new ToolBar(this, mapEventAdapter);
    mainPanel.add(toolBar, BorderLayout.NORTH);
    mainPanel.add(baseSplit);
    mainPanel.add(statusBar, BorderLayout.SOUTH);

    return mainPanel;
  }

  /** saves unsaved files (user option), saves the configuration and exits
   * the application */
  public void exit()
  {
    if (checkSave())
    {
      try
      {
        configuration.write("tiled.conf");
      } catch (Exception e)
      {
        e.printStackTrace();
      }
      System.exit(0);
    }
  }

  private void createStatusBar()
  {
    statusBar = new JPanel();
    statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));

    zoomLabel = new JLabel("100%");
    zoomLabel.setPreferredSize(zoomLabel.getPreferredSize());
    tileCoordsLabel = new JLabel(" ", SwingConstants.CENTER);

    statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
    JPanel largePart = new JPanel();

    statusBar.add(largePart);
    statusBar.add(tileCoordsLabel);
    statusBar.add(Box.createRigidArea(new Dimension(20, 0)));
    statusBar.add(zoomLabel);
  }

  private void updateLayerTable()
  {
    layerEditPanel.setMap(currentLayer, currentMap);
    updateLayerOperations();
    if (currentMap != null)
    {
      tilePalettePanel.setTilesets(currentMap.getTilesets());
    }
  }

  public void updateLayerOperations()
  {
    int nrLayers = 0;

    if (currentMap != null)
    {
      nrLayers = currentMap.getTotalLayers();
    }

    boolean validSelection = currentLayer >= 0;
    boolean notBottom = currentLayer > 0;
    boolean notTop = currentLayer < nrLayers - 1 && validSelection;

    mainMenu.updateLayerOperations(validSelection, notBottom, notTop,
        nrLayers > 1);
    layerEditPanel.updateLayerOperations(validSelection, notBottom, notTop);
  }

  /**
   * Returns the current map.
   * 
   * @return The currently selected map.
   */
  public Map getCurrentMap()
  {
    return currentMap;
  }

  /**
   * Returns the currently selected layer.
   * 
   * @return THe currently selected layer.
   */
  public MapLayer getCurrentLayer()
  {
    return currentMap.getLayer(currentLayer);
  }

  /**
   * Returns the main application frame.
   * 
   * @return The frame of the main application
   */
  public Frame getAppFrame()
  {
    return appFrame;
  }

  public void updateHistory()
  {
    // editHistoryList.setListData(undoStack.getEdits());
    mainMenu.setUndo(undoStack.canUndo(), undoStack.getUndoPresentationName());
    mainMenu.setRedo(undoStack.canRedo(), undoStack.getRedoPresentationName());
    updateTitle();
  }

  public void doLayerStateChange(ActionEvent event)
  {
    if (currentMap == null)
    {
      return;
    }

    String command = event.getActionCommand();
    List<MapLayer> layersBefore = currentMap.getLayerList();

    if (command.equals("Add Layer"))
    {
      currentMap.addLayer();
      setCurrentLayer(currentMap.getTotalLayers() - 1);
    } else if (command.equals("Duplicate Layer"))
    {
      if (currentLayer >= 0)
      {
        try
        {
          MapLayer clone = (MapLayer) getCurrentLayer().clone();
          clone.setName(clone.getName() + " copy");
          currentMap.addLayer(clone);
        } catch (CloneNotSupportedException ex)
        {
          ex.printStackTrace();
        }
        setCurrentLayer(currentMap.getTotalLayers() - 1);
      }
    } else if (command.equals("Move Layer Up"))
    {
      if (currentLayer >= 0)
      {
        try
        {
          currentMap.swapLayerUp(currentLayer);
          setCurrentLayer(currentLayer + 1);
        } catch (Exception ex)
        {
          System.out.println(ex.toString());
        }
      }
    } else if (command.equals("Move Layer Down"))
    {
      if (currentLayer >= 0)
      {
        try
        {
          currentMap.swapLayerDown(currentLayer);
          setCurrentLayer(currentLayer - 1);
        } catch (Exception ex)
        {
          System.out.println(ex.toString());
        }
      }
    } else if (command.equals("Delete Layer"))
    {
      if (currentLayer >= 0)
      {
        currentMap.removeLayer(currentLayer);
        setCurrentLayer(currentLayer < 0 ? 0 : currentLayer);
      }
    } else if (command.equals("Merge Down"))
    {
      if (currentLayer >= 0)
      {
        try
        {
          currentMap.mergeLayerDown(currentLayer);
          setCurrentLayer(currentLayer - 1);
        } catch (Exception ex)
        {
          System.out.println(ex.toString());
        }
      }
    } else if (command.equals("Merge All"))
    {
      // TODO: put this back in for 0.5.2
      /*
       * if( JOptionPane.showConfirmDialog(appFrame, "Do you wish to merge tile
       * images, and create a new tile set?", "Merge Tiles?",
       * JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ) {
       * TileMergeHelper tmh = new TileMergeHelper(currentMap); int len =
       * currentMap.getTotalLayers(); //TODO: Add a dialog option: "Yes, visible
       * only" TileLayer newLayer = tmh.merge(0, len, true);
       * currentMap.removeAllLayers(); currentMap.addLayer(newLayer);
       * currentMap.addTileset(tmh.getSet()); } else {
       */

      while (currentMap.getTotalLayers() > 1)
      {
        try
        {
          currentMap.mergeLayerDown(currentMap.getTotalLayers() - 1);
        } catch (Exception ex)
        {
        }
      }
      // }
      setCurrentLayer(0);
    }

    undoSupport.postEdit(new MapLayerStateEdit(currentMap, layersBefore,
        currentMap.getLayerList(), command));
  }

  private void doMouse(MouseEvent event)
  {
    // if (currentMap == null || currentLayer < 0) {
    // return;
    // }
    //
    // Point tile = mapView.screenToTileCoords(event.getX(), event.getY());
    // MapLayer layer = getCurrentLayer();
    //
    // if (layer == null) {
    // return;
    // } else if (mouseButton == MouseEvent.BUTTON3) {
    // if (layer instanceof TileLayer) {
    // Tile newTile = ((TileLayer)layer).getTileAt(tile.x, tile.y);
    // setCurrentTile(newTile);
    // } else if (layer instanceof ObjectGroup) {
    // // TODO: Add support for ObjectGroups here
    // }
    // } else if (mouseButton == MouseEvent.BUTTON1) {
    // switch (currentPointerState) {
    // case PS_PAINT:
    // paintEdit.setPresentationName("Paint");
    // if (layer instanceof TileLayer) {
    // Rectangle affectedRegion = currentBrush.commitPaint(
    // currentMap, tile.x, tile.y, currentLayer);
    // mapView.repaintRegion(affectedRegion);
    // }
    // break;
    // case PS_ERASE:
    // paintEdit.setPresentationName("Erase");
    // if (layer instanceof TileLayer) {
    // ((TileLayer)layer).setTileAt(tile.x, tile.y,
    // currentMap.getNullTile());
    // }
    // mapView.repaintRegion(new Rectangle(tile.x, tile.y, 1, 1));
    // break;
    // case PS_POUR: // POUR only works on TileLayers
    // paintEdit = null;
    // if (layer instanceof TileLayer) {
    // Tile oldTile = ((TileLayer)layer).getTileAt(tile.x, tile.y);
    // pour((TileLayer) layer, tile.x, tile.y, currentTiles, oldTile);
    // mapView.repaint();
    // }
    // break;
    // case PS_EYED:
    // if (layer instanceof TileLayer) {
    // Tile newTile = ((TileLayer)layer).getTileAt(
    // tile.x, tile.y);
    // setCurrentTile(newTile);
    // } else if (layer instanceof ObjectGroup) {
    // // TODO: Add support for ObjectGroups here
    // }
    // break;
    // case PS_MOVE:
    // Point translation = new Point(
    // tile.x - mousePressLocation.x,
    // tile.y - mousePressLocation.y);
    //
    // layer.translate(translation.x, translation.y);
    // moveDist.translate(translation.x, translation.y);
    // mapView.repaint();
    // break;
    // case PS_MARQUEE:
    // if (marqueeSelection != null) {
    // Point limp = mouseInitialPressLocation;
    // Rectangle oldArea =
    // marqueeSelection.getSelectedAreaBounds();
    // int minx = Math.min(limp.x, tile.x);
    // int miny = Math.min(limp.y, tile.y);
    //
    // if(event.isShiftDown()) {
    // marqueeSelection.add(new Area(new Rectangle(minx, miny,
    // (Math.max(limp.x, tile.x) - minx)+1,
    // (Math.max(limp.y, tile.y) - miny)+1)));
    // } else if(event.isControlDown()) {
    // marqueeSelection.subtract(new Area(new Rectangle(minx, miny,
    // (Math.max(limp.x, tile.x) - minx)+1,
    // (Math.max(limp.y, tile.y) - miny)+1)));
    // } else {
    // marqueeSelection.selectRegion(new Rectangle(minx, miny,
    // (Math.max(limp.x, tile.x) - minx)+1,
    // (Math.max(limp.y, tile.y) - miny)+1));
    //
    // }
    // if (oldArea != null) {
    // oldArea.add(
    // marqueeSelection.getSelectedAreaBounds());
    // mapView.repaintRegion(oldArea);
    // }
    // }
    // break;
    // }
    // }
  }

  public void mouseExited(MouseEvent e)
  {
    tileCoordsLabel.setText(" ");
  }

  public void mouseClicked(MouseEvent e)
  {
  }

  public void mouseEntered(MouseEvent e)
  {
  }

  public void mousePressed(MouseEvent e)
  {
    // mouseButton = e.getButton();
    // bMouseIsDown = true;
    // mousePressLocation = mapView.screenToTileCoords(e.getX(), e.getY());
    // mouseInitialPressLocation = mousePressLocation;
    //
    // if (mouseButton == MouseEvent.BUTTON1) {
    // switch (currentPointerState) {
    // case PS_PAINT:
    // case PS_ERASE:
    // case PS_POUR:
    // MapLayer layer = getCurrentLayer();
    // paintEdit =
    // new MapLayerEdit(layer, createLayerCopy(layer), null);
    // break;
    // default:
    // }
    // }
    //
    // if (currentPointerState == PS_MARQUEE) {
    // if (marqueeSelection == null) {
    // marqueeSelection = new SelectionLayer(
    // currentMap.getWidth(), currentMap.getHeight());
    // currentMap.addLayerSpecial(marqueeSelection);
    // }
    // } else if (currentPointerState == PS_MOVE) {
    // // Initialize move distance to (0, 0)
    // moveDist = new Point(0, 0);
    // }
    //
    // doMouse(e);
  }

  public void mouseReleased(MouseEvent event)
  {
    // mouseButton = MouseEvent.NOBUTTON;
    // bMouseIsDown = false;
    // MapLayer layer = getCurrentLayer();
    // Point limp = mouseInitialPressLocation;
    //
    // if (currentPointerState == PS_MARQUEE) {
    // Point tile = mapView.screenToTileCoords(event.getX(), event.getY());
    // if (tile.y - limp.y == 0 && tile.x - limp.x == 0) {
    // if (marqueeSelection != null) {
    // currentMap.removeLayerSpecial(marqueeSelection);
    // marqueeSelection = null;
    // }
    // }
    // } else if (currentPointerState == PS_MOVE) {
    // if (layer != null && moveDist.x != 0 || moveDist.x != 0) {
    // undoSupport.postEdit(new MoveLayerEdit(layer, moveDist));
    // }
    // }
    //
    // if (paintEdit != null) {
    // if (layer != null) {
    // try {
    // MapLayer endLayer = paintEdit.getStart().createDiff(layer);
    // paintEdit.end(endLayer);
    // undoSupport.postEdit(paintEdit);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // paintEdit = null;
    // }
  }

  public void mouseMoved(MouseEvent e)
  {
    // if (bMouseIsDown) {
    // doMouse(e);
    // }
    //
    // Point tile = mapView.screenToTileCoords(e.getX(), e.getY());
    // if (currentMap.inBounds(tile.x, tile.y)) {
    // tileCoordsLabel.setText("" + tile.x + ", " + tile.y);
    // } else {
    // tileCoordsLabel.setText(" ");
    // }
    //
    // updateCursorHighlight(tile);
  }

  public void mouseDragged(MouseEvent e)
  {
    // doMouse(e);
    // mousePressLocation = mapView.screenToTileCoords(e.getX(), e.getY());
    // Point tile = mapView.screenToTileCoords(e.getX(), e.getY());
    // if (currentMap.inBounds(tile.x, tile.y)) {
    // tileCoordsLabel.setText("" + tile.x + ", " + tile.y);
    // } else {
    // tileCoordsLabel.setText(" ");
    // }
    //
    // updateCursorHighlight(tile);
  }


  public void actionPerformed(ActionEvent event)
  {
    handleEvent(event);
  }

  private void handleEvent(ActionEvent event)
  {
    String command = event.getActionCommand();

    if (command.equals("Show Boundaries")
        || command.equals("Hide Boundaries"))
    {
      // mapView.toggleMode(MapView.PF_BOUNDARYMODE);
    } else if (command.equals("Show Grid"))
    {
      // Toggle grid
      // mapView.toggleMode(MapView.PF_GRIDMODE);
    } else if (command.equals("Show Coordinates"))
    {
      // Toggle coordinates
      // mapView.toggleMode(MapView.PF_COORDINATES);
      // mapView.repaint();
    } else if (command.equals("Resize"))
    {
      ResizeDialog rd = new ResizeDialog(appFrame, this);
      rd.setVisible(true);
    } else if (command.equals("Search"))
    {
      SearchDialog sd = new SearchDialog(appFrame, currentMap);
      sd.setVisible(true);
    } else if (command.equals("About Tiled"))
    {
      if (aboutDialog == null)
      {
        aboutDialog = new AboutDialog(appFrame);
      }
      aboutDialog.setVisible(true);
    } else if (command.equals("About Plug-ins"))
    {
      PluginDialog pluginDialog = new PluginDialog(appFrame, pluginLoader);
      pluginDialog.setVisible(true);
    } else if (command.startsWith("_open"))
    {
      try
      {
        loadMap(configuration.getValue("tiled.recent." + command.substring(5)));
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    } else if (command.equals("Preferences..."))
    {
      ConfigurationDialog d = new ConfigurationDialog(appFrame);
      d.configure();
    } else
    {
      System.out.println(event);
    }
  }

  public void componentHidden(ComponentEvent event)
  {
  }

  public void componentMoved(ComponentEvent event)
  {
  }

  public void componentResized(ComponentEvent event)
  {
    // This can currently only happen when the map changes size
    // zoomLabel.setText("" + (int)(mapView.getZoom() * 100) + "%");
  }

  public void componentShown(ComponentEvent event)
  {
  }

  public void mapChanged(MapChangedEvent e)
  {
    if (e.getMap() == currentMap)
    {
      // mapScrollPane.setViewportView(mapView);
      updateLayerTable();
      // mapView.repaint();
    }
  }

  private class UndoAdapter implements UndoableEditListener
  {
    public void undoableEditHappened(UndoableEditEvent evt)
    {
      undoStack.addEdit(evt.getEdit());
      updateHistory();
    }
  }

  private void pour(TileLayer layer, int x, int y, List<Tile> tiles, Tile oldTile)
  {

    Tile newTile = (tiles != null && tiles.size() > 0) ? tiles.get(0) : null;

    if (newTile == oldTile)
      return;

    Rectangle area = null;
    TileLayer before = new TileLayer(layer);
    TileLayer after;

    if (marqueeSelection == null)
    {
      area = new Rectangle(new Point(x, y));
      Stack<Point> stack = new Stack<Point>();

      stack.push(new Point(x, y));
      while (!stack.empty())
      {
        // Remove the next tile from the stack
        Point p = (Point) stack.pop();

        // If the tile it meets the requirements, set it and push its
        // neighbouring tiles on the stack.
        if (currentMap.inBounds(p.x, p.y)
            && layer.getTileAt(p.x, p.y) == oldTile)
        {
          layer.setTileAt(p.x, p.y, newTile);
          area.add(p);

          stack.push(new Point(p.x, p.y - 1));
          stack.push(new Point(p.x, p.y + 1));
          stack.push(new Point(p.x + 1, p.y));
          stack.push(new Point(p.x - 1, p.y));
        }
      }
    } else
    {
      if (marqueeSelection.getSelectedArea().contains(x, y))
      {
        area = marqueeSelection.getSelectedAreaBounds();
        for (int i = area.y; i < area.height + area.y; i++)
        {
          for (int j = area.x; j < area.width + area.x; j++)
          {
            if (marqueeSelection.getSelectedArea().contains(j, i))
            {
              layer.setTileAt(j, i, newTile);
            }
          }
        }
      } else
      {
        return;
      }
    }

    Rectangle bounds = new Rectangle(area.x, area.y, area.width + 1,
        area.height + 1);
    after = new TileLayer(bounds);
    after.copyFrom(layer);

    MapLayerEdit mle = new MapLayerEdit(layer, before, after);
    mle.setPresentationName("Fill");
    undoSupport.postEdit(mle);
  }

  public void setBrush(Brush b)
  {
    currentBrush = b;
  }

  /** updates the title to match the currently loaded map name */
  private void updateTitle()
  {
    String title = "Tiled";

    if (currentMap != null)
    {
      String filename = currentMap.getFilename();
      title += " - ";
      if (filename != null)
      {
        title += currentMap.getFilename();
      } else
      {
        title += "Untitled";
      }
      if (unsavedChanges())
      {
        title += "*";
      }
    }

    appFrame.setTitle(title);
  }

  /** checks if there are changes in the map and asks the user to save them */
  public boolean checkSave()
  {
    if (unsavedChanges())
    {
      int ret = JOptionPane.showConfirmDialog(appFrame,
          "There are unsaved changes for the current map. " + "Save changes?",
          "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);

      if (ret == JOptionPane.YES_OPTION)
      {
        saveMap(true);
      } else if (ret == JOptionPane.CANCEL_OPTION)
      {
        return false;
      }
    }
    return true;
  }

  /** returns true when there are unsaved changes */
  private boolean unsavedChanges()
  {
    return (currentMap != null && undoStack.canUndo() && !undoStack.isAllSaved());
  }

  /**
   * Loads a map.
   * 
   * @param file
   *          filename of map to load
   * @return <code>true</code> if the file was loaded, <code>false</code> if
   *         an error occured
   */
  public boolean loadMap(String file)
  {
    try
    {
      Map m = MapHelper.loadMap(file);

      if (m != null)
      {
        setCurrentMap(m);
        updateRecent(file);
        // This is to try and clean up any previously loaded stuffs
        return true;
      } else
      {
        JOptionPane.showMessageDialog(appFrame, "Unsupported map format",
            "Error while loading map", JOptionPane.ERROR_MESSAGE);
      }
    } catch (Exception e)
    {
      JOptionPane.showMessageDialog(appFrame, "Error while loading "
          + file
          + ": "
          + e.getMessage()
          + (e.getCause() != null ? "\nCause: " + e.getCause().getMessage()
              : ""), "Error while loading map", JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }

    return false;
  }

  /**
   * Saves the current map, optionally with a "Save As" dialog. If
   * <code>filename</code> is <code>null</code> or <code>bSaveAs</code> is
   * passed <code>true</code>, a "Save As" dialog is opened.
   * 
   * @see MapHelper#saveMap(Map, String)
   * @param filename
   *          Filename to save the current map to.
   * @param bSaveAs
   *          Pass <code>true</code> to ask for a new filename using a "Save
   *          As" dialog.
   */
  public void saveMap(boolean bSaveAs)
  {
    if (currentMap == null)
    {
      return;
    }

    String filename = currentMap.getFilename(); 

    if (bSaveAs || filename == null)
    {
      JFileChooser ch;

      if (filename == null)
      {
        ch = new JFileChooser();
      } else
      {
        ch = new JFileChooser(filename);
      }

      MapWriter writers[] = (MapWriter[]) pluginLoader.getWriters();
      for (int i = 0; i < writers.length; i++)
      {
        try
        {
          ch.addChoosableFileFilter(new TiledFileFilter(writers[i].getFilter(),writers[i].getName()));
        } catch (Exception e)
        {
          e.printStackTrace();
        }
      }

      ch.addChoosableFileFilter(new TiledFileFilter(TiledFileFilter.FILTER_TMX));

      if (ch.showSaveDialog(appFrame) == JFileChooser.APPROVE_OPTION)
      {
        filename = ch.getSelectedFile().getAbsolutePath();
      } else
      {
        // User cancelled operation, do nothing
        return;
      }
    }

    try
    {
      // Check if file exists
      File exist = new File(filename);
      if (exist.exists() && bSaveAs)
      {
        int result = JOptionPane
            .showConfirmDialog(appFrame,
                "The file already exists. Are you sure you want to "
                    + "overwrite it?", "Overwrite file?",
                JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.OK_OPTION)
        {
          return;
        }
      }

      MapHelper.saveMap(currentMap, filename);
      currentMap.setFilename(filename);
      updateRecent(filename);
      undoStack.commitSave();
      updateTitle();
    } catch (Exception e)
    {
      e.printStackTrace();
      JOptionPane.showMessageDialog(appFrame, "Error while saving " + filename
          + ": " + e.toString(), "Error while saving map",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Attempts to draw the entire map to an image file of the format of the
   * extension.
   */
  public void saveMapImage()
  {
    // JFileChooser ch = new JFileChooser();
    // ch.setDialogTitle("Save as image");
    //
    // if (ch.showSaveDialog(appFrame) == JFileChooser.APPROVE_OPTION) {
    // filename = ch.getSelectedFile().getAbsolutePath();
    // }
    //
    // if (filename != null) {
    // MapView myView = MapView.createViewforMap(currentMap);
    // if (mapView.getMode(MapView.PF_GRIDMODE))
    // myView.enableMode(MapView.PF_GRIDMODE);
    // myView.enableMode(MapView.PF_NOSPECIAL);
    // myView.setZoom(mapView.getZoom());
    // Dimension d = myView.getPreferredSize();
    // BufferedImage i = new BufferedImage(d.width, d.height,
    // BufferedImage.TYPE_INT_ARGB);
    // Graphics2D g = i.createGraphics();
    // g.setClip(0, 0, d.width, d.height);
    // myView.paint(g);
    //
    // String format = filename.substring(filename.lastIndexOf('.') + 1);
    //
    // try {
    // ImageIO.write(i, format, new File(filename));
    // } catch (IOException e) {
    // e.printStackTrace();
    // JOptionPane.showMessageDialog(appFrame,
    // "Error while saving " + filename + ": " + e.toString(),
    // "Error while saving map image",
    // JOptionPane.ERROR_MESSAGE);
    // }
    // }
  }

  public void openMap()
  {
    if (!checkSave())
    {
      return;
    }
    
    String startLocation = "";

    // Start at the location of the most recently loaded map file
    if (configuration.hasOption("tiled.recent.1"))
    {
      startLocation = configuration.getValue("tiled.recent.1");
    }

    JFileChooser ch = new JFileChooser(startLocation);

    try
    {
      MapReader readers[] = (MapReader[]) pluginLoader.getReaders();
      for (int i = 0; i < readers.length; i++)
      {
        ch.addChoosableFileFilter(new TiledFileFilter(readers[i].getFilter(),
            readers[i].getName()));
      }
    } catch (Throwable e)
    {
      JOptionPane.showMessageDialog(appFrame, "Error while loading plugins: "
          + e.getMessage(), "Error while loading map",
          JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }

    ch.addChoosableFileFilter(new TiledFileFilter(TiledFileFilter.FILTER_TMX));

    int ret = ch.showOpenDialog(appFrame);
    if (ret == JFileChooser.APPROVE_OPTION)
    {
      loadMap(ch.getSelectedFile().getAbsolutePath());
    }
  }
  
  /** closes the current map */
  public void closeMap()
  {
    if (checkSave())
    {
      setCurrentMap(null);
    }
  }

  /** creates a new map */
  public void newMap()
  {
    if (!checkSave())
    {
      return;
    }
    NewMapDialog nmd = new NewMapDialog(appFrame);
    Map newMap = nmd.create();
    if (newMap != null)
    {
      setCurrentMap(newMap);
    }
  }

  public MapLayer createLayerCopy(MapLayer layer)
  {
    if (layer instanceof TileLayer)
    {
      return new TileLayer((TileLayer) layer);
    } else if (layer instanceof ObjectGroup)
    {
      return new ObjectGroup((ObjectGroup) layer);
    }
    return null;
  }

  private void updateRecent(String mapFile)
  {
    List<String> recent = new ArrayList<String>();
    try
    {
      recent.add(configuration.getValue("tiled.recent.1"));
      recent.add(configuration.getValue("tiled.recent.2"));
      recent.add(configuration.getValue("tiled.recent.3"));
      recent.add(configuration.getValue("tiled.recent.4"));
    } catch (Exception e)
    {
    }

    // If a map file is given, add it to the recent list
    if (mapFile != null)
    {
      // Remove any existing entry that is the same
      for (int i = 0; i < recent.size(); i++)
      {
        String filename = recent.get(i);
        if (filename != null && filename.equals(mapFile))
        {
          recent.remove(i);
          i--;
        }
      }

      recent.add(0, mapFile);

      if (recent.size() > 4)
      {
        recent = recent.subList(0, 3);
      }
    }

    mainMenu.clearAllRecent();

    for (int i = 0; i < recent.size(); i++)
    {
      String file = (String) recent.get(i);
      if (file != null)
      {
        String name = file.substring(file.lastIndexOf(File.separatorChar) + 1);

        configuration.addConfigPair("tiled.recent." + (i + 1), file);
        mainMenu.addRecent(name, "_open" + (i + 1));
      }
    }
  }

  public void setCurrentMap(Map newMap)
  {
    currentMap = newMap;
    boolean mapLoaded = (currentMap != null);

    if (!mapLoaded)
    {
      mapEventAdapter.fireEvent(MapEventAdapter.ME_MAPINACTIVE);
      mapView = null;
      setCurrentPointerState(PS_POINT);
      tileCoordsLabel.setPreferredSize(null);
      tileCoordsLabel.setText(" ");
      zoomLabel.setText(" ");
      setCurrentTile(null);
      mapEditPanel.setMapView(null);
    } else
    {
      mapEventAdapter.fireEvent(MapEventAdapter.ME_MAPACTIVE);
      mapView = new Orthogonal();
      mapView.setMap(currentMap);
      mapEditPanel.setMapView(mapView);
      setCurrentPointerState(PS_PAINT);

      currentMap.addMapChangeListener(this);

      // mainMenu.setShowGrid(mapView.getMode(MapView.PF_GRIDMODE));
      // mainMenu.setShowCoordinates(mapView.getMode(MapView.PF_GRIDMODE));

      List<TileSet> tilesets = currentMap.getTilesets();
      if (tilesets.size() > 0)
      {
        TileSet first = (TileSet) tilesets.get(0);
        setCurrentTile(first.getFirstTile());
      } else
      {
        setCurrentTile(null);
      }

      tileCoordsLabel.setText("" + (currentMap.getWidth() - 1) + ", "
          + (currentMap.getHeight() - 1));
      tileCoordsLabel.setPreferredSize(null);
      Dimension size = tileCoordsLabel.getPreferredSize();
      tileCoordsLabel.setText(" ");
      tileCoordsLabel.setMinimumSize(size);
      tileCoordsLabel.setPreferredSize(size);
      //zoomLabel.setText("" + (int)(mapEditPanel.getZoom() * 100) + "%");
    }

    zoomInAction.setEnabled(mapLoaded);
    zoomOutAction.setEnabled(mapLoaded);
    // zoomNormalAction.setEnabled(mapLoaded && mapView.getZoomLevel() !=
    // MapView.ZOOM_NORMALSIZE);

    if (tilePaletteDialog != null)
    {
      tilePaletteDialog.setMap(currentMap);
    }

    if (currentMap != null)
    {
      currentMap.addLayerSpecial(cursorHighlight);
    }

    undoStack.discardAllEdits();
    updateLayerTable();
    updateTitle();
    updateHistory();
  }

  private void setCurrentLayer(int index)
  {
    layerEditPanel.setLayer(index, currentMap);
  }

  /**
   * Changes the currently selected tile.
   * 
   * @param tile
   *          the new tile to be selected
   */
  private void setCurrentTile(Tile tile)
  {
    ShapeBrush brush = new ShapeBrush();
    brush.makeQuadBrush(new Rectangle(0, 0, 1, 1));
    brush.setTile(tile);
    currentBrush = brush;
  }

  /**
   * Changes the currently selected tile.
   * 
   * @param tile
   *          the new tile to be selected
   */
  public void setCurrentTiles(TileSelectionEvent e)
  {
    List<Tile> tiles = e.getTiles();
    if (tiles != null)
    {
      currentTiles = tiles;
      if (tiles.size() == 1)
      {
        setCurrentTile(tiles.get(0));
      } else if (tiles.size() > 1)
      {
        currentBrush = e.getBrush();
      }
    }
  }

  private void setCurrentPointerState(int state)
  {
  }

  /**
   * Loads an image that is part of the distribution jar
   * 
   * @param fname
   * @return A BufferedImage instance of the image
   * @throws IOException
   */
  public static BufferedImage loadImageResource(String fname)
      throws IOException
  {
    return ImageIO.read(MapEditor.class.getResourceAsStream("/tiled/mapeditor/"
        + fname));
  }

  public static ImageIcon loadIcon(String fname)
  {
    try
    {
      return new ImageIcon(loadImageResource(fname));
    } catch (IOException e)
    {
      System.out.println("Failed to load icon: " + fname);
      return null;
    }
  }

  /**
   * Starts Tiled.
   * 
   * @param args
   *          the first argument may be a map file
   */
  public static void main(String[] args)
  {
    MapEditor editor = new MapEditor();

    if (args.length > 0)
    {
      String toLoad = args[0];
      if (!Util.checkRoot(toLoad) || toLoad.startsWith("."))
      {
        if (toLoad.startsWith("."))
        {
          toLoad = toLoad.substring(1);
        }
        toLoad = System.getProperty("user.dir") + File.separatorChar + toLoad;
      }
      editor.loadMap(toLoad);
    }
  }

}
