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

package tiled.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import tiled.core.Map;
import tiled.core.TileSet;
import tiled.mapeditor.dialog.ExceptionDialog;
import tiled.mapeditor.plugin.PluginClassLoader;
import tiled.mapeditor.plugin.PluginManager;
import tiled.plugins.MapReaderPlugin;
import tiled.plugins.MapWriterPlugin;
import tiled.plugins.TiledPlugin;
import tiled.plugins.tiled.XMLMapTransformer;
import tiled.plugins.tiled.XMLMapWriter;
import tiled.util.TiledConfiguration;

/**
 * A handler for saving and loading maps.
 */
public class MapHelper {
	private static PluginClassLoader pluginLoader;

	/**
	 * Called to tell the MapHelper which
	 * {@link tiled.mapeditor.plugin.PluginClassLoader} to use when finding a
	 * suitable plugin for a filename.
	 * 
	 * @param p
	 *            the PluginClassLoader instance to use
	 */
	public static void init(PluginClassLoader p) {
		pluginLoader = p;
	}

	/**
	 * Saves the current map. Use the extension (.xxx) of the filename to
	 * determine the plugin to use when writing the file. Throws an exception
	 * when the extension is not supported by either the TMX writer or a plugin.
	 * 
	 * @param filename
	 *            filename to save the current map to
	 * @param currentMap
	 *            {@link tiled.core.Map} instance to save to the file
	 * @see MapWriter#writeMap(Map, String)
	 * @exception Exception
	 */
	public static void saveMap(Map currentMap, String filename) throws Exception {
		MapWriter mw = null;
		if (filename.endsWith("tmx") || filename.endsWith("tmx.gz")) {
			// Override, so people can't overtake our format
			mw = new XMLMapWriter();
		} else {
			mw = (MapWriter) pluginLoader.getWriterFor(filename);
		}

		if (mw != null) {
			Stack<String> errors = new Stack<String>();
			mw.setErrorStack(errors);
			mw.writeMap(currentMap, filename);
			currentMap.setFilename(filename);
			reportPluginMessages(errors);
		} else {
			throw new Exception("Unsupported map format");
		}
	}

	/**
	 * Saves a tileset. Use the extension (.xxx) of the filename to determine
	 * the plugin to use when writing the file. Throws an exception when the
	 * extension is not supported by either the TMX writer or a plugin.
	 * 
	 * @param filename
	 *            Filename to save the tileset to.
	 * @param set
	 *            The TileSet instance to save to the file
	 * @see MapWriter#writeTileset(TileSet, String)
	 * @exception Exception
	 */
	public static void saveTileset(TileSet set, String filename) throws Exception {
		MapWriter mw = null;
		if (filename.endsWith(".tsx")) {
			// Override, so people can't overtake our format
			mw = new XMLMapWriter();
		} else {
			mw = (MapWriter) pluginLoader.getWriterFor(filename);
		}

		if (mw != null) {
			Stack<String> errors = new Stack<String>();
			mw.setErrorStack(errors);
			mw.writeTileset(set, filename);
			set.setSource(filename);
			reportPluginMessages(errors);
		} else {
			throw new Exception("Unsupported tileset format");
		}
	}

