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

package tiled.plugins.tiled;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

import javax.swing.filechooser.FileFilter;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.core.TileSet;
import tiled.io.ImageHelper;
import tiled.mapeditor.selection.SelectionLayer;
import tiled.plugins.MapWriterPlugin;
import tiled.util.Base64;
import tiled.util.TiledConfiguration;

/**
 * @author mtotz
 * 
 */
public class MapWriter implements MapWriterPlugin {
	private String workPath;

	public MapWriter() {
		workPath = "";
	}

	/** writes the map. */
	public void writeMap(Map map, String filename) {
		try {
			File file = new File(filename);
			workPath = filename;
			writeMap(map, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/** writes the map. */
	public void writeMap(Map map, OutputStream outputStream) {
		Writer writer = new OutputStreamWriter(outputStream);
		XMLWriter xmlWriter = new XMLWriter(writer);

		try {
			xmlWriter.startDocument();
			xmlWriter.startElement("map");
			xmlWriter.writeAttribute("version", "0.99a");

			switch (map.getOrientation()) {
			case Map.MDO_ORTHO:
				xmlWriter.writeAttribute("orientation", "orthogonal");
				break;
			case Map.MDO_ISO:
				xmlWriter.writeAttribute("orientation", "isometric");
				break;
			case Map.MDO_OBLIQUE:
				xmlWriter.writeAttribute("orientation", "oblique");
				break;
			case Map.MDO_HEX:
				xmlWriter.writeAttribute("orientation", "hexagonal");
				break;
			case Map.MDO_SHIFTED:
				xmlWriter.writeAttribute("orientation", "shifted");
				break;
			}

			xmlWriter.writeAttribute("width", "" + map.getWidth());
			xmlWriter.writeAttribute("height", "" + map.getHeight());
			xmlWriter.writeAttribute("tilewidth", "" + map.getTileWidth());
			xmlWriter.writeAttribute("tileheight", "" + map.getTileHeight());

			Properties props = map.getProperties();
			for (Enumeration<Object> keys = props.keys(); keys.hasMoreElements();) {
				String key = (String) keys.nextElement();
				xmlWriter.startElement("property");
				xmlWriter.writeAttribute("name", key);
				xmlWriter.writeAttribute("value", props.getProperty(key));
				xmlWriter.endElement();
			}
			int firstgid = 1;
			for (TileSet tileset : map.getTilesets()) {
				tileset.setFirstGid(firstgid);
				writeTilesetReference(tileset, xmlWriter, workPath);
				firstgid += tileset.getMaxTileId() + 1;
			}

			for (MapLayer layer : map) {
				writeMapLayer(layer, xmlWriter);
			}

			xmlWriter.endElement();

			xmlWriter.endDocument();
			writer.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Writes a reference to an external tileset into a XML document. In the
	 * degenerate case where the tileset is not stored in an external file,
	 * writes the contents of the tileset instead.
	 */
	private void writeTilesetReference(TileSet set, XMLWriter w, String wp) throws IOException {

		try {
			String source = set.getSource();

			if (source == null) {
				writeTileset(set, w, wp);
			} else {
				w.startElement("tileset");
				try {
					w.writeAttribute("firstgid", "" + set.getFirstGid());
					w.writeAttribute("source", source.substring(source.lastIndexOf(File.separatorChar) + 1));
					if (set.getBaseDir() != null) {
						w.writeAttribute("basedir", set.getBaseDir());
					}
				} finally {
					w.endElement();
				}
			}
		} catch (XMLWriterException e) {
			e.printStackTrace();
		}
	}

	private void writeTileset(TileSet set, XMLWriter w, String wp) throws IOException {

		try {
			String tilebmpFile = set.getTilebmpFile();
			String name = set.getName();

			w.startElement("tileset");

			if (name != null) {
				w.writeAttribute("name", name);
			}

			w.writeAttribute("firstgid", "" + set.getFirstGid());

			if (tilebmpFile != null) {
				w.writeAttribute("tilewidth", "" + set.getStandardWidth());
				w.writeAttribute("tileheight", "" + set.getStandardHeight());
				// w.writeAttribute("spacing", "0");
			}

			if (set.getBaseDir() != null) {
				w.writeAttribute("basedir", set.getBaseDir());
			}

			if (tilebmpFile != null) {
				w.startElement("image");
				w.writeAttribute("source", getRelativePath(wp, tilebmpFile));

				Color trans = set.getTransparentColor();
				if (trans != null) {
					w.writeAttribute("trans", Integer.toHexString(trans.getRGB()).substring(2));
				}
				w.endElement();
			} else {
				// Embedded tileset

				TiledConfiguration conf = TiledConfiguration.getInstance();
				if (conf.keyHasValue("tmx.save.tileSetImages", "1")) {
					Iterator<String> ids = set.getImageIds().iterator();
					while (ids.hasNext()) {
						String id = ids.next();
						w.startElement("image");
						w.writeAttribute("format", "png");
						w.writeAttribute("id", id);
						w.startElement("data");
						w.writeAttribute("encoding", "base64");
						w.writeCDATA(new String(Base64.encode(ImageHelper.imageToPNG(set.getImageById(id)))));
						w.endElement();
						w.endElement();
					}
				} else if (conf.keyHasValue("tmx.save.embedImages", "0")) {
					String imgSource = conf.getValue("tmx.save.tileImagePrefix") + "set.png";
					w.writeAttribute("source", imgSource);

					String tilesetFilename = (wp.substring(0, wp.lastIndexOf(File.separatorChar) + 1) + imgSource);
					FileOutputStream fw = new FileOutputStream(new File(tilesetFilename));
					// byte[] data = ImageHelper.imageToPNG(setImage);
					// fw.write(data, 0, data.length);
					fw.close();
				}

				// Check to see if there is a need to write tile elements
				if (set.isOneForOne()) {
					boolean needWrite = false;

					if (conf.keyHasValue("tmx.save.embedImages", "1")) {
						needWrite = true;
					} else {
						for (Tile tile : set) {
							if (!tile.getProperties().isEmpty()) {
								needWrite = true;
								break;
								// As long as one has properties, they all
								// need to be written.
								// TODO: This shouldn't be necessary
							}
						}
					}

					if (needWrite) {
						for (Tile tile : set) {
							writeTile(tile, w);
						}
					}
				}
			}
			w.endElement();
		} catch (XMLWriterException e) {
			e.printStackTrace();
		}
	}

	private void writeTile(Tile tile, XMLWriter w) throws IOException {
		try {
			w.startElement("tile");

			int tileId = tile.getId();

			w.writeAttribute("id", "" + tileId);

			// if (groundHeight != getHeight()) {
			// w.writeAttribute("groundheight", "" + groundHeight);
			// }

			Properties props = tile.getProperties();
			for (Enumeration<Object> keys = props.keys(); keys.hasMoreElements();) {
				String key = (String) keys.nextElement();
				w.startElement("property");
				w.writeAttribute("name", key);
				w.writeAttribute("value", props.getProperty(key));
				w.endElement();
			}

			Image tileImage = tile.getImage();

			TiledConfiguration conf = TiledConfiguration.getInstance();

			// Write encoded data
			if (tileImage != null && !conf.keyHasValue("tmx.save.tileSetImages", "1")) {
				if (conf.keyHasValue("tmx.save.embedImages", "1") && conf.keyHasValue("tmx.save.tileSetImages", "0")) {
					w.startElement("image");
					w.writeAttribute("format", "png");
					w.startElement("data");
					w.writeAttribute("encoding", "base64");
					w.writeCDATA(new String(Base64.encode(ImageHelper.imageToPNG(tileImage))));
					w.endElement();
					w.endElement();
				} else if (conf.keyHasValue("tmx.save.tileSetImages", "1")) {
					w.startElement("image");
					w.writeAttribute("id", "" + tile.getImageId());
					int orientation = tile.getImageOrientation();
					int rotation = 0;
					boolean flipped = (orientation & 1) == ((orientation & 2) >> 1);
					if ((orientation & 4) == 4) {
						rotation = ((orientation & 1) == 1) ? 270 : 90;
					} else {
						rotation = ((orientation & 2) == 2) ? 180 : 0;
					}
					if (rotation != 0) {
						w.writeAttribute("rotation", "" + rotation);
					}
					if (flipped) {
						w.writeAttribute("flipped", "true");
					}
					w.endElement();
				} else {
					String prefix = conf.getValue("tmx.save.tileImagePrefix");
					String filename = conf.getValue("tmx.save.maplocation") + prefix + tileId + ".png";
					w.startElement("image");
					w.writeAttribute("source", prefix + tileId + ".png");
					FileOutputStream fw = new FileOutputStream(new File(filename));
					byte[] data = ImageHelper.imageToPNG(tileImage);
					fw.write(data, 0, data.length);
					fw.close();
					w.endElement();
				}
			}

			w.endElement();
		} catch (XMLWriterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes this layer to an XMLWriter. This should be done <b>after</b> the
	 * first global ids for the tilesets are determined, in order for the right
	 * gids to be written to the layer data.
	 */
	private void writeMapLayer(MapLayer l, XMLWriter w) throws IOException {
		try {
			TiledConfiguration conf = TiledConfiguration.getInstance();
			boolean encodeLayerData = conf.keyHasValue("tmx.save.encodeLayerData", "1");
			boolean compressLayerData = conf.keyHasValue("tmx.save.layerCompression", "1") && encodeLayerData;

			Rectangle bounds = l.getBounds();

			if (l.getClass() == SelectionLayer.class) {
				w.startElement("selection");
				// } else if(l instanceof ObjectGroup){
				// w.startElement("objectgroup");
			} else {
				w.startElement("layer");
			}

			w.writeAttribute("name", l.getName());
			w.writeAttribute("width", "" + bounds.width);
			w.writeAttribute("height", "" + bounds.height);
			if (bounds.x != 0) {
				w.writeAttribute("xoffset", "" + bounds.x);
			}
			if (bounds.y != 0) {
				w.writeAttribute("yoffset", "" + bounds.y);
			}

			if (!l.isVisible()) {
				w.writeAttribute("visible", "0");
			}
			if (l.getOpacity() < 1.0f) {
				w.writeAttribute("opacity", "" + l.getOpacity());
			}

			Properties props = l.getProperties();
			for (Enumeration<Object> keys = props.keys(); keys.hasMoreElements();) {
				String key = (String) keys.nextElement();
				w.startElement("property");
				w.writeAttribute("name", key);
				w.writeAttribute("value", props.getProperty(key));
				w.endElement();
			}

			// if (l instanceof ObjectGroup){
			// writeObjectGroup((ObjectGroup)l, w);
			// } else {
			w.startElement("data");
			if (encodeLayerData) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				OutputStream out;

				w.writeAttribute("encoding", "base64");

				if (compressLayerData) {
					w.writeAttribute("compression", "gzip");
					out = new GZIPOutputStream(baos);
				} else {
					out = baos;
				}

				for (int y = 0; y < l.getHeight(); y++) {
					for (int x = 0; x < l.getWidth(); x++) {
						Tile tile = ((TileLayer) l).getTileAt(x, y);
						int gid = 0;

						if (tile != null) {
							gid = tile.getGid();
						}

						out.write((gid) & 0x000000FF);
						out.write((gid >> 8) & 0x000000FF);
						out.write((gid >> 16) & 0x000000FF);
						out.write((gid >> 24) & 0x000000FF);
					}
				}

				if (compressLayerData) {
					((GZIPOutputStream) out).finish();
				}

				w.writeCDATA(new String(Base64.encode(baos.toByteArray())));
			} else {
				for (int y = 0; y < l.getHeight(); y++) {
					for (int x = 0; x < l.getWidth(); x++) {
						Tile tile = ((TileLayer) l).getTileAt(x, y);
						int gid = 0;

						if (tile != null) {
							gid = tile.getGid();
						}

						w.startElement("tile");
						w.writeAttribute("gid", "" + gid);
						w.endElement();
					}
				}
			}
			w.endElement();
			// }
			w.endElement();
		} catch (XMLWriterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the relative path from one file to the other. The function
	 * expects absolute paths, relative paths will be converted to absolute
	 * using the working directory.
	 * 
	 * @param from
	 *            the path of the origin file
	 * @param to
	 *            the path of the destination file
	 * @return the relative path from origin to destination
	 */
	public static String getRelativePath(String from, String to) {
		// Make the two paths absolute and unique
		try {
			from = new File(from).getCanonicalPath();
			to = new File(to).getCanonicalPath();
		} catch (IOException e) {
		}

		File fromFile = new File(from);
		File toFile = new File(to);
		List<String> fromParents = new ArrayList<String>();
		List<String> toParents = new ArrayList<String>();

		// Iterate to find both parent lists
		while (fromFile != null) {
			fromParents.add(0, fromFile.getName());
			fromFile = fromFile.getParentFile();
		}
		while (toFile != null) {
			toParents.add(0, toFile.getName());
			toFile = toFile.getParentFile();
		}

		// Iterate while parents are the same
		int shared = 0;
		int maxShared = Math.min(fromParents.size(), toParents.size());
		for (shared = 0; shared < maxShared; shared++) {
			String fromParent = (String) fromParents.get(shared);
			String toParent = (String) toParents.get(shared);
			if (!fromParent.equals(toParent)) {
				break;
			}
		}

		// Append .. for each remaining parent in fromParents
		StringBuilder relPathBuf = new StringBuilder();
		for (int i = shared; i < fromParents.size() - 1; i++) {
			relPathBuf.append(".." + File.separator);
		}

		// Add the remaining part in toParents
		for (int i = shared; i < toParents.size() - 1; i++) {
			relPathBuf.append(toParents.get(i) + File.separator);
		}
		relPathBuf.append(new File(to).getName());
		String relPath = relPathBuf.toString();

		// Turn around the slashes when path is relative
		try {
			String absPath = new File(relPath).getCanonicalPath();

			if (!absPath.equals(relPath)) {
				// Path is not absolute, turn slashes around
				// Assumes: \ does not occur in filenames
				relPath = relPath.replace('\\', '/');
			}
		} catch (Exception e) {
		}

		return relPath;
	}

	/** all filefilters. */
	public FileFilter[] getFilters() {
		return new FileFilter[] { new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() || pathname.getName().toLowerCase().endsWith(".tmx");
			}

			@Override
			public String getDescription() {
				return "Tiled XML map (*.tmx)";
			}

		} };
	}

	/** returns the description. */
	public String getPluginDescription() {
		return "The core Tiled TMX format writer\n" + "\n" + "Tiled Map Editor, (c) 2005\n" + "Adam Turk\n"
				+ "Bjorn Lindeijer";
	}

	/** not used. */
	public void setMessageList(List<String> errorList) {
		// not used
	}

}
