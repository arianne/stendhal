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
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.UndoableEditSupport;

import tiled.core.*;
import tiled.view.*;
import tiled.mapeditor.TilesetManager;
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
public class MapEditor implements ActionListener,
    MouseListener, MouseMotionListener, MapChangeListener,
    ComponentListener
{
    // Constants and the like
    public static final int PS_POINT   = 0;
    public static final int PS_PAINT   = 1;
    public static final int PS_ERASE   = 2;
    public static final int PS_POUR    = 3;
    public static final int PS_EYED    = 4;
    public static final int PS_MARQUEE = 5;
    public static final int PS_MOVE    = 6;
    public static final int PS_MOVEOBJ = 7;

    private Cursor curDefault = null;
    private Cursor curPaint   = null;
    private Cursor curErase   = null;
    private Cursor curPour    = null;
    private Cursor curEyed    = null;
    private Cursor curMarquee = null;

    /** current release version */
    public static final String version = "0.0.1";

    public Map currentMap;
    private MapView mapView;
    private UndoStack undoStack;
    private UndoableEditSupport undoSupport;
    private MapEventAdapter mapEventAdapter;
    private PluginClassLoader pluginLoader;
    public TiledConfiguration configuration;


    int currentPointerState;
//    Tile currentTile;
    private List<Tile> currentTiles;

    public int currentLayer = -1;
    boolean bMouseIsDown = false;
    SelectionLayer cursorHighlight;
    Point mousePressLocation, mouseInitialPressLocation;
    Point moveDist;
    int mouseButton;
    Brush currentBrush;
    SelectionLayer marqueeSelection = null;
    MapLayer clipboardLayer = null;

    // GUI components
    MainMenu         mainMenu;
    ToolBar          toolBar;
    LayerEditPanel   layerEditPanel;
    TilesetChooserTabbedPane tilePalettePanel;
    
    JPanel      mainPanel;
    
    JPanel      statusBar;
    public JScrollPane mapScrollPane;

    JFrame      appFrame;
    JLabel      zoomLabel, tileCoordsLabel;


    TilePaletteDialog tilePaletteDialog;
    AboutDialog aboutDialog;
    MapLayerEdit paintEdit;

    // Actions
    public Action zoomInAction;
    public Action zoomOutAction;
    public Action zoomNormalAction;
    public Action undoAction;
    public Action redoAction;
    public Action rot90Action;
    public Action rot180Action;
    public Action rot270Action;
    public Action flipHorAction;
    public Action flipVerAction;
    public Action copyAction;
    public Action cutAction;
    public Action pasteAction;
    public Action selectAllAction;
    public Action inverseAction;
    public Action cancelSelectionAction;

    public MapEditor() {
        // Get instance of configuration
        configuration = TiledConfiguration.getInstance();

        curEyed = new Cursor(Cursor.CROSSHAIR_CURSOR);
        curDefault = new Cursor(Cursor.DEFAULT_CURSOR);

        undoStack = new UndoStack();
        undoSupport = new UndoableEditSupport();
        undoSupport.addUndoableEditListener(new UndoAdapter());

        cursorHighlight = new SelectionLayer(1, 1);
        cursorHighlight.select(0,0);
        cursorHighlight.setVisible(configuration.keyHasValue("tiled.cursorhighlight", 1));

        mapEventAdapter = new MapEventAdapter();

        //Create a default brush
        ShapeBrush sb = new ShapeBrush();
        sb.makeQuadBrush(new Rectangle(0, 0, 1, 1));
        setBrush(sb);

        // Create the actions
        zoomInAction = new ZoomInAction();
        zoomOutAction = new ZoomOutAction();
        zoomNormalAction = new ZoomNormalAction();
        undoAction = new UndoAction();
        redoAction = new RedoAction();
        rot90Action = new LayerTransformAction(MapLayer.ROTATE_90);
        rot180Action = new LayerTransformAction(MapLayer.ROTATE_180);
        rot270Action = new LayerTransformAction(MapLayer.ROTATE_270);
        flipHorAction = new LayerTransformAction(MapLayer.MIRROR_HORIZONTAL);
        flipVerAction = new LayerTransformAction(MapLayer.MIRROR_VERTICAL);
        copyAction = new CopyAction();
        pasteAction = new PasteAction();
        cutAction = new CutAction();
        selectAllAction = new SelectAllAction();
        cancelSelectionAction = new CancelSelectionAction();
        inverseAction = new InverseSelectionAction();

        // Create our frame
        appFrame = new JFrame("Stendhal Mapeditor");
        appFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        appFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                exit();
            }
        });
        appFrame.setContentPane(createContentPane());
        // add the main menu
        mainMenu = new MainMenu(this,mapEventAdapter);
        appFrame.setJMenuBar(mainMenu);
        appFrame.setSize(600, 400);
        setCurrentMap(null);
        updateRecent(null);

        appFrame.setVisible(true);

        // Load plugins
        pluginLoader  = PluginClassLoader.getInstance();
        try {
            pluginLoader.readPlugins(null, appFrame);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(appFrame,
                    e.toString(), "Plugin loader",
                    JOptionPane.WARNING_MESSAGE);
        }
        MapHelper.init(pluginLoader);
    }

    private JPanel createContentPane() {
        mapScrollPane = new JScrollPane(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        createStatusBar();
        
        layerEditPanel = new LayerEditPanel(this,mapEventAdapter);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, mapScrollPane, layerEditPanel);
        mainSplit.setResizeWeight(1.0);
        
        tilePalettePanel = new TilesetChooserTabbedPane(this);
        JSplitPane baseSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, mainSplit, tilePalettePanel);
        baseSplit.setOneTouchExpandable(true);
        

        mainPanel = new JPanel(new BorderLayout());
        toolBar = new ToolBar(this,mapEventAdapter);
        mainPanel.add(toolBar,BorderLayout.NORTH);
        mainPanel.add(baseSplit);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void exit() {
        if (checkSave()) {
            try {
                configuration.write("tiled.conf");
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }

    private void createStatusBar() {
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

    private void updateLayerTable() {
        layerEditPanel.updateLayerTable(currentLayer,currentMap);
        updateLayerOperations();
        if (currentMap != null)
        {
          tilePalettePanel.setTilesets(currentMap.getTilesets());
        }
    }

    public void updateLayerOperations()
    {
      int nrLayers = 0;

      if (currentMap != null) {
          nrLayers = currentMap.getTotalLayers();
      }

      boolean validSelection = currentLayer >= 0;
      boolean notBottom = currentLayer > 0;
      boolean notTop = currentLayer < nrLayers - 1 && validSelection;

      mainMenu.updateLayerOperations(validSelection, notBottom, notTop,nrLayers > 1);
      layerEditPanel.updateLayerOperations(validSelection, notBottom, notTop);
    }

    /**
     * Returns the current map.
     * 
     * @return The currently selected map.
     */
    public Map getCurrentMap() {
        return currentMap;
    }

    /**
     * Returns the currently selected layer.
     * 
     * @return THe currently selected layer.
     */
    public MapLayer getCurrentLayer() {
        return currentMap.getLayer(currentLayer);
    }

    /**
     * Returns the main application frame.
     * 
     * @return The frame of the main application
     */
    public Frame getAppFrame() {
        return appFrame;
    }

    private void updateHistory() {
        //editHistoryList.setListData(undoStack.getEdits());
      mainMenu.setUndo(undoStack.canUndo(), undoStack.getUndoPresentationName());
      mainMenu.setRedo(undoStack.canRedo(), undoStack.getRedoPresentationName());
      updateTitle();
    }

    private void doLayerStateChange(ActionEvent event) {
        if (currentMap == null) {
            return;
        }

        String command = event.getActionCommand();
        List<MapLayer> layersBefore = currentMap.getLayerVector();

        if (command.equals("Add Layer")) {
            currentMap.addLayer();
            setCurrentLayer(currentMap.getTotalLayers() - 1);
        } else if (command.equals("Duplicate Layer")) {
            if (currentLayer >= 0) {
                try {
                    MapLayer clone =
                        (MapLayer)getCurrentLayer().clone();
                    clone.setName(clone.getName() + " copy");
                    currentMap.addLayer(clone);
                } catch (CloneNotSupportedException ex) {
                    ex.printStackTrace();
                }
                setCurrentLayer(currentMap.getTotalLayers() - 1);
            }
        } else if (command.equals("Move Layer Up")) {
            if (currentLayer >= 0) {
                try {
                    currentMap.swapLayerUp(currentLayer);
                    setCurrentLayer(currentLayer + 1);
                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }
        } else if (command.equals("Move Layer Down")) {
            if (currentLayer >= 0) {
                try {
                    currentMap.swapLayerDown(currentLayer);
                    setCurrentLayer(currentLayer - 1);
                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }
        } else if (command.equals("Delete Layer")) {
            if (currentLayer >= 0) {
                currentMap.removeLayer(currentLayer);
                setCurrentLayer(currentLayer < 0 ? 0 : currentLayer);
            }
        } else if (command.equals("Merge Down")) {
            if (currentLayer >= 0) {
                try {
                    currentMap.mergeLayerDown(currentLayer);
                    setCurrentLayer(currentLayer - 1);
                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }
        } else if (command.equals("Merge All")) {
            //TODO: put this back in for 0.5.2
            /*if( JOptionPane.showConfirmDialog(appFrame,
                    "Do you wish to merge tile images, and create a new tile set?",
                    "Merge Tiles?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ) {
                TileMergeHelper tmh = new TileMergeHelper(currentMap);
                int len = currentMap.getTotalLayers();
                //TODO: Add a dialog option: "Yes, visible only"
                TileLayer newLayer = tmh.merge(0, len, true);
                currentMap.removeAllLayers();
                currentMap.addLayer(newLayer);
                currentMap.addTileset(tmh.getSet());
            } else {*/
            
	            while (currentMap.getTotalLayers() > 1) {
	                try {
	                    currentMap.mergeLayerDown(
	                            currentMap.getTotalLayers() - 1);
	                } catch (Exception ex) {}
	            }
            //}
            setCurrentLayer(0);
        }

        undoSupport.postEdit(new MapLayerStateEdit(currentMap, layersBefore,
                    currentMap.getLayerVector(), command));
    }

    private void doMouse(MouseEvent event) {
        if (currentMap == null || currentLayer < 0) {
            return;
        }

        Point tile = mapView.screenToTileCoords(event.getX(), event.getY());
        MapLayer layer = getCurrentLayer();

        if (layer == null) {
            return;
        } else if (mouseButton == MouseEvent.BUTTON3) {
            if (layer instanceof TileLayer) {
                Tile newTile = ((TileLayer)layer).getTileAt(tile.x, tile.y);
                setCurrentTile(newTile);
            } else if (layer instanceof ObjectGroup) {
                // TODO: Add support for ObjectGroups here
            }
        } else if (mouseButton == MouseEvent.BUTTON1) {
            switch (currentPointerState) {
                case PS_PAINT:
                    paintEdit.setPresentationName("Paint");
                    if (layer instanceof TileLayer) {
                        Rectangle affectedRegion = currentBrush.commitPaint(
                                currentMap, tile.x, tile.y, currentLayer);
                        mapView.repaintRegion(affectedRegion);
                    }
                    break;
                case PS_ERASE:
                    paintEdit.setPresentationName("Erase");
                    if (layer instanceof TileLayer) {
                        ((TileLayer)layer).setTileAt(tile.x, tile.y,
                                                     currentMap.getNullTile());
                    }
                    mapView.repaintRegion(new Rectangle(tile.x, tile.y, 1, 1));
                    break;
                case PS_POUR:  // POUR only works on TileLayers
                    paintEdit = null;
                    if (layer instanceof TileLayer) {
                        Tile oldTile = ((TileLayer)layer).getTileAt(tile.x, tile.y);
                        pour((TileLayer) layer, tile.x, tile.y, currentTiles, oldTile);
                        mapView.repaint();
                    }
                    break;
                case PS_EYED:
                    if (layer instanceof TileLayer) {
                        Tile newTile = ((TileLayer)layer).getTileAt(
                                tile.x, tile.y);
                        setCurrentTile(newTile);
                    } else if (layer instanceof ObjectGroup) {
                        // TODO: Add support for ObjectGroups here
                    }
                    break;
                case PS_MOVE:
                    Point translation = new Point(
                            tile.x - mousePressLocation.x,
                            tile.y - mousePressLocation.y);

                    layer.translate(translation.x, translation.y);
                    moveDist.translate(translation.x, translation.y);
                    mapView.repaint();
                    break;
                case PS_MARQUEE:
                    if (marqueeSelection != null) {
                        Point limp = mouseInitialPressLocation;
                        Rectangle oldArea =
                            marqueeSelection.getSelectedAreaBounds();
                        int minx = Math.min(limp.x, tile.x);
                        int miny = Math.min(limp.y, tile.y);

                        if(event.isShiftDown()) {
                            marqueeSelection.add(new Area(new Rectangle(minx, miny,
                                    (Math.max(limp.x, tile.x) - minx)+1,
                                    (Math.max(limp.y, tile.y) - miny)+1)));
                        } else if(event.isControlDown()) {
                            marqueeSelection.subtract(new Area(new Rectangle(minx, miny,
                                    (Math.max(limp.x, tile.x) - minx)+1,
                                    (Math.max(limp.y, tile.y) - miny)+1)));
                        } else {
                            marqueeSelection.selectRegion(new Rectangle(minx, miny,
                                    (Math.max(limp.x, tile.x) - minx)+1,
                                    (Math.max(limp.y, tile.y) - miny)+1));

                        }
                        if (oldArea != null) {
                            oldArea.add(
                                    marqueeSelection.getSelectedAreaBounds());
                            mapView.repaintRegion(oldArea);
                        }
                    }
                    break;
            }
        }
    }

    public void mouseExited(MouseEvent e) {
        tileCoordsLabel.setText(" ");
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        mouseButton = e.getButton();
        bMouseIsDown = true;
        mousePressLocation = mapView.screenToTileCoords(e.getX(), e.getY());
        mouseInitialPressLocation = mousePressLocation;

        if (mouseButton == MouseEvent.BUTTON1) {
            switch (currentPointerState) {
                case PS_PAINT:
                case PS_ERASE:
                case PS_POUR:
                    MapLayer layer = getCurrentLayer();
                    paintEdit =
                        new MapLayerEdit(layer, createLayerCopy(layer), null);
                    break;
                default:
            }
        }

        if (currentPointerState == PS_MARQUEE) {
            if (marqueeSelection == null) {
                marqueeSelection = new SelectionLayer(
                        currentMap.getWidth(), currentMap.getHeight());
                currentMap.addLayerSpecial(marqueeSelection);
            }
        } else if (currentPointerState == PS_MOVE) {
            // Initialize move distance to (0, 0)
            moveDist = new Point(0, 0);
        }

        doMouse(e);
    }

    public void mouseReleased(MouseEvent event) {
        mouseButton = MouseEvent.NOBUTTON;
        bMouseIsDown = false;
        MapLayer layer = getCurrentLayer();
        Point limp = mouseInitialPressLocation;

       if (currentPointerState == PS_MARQUEE) {
           Point tile = mapView.screenToTileCoords(event.getX(), event.getY());
           if (tile.y - limp.y == 0 && tile.x - limp.x == 0) {
               if (marqueeSelection != null) {
                   currentMap.removeLayerSpecial(marqueeSelection);
                   marqueeSelection = null;
               }
           }
        } else if (currentPointerState == PS_MOVE) {
            if (layer != null && moveDist.x != 0 || moveDist.x != 0) {
                undoSupport.postEdit(new MoveLayerEdit(layer, moveDist));
            }
        }

        if (paintEdit != null) {
            if (layer != null) {
                try {
                    MapLayer endLayer = paintEdit.getStart().createDiff(layer);
                    paintEdit.end(endLayer);
                    undoSupport.postEdit(paintEdit);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            paintEdit = null;
        }
    }

    public void mouseMoved(MouseEvent e) {
        if (bMouseIsDown) {
            doMouse(e);
        }

        Point tile = mapView.screenToTileCoords(e.getX(), e.getY());
        if (currentMap.inBounds(tile.x, tile.y)) {
            tileCoordsLabel.setText("" + tile.x + ", " + tile.y);
        } else {
            tileCoordsLabel.setText(" ");
        }

        updateCursorHighlight(tile);
    }

    public void mouseDragged(MouseEvent e) {
        doMouse(e);
        mousePressLocation = mapView.screenToTileCoords(e.getX(), e.getY());
        Point tile = mapView.screenToTileCoords(e.getX(), e.getY());
        if (currentMap.inBounds(tile.x, tile.y)) {
            tileCoordsLabel.setText("" + tile.x + ", " + tile.y);
        } else {
            tileCoordsLabel.setText(" ");
        }

        updateCursorHighlight(tile);
    }

    private void updateCursorHighlight(Point tile) {
        if (configuration.keyHasValue("tiled.cursorhighlight", 1)) {
            Rectangle redraw = cursorHighlight.getBounds();

            if (redraw.x != tile.x || redraw.y != tile.y) {
                Rectangle r1 = new Rectangle(tile.x, tile.y, 1, 1);
                Rectangle r2 = new Rectangle(redraw.x, redraw.y, 1, 1);
                cursorHighlight.setOffset(tile.x, tile.y);
                mapView.repaintRegion(r1);
                mapView.repaintRegion(r2);
            }
        }
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        if (command.equals("paint")) {
            setCurrentPointerState(PS_PAINT);
        } else if (command.equals("erase")) {
            setCurrentPointerState(PS_ERASE);
        } else if (command.equals("point")) {
            setCurrentPointerState(PS_POINT);
        } else if (command.equals("pour")) {
            setCurrentPointerState(PS_POUR);
        } else if (command.equals("eyed")) {
            setCurrentPointerState(PS_EYED);
        } else if (command.equals("marquee")) {
            setCurrentPointerState(PS_MARQUEE);
        } else if (command.equals("move")) {
            setCurrentPointerState(PS_MOVE);
        } else if (command.equals("moveobject")) {
            setCurrentPointerState(PS_MOVEOBJ);
        } else if (command.equals("palette")) {
            if (currentMap != null) {
                if (tilePaletteDialog == null) {
                    tilePaletteDialog =
                        new TilePaletteDialog(this, currentMap);
                }
                tilePaletteDialog.setVisible(true);
            }
        } else {
            handleEvent(event);
        }
    }

    private void handleEvent(ActionEvent event) {
        String command = event.getActionCommand();

        if (command.equals("Open...")) {
            if (checkSave()) {
                openMap();
            }
        } else if (command.equals("Exit")) {
            exit();
        } else if (command.equals("Close")) {
            if (checkSave()) {
                setCurrentMap(null);
            }
        } else if (command.equals("New...")) {
            if (checkSave()) {
                newMap();
            }
        } else if (command.equals("Print...")) {
            try {
                MapPrinter mp = new MapPrinter();
                mp.print(mapView);
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        } else if (command.equals("Brush...")) {
                BrushDialog bd = new BrushDialog(this, appFrame, currentBrush);
                bd.setVisible(true);
        } else if (command.equals("Add Layer") ||
                command.equals("Duplicate Layer") ||
                command.equals("Delete Layer") ||
                command.equals("Move Layer Up") ||
                command.equals("Move Layer Down") ||
                command.equals("Merge Down") ||
                command.equals("Merge All")) {
            doLayerStateChange(event);
        } else if (command.equals("New Tileset...")) {
            if (currentMap != null) {
                NewTilesetDialog dialog =
                    new NewTilesetDialog(appFrame, currentMap);
                TileSet newSet = dialog.create();
                if (newSet != null) {
                    currentMap.addTileset(newSet);
                }
            }
        } else if (command.equals("Import Tileset...")) {
            if (currentMap != null) {
                JFileChooser ch = new JFileChooser(currentMap.getFilename());
                MapReader readers[] = (MapReader[]) pluginLoader.getReaders();
                for(int i = 0; i < readers.length; i++) {
                    try {
                        ch.addChoosableFileFilter(new TiledFileFilter(
                                    readers[i].getFilter(),
                                    readers[i].getName()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                ch.addChoosableFileFilter(
                        new TiledFileFilter(TiledFileFilter.FILTER_TSX));

                int ret = ch.showOpenDialog(appFrame);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    String filename = ch.getSelectedFile().getAbsolutePath();
                    try {
                        TileSet set = MapHelper.loadTileset(filename);
                        currentMap.addTileset(set);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (command.equals("Tileset Manager")) {
            if (currentMap != null) {
                TilesetManager manager = new TilesetManager(appFrame, currentMap);
                manager.setVisible(true);
            }
        } else if (command.equals("Save")) {
            if (currentMap != null) {
                saveMap(currentMap.getFilename(), false);
            }
        } else if (command.equals("Save as...")) {
            if (currentMap != null) {
                saveMap(currentMap.getFilename(), true);
            }
        } else if (command.equals("Save as Image...")) {
            if (currentMap != null) {
                saveMapImage(null);
            }
        } else if (command.equals("Properties")) {
            PropertiesDialog pd = new PropertiesDialog(appFrame,
                    currentMap.getProperties());
            pd.setTitle("Map Properties");
            pd.getProps();
        } else if (command.equals("Layer Properties")) {
            MapLayer layer = getCurrentLayer();
            PropertiesDialog lpd =
                new PropertiesDialog(appFrame, layer.getProperties());
            lpd.setTitle(layer.getName() + " Properties");
            lpd.getProps();
        } else if (command.equals("Show Boundaries") ||
                command.equals("Hide Boundaries")) {
            mapView.toggleMode(MapView.PF_BOUNDARYMODE);
        } else if (command.equals("Show Grid")) {
            // Toggle grid
            mapView.toggleMode(MapView.PF_GRIDMODE);
        } else if (command.equals("Show Coordinates")) {
            // Toggle coordinates
            mapView.toggleMode(MapView.PF_COORDINATES);
            mapView.repaint();
        } else if (command.equals("Highlight Cursor")) {
            configuration.addConfigPair("tiled.cursorhighlight",
                    Integer.toString(mainMenu.isHighlightCursorSelected() ? 1 : 0));
            cursorHighlight.setVisible(mainMenu.isHighlightCursorSelected());
        } else if (command.equals("Resize")) {
            ResizeDialog rd = new ResizeDialog(appFrame, this);
            rd.setVisible(true);
        }  else if (command.equals("Search")) {
            SearchDialog sd = new SearchDialog(appFrame, currentMap);
            sd.setVisible(true);
        } else if (command.equals("About Tiled")) {
            if (aboutDialog == null) {
                aboutDialog = new AboutDialog(appFrame);
            }
            aboutDialog.setVisible(true);
        } else if (command.equals("About Plug-ins")) {
            PluginDialog pluginDialog =
                new PluginDialog(appFrame, pluginLoader);
            pluginDialog.setVisible(true);
        } else if (command.startsWith("_open")) {
            try {
                loadMap(configuration.getValue(
                            "tiled.recent." + command.substring(5)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (command.equals("Preferences...")) {
            ConfigurationDialog d = new ConfigurationDialog(appFrame);
            d.configure();
        } else {
            System.out.println(event);
        }
    }

    public void componentHidden(ComponentEvent event) {
    }

    public void componentMoved(ComponentEvent event) {
    }

    public void componentResized(ComponentEvent event) {
        // This can currently only happen when the map changes size
        zoomLabel.setText("" + (int)(mapView.getZoom() * 100) + "%");
    }

    public void componentShown(ComponentEvent event) {
    }

    public void mapChanged(MapChangedEvent e) {
        if (e.getMap() == currentMap) {
            mapScrollPane.setViewportView(mapView);
            updateLayerTable();
            if (tilePaletteDialog != null) {
                tilePaletteDialog.setMap(currentMap);
            }
            mapView.repaint();
        }
    }

    private class UndoAction extends AbstractAction {
      private static final long serialVersionUID = -1129586889816507546L;

        public UndoAction() {
            super("Undo");
            putValue(SHORT_DESCRIPTION, "Undo one action");
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control Z"));
        }
        public void actionPerformed(ActionEvent evt) {
            undoStack.undo();
            updateHistory();
            mapView.repaint();
        }
    }

    private class RedoAction extends AbstractAction {
      private static final long serialVersionUID = 2467790103953607697L;

        public RedoAction() {
            super("Redo");
            putValue(SHORT_DESCRIPTION, "Redo one action");
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control Y"));
        }
        public void actionPerformed(ActionEvent evt) {
            undoStack.redo();
            updateHistory();
            mapView.repaint();
        }
    }

    private class LayerTransformAction extends AbstractAction {
      private static final long serialVersionUID = 1727912057792605353L;

        private int transform;
        public LayerTransformAction(int transform) {
            this.transform = transform;
            switch (transform) {
                case MapLayer.ROTATE_90:
                    putValue(NAME, "Rotate 90 degrees CW");
                    putValue(SHORT_DESCRIPTION,
                            "Rotate layer 90 degrees clockwise");
                    putValue(SMALL_ICON,
                            loadIcon("resources/gimp-rotate-90-16.png"));
                    break;
                case MapLayer.ROTATE_180:
                    putValue(NAME, "Rotate 180 degrees CW");
                    putValue(SHORT_DESCRIPTION,
                            "Rotate layer 180 degrees clockwise");
                    putValue(SMALL_ICON,
                            loadIcon("resources/gimp-rotate-180-16.png"));
                    break;
                case MapLayer.ROTATE_270:
                    putValue(NAME, "Rotate 90 degrees CCW");
                    putValue(SHORT_DESCRIPTION,
                            "Rotate layer 90 degrees counterclockwise");
                    putValue(SMALL_ICON,
                            loadIcon("resources/gimp-rotate-270-16.png"));
                    break;
                case MapLayer.MIRROR_VERTICAL:
                    putValue(NAME, "Flip vertically");
                    putValue(SHORT_DESCRIPTION, "Flip layer vertically");
                    putValue(SMALL_ICON,
                            loadIcon("resources/gimp-flip-vertical-16.png"));
                    break;
                case MapLayer.MIRROR_HORIZONTAL:
                    putValue(NAME, "Flip horizontally");
                    putValue(SHORT_DESCRIPTION, "Flip layer horizontally");
                    putValue(SMALL_ICON,
                            loadIcon("resources/gimp-flip-horizontal-16.png"));
                    break;
            }
        }
        public void actionPerformed(ActionEvent evt) {
            MapLayer currentLayer = getCurrentLayer();
            MapLayer layer = currentLayer;
            MapLayerEdit transEdit;
            transEdit = new MapLayerEdit(
                    currentLayer, createLayerCopy(currentLayer));

            if (marqueeSelection != null) {
                if (currentLayer instanceof TileLayer) {
                    layer = new TileLayer(
                            marqueeSelection.getSelectedAreaBounds());
                } else if (currentLayer instanceof ObjectGroup) {
                    layer = new ObjectGroup(
                            marqueeSelection.getSelectedAreaBounds());
                }
                layer.setMap(currentMap);
                layer.maskedCopyFrom(
                        currentLayer,
                        marqueeSelection.getSelectedArea());
            }

            switch (transform) {
                case MapLayer.ROTATE_90:
                case MapLayer.ROTATE_180:
                case MapLayer.ROTATE_270:
                    transEdit.setPresentationName("Rotate");
                    layer.rotate(transform);
                    //if(marqueeSelection != null) marqueeSelection.rotate(transform);
                    break;
                case MapLayer.MIRROR_VERTICAL:
                    transEdit.setPresentationName("Vertical Flip");
                    layer.mirror(MapLayer.MIRROR_VERTICAL);
                    //if(marqueeSelection != null) marqueeSelection.mirror(transform);
                    break;
                case MapLayer.MIRROR_HORIZONTAL:
                    transEdit.setPresentationName("Horizontal Flip");
                    layer.mirror(MapLayer.MIRROR_HORIZONTAL);
                    //if(marqueeSelection != null) marqueeSelection.mirror(transform);
                    break;
            }

            if (marqueeSelection != null ) {
                layer.mergeOnto(currentLayer);
            }

            transEdit.end(createLayerCopy(currentLayer));
            undoSupport.postEdit(transEdit);
            mapView.repaint();
        }
    }

    private class CancelSelectionAction extends AbstractAction {
      private static final long serialVersionUID = -6217788914300686640L;

        public CancelSelectionAction() {
            super("None");
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control shift A"));
            putValue(SHORT_DESCRIPTION, "Cancel selection");
        }

        public void actionPerformed(ActionEvent e) {
            if (currentMap != null) {
                if (marqueeSelection != null) {
                    currentMap.removeLayerSpecial(marqueeSelection);
                }

                marqueeSelection = null;
            }
        }
    }

    private class SelectAllAction extends AbstractAction {
      private static final long serialVersionUID = -1980981542520629392L;

        public SelectAllAction() {
            super("All");
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control A"));
            putValue(SHORT_DESCRIPTION, "Select entire map");
        }

        public void actionPerformed(ActionEvent e) {
            if (currentMap != null) {
                if (marqueeSelection != null) {
                    currentMap.removeLayerSpecial(marqueeSelection);
                }
                marqueeSelection = new SelectionLayer(
                        currentMap.getWidth(), currentMap.getHeight());
                marqueeSelection.selectRegion(marqueeSelection.getBounds());
                currentMap.addLayerSpecial(marqueeSelection);
            }
        }
    }

    private class InverseSelectionAction extends AbstractAction {
      private static final long serialVersionUID = -3030827051213056224L;

        public InverseSelectionAction() {
            super("Invert");
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control I"));
            putValue(SHORT_DESCRIPTION, "Inverse of the current selection");
        }

        public void actionPerformed(ActionEvent e) {
            if (marqueeSelection != null) {
                marqueeSelection.invert();
                mapView.repaint();
            }
        }
    }

    private class ZoomInAction extends AbstractAction {
      private static final long serialVersionUID = -5253002744432344462L;

        public ZoomInAction() {
            super("Zoom In");
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control EQUALS"));
            putValue(SHORT_DESCRIPTION, "Zoom in one level");
            putValue(SMALL_ICON, loadIcon("resources/gnome-zoom-in.png"));
        }
        public void actionPerformed(ActionEvent evt) {
            if (currentMap != null) {
                zoomOutAction.setEnabled(true);
                if (!mapView.zoomIn()) {
                    setEnabled(false);
                }
                zoomNormalAction.setEnabled(mapView.getZoomLevel() !=
                        MapView.ZOOM_NORMALSIZE);
            }
        }
    }

    private class ZoomOutAction extends AbstractAction {
      private static final long serialVersionUID = 4963537857700059134L;

        public ZoomOutAction() {
            super("Zoom Out");
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control MINUS"));
            putValue(SHORT_DESCRIPTION, "Zoom out one level");
            putValue(SMALL_ICON, loadIcon("resources/gnome-zoom-out.png"));
        }
        public void actionPerformed(ActionEvent evt) {
            if (currentMap != null) {
                zoomInAction.setEnabled(true);
                if (!mapView.zoomOut()) {
                    setEnabled(false);
                }
                zoomNormalAction.setEnabled(mapView.getZoomLevel() !=
                        MapView.ZOOM_NORMALSIZE);
            }
        }
    }

    private class ZoomNormalAction extends AbstractAction {
      private static final long serialVersionUID = 4808296576226530709L;

        public ZoomNormalAction() {
            super("Zoom Normalsize");
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control 1"));
            putValue(SHORT_DESCRIPTION, "Zoom 100%");
        }
        public void actionPerformed(ActionEvent evt) {
            if (currentMap != null) {
                zoomInAction.setEnabled(true);
                zoomOutAction.setEnabled(true);
                setEnabled(false);
                mapView.setZoomLevel(MapView.ZOOM_NORMALSIZE);
            }
        }
    }

    private class CopyAction extends AbstractAction {
      private static final long serialVersionUID = -7093838522430390018L;

        public CopyAction() {
            super("Copy");
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control C"));
            putValue(SHORT_DESCRIPTION, "Copy");
        }
        public void actionPerformed(ActionEvent evt) {
            if (currentMap != null && marqueeSelection != null) {
                if (getCurrentLayer() instanceof TileLayer) {
                    clipboardLayer = new TileLayer(
                            marqueeSelection.getSelectedAreaBounds());
                } else if (getCurrentLayer() instanceof ObjectGroup) {
                    clipboardLayer = new ObjectGroup(
                            marqueeSelection.getSelectedAreaBounds());
                }
                clipboardLayer.maskedCopyFrom(
                        getCurrentLayer(),
                        marqueeSelection.getSelectedArea());
            }
        }
    }

    private class CutAction extends AbstractAction {
      private static final long serialVersionUID = -244183316986816427L;

        public CutAction() {
            super("Cut");
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control X"));
            putValue(SHORT_DESCRIPTION, "Cut");
        }
        public void actionPerformed(ActionEvent evt) {
            if (currentMap != null && marqueeSelection != null) {
                MapLayer ml = getCurrentLayer();

                if (getCurrentLayer() instanceof TileLayer) {
                    clipboardLayer = new TileLayer(
                            marqueeSelection.getSelectedAreaBounds());
                } else if (getCurrentLayer() instanceof ObjectGroup) {
                    clipboardLayer = new ObjectGroup(
                            marqueeSelection.getSelectedAreaBounds());
                }
                clipboardLayer.maskedCopyFrom(
                        ml, marqueeSelection.getSelectedArea());

                Rectangle area = marqueeSelection.getSelectedAreaBounds();
                Area mask = marqueeSelection.getSelectedArea();
                if (ml instanceof TileLayer) {
                    TileLayer tl = (TileLayer)ml;
                    for (int i = area.y; i < area.height+area.y; i++) {
                        for (int j = area.x; j < area.width + area.x; j++){
                            if (mask.contains(j,i)) {
                                tl.setTileAt(j, i, currentMap.getNullTile());
                            }
                        }
                    }
                }
                mapView.repaintRegion(area);
            }
        }
    }

    private class PasteAction extends AbstractAction {
      private static final long serialVersionUID = -9094834729794547379L;

        public PasteAction() {
            super("Paste");
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control V"));
            putValue(SHORT_DESCRIPTION, "Paste");
        }
        public void actionPerformed(ActionEvent evt) {
            if (currentMap != null && clipboardLayer != null) {
                List<MapLayer> layersBefore = currentMap.getLayerVector();
                MapLayer ml = createLayerCopy(clipboardLayer);
                ml.setName("Layer " + currentMap.getTotalLayers());
                currentMap.addLayer(ml);
                undoSupport.postEdit(
                        new MapLayerStateEdit(currentMap, layersBefore,
                            currentMap.getLayerVector(),
                            "Paste Selection"));
            }
        }
    }

    private class UndoAdapter implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent evt) {
            undoStack.addEdit(evt.getEdit());
            updateHistory();
        }
    }

    private void pour(TileLayer layer, int x, int y,
            List<Tile> tiles, Tile oldTile) {
      
      Tile newTile = (tiles != null && tiles.size() > 0) ? tiles.get(0) : null;
      
        if (newTile == oldTile) return;

        Rectangle area = null;
        TileLayer before = new TileLayer(layer);
        TileLayer after;

        if (marqueeSelection == null) {
            area = new Rectangle(new Point(x, y));
            Stack<Point> stack = new Stack<Point>();

            stack.push(new Point(x, y));
            while (!stack.empty()) {
                // Remove the next tile from the stack
                Point p = (Point)stack.pop();

                // If the tile it meets the requirements, set it and push its
                // neighbouring tiles on the stack.
                if (currentMap.inBounds(p.x, p.y) &&
                        layer.getTileAt(p.x, p.y) == oldTile)
                {
                    layer.setTileAt(p.x, p.y, newTile);
                    area.add(p);

                    stack.push(new Point(p.x, p.y - 1));
                    stack.push(new Point(p.x, p.y + 1));
                    stack.push(new Point(p.x + 1, p.y));
                    stack.push(new Point(p.x - 1, p.y));
                }
            }
        } else {
            if (marqueeSelection.getSelectedArea().contains(x, y)) {
                area = marqueeSelection.getSelectedAreaBounds();
                for (int i = area.y; i < area.height+area.y; i++) {
                    for (int j = area.x;j<area.width+area.x;j++){
                        if (marqueeSelection.getSelectedArea().contains(j, i)){
                            layer.setTileAt(j, i, newTile);
                        }
                    }
                }
            } else {
                return;
            }
        }

        Rectangle bounds = new Rectangle(
                area.x, area.y, area.width + 1, area.height + 1);
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

    private void updateTitle() {
        String title = "Tiled";

        if (currentMap != null) {
            String filename = currentMap.getFilename();
            title += " - ";
            if (filename != null) {
                title += currentMap.getFilename();
            } else {
                title += "Untitled";
            }
            if (unsavedChanges()) {
                title += "*";
            }
        }

        appFrame.setTitle(title);
    }

    private boolean checkSave() {
        if (unsavedChanges()) {
            int ret = JOptionPane.showConfirmDialog(appFrame,
                    "There are unsaved changes for the current map. " +
                    "Save changes?",
                    "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);

            if (ret == JOptionPane.YES_OPTION) {
                saveMap(currentMap.getFilename(), true);
            } else if (ret == JOptionPane.CANCEL_OPTION){
                return false;
            }
        }
        return true;
    }

    private boolean unsavedChanges() {
        return (currentMap != null && undoStack.canUndo() &&
                !undoStack.isAllSaved());
    }

    /**
     * Loads a map.
     *
     * @param file filename of map to load
     * @return <code>true</code> if the file was loaded, <code>false</code> if
     *         an error occured
     */
    public boolean loadMap(String file) {
        try {
            Map m = MapHelper.loadMap(file);

            if (m != null) {
                setCurrentMap(m);
                updateRecent(file);
                //This is to try and clean up any previously loaded stuffs
                return true;
            } else {
                JOptionPane.showMessageDialog(appFrame,
                        "Unsupported map format", "Error while loading map",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(appFrame,
                    "Error while loading " + file + ": " +
                    e.getMessage() + (e.getCause() != null ? "\nCause: " +
                        e.getCause().getMessage() : ""),
                    "Error while loading map",
                    JOptionPane.ERROR_MESSAGE);
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
     * @param filename Filename to save the current map to.
     * @param bSaveAs  Pass <code>true</code> to ask for a new filename using
     *                 a "Save As" dialog.
     */
    public void saveMap(String filename, boolean bSaveAs) {
        if (bSaveAs || filename == null) {
            JFileChooser ch;

            if (filename == null) {
                ch = new JFileChooser();
            } else {
                ch = new JFileChooser(filename);
            }

            MapWriter writers[] = (MapWriter[]) pluginLoader.getWriters();
            for(int i = 0; i < writers.length; i++) {
                try {
                    ch.addChoosableFileFilter(new TiledFileFilter(
                                writers[i].getFilter(), writers[i].getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ch.addChoosableFileFilter(
                    new TiledFileFilter(TiledFileFilter.FILTER_TMX));

            if (ch.showSaveDialog(appFrame) == JFileChooser.APPROVE_OPTION) {
                filename = ch.getSelectedFile().getAbsolutePath();
            } else {
                // User cancelled operation, do nothing
                return;
            }
        }

        try {
            // Check if file exists
            File exist = new File(filename);
            if (exist.exists() && bSaveAs) {
                int result = JOptionPane.showConfirmDialog(appFrame,
                        "The file already exists. Are you sure you want to " +
                        "overwrite it?", "Overwrite file?",
                        JOptionPane.YES_NO_OPTION);
                if (result != JOptionPane.OK_OPTION) {
                    return;
                }
            }

            MapHelper.saveMap(currentMap, filename);
            currentMap.setFilename(filename);
            updateRecent(filename);
            undoStack.commitSave();
            updateTitle();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(appFrame,
                    "Error while saving " + filename + ": " + e.toString(),
                    "Error while saving map",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Attempts to draw the entire map to an image file
     * of the format of the extension. (filename.ext)
     *
     * @param filename Image filename to save map render to.
     */
    public void saveMapImage(String filename) {
        if (filename == null) {
            JFileChooser ch = new JFileChooser();
            ch.setDialogTitle("Save as image");

            if (ch.showSaveDialog(appFrame) == JFileChooser.APPROVE_OPTION) {
                filename = ch.getSelectedFile().getAbsolutePath();
            }
        }

        if (filename != null) {
            MapView myView = MapView.createViewforMap(currentMap);
            if (mapView.getMode(MapView.PF_GRIDMODE))
                myView.enableMode(MapView.PF_GRIDMODE);
            myView.enableMode(MapView.PF_NOSPECIAL);
            myView.setZoom(mapView.getZoom());
            Dimension d = myView.getPreferredSize();
            BufferedImage i = new BufferedImage(d.width, d.height,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = i.createGraphics();
            g.setClip(0, 0, d.width, d.height);
            myView.paint(g);

            String format = filename.substring(filename.lastIndexOf('.') + 1);

            try {
                ImageIO.write(i, format, new File(filename));
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(appFrame,
                        "Error while saving " + filename + ": " + e.toString(),
                        "Error while saving map image",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openMap() {
        String startLocation = "";

        // Start at the location of the most recently loaded map file
        if (configuration.hasOption("tiled.recent.1")) {
            startLocation = configuration.getValue("tiled.recent.1");
        }

        JFileChooser ch = new JFileChooser(startLocation);

        try {
            MapReader readers[] = (MapReader[]) pluginLoader.getReaders();
            for(int i = 0; i < readers.length; i++) {
                ch.addChoosableFileFilter(new TiledFileFilter(
                            readers[i].getFilter(), readers[i].getName()));
            }
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(appFrame,
                    "Error while loading plugins: " + e.getMessage(),
                    "Error while loading map",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        ch.addChoosableFileFilter(
                new TiledFileFilter(TiledFileFilter.FILTER_TMX));

        int ret = ch.showOpenDialog(appFrame);
        if (ret == JFileChooser.APPROVE_OPTION) {
            loadMap(ch.getSelectedFile().getAbsolutePath());
        }
    }

    private void newMap() {
        NewMapDialog nmd = new NewMapDialog(appFrame);
        Map newMap = nmd.create();
        if (newMap != null) {
            setCurrentMap(newMap);
        }
    }

    private MapLayer createLayerCopy(MapLayer layer) {
        if (layer instanceof TileLayer) {
            return new TileLayer((TileLayer)layer);
        } else if (layer instanceof ObjectGroup) {
            return new ObjectGroup((ObjectGroup)layer);
        }
        return null;
    }

    private void updateRecent(String mapFile) {
        List<String> recent = new ArrayList<String>();
        try {
            recent.add(configuration.getValue("tiled.recent.1"));
            recent.add(configuration.getValue("tiled.recent.2"));
            recent.add(configuration.getValue("tiled.recent.3"));
            recent.add(configuration.getValue("tiled.recent.4"));
        } catch (Exception e) {
        }

        // If a map file is given, add it to the recent list
        if (mapFile != null) {
            // Remove any existing entry that is the same
            for (int i = 0; i < recent.size(); i++) {
                String filename = recent.get(i);
                if (filename!=null && filename.equals(mapFile)) {
                    recent.remove(i);
                    i--;
                }
            }

            recent.add(0, mapFile);

            if (recent.size() > 4) {
              recent = recent.subList(0,3);
            }
        }

        
        mainMenu.clearAllRecent();

        for (int i = 0; i < recent.size(); i++) {
            String file = (String)recent.get(i);
            if (file != null) {
                String name = file.substring(file.lastIndexOf(File.separatorChar) + 1);

                configuration.addConfigPair("tiled.recent." + (i + 1), file);
                mainMenu.addRecent(name,"_open" + (i + 1));
            }
        }
    }

    private void setCurrentMap(Map newMap) {
        currentMap = newMap;
        boolean mapLoaded = (currentMap != null);

        if (!mapLoaded) {
            mapEventAdapter.fireEvent(MapEventAdapter.ME_MAPINACTIVE);
            mapView = null;
            mapScrollPane.setViewportView(Box.createRigidArea(new Dimension(0,0)));
            setCurrentPointerState(PS_POINT);
            tileCoordsLabel.setPreferredSize(null);
            tileCoordsLabel.setText(" ");
            zoomLabel.setText(" ");
//            tilePalettePanel.setTilesets(null);
            setCurrentTile(null);
        } else {
            mapEventAdapter.fireEvent(MapEventAdapter.ME_MAPACTIVE);
            mapView = MapView.createViewforMap(currentMap);
            mapView.addMouseListener(this);
            mapView.addMouseMotionListener(this);
            mapView.addComponentListener(this);
            JViewport mapViewport = new JViewport();
            mapViewport.setView(mapView);
            mapScrollPane.setViewport(mapViewport);
            setCurrentPointerState(PS_PAINT);

            currentMap.addMapChangeListener(this);

            
            mainMenu.setShowGrid(mapView.getMode(MapView.PF_GRIDMODE));
            mainMenu.setShowCoordinates(mapView.getMode(MapView.PF_GRIDMODE));

            List<TileSet> tilesets = currentMap.getTilesets();
            if (tilesets.size() > 0) {
                TileSet first = (TileSet)tilesets.get(0);
                setCurrentTile(first.getFirstTile());
            } else {
                setCurrentTile(null);
            }

            tileCoordsLabel.setText("" + (currentMap.getWidth() - 1) + ", " +
                    (currentMap.getHeight() - 1));
            tileCoordsLabel.setPreferredSize(null);
            Dimension size = tileCoordsLabel.getPreferredSize();
            tileCoordsLabel.setText(" ");
            tileCoordsLabel.setMinimumSize(size);
            tileCoordsLabel.setPreferredSize(size);
            zoomLabel.setText("" + (int)(mapView.getZoom() * 100) + "%");
        }

        zoomInAction.setEnabled(mapLoaded);
        zoomOutAction.setEnabled(mapLoaded);
        zoomNormalAction.setEnabled(mapLoaded && mapView.getZoomLevel() != MapView.ZOOM_NORMALSIZE);

        if (tilePaletteDialog != null) {
            tilePaletteDialog.setMap(currentMap);
        }

        /*
        if (miniMap != null && currentMap != null) {
            miniMap.setView(MapView.createViewforMap(currentMap));
        }
        */

        if (currentMap != null) {
            currentMap.addLayerSpecial(cursorHighlight);
        }

        undoStack.discardAllEdits();
        updateLayerTable();
        updateTitle();
        updateHistory();
    }

    private void setCurrentLayer(int index) {
      layerEditPanel.setCurrentLayer(index,currentMap);
    }

    /**
     * Changes the currently selected tile.
     *
     * @param tile the new tile to be selected
     */
    private void setCurrentTile(Tile tile)
    {
      ShapeBrush brush = new ShapeBrush();
      brush.makeQuadBrush(new Rectangle(0,0,1,1));
      brush.setTile(tile);
      currentBrush = brush;
    }
    
    /**
     * Changes the currently selected tile.
     *
     * @param tile the new tile to be selected
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
        }
        else if (tiles.size() > 1)
        {
          currentBrush = e.getBrush();
        }
      }
    }
    

    private void setCurrentPointerState(int state) {
        /*if(currentPointerState == PS_MARQUEE && state != PS_MARQUEE) {
            // Special logic for selection
            if (marqueeSelection != null) {
                currentMap.removeLayerSpecial(marqueeSelection);
                marqueeSelection = null;
            }
        }*/

        currentPointerState = state;
        
        toolBar.setButtonStates(state);

        // Set the matching cursor
        if (mapView != null) {
            switch (currentPointerState) {
                case PS_PAINT:
                    mapView.setCursor(curPaint);
                    break;
                case PS_ERASE:
                    mapView.setCursor(curErase);
                    break;
                case PS_POINT:
                    mapView.setCursor(curDefault);
                    break;
                case PS_POUR:
                    mapView.setCursor(curPour);
                    break;
                case PS_EYED:
                    mapView.setCursor(curEyed);
                    break;
                case PS_MARQUEE:
                    mapView.setCursor(curMarquee);
                    break;
            }
        }
    }

    /**
     * Loads an image that is part of the distribution jar
     * 
     * @param fname
     * @return A BufferedImage instance of the image
     * @throws IOException
     */
    public static BufferedImage loadImageResource(String fname)
        throws IOException {
        return ImageIO.read(MapEditor.class.getResourceAsStream("/tiled/mapeditor/"+fname));
    }

    public static ImageIcon loadIcon(String fname) {
        try {
            return new ImageIcon(loadImageResource(fname));
        } catch (IOException e) {
            System.out.println("Failed to load icon: " + fname);
            return null;
        }
    }

    /**
     * Starts Tiled.
     *
     * @param args the first argument may be a map file
     */
    public static void main(String[] args) {
        //try {
        //    UIManager.setLookAndFeel(
        //            UIManager.getSystemLookAndFeelClassName());
        //} catch (Exception e) {}

        MapEditor editor = new MapEditor();

        if (args.length > 0) {
            String toLoad = args[0];
            if (!Util.checkRoot(toLoad) || toLoad.startsWith(".")) {
                if (toLoad.startsWith(".")) {
                    toLoad = toLoad.substring(1);
                }
                toLoad = System.getProperty("user.dir") +
                    File.separatorChar + toLoad;
            }
            editor.loadMap(toLoad);
        }
    }
}
