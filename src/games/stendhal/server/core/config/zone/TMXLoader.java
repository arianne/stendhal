/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.config.zone;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import games.stendhal.common.Base64;
import games.stendhal.common.tiled.LayerDefinition;
import games.stendhal.common.tiled.StendhalMapStructure;
import games.stendhal.common.tiled.TileSetDefinition;

/**
 * Loads a TMX file to server so it can understand: a) The objects layer b) The
 * collision layer c) The protection layer. d) All the layers that are sent to
 * client e) The tileset data that is also transfered to client f) A preview of
 * the zone for the minimap.
 *
 * Client would get the layers plus the tileset info.
 *
 * @author miguel
 *
 */
public class TMXLoader {
	private static Logger logger = Logger.getLogger(TMXLoader.class);

	private StendhalMapStructure stendhalMap;

	private String xmlPath;

	private static String makeUrl(final String filename) throws MalformedURLException {
		final String url;
		if ((filename.indexOf("://") > -1) || filename.startsWith("file:")) {
			url = filename;
		} else {
			url = (new File(filename)).toURI().toURL().toString();
		}
		return url;
	}

	private static String getAttributeValue(final Node node, final String attribname) {
		final NamedNodeMap attributes = node.getAttributes();
		String att = null;
		if (attributes != null) {
			final Node attribute = attributes.getNamedItem(attribname);
			if (attribute != null) {
				att = attribute.getNodeValue();
			}
		}
		return att;
	}

	private static int getAttribute(final Node node, final String attribname, final int def) {
		final String attr = getAttributeValue(node, attribname);
		if (attr != null) {
			return Integer.parseInt(attr);
		} else {
			return def;
		}
	}

	private TileSetDefinition unmarshalTileset(final Node t) throws Exception {
		final String name = getAttributeValue(t, "name");
		final int firstGid = getAttribute(t, "firstgid", 1);

		final TileSetDefinition set = new TileSetDefinition(name, null, firstGid);

		final boolean hasTilesetImage = false;
		final NodeList children = t.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);