	/**
	 * Loads a tileset. Use the extension (.xxx) of the filename to determine
	 * the plugin to use when reading the file. Throws an exception when the
	 * extension is not supported by either the TMX writer or a plugin.
	 * 
	 * @param file
	 *            filename of map to load
	 * @return A new TileSet, loaded from the specified file by a plugin
	 * @throws Exception
	 * @see MapReader#readTileset(String)
	 */
	public static TileSet loadTileset(String file) throws Exception {
		TileSet ret = null;
		try {
			MapReader mr = null;
			if (file.endsWith(".tsx")) {
				// Override, so people can't overtake our format
				mr = new XMLMapTransformer();
			} else {
				mr = (MapReader) pluginLoader.getReaderFor(file);
			}

			if (mr != null) {
				Stack<String> errors = new Stack<String>();
				mr.setErrorStack(errors);
				ret = mr.readTileset(file);
				ret.setSource(file);
				reportPluginMessages(errors);
			} else {
				throw new Exception("Unsupported tileset format");
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage()
					+ (e.getCause() != null ? "\nCause: " + e.getCause().getMessage() : ""),
					"Error while loading tileset", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while loading " + file + ": " + e.getMessage()
					+ (e.getCause() != null ? "\nCause: " + e.getCause().getMessage() : ""),
					"Error while loading tileset", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * Reports messages from the plugin to the user in a dialog.
	 * 
	 * @param s
	 *            A Stack which was used by the plugin to record any messages it
	 *            had for the user
	 */
	private static void reportPluginMessages(Stack<String> st) {
		// TODO: maybe have a nice dialog with a scrollbar, in case there are a
		// lot of messages...
		TiledConfiguration config = TiledConfiguration.getInstance();

		if (config.keyHasValue("tiled.report.io", 1)) {
			if (st.size() > 0) {
				StringBuilder warnings = new StringBuilder();
				for (String s : st) {
					warnings.append(s + "\n");
				}
				JOptionPane.showMessageDialog(null, warnings.toString(), "Loading Messages",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	/** returns a list of all currently registered map reader plugins. */
	public static List<MapReaderPlugin> getMapReaderPlugins() {
		PluginManager pluginManager = PluginManager.getInstance();
		List<Class< ? extends TiledPlugin>> list = pluginManager.getPlugins(MapReaderPlugin.class);
		List<MapReaderPlugin> plugins = new ArrayList<MapReaderPlugin>();
		// instantiate the plugins
		for (Class< ? extends TiledPlugin> clazz : list) {
			try {
				plugins.add((MapReaderPlugin) clazz.newInstance());
			} catch (Exception e) {
				// ignore broken plugins
				e.printStackTrace();
			}
		}
		return plugins;
	}

	/** returns a list of all currently registered map writer plugins. */
	public static List<MapWriterPlugin> getMapWriterPlugins() {
		PluginManager pluginManager = PluginManager.getInstance();

		List<Class< ? extends TiledPlugin>> list = pluginManager.getPlugins(MapWriterPlugin.class);
		List<MapWriterPlugin> plugins = new ArrayList<MapWriterPlugin>();

		// instantiate the plugins
		for (Class< ? extends TiledPlugin> clazz : list) {
			try {
				plugins.add((MapWriterPlugin) clazz.newInstance());
			} catch (Exception e) {
				// ignore broken plugins
				e.printStackTrace();
			}
		}
		return plugins;
	}

	/** opens a file chooser to open a new map. */
	public static Map loadMap(JFrame appFrame) {
		Map ret = null;
		String mapFile = null;
		try {
			TiledConfiguration configuration = TiledConfiguration.getInstance();
			File startFile = null;
			if (configuration.hasOption("tiled.recent.1")) {
				String startLocation = configuration.getValue("tiled.recent.1");
				startFile = (startLocation != null) ? new File(startLocation) : null;
			}

			List<MapReaderPlugin> plugins = getMapReaderPlugins();

			List<FileFilter> filters = new ArrayList<FileFilter>();
			// Now get all file filters
			for (MapReaderPlugin plugin : plugins) {
				filters.addAll(Arrays.asList(plugin.getFilters()));
			}

			// open the file chooser
			JFileChooser fileChooser = new JFileChooser("");
			fileChooser.setSelectedFile(startFile);
			for (FileFilter filter : filters) {
				fileChooser.addChoosableFileFilter(filter);
			}

			int result = fileChooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				mapFile = file.getAbsolutePath();
				// get filter
				for (MapReaderPlugin plugin : plugins) {
					for (FileFilter filter : plugin.getFilters()) {
						if (filter.accept(file)) {
							System.out.println("map " + file.getAbsolutePath() + " read with plugin "
									+ plugin.getPluginDescription());
							List<String> messageList = new ArrayList<String>();
							plugin.setMessageList(messageList);
							Map newMap = plugin.readMap(mapFile);
							if (newMap != null) {
								newMap.setFilename(mapFile);
							}
							return newMap;
						}
					}
				}
			}

		} catch (Exception e) {
			ExceptionDialog.showDialog(appFrame, e, "Exception while loading map " + mapFile);
		}
		return ret;
	}

	/** loads the given map. */
	public static Map loadMap(JFrame appFrame, String fileName, StringBuilder errorBuf) {
		Map ret = null;
		try {
			File mapFile = new File(fileName);
			if (mapFile == null || !mapFile.isFile()) {
				errorBuf.append("File " + mapFile + " does not exists.");
				return null;
			}

			List<MapReaderPlugin> plugins = getMapReaderPlugins();

			MapReaderPlugin readerPlugin = null;
			// Now get all file filters
			for (MapReaderPlugin plugin : plugins) {
				for (FileFilter filter : plugin.getFilters()) {
					if (filter.accept(mapFile)) {
						readerPlugin = plugin;
						break;
					}
				}
			}

			if (readerPlugin == null) {
				errorBuf.append("no plugin found to read " + fileName);
				return null;
			}

			List<String> messageList = new ArrayList<String>();
			readerPlugin.setMessageList(messageList);
			Map newMap = readerPlugin.readMap(mapFile.getAbsolutePath());
			if (newMap != null) {
				newMap.setFilename(mapFile.getAbsolutePath());
			}
			return newMap;
		} catch (Exception e) {
			ExceptionDialog.showDialog(appFrame, e, "Exception while loading map " + fileName);
		}
		return ret;
	}

	/** saves the map. */
	public static boolean saveMapNew(JFrame appFrame, Map map, boolean saveAs, StringBuilder errorBuf) {
		if (map == null) {
			return false;
		}

		File file = new File(map.getFilename());
		try {
			List<MapWriterPlugin> plugins = MapHelper.getMapWriterPlugins();

			List<FileFilter> filters = new ArrayList<FileFilter>();
			// Now get all file filters
			for (MapWriterPlugin plugin : plugins) {
				filters.addAll(Arrays.asList(plugin.getFilters()));
			}

			if (saveAs) {
				// open the file chooser
				JFileChooser fileChooser = new JFileChooser("");
				fileChooser.setSelectedFile(file);
				for (FileFilter filter : filters) {
					fileChooser.addChoosableFileFilter(filter);
				}

				int result = fileChooser.showSaveDialog(null);
				if (result != JFileChooser.APPROVE_OPTION) {
					return false;
				}
				file = fileChooser.getSelectedFile();
				// FileFilter filter = fileChooser.getFileFilter();
			}

			MapWriterPlugin writerPlugin = null;
			// find the correct plugin
			for (MapWriterPlugin plugin : plugins) {
				for (FileFilter filter : plugin.getFilters()) {
					if (filter.accept(file)) {
						writerPlugin = plugin;
						break;
					}
				}
			}

			if (writerPlugin == null) {
				errorBuf.append("no plugin found to write " + file);
				return false;
			}

			List<String> messageList = new ArrayList<String>();
			writerPlugin.setMessageList(messageList);
			writerPlugin.writeMap(map, file.getAbsolutePath());
			return true;
		} catch (Exception e) {
			ExceptionDialog.showDialog(appFrame, e, "Exception while loading map " + file);
		}
		return false;
	}
}
