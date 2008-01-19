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

package tiled.plugins.stendhal;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;
import java.util.zip.DeflaterOutputStream;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.PropertiesLayer;
import tiled.core.StatefulTile;
import tiled.core.Tile;
import tiled.core.TileGroup;
import tiled.core.TileLayer;
import tiled.core.TileSet;

/**
 * Baseclass for writing the .(x)stend map file format.
 * 
 * @author mtotz
 */
public class Writer {
	/** */
	public void setMessageList(List<String> errorList) {
		// not used
	}

	/**
	 * Writes the map.
	 * 
	 * @param map
	 *            the map
	 * @param outputStream
	 *            the outputstream where to write to
	 * @param compress
	 *            should the map be compressed?
	 */
	protected void writeMap(Map map, OutputStream outputStream, boolean compress) {
		if (compress) {
			outputStream = new DeflaterOutputStream(outputStream);
		}

		PrintStream writer = new PrintStream(outputStream);

		String mapName = map.getFilename();
		int index = mapName.indexOf(File.separatorChar);
		mapName = index > 0 ? mapName.substring(index + 1) : mapName;
		index = mapName.indexOf('.');
		mapName = index > 0 ? mapName.substring(0, index) : mapName;

		writer.print("<map name=\"" + mapName + "\" width=\"" + map.getWidth() + "\" height=\"" + map.getHeight()
				+ "\" >\n");

		// size-tag for backwards compatibilty
		writer.print("<size width=\"" + map.getWidth() + "\" height=\"" + map.getHeight() + "\" />\n");

		printProperties(writer, map.getProperties());

		for (TileSet set : map.getTilesets()) {
			printTileset(writer, set);
		}

		for (TileGroup brush : map.getUserBrushes()) {
			printBrush(writer, brush);
		}

		for (MapLayer layer : map) {
			printLayer(writer, layer);
		}

		printPropertiesLayer(writer, map.getPropertiesLayer());

		writer.print("</map>\n");
		writer.flush();
		if (compress) {
			try {
				outputStream.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	/** writes the properties. */
	private void printProperties(PrintStream writer, Properties properties) {
		writer.print("<properties>\n");
		for (Object key : properties.keySet()) {
			writer.print(key + "=" + properties.get(key) + "\n");
		}
		writer.print("</properties>\n");
	}

	/** writes the user brushes to the file. */
	private void printBrush(PrintStream writer, TileGroup brush) {
		writer.print("<brush name=\"" + brush.getName() + "\" x=\"" + brush.getX() + "\" y=\"" + brush.getY() + "\">\n");
		java.util.Map<TileLayer, List<StatefulTile>> tileMap = brush.getTileLayers();

		for (TileLayer layer : tileMap.keySet()) {
			writer.print("<layer name=" + layer.getName() + ">\n");
			List<StatefulTile> tileList = tileMap.get(layer);
			for (StatefulTile tile : tileList) {
				if (tile != null) {
					writer.print(tile.p.x + "; " + tile.p.y + "; " + tile.tile.getGid() + "\n");
				}
			}
			writer.print("</layer>\n");
		}
		writer.print("</brush>\n");
	}

	/** writes the tileset to the xml file. */
	private void printTileset(PrintStream writer, TileSet set) {
		writer.print("<tileset name=\"" + set.getName() + "\" firstgid=\"" + set.getFirstGid() + "\" ");
		writer.print("tilewidth=\"" + set.getStandardWidth() + "\" tileheight=\"" + set.getStandardHeight() + "\">\n");
		writer.print("<image source=\"" + set.getTilebmpFile() + "\"");
		Color color = set.getTransparentColor();
		if (color != null) {
			String colorString = Integer.toHexString(color.getRGB());
			writer.print(" trans=\"" + colorString + "\"");
		}
		writer.print("/>\n");

		// write tile properties
		for (int i = 0; i < set.size(); i++) {
			Tile tile = set.getTile(i);
			if (tile != null) {
				Properties properties = tile.getProperties();
				if (properties != null && properties.size() > 0) {
					writer.print("<tile id=\"" + i + "\">\n");
					printProperties(writer, properties);
					writer.print("</tile>");
				}
			}
		}
		writer.print("</tileset>\n");
	}

	/** writes the layer to the xml file. */
	private void printLayer(PrintStream writer, MapLayer layer) {
		TileLayer tileLayer = (TileLayer) layer;
		writer.print("<layer name=\"" + layer.getName() + "\" opacity=\"" + layer.getOpacity() + "\">\n");
		for (int y = 0; y < layer.getHeight(); y++) {
			for (int x = 0; x < layer.getWidth(); x++) {
				Tile tile = tileLayer.getTileAt(x, y);
				if (tile != null) {
					writer.print(tile.getGid() + 1);
				} else {
					writer.print(0);
				}
				if (x < layer.getWidth() - 1) {
					writer.print(",");
				}

			}
			writer.print("\n");
		}
		writer.print("</layer>\n");
	}

	/** writes the properties layer. */
	private void printPropertiesLayer(PrintStream writer, PropertiesLayer layer) {
		writer.print("<propertieslayer>\n");
		for (int y = 0; y < layer.getHeight(); y++) {
			for (int x = 0; x < layer.getWidth(); x++) {
				Properties props = layer.getProps(x, y);
				if (props.size() > 0) {
					writer.print("<tile x=\"" + x + "\" y=\"" + y + "\">\n");
					printProperties(writer, props);
					writer.print("</tile>\n");
				}
			}
		}
		writer.print("</propertieslayer>\n");
	}

}
