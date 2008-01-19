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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tiled.core.Map;
import tiled.core.PropertiesLayer;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.core.TileSet;

/**
 * Common base class for reading the *.(x)stend file format.
 * 
 * @author mtotz
 * 
 */
public class Reader {
	private List<String> errorList;
	/** tilegid for each tileset. */
	private int tilegid;

	/** reads the map. */
	public Map readMap(InputStream inputStream, boolean isCompressed) {
		// Use the default (non-validating) parser
		try {
			if (isCompressed) {
				inputStream = new java.util.zip.InflaterInputStream(inputStream);
			}

			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(inputStream);
			Element mapElement = doc.getDocumentElement();

			return parseMap(mapElement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** */
	public void setMessageList(List<String> errorList) {
		this.errorList = errorList;
	}

	/** helper method to get a named childnode. */
	private Node getNode(Node parent, String nodeName) {
		NodeList nodeList = parent.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (nodeName.equals(node.getNodeName())) {
				return node;
			}
		}

		return null;
	}

	/** helper method to get all childnodes with the specified name. */
	private List<Node> getNodes(Node parent, String nodeName) {
		List<Node> nodeList = new ArrayList<Node>();
		NodeList childNodeList = parent.getChildNodes();

		for (int i = 0; i < childNodeList.getLength(); i++) {
			Node node = childNodeList.item(i);
			if (nodeName.equals(node.getNodeName())) {
				nodeList.add(node);
			}
		}

		return nodeList;
	}

	/**
	 * reads the &lt;map&gt;-Element.
	 */
	private Map parseMap(Node mapNode) {
		Node size = getNode(mapNode, "size");
		if (size == null) {
			errorList.add("map corrupted, size tag missing");
			return null;
		}
		NamedNodeMap attr = size.getAttributes();
		Node widthNode = attr.getNamedItem("width");
		Node heightNode = attr.getNamedItem("height");
		if (widthNode == null || heightNode == null) {
			errorList.add("map corrupted, size tag does not contain attributes width and height");
			return null;
		}

		int width;
		int height;
		try {
			width = Integer.parseInt(widthNode.getNodeValue());
			height = Integer.parseInt(heightNode.getNodeValue());
		} catch (NumberFormatException e) {
			errorList.add("map corrupted, size tag attributes width and height must be integers");
			return null;
		}

		Map map = new Map(width, height);

		// read map properties
		Node propsNode = getNode(mapNode, "properties");
		if (propsNode != null) {
			Properties props = new Properties();
			readProperties(propsNode, props);
			map.setProperties(props);
		}

		readTilesets(map, getNodes(mapNode, "tileset"));
		readLayer(map, getNodes(mapNode, "layer"));
		readPropertyLayer(map, getNode(mapNode, "propertieslayer"));
		readBrushes(map, getNodes(mapNode, "brush"));
		return map;
	}

	/**
	 * reads the &lt;brush&gt;-Elements.
	 * 
	 * @param map
	 *            the map
	 * @param nodes
	 *            the brush nodes
	 */
	private void readBrushes(Map map, List<Node> nodes) {
		// TODO Auto-generated method stub

	}

	/**
	 * reads the &lt;tileset&gt;-Elements.
	 * 
	 * @param map
	 *            the map
	 * @param nodes
	 *            the tileset-elements
	 */
	private void readTilesets(Map map, List<Node> nodes) {
		// add some predefined tilesets
		try {
			tilegid = 1;
			// set the map-tile sizes
			map.setTileWidth(32);
			map.setTileHeight(32);

			if (nodes.size() == 0) {
				addDefaultTilesets(map);
			}

			for (Node node : nodes) {
				// read tilesets name
				Node nameNode = node.getAttributes().getNamedItem("name");
				if (nameNode == null) {
					System.out.println("tileset tag is missing the name attribute, skipped");
					errorList.add("tileset tag is missing the name attribute, skipped");
					continue;
				}
				String name = nameNode.getNodeValue();

				Node imageNode = getNode(node, "image");
				if (imageNode == null) {
					errorList.add("tileset " + name + " does not have an image tag");
					System.out.println("tileset " + name + " does not have an image tag");
				}
				Node sourceNode = imageNode.getAttributes().getNamedItem("source");
				if (sourceNode == null) {
					errorList.add("tileset " + name + " image tag is missing the source attribute, skipped");
					System.out.println("tileset " + name + " image tag is missing the source attribute, skipped");
					continue;
				}
				String image = sourceNode.getNodeValue();
				TileSet set = addTileSet(map, image, name);
				// now read tile properties
				List<Node> tileNodes = getNodes(node, "tile");
				for (Node tileNode : tileNodes) {
					try {
						Node idNode = tileNode.getAttributes().getNamedItem("id");
						Node properties = getNode(tileNode, "properties");
						if (idNode != null && properties != null) {
							int id = Integer.parseInt(idNode.getNodeValue());
							Tile tile = set.getTile(id);
							readProperties(properties, tile.getProperties());
						}
					} catch (NumberFormatException e) { 
						// ignore 
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** reads a &lt;properties&gt;-Elements. */
	private void readProperties(Node node, Properties properties) {
		String content = node.getTextContent();
		try {
			properties.load(new ByteArrayInputStream(content.getBytes()));
		} catch (IOException e) {
			// ignore
			e.printStackTrace();
		}

	}

	/** adds some default tilesets. */
	private void addDefaultTilesets(Map map) throws Exception {
		addTileSet(map, "tiled/zelda_outside_0_chipset.png", "outside 0");
		addTileSet(map, "tiled/zelda_outside_1_chipset.png", "outside 1");
		addTileSet(map, "tiled/zelda_dungeon_0_chipset.png", "dungeon 0");
		addTileSet(map, "tiled/zelda_dungeon_1_chipset.png", "dungeon 1");
		addTileSet(map, "tiled/zelda_interior_0_chipset.png", "interior 0");
		addTileSet(map, "tiled/zelda_navigation_chipset.png", "navigation");
		addTileSet(map, "tiled/zelda_objects_chipset.png", "objects");
		addTileSet(map, "tiled/zelda_collision_chipset.png", "collision");
		addTileSet(map, "tiled/zelda_building_0_tileset.png", "building 0");
		addTileSet(map, "tiled/zelda_outside_2_chipset.png", "outside 2");
	}

	/** adds a tile bitmap to the map. */
	private TileSet addTileSet(Map map, String path, String name) throws Exception {
		TileSet set = new TileSet();
		set.setFirstGid(tilegid);
		set.importTileBitmap(path, 32, 32, 0, true);
		name = (name.indexOf('.') > 0) ? name.substring(0, name.indexOf('.')) : name;
		set.setName(name);

		map.addTileset(set);
		tilegid += set.size();
		return set;
	}

	/**
	 * reads the &lt;layer&gt;-Elements.
	 * 
	 * @param map
	 *            the map
	 * @param nodes
	 *            the layer elements
	 */
	private void readLayer(Map map, List<Node> nodes) {
		for (Node node : nodes) {
			// read layers name
			Node nameNode = node.getAttributes().getNamedItem("name");
			if (nameNode == null) {
				errorList.add("layer tag is missing the name attribute, skipped");
				continue;
			}

			// create the layer
			TileLayer tileLayer = new TileLayer(map.getWidth(), map.getHeight());
			tileLayer.setName(nameNode.getNodeValue());

			Node opacity = node.getAttributes().getNamedItem("opacity");
			if (opacity != null) {
				try {
					float op = Float.parseFloat(opacity.getNodeValue());
					tileLayer.setOpacity(op);
				} catch (NumberFormatException e) {
					errorList.add("opacity is not a float");
				}
			}

			String content = node.getTextContent();

			// now read the data
			BufferedReader file = new BufferedReader(new StringReader(content.trim()));
			List<TileSet> tileSets = map.getTilesets();

			int y = 0;
			String text;
			try {
				while ((text = file.readLine()) != null) {
					int x = 0;
					text = text.trim();
					if (text.equals("")) {
						continue;
					}

					String[] items = text.split(",");
					for (String item : items) {
						int i = Integer.parseInt(item) - 1;
						Tile tile = map.getNullTile();
						int firstGid = 0;
						if (i >= 0) {
							// find tile
							for (TileSet tileSet : tileSets) {
								firstGid = tileSet.getFirstGid();
								if (i >= firstGid && i < (firstGid + tileSet.size())) {
									tile = tileSet.getTile(i - firstGid);
									break;
								}
							}
							if (tile == null) {
								errorList.add("Kein Tileset für " + i);
							}
						}

						tileLayer.setTileAt(x, y, tile);
						x++;
					}
					y++;
				}

				// layer is complete, add it to the map
				map.addLayer(tileLayer);

			} catch (Exception e) {
				// Exceptions are forwarded
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * reads the &lt;propertylayer&gt;-Element.
	 * 
	 * @param map
	 *            the map
	 * @param nodes
	 *            the propertylayer element
	 */
	private void readPropertyLayer(Map map, Node node) {
		if (node == null) {
			return;
		}

		PropertiesLayer layer = map.getPropertiesLayer();
		List<Node> tileNodes = getNodes(node, "tile");
		for (Node tileNode : tileNodes) {
			Node xNode = tileNode.getAttributes().getNamedItem("x");
			Node yNode = tileNode.getAttributes().getNamedItem("y");
			if (xNode != null && yNode != null) {
				try {
					int x = Integer.parseInt(xNode.getNodeValue());
					int y = Integer.parseInt(yNode.getNodeValue());
					Properties props = new Properties();
					readProperties(getNode(tileNode, "properties"), props);
					layer.setProps(x, y, props);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