			if (child.getNodeName().equalsIgnoreCase("image")) {
				if (hasTilesetImage) {
					continue;
				}

				set.setSource(getAttributeValue(child, "source"));
			}
		}

		return set;
	}

	/**
	 * Reads properties from amongst the given children. When a "properties"
	 * element is encountered, it recursively calls itself with the children of
	 * this node. This function ensures backward compatibility with tmx version
	 * 0.99a.
	 *
	 * @param children
	 *            the children amongst which to find properties
	 * @param props
	 *            the properties object to set the properties of
	 */
	@SuppressWarnings("unused")
	private static void readProperties(final NodeList children, final Properties props) {
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			if ("property".equalsIgnoreCase(child.getNodeName())) {
				props.setProperty(getAttributeValue(child, "name"),
						getAttributeValue(child, "value"));
			} else if ("properties".equals(child.getNodeName())) {
				readProperties(child.getChildNodes(), props);
			}
		}
	}

	/**
	 * Loads a map layer from a layer node.
	 * @param t
	 * @return the layer definition for the node
	 * @throws Exception
	 */
	private LayerDefinition readLayer(final Node t) throws Exception {
		final int layerWidth = getAttribute(t, "width", stendhalMap.getWidth());
		final int layerHeight = getAttribute(t, "height", stendhalMap.getHeight());

		final LayerDefinition layer = new LayerDefinition(layerWidth, layerHeight);

		final int offsetX = getAttribute(t, "x", 0);
		final int offsetY = getAttribute(t, "y", 0);

		if ((offsetX != 0) || (offsetY != 0)) {
			System.err.println("Severe error: maps has offset displacement");
		}

		layer.setName(getAttributeValue(t, "name"));

		for (Node child = t.getFirstChild(); child != null; child = child.getNextSibling()) {
			if ("data".equalsIgnoreCase(child.getNodeName())) {
				final String encoding = getAttributeValue(child, "encoding");

				if ((encoding != null) && "base64".equalsIgnoreCase(encoding)) {
					final Node cdata = child.getFirstChild();
					if (cdata != null) {
						final char[] enc = cdata.getNodeValue().trim().toCharArray();
						final byte[] dec = Base64.decode(enc);
						final ByteArrayInputStream bais = new ByteArrayInputStream(
								dec);
						InputStream is;

						final String comp = getAttributeValue(child, "compression");

						if ("gzip".equalsIgnoreCase(comp)) {
							is = new GZIPInputStream(bais);
						} else if ("zlib".equalsIgnoreCase(comp)) {
							is = new InflaterInputStream(bais);
						} else {
							is = bais;
						}

						final byte[] raw = layer.exposeRaw();
						int offset = 0;

						while (offset != raw.length) {
							offset += is.read(raw, offset, raw.length - offset);
						}

						bais.close();
					}
				}
			}
		}

		return layer;
	}

	private void buildMap(final Document doc) throws Exception {
		Node mapNode = doc.getDocumentElement();

		if (!"map".equals(mapNode.getNodeName())) {
			throw new Exception("Not a valid tmx map file.");
		}

		// Get the map dimensions and create the map
		final int mapWidth = getAttribute(mapNode, "width", 0);
		final int mapHeight = getAttribute(mapNode, "height", 0);

		if ((mapWidth > 0) && (mapHeight > 0)) {
			stendhalMap = new StendhalMapStructure(mapWidth, mapHeight);
		}

		if (stendhalMap == null) {
			throw new Exception("Couldn't locate map dimensions.");
		}

		// Load the tilesets, properties, layers and objectgroups
		for (Node sibs = mapNode.getFirstChild(); sibs != null; sibs = sibs.getNextSibling()) {
			if ("tileset".equals(sibs.getNodeName())) {
				stendhalMap.addTileset(unmarshalTileset(sibs));
			} else if ("layer".equals(sibs.getNodeName())) {
				stendhalMap.addLayer(readLayer(sibs));
			}
		}
	}

	private StendhalMapStructure unmarshal(final InputStream in) throws Exception {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc;
		try {
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setExpandEntityReferences(false);
			// Xerces normally tries to retrieve the dtd, even when it's not used - and
			// dies if it fails.
			try {
				factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			} catch (final IllegalArgumentException e) {
				logger.warn(e, e);
			}

			final DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(in, xmlPath);
		} catch (final SAXException e) {
			logger.error(e, e);
			throw new Exception("Error while parsing map file: " + e.toString());
		}

		buildMap(doc);
		return stendhalMap;
	}

	public StendhalMapStructure readMap(final String filename) throws Exception {
		xmlPath = filename.substring(0,
				filename.lastIndexOf(File.separatorChar) + 1);

		InputStream is = getClass().getClassLoader().getResourceAsStream(
				filename);

		if (is == null) {
			final String xmlFile = makeUrl(filename);
			// xmlPath = makeUrl(xmlPath);

			final URL url = new URL(xmlFile);
			is = url.openStream();
		}

		// Wrap with GZIP decoder for .tmx.gz files
		if (filename.endsWith(".gz")) {
			is = new GZIPInputStream(is);
		}

		return unmarshal(is);
	}

	public static void main(final String[] args) throws Exception {
		System.out.println("Test: loading map");

		StendhalMapStructure map = null;
		/*
		 * long start=System.currentTimeMillis(); for(int i=0;i<90;i++) {
		 * map=new TMXLoader().readMap("tiled/interiors/abstract/afterlife.tmx");
		 * map=new TMXLoader().readMap("tiled/Level 0/ados/city_n.tmx");
		 * map=new TMXLoader().readMap("tiled/Level 0/semos/city.tmx");
		 * map=new TMXLoader().readMap("tiled/Level 0/nalwor/city.tmx");
		 * map=new TMXLoader().readMap("tiled/Level 0/orril/castle.tmx");
		 * }
		 *
		 * System.out.println("Time ellapsed (ms): " + (System.currentTimeMillis()-start)); /
		 */
		map = new TMXLoader().readMap("tiled/Level 0/semos/village_w.tmx");
		map.build();
		System.out.printf("MAP W: %d H:%d\n", map.getWidth(), map.getHeight());
		final List<TileSetDefinition> tilesets = map.getTilesets();
		for (final TileSetDefinition set : tilesets) {
			System.out.printf("TILESET firstGID: '%d' name: '%s'\n",
					set.getFirstGid(), set.getSource());
		}

		final List<LayerDefinition> layers = map.getLayers();
		for (final LayerDefinition layer : layers) {
			System.out.printf("LAYER name: %s\n", layer.getName());
			final int w = layer.getWidth();
			final int h = layer.getHeight();

			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					final int gid = layer.getTileAt(x, y);
					if (x == w - 1) {
						System.out.print(gid);
					} else {
						System.out.print(gid + ",");
					}

				}
				System.out.println();
			}
		}

	}

	public static StendhalMapStructure load(final String filename) throws Exception {
		return new TMXLoader().readMap(filename);
	}
}
