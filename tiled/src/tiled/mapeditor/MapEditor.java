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

package tiled.mapeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.StatefulTile;
import tiled.io.MapHelper;
import tiled.mapeditor.actions.ZoomInAction;
import tiled.mapeditor.actions.ZoomOutAction;
import tiled.mapeditor.brush.Brush;
import tiled.mapeditor.brush.ShapeBrush;
import tiled.mapeditor.builder.Builder;
import tiled.mapeditor.builder.SimpleBuilder;
import tiled.mapeditor.dialog.AboutDialog;
import tiled.mapeditor.dialog.ConfigurationDialog;
import tiled.mapeditor.dialog.NewMapDialog;
import tiled.mapeditor.dialog.PluginDialog;
import tiled.mapeditor.dialog.ResizeDialog;
import tiled.mapeditor.dialog.SearchDialog;
import tiled.mapeditor.plugin.PluginClassLoader;
import tiled.mapeditor.undo.UndoStack;
import tiled.mapeditor.util.ActionManager;
import tiled.mapeditor.util.MapChangeListener;
import tiled.mapeditor.util.MapChangedEvent;
import tiled.mapeditor.util.MapEventAdapter;
import tiled.mapeditor.widget.LayerEditPanel;
import tiled.mapeditor.widget.MainMenu;
import tiled.mapeditor.widget.MapEditPanel;
import tiled.mapeditor.widget.StatusBar;
import tiled.mapeditor.widget.TilesetChooser;
import tiled.mapeditor.widget.TilesetChooserTabbedPane;
import tiled.mapeditor.widget.TilesetChooserTree;
import tiled.mapeditor.widget.ToolBar;
import tiled.util.TiledConfiguration;
import tiled.util.Util;
import tiled.view.MapView;
import tiled.view.Orthogonal;

/**
 * The main class for the Tiled Map Editor.
 */
public class MapEditor implements ActionListener, MapChangeListener, ComponentListener {
	// Constants and the like
	public static final int PS_POINT = 0;
	public static final int PS_PAINT = 1;
	public static final int PS_ERASE = 2;
	public static final int PS_POUR = 3;
	public static final int PS_EYED = 4;
	public static final int PS_MARQUEE = 5;
	public static final int PS_MOVE = 6;
	public static final int PS_MOVEOBJ = 7;

	/** current release version. */
	public static final String VERSION = "0.0.2";
	public static final String TITLE = "Stendhal Mapeditor";

	public MapView mapView;
	public UndoStack undoStack;
	private MapEventAdapter mapEventAdapter;
	public PluginClassLoader pluginLoader;
	public TiledConfiguration configuration;

	private List<Point> selectedTiles;
	public Map currentMap;
	private Brush currentBrush;
	public Builder currentBuilder;
	public int currentLayer = -1;
	private List<StatefulTile> currentTiles;

	public MapLayer clipboardLayer = null;

	// GUI components
	public MainMenu mainMenu;
	public ToolBar toolBar;
	public LayerEditPanel layerEditPanel;
	public TilesetChooser tilePalettePanel;
	public MapEditPanel mapEditPanel;
	private JSplitPane baseSplit;

	private JPanel mainPanel;

	public StatusBar statusBar;
	public JScrollPane mapScrollPane;

	public JFrame appFrame;

	private AboutDialog aboutDialog;

	/** keeps track of all swing actions. */
	public ActionManager actionManager;

