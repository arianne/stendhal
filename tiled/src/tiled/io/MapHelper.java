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

package tiled.io;

import java.io.IOException;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.JOptionPane;

import tiled.core.Map;
import tiled.core.TileSet;
import tiled.io.xml.XMLMapTransformer;
import tiled.io.xml.XMLMapWriter;
import tiled.mapeditor.plugin.PluginClassLoader;
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
     * @param p the PluginClassLoader instance to use
     */
    public static void init(PluginClassLoader p) {
        pluginLoader = p;
    }

    /**
     * Saves the current map. Use the extension (.xxx) of the filename to
     * determine the plugin to use when writing the file. Throws an exception
     * when the extension is not supported by either the TMX writer or a
     * plugin.
     *
     * @param filename filename to save the current map to
     * @param currentMap {@link tiled.core.Map} instance to save to the file
     * @see MapWriter#writeMap(Map, String)
     * @exception Exception
     */
    public static void saveMap(Map currentMap, String filename)
        throws Exception
    {
        MapWriter mw = null;
        if (filename.endsWith("tmx") || filename.endsWith("tmx.gz")) {
            // Override, so people can't overtake our format
            mw = new XMLMapWriter();
        } else {
            mw = (MapWriter)pluginLoader.getWriterFor(filename);
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
     * Saves a tileset.  Use the extension (.xxx) of the filename to determine
     * the plugin to use when writing the file. Throws an exception when the
     * extension is not supported by either the TMX writer or a plugin.
     *
     * @param filename Filename to save the tileset to.
     * @param set The TileSet instance to save to the file
     * @see MapWriter#writeTileset(TileSet, String)
     * @exception Exception
     */
    public static void saveTileset(TileSet set, String filename)
        throws Exception
    {
        MapWriter mw = null;
        if (filename.endsWith(".tsx")) {
            // Override, so people can't overtake our format
            mw = new XMLMapWriter();
        } else {
            mw = (MapWriter)pluginLoader.getWriterFor(filename);
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
     * Loads a map. Use the extension (.xxx) of the filename to determine
     * the plugin to use when reading the file. Throws an exception when the
     * extension is not supported by either the TMX writer or a plugin.
     *
     * @param file filename of map to load
     * @return a new Map, loaded from the specified file by a plugin
     * @throws Exception
     * @see MapReader#readMap(String)
     */
    public static Map loadMap(String file) throws Exception {
        Map ret = null;
        try {
            MapReader mr = null;
            if (file.endsWith(".tmx") || file.endsWith(".tmx.gz")) {
                // Override, so people can't overtake our format
                mr = new XMLMapTransformer();
            } else {
                mr = (MapReader)pluginLoader.getReaderFor(file);
            }

            if (mr != null) {
                Stack<String> errors = new Stack<String>();
                mr.setErrorStack(errors);
                ret = mr.readMap(file);
                ret.setFilename(file);
                reportPluginMessages(errors);
            } else {
                throw new Exception("Unsupported map format");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage() + (e.getCause() != null ? "\nCause: " +
                        e.getCause().getMessage() : ""),
                    "Error while loading map",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error while loading " + file + ": " +
                    e.getMessage() + (e.getCause() != null ? "\nCause: " +
                        e.getCause().getMessage() : ""),
                    "Error while loading map",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Loads a tileset. Use the extension (.xxx) of the filename to determine
     * the plugin to use when reading the file. Throws an exception when the
     * extension is not supported by either the TMX writer or a plugin.
     *
     * @param file filename of map to load
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
                mr = (MapReader)pluginLoader.getReaderFor(file);
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
            JOptionPane.showMessageDialog(null,
                    e.getMessage() + (e.getCause() != null ? "\nCause: " +
                        e.getCause().getMessage() : ""),
                    "Error while loading tileset",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error while loading " + file + ": " +
                    e.getMessage() + (e.getCause() != null ? "\nCause: " +
                        e.getCause().getMessage() : ""),
                    "Error while loading tileset",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Reports messages from the plugin to the user in a dialog.
     *
     * @param s A Stack which was used by the plugin to record any messages it
     *          had for the user
     */
    private static void reportPluginMessages(Stack s) {
        // TODO: maybe have a nice dialog with a scrollbar, in case there are a
        // lot of messages...
        TiledConfiguration config = TiledConfiguration.getInstance();

        if (config.keyHasValue("tiled.report.io", 1)) {
            if (s.size() > 0) {
                Iterator itr = s.iterator();
                StringBuffer warnings = new StringBuffer();
                while (itr.hasNext()) {
                    warnings.append((String)itr.next() + "\n");
                }
                JOptionPane.showMessageDialog(null, warnings.toString(),
                        "Loading Messages",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