	public MapEditor() {
		// Get instance of configuration
		configuration = TiledConfiguration.getInstance();

		undoStack = new UndoStack(this);

		mapEventAdapter = new MapEventAdapter();

		// Create a default brush
		setBrush(ShapeBrush.makeRectBrush(1, 1));

		// Create the actions
		actionManager = new ActionManager(this);

		// Create our frame
		appFrame = new JFrame(TITLE);
		appFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		appFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				exit();
			}
		});
		appFrame.setContentPane(createContentPane());
		// add the main menu
		mainMenu = new MainMenu(this, mapEventAdapter);
		appFrame.setJMenuBar(mainMenu);
		appFrame.setSize(1024, 768);
		setCurrentMap(null);
		updateRecent(null);

		appFrame.setVisible(true);

		// Load plugins
		pluginLoader = PluginClassLoader.getInstance();
		try {
			pluginLoader.readPlugins(null, appFrame);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(appFrame, e.toString(), "Plugin loader", JOptionPane.WARNING_MESSAGE);
		}
		MapHelper.init(pluginLoader);
	}

	private JPanel createContentPane() {
		statusBar = new StatusBar();

		// minimap needs the mapScrollPane
		mapScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		layerEditPanel = new LayerEditPanel(this, mapEventAdapter);

		mapEditPanel = new MapEditPanel(this);
		mapEditPanel.setMinimapPanel(layerEditPanel.getMiniMap());
		mapScrollPane.setViewportView(mapEditPanel);

		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, mapScrollPane, layerEditPanel);
		mainSplit.setResizeWeight(1.0);

		tilePalettePanel = new TilesetChooserTree(this);
		baseSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, mainSplit, tilePalettePanel);
		baseSplit.setOneTouchExpandable(true);
		baseSplit.setResizeWeight(0.9);

		mainPanel = new JPanel(new BorderLayout());
		toolBar = new ToolBar(this, mapEventAdapter);
		mainPanel.add(toolBar, BorderLayout.NORTH);
		mainPanel.add(baseSplit);
		mainPanel.add(statusBar, BorderLayout.SOUTH);

		return mainPanel;
	}

	/**
	 * saves unsaved files (user option), saves the configuration and exits the
	 * application.
	 */
	public void exit() {
		if (checkSave()) {
			try {
				configuration.write("tiled.conf");
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
	}

	private void updateLayerTable() {
		layerEditPanel.setMap(currentLayer, currentMap);
		updateLayerOperations();
	}

	public void updateLayerOperations() {
		int nrLayers = 0;

		if (currentMap != null) {
			nrLayers = currentMap.getTotalLayers();
		}

		boolean validSelection = currentLayer >= 0;
		boolean notBottom = currentLayer > 0;
		boolean notTop = currentLayer < nrLayers - 1 && validSelection;

		mainMenu.updateLayerOperations(validSelection, notBottom, notTop, nrLayers > 1);
		layerEditPanel.updateLayerOperations(validSelection, notBottom, notTop);
	}

	/**
	 * Updates the builder to match the current selected map/layer/brush.
	 * Creates a new SimpleBuilder when there is none
	 */
	public void updateBuilder() {
		if (currentBuilder == null) {
			SimpleBuilder builder = new SimpleBuilder(currentMap, currentBrush, currentLayer);
			builder.setUndoManager(undoStack);
			currentBuilder = builder;
		} else {
			currentBuilder.setMap(currentMap);
			currentBuilder.setBrush(currentBrush);
			currentBuilder.setStartLayer(currentLayer);
		}
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

	public void updateHistory() {
		// editHistoryList.setListData(undoStack.getEdits());
		mainMenu.setUndo(undoStack.canUndo(), undoStack.getUndoPresentationName());
		mainMenu.setRedo(undoStack.canRedo(), undoStack.getRedoPresentationName());
		updateTitle();
	}

	public void doLayerStateChange(ActionEvent event) {
		if (currentMap == null) {
			return;
		}

		String command = event.getActionCommand();

		if (command.equals("Merge Down")) {
			if (currentLayer >= 0) {
				try {
					currentMap.mergeLayerDown(currentLayer);
					setCurrentLayer(currentLayer - 1);
				} catch (Exception ex) {
					System.out.println(ex.toString());
				}
			}
		} else if (command.equals("Merge All")) {
			// TODO: put this back in for 0.5.2
			/*
			 * if( JOptionPane.showConfirmDialog(appFrame, "Do you wish to merge
			 * tile images, and create a new tile set?", "Merge Tiles?",
			 * JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ) {
			 * TileMergeHelper tmh = new TileMergeHelper(currentMap); int len =
			 * currentMap.getTotalLayers(); //TODO: Add a dialog option: "Yes,
			 * visible only" TileLayer newLayer = tmh.merge(0, len, true);
			 * currentMap.removeAllLayers(); currentMap.addLayer(newLayer);
			 * currentMap.addTileset(tmh.getSet()); } else {
			 */

			while (currentMap.getTotalLayers() > 1) {
				try {
					currentMap.mergeLayerDown(currentMap.getTotalLayers() - 1);
				} catch (Exception ex) {
				}
			}
			// }
			setCurrentLayer(0);
		}

		// undoSupport.postEdit(new MapLayerStateEdit(currentMap, layersBefore,
		// currentMap.getLayerList(), command));
	}

	// private void doMouse(MouseEvent event)
	// {
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
	// }

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
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

	public void mouseReleased(MouseEvent event) {
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

	public void mouseMoved(MouseEvent e) {
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

	public void mouseDragged(MouseEvent e) {
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

	public void actionPerformed(ActionEvent event) {
		handleEvent(event);
	}

	private void handleEvent(ActionEvent event) {
		String command = event.getActionCommand();

		if (command.equals("Resize")) {
			ResizeDialog rd = new ResizeDialog(appFrame, this);
			rd.setVisible(true);
		} else if (command.equals("Search")) {
			SearchDialog sd = new SearchDialog(appFrame, currentMap);
			sd.setVisible(true);
		} else if (command.equals("About")) {
			if (aboutDialog == null) {
				aboutDialog = new AboutDialog(appFrame);
			}
			aboutDialog.setVisible(true);
		} else if (command.equals("About Plug-ins")) {
			PluginDialog pluginDialog = new PluginDialog(appFrame, pluginLoader);
			pluginDialog.setVisible(true);
		} else if (command.startsWith("_open")) {
			try {
				loadMap(configuration.getValue("tiled.recent." + command.substring(5)));
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
	}

	public void componentShown(ComponentEvent event) {
	}

	/** map properties/fields changed. */
	public void mapChanged(MapChangedEvent e) {
		if (e.getMap() == currentMap) {
			updateLayerTable();

			// update the tilesets when they change
			if (e.getType() == MapChangedEvent.Type.TILESETS) {
				tilePalettePanel.setTilesets(currentMap.getTilesets());
			}
		}
	}

	// private class UndoAdapter implements UndoableEditListener
	// {
	// public void undoableEditHappened(UndoableEditEvent evt)
	// {
	// undoStack.addEdit(evt.getEdit());
	// updateHistory();
	// }
	// }

	// private void pour(TileLayer layer, int x, int y, List<Tile> tiles, Tile
	// oldTile)
	// {
	//
	// Tile newTile = (tiles != null && tiles.size() > 0) ? tiles.get(0) : null;
	//
	// if (newTile == oldTile)
	// return;
	//
	// Rectangle area = null;
	// TileLayer before = new TileLayer(layer);
	// TileLayer after;
	//
	// if (marqueeSelection == null)
	// {
	// area = new Rectangle(new Point(x, y));
	// Stack<Point> stack = new Stack<Point>();
	//
	// stack.push(new Point(x, y));
	// while (!stack.empty())
	// {
	// // Remove the next tile from the stack
	// Point p = (Point) stack.pop();
	//
	// // If the tile it meets the requirements, set it and push its
	// // neighbouring tiles on the stack.
	// if (currentMap.inBounds(p.x, p.y)
	// && layer.getTileAt(p.x, p.y) == oldTile)
	// {
	// layer.setTileAt(p.x, p.y, newTile);
	// area.add(p);
	//
	// stack.push(new Point(p.x, p.y - 1));
	// stack.push(new Point(p.x, p.y + 1));
	// stack.push(new Point(p.x + 1, p.y));
	// stack.push(new Point(p.x - 1, p.y));
	// }
	// }
	// } else
	// {
	// if (marqueeSelection.getSelectedArea().contains(x, y))
	// {
	// area = marqueeSelection.getSelectedAreaBounds();
	// for (int i = area.y; i < area.height + area.y; i++)
	// {
	// for (int j = area.x; j < area.width + area.x; j++)
	// {
	// if (marqueeSelection.getSelectedArea().contains(j, i))
	// {
	// layer.setTileAt(j, i, newTile);
	// }
	// }
	// }
	// } else
	// {
	// return;
	// }
	// }
	//
	// Rectangle bounds = new Rectangle(area.x, area.y, area.width + 1,
	// area.height + 1);
	// after = new TileLayer(bounds);
	// after.copyFrom(layer);
	//
	// MapLayerEdit mle = new MapLayerEdit(layer, before, after);
	// mle.setPresentationName("Fill");
	// undoSupport.postEdit(mle);
	// }

	public void setBrush(Brush b) {
		if (b != null && currentBrush != null) {
			b.setTiles(currentBrush.getTiles());
		}
		currentBrush = b;
		if (currentBuilder != null) {
			currentBuilder.setBrush(currentBrush);
		}

		if (layerEditPanel != null) {
			layerEditPanel.updateBrush(currentBrush);
		}
	}

	/** updates the title to match the currently loaded map name. */
	private void updateTitle() {
		String title = TITLE;

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

	/** checks if there are changes in the map and asks the user to save them. */
	public boolean checkSave() {
		if (unsavedChanges()) {
			int ret = JOptionPane.showConfirmDialog(appFrame, "There are unsaved changes for the current map. "
					+ "Save changes?", "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);

			if (ret == JOptionPane.YES_OPTION) {
				saveMap(true);
			} else if (ret == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}
		return true;
	}

	/** returns true when there are unsaved changes. */
	private boolean unsavedChanges() {
		return (currentMap != null && undoStack.canUndo() && !undoStack.isAllSaved());
	}

	/** opens a file chooser dialog to open a new map. */
	public void openMap() {
		if (!checkSave()) {
			return;
		}

		Map newMap = MapHelper.loadMap(appFrame);
		if (newMap != null) {
			setCurrentMap(newMap);
			updateRecent(newMap.getFilename());
			return;
		}
	}

	/**
	 * Loads a map.
	 * 
	 * @param file
	 *            ocure filename of map to load
	 * @return <code>true</code> if the file was loaded, <code>false</code>
	 *         if an error occurred
	 */
	public boolean loadMap(String file) {
		StringBuffer errorBuffer = new StringBuffer();
		Map map = MapHelper.loadMap(appFrame, file, errorBuffer);

		if (map == null && errorBuffer.length() > 0) {
			JOptionPane.showMessageDialog(appFrame, errorBuffer.toString(), "Error while loading map",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (map != null) {
			setCurrentMap(map);
			updateRecent(file);
			// This is to try and clean up any previously loaded stuffs
			return true;
		}

		return false;
	}

	/**
	 * Saves the current map, optionally with a "Save As" dialog. If
	 * <code>filename</code> is <code>null</code> or <code>bSaveAs</code>
	 * is passed <code>true</code>, a "Save As" dialog is opened.
	 * 
	 * @see MapHelper#saveMap(Map, String)
	 * @param filename
	 *            Filename to save the current map to.
	 * @param bSaveAs
	 *            Pass <code>true</code> to ask for a new filename using a
	 *            "Save As" dialog.
	 */
	public void saveMap(boolean bSaveAs) {
		StringBuffer buf = new StringBuffer();
		MapHelper.saveMapNew(appFrame, currentMap, bSaveAs, buf);

		// if (currentMap == null)
		// {
		// return;
		// }
		//
		// String filename = currentMap.getFilename();
		//
		// if (bSaveAs || filename == null)
		// {
		// JFileChooser ch;
		//
		// if (filename == null)
		// {
		// ch = new JFileChooser();
		// } else
		// {
		// ch = new JFileChooser(filename);
		// }
		//
		// MapWriter writers[] = pluginLoader.getWriters();
		// for (int i = 0; i < writers.length; i++)
		// {
		// try
		// {
		// ch.addChoosableFileFilter(new
		// TiledFileFilter(writers[i].getFilter(),writers[i].getName()));
		// } catch (Exception e)
		// {
		// e.printStackTrace();
		// }
		// }
		//
		// ch.addChoosableFileFilter(new
		// TiledFileFilter(TiledFileFilter.FILTER_TMX));
		//
		// if (ch.showSaveDialog(appFrame) == JFileChooser.APPROVE_OPTION)
		// {
		// filename = ch.getSelectedFile().getAbsolutePath();
		// } else
		// {
		// // User cancelled operation, do nothing
		// return;
		// }
		// }
		//
		// try
		// {
		// // Check if file exists
		// File exist = new File(filename);
		// if (exist.exists() && bSaveAs)
		// {
		// int result = JOptionPane
		// .showConfirmDialog(appFrame,
		// "The file already exists. Are you sure you want to "
		// + "overwrite it?", "Overwrite file?",
		// JOptionPane.YES_NO_OPTION);
		// if (result != JOptionPane.OK_OPTION)
		// {
		// return;
		// }
		// }
		//
		// MapHelper.saveMap(currentMap, filename);
		// currentMap.setFilename(filename);
		// updateRecent(filename);
		// undoStack.commitSave();
		// updateTitle();
		// } catch (Exception e)
		// {
		// e.printStackTrace();
		// JOptionPane.showMessageDialog(appFrame, "Error while saving " +
		// filename
		// + ": " + e.toString(), "Error while saving map",
		// JOptionPane.ERROR_MESSAGE);
		// }
	}

	/**
	 * Attempts to draw the entire map to an image file of the format of the
	 * extension.
	 */
	public void saveMapImage() {
		try {
			JFileChooser ch = new JFileChooser();
			ch.setDialogTitle("Save as image");

			if (ch.showSaveDialog(appFrame) == JFileChooser.APPROVE_OPTION) {
				String filename = ch.getSelectedFile().getAbsolutePath();

				Dimension d = mapView.getSize();
				BufferedImage i = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = i.createGraphics();

				g.setClip(0, 0, d.width, d.height);
				mapView.draw(g);

				String format = filename.substring(filename.lastIndexOf('.') + 1);

				try {
					ImageIO.write(i, format, new File(filename));
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(appFrame, "Error while saving " + filename + ": " + e.toString(),
							"Error while saving map image", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (OutOfMemoryError e) {
			JOptionPane.showMessageDialog(appFrame, "Error while saving image: " + e, "Error while saving map image",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/** closes the current map. */
	public void closeMap() {
		if (checkSave()) {
			setCurrentMap(null);
		}
	}

	/** creates a new map. */
	public void newMap() {
		if (!checkSave()) {
			return;
		}
		NewMapDialog nmd = new NewMapDialog(appFrame);
		Map newMap = nmd.create();
		if (newMap != null) {
			setCurrentMap(newMap);
		}
	}

	// public MapLayer createLayerCopy(MapLayer layer)
	// {
	// if (layer instanceof TileLayer)
	// {
	// return new TileLayer((TileLayer) layer);
	// } else if (layer instanceof ObjectGroup)
	// {
	// return new ObjectGroup((ObjectGroup) layer);
	// }
	// return null;
	// }

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
				if (filename != null && filename.equals(mapFile)) {
					recent.remove(i);
					i--;
				}
			}

			recent.add(0, mapFile);

			if (recent.size() > 4) {
				recent = recent.subList(0, 3);
			}
		}

		mainMenu.clearAllRecent();

		for (int i = 0; i < recent.size(); i++) {
			String file = recent.get(i);
			if (file != null) {
				String name = file.substring(file.lastIndexOf(File.separatorChar) + 1);

				configuration.addConfigPair("tiled.recent." + (i + 1), file);
				mainMenu.addRecent(name, "_open" + (i + 1));
			}
		}
	}

	public void setCurrentMap(Map newMap) {
		if (currentMap != null) {
			currentMap.removeMapChangeListener(this);
		}

		currentMap = newMap;
		boolean mapLoaded = (currentMap != null);

		if (!mapLoaded) {
			mapEventAdapter.fireEvent(MapEventAdapter.ME_MAPINACTIVE);
			mapView = null;
			setCurrentPointerState(PS_POINT);
			statusBar.clearLabels();
			mapEditPanel.setMapView(null);
			currentBuilder = null;
		} else {
			mapEventAdapter.fireEvent(MapEventAdapter.ME_MAPACTIVE);
			mapView = new Orthogonal();
			mapView.setMap(currentMap);
			mapEditPanel.setMapView(mapView);
			setCurrentPointerState(PS_PAINT);
			setCurrentLayer(0);

			currentMap.addMapChangeListener(MapChangedEvent.Type.BRUSHES, this);
			currentMap.addMapChangeListener(MapChangedEvent.Type.LAYERS, this);
			currentMap.addMapChangeListener(MapChangedEvent.Type.TILESETS, this);
			statusBar.setMap(currentMap);
			statusBar.setZoom(mapEditPanel.getMapView().getScale());

			updateBuilder();
		}

		actionManager.getAction(ZoomInAction.class).setEnabled(mapLoaded);
		actionManager.getAction(ZoomOutAction.class).setEnabled(mapLoaded);

		undoStack.discardAllEdits();
		updateLayerTable();
		tilePalettePanel.setMap(currentMap);
		toolBar.setMap(currentMap);
		updateTitle();
		updateHistory();
	}

	public void setCurrentLayer(int index) {
		currentLayer = index;
		layerEditPanel.setLayer(index, currentMap);
		if (currentBuilder != null) {
			currentBuilder.setStartLayer(index);
		}
	}

	/**
	 * Changes the currently selected tiles from an TileSelectionEvent.
	 * 
	 * @param e
	 *            the event
	 */
	public void setCurrentTiles(List<StatefulTile> tiles) {
		currentTiles = tiles;
		currentBrush.setTiles(tiles);
		toolBar.setButtonStates(PS_PAINT);

		if (layerEditPanel != null) {
			layerEditPanel.updateBrush(currentBrush);
		}

	}

	/**
	 * sets the tile wich deletes the content.
	 * 
	 * @param delete
	 *            true when delete is enabled, false otherwise
	 */
	public void toggleDeleteTile(boolean delete) {
		if (delete) {
			List<StatefulTile> list = new ArrayList<StatefulTile>();
			list.add(new StatefulTile(new Point(0, 0), 0, currentMap.getNullTile()));
			currentBrush.setTiles(list);
			toolBar.setButtonStates(PS_ERASE);
			toolBar.getBrushMenu().selectDefaultDeleteBrush();
		} else {
			currentBrush.setTiles(currentTiles);
			toolBar.setButtonStates(PS_PAINT);
			toolBar.getBrushMenu().unselectDefaultDeleteBrush();
		}
	}

	private void setCurrentPointerState(int state) {
	}

	/**
	 * Loads an image that is part of the distribution jar.
	 * 
	 * @param fname
	 * @return A BufferedImage instance of the image
	 * @throws IOException
	 */
	public static BufferedImage loadImageResource(String fname) throws IOException {
		return ImageIO.read(MapEditor.class.getResourceAsStream("/tiled/mapeditor/" + fname));
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
	 * @param args
	 *            the first argument may be a map file
	 */
	public static void main(String[] args) {
		MapEditor editor = new MapEditor();

		if (args.length > 0) {
			String toLoad = args[0];
			if (!Util.checkRoot(toLoad) || toLoad.startsWith(".")) {
				if (toLoad.startsWith(".")) {
					toLoad = toLoad.substring(1);
				}
				toLoad = System.getProperty("user.dir") + File.separatorChar + toLoad;
			}
			editor.loadMap(toLoad);
		}
	}

	/**
	 * sets the selected tiles. The points are in tile coordinates
	 * 
	 * @param tiles
	 *            the selected tiles
	 * @param selectionRectangle
	 *            the selection rectangle in pixel coordinates
	 */
	public void setSelectedTiles(List<Point> tiles) {
		selectedTiles = tiles;
	}

	/**
	 * returns the selected tiles. The points are in tile coordinates
	 * 
	 * @return the currently selected tiles
	 */
	public List<Point> getSelectedTiles() {
		if (selectedTiles == null) {
			selectedTiles = new ArrayList<Point>();
		}
		return selectedTiles;
	}

	/** clears the selected tiles list. */
	public void clearSelectedTiles() {
		getSelectedTiles().clear();
	}

	/**
	 * switches between a tabbed tileset chooser and a treelike view.
	 * 
	 * @param enableTreeview
	 *            true, shows the tree; false, shows the tabbed pane
	 */
	public void toggleTreeTilesetChooser(boolean enableTreeview) {
		if (enableTreeview) {
			tilePalettePanel = new TilesetChooserTree(this);
		} else {
			tilePalettePanel = new TilesetChooserTabbedPane(this);
		}
		tilePalettePanel.setMap(getCurrentMap());
		baseSplit.setRightComponent(tilePalettePanel);
	}
}
