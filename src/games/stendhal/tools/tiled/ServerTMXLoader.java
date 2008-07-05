package games.stendhal.tools.tiled;

import games.stendhal.common.Base64;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
public class ServerTMXLoader {

	private StendhalMapStructure stendhalMap;

	private String xmlPath;

	private static String makeUrl(String filename) throws MalformedURLException {
		final String url;
		if (filename.indexOf("://") > 0 || filename.startsWith("file:")) {
			url = filename;
		} else {
			url = (new File(filename)).toURI().toURL().toString();
		}
		return url;
	}

	private static String getAttributeValue(Node node, String attribname) {
		NamedNodeMap attributes = node.getAttributes();
		String att = null;
		if (attributes != null) {
			Node attribute = attributes.getNamedItem(attribname);
			if (attribute != null) {
				att = attribute.getNodeValue();
			}
		}
		return att;
	}

	private static int getAttribute(Node node, String attribname, int def) {
		String attr = getAttributeValue(node, attribname);
		if (attr != null) {
			return Integer.parseInt(attr);
		} else {
			return def;
		}
	}

	private TileSetDefinition unmarshalTileset(Node t) throws Exception {
		String name = getAttributeValue(t, "name");
		int firstGid = getAttribute(t, "firstgid", 1);

		TileSetDefinition set = new TileSetDefinition(name, firstGid);

		boolean hasTilesetImage = false;
		NodeList children = t.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);

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
	private static void readProperties(NodeList children, Properties props) {
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
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
	private LayerDefinition readLayer(Node t) throws Exception {
		int layerWidth = getAttribute(t, "width", stendhalMap.width);
		int layerHeight = getAttribute(t, "height", stendhalMap.height);

		LayerDefinition layer = new LayerDefinition(layerWidth, layerHeight);

		int offsetX = getAttribute(t, "x", 0);
		int offsetY = getAttribute(t, "y", 0);

		if (offsetX != 0 || offsetY != 0) {
			System.err.println("Severe error: maps has offset displacement");
		}

		layer.setName(getAttributeValue(t, "name"));

		// TODO: XXX: Ignored by now.
		// readProperties(t.getChildNodes(), ml.getProperties());

		for (Node child = t.getFirstChild(); child != null; child = child.getNextSibling()) {
			if ("data".equalsIgnoreCase(child.getNodeName())) {
				String encoding = getAttributeValue(child, "encoding");

				if (encoding != null && "base64".equalsIgnoreCase(encoding)) {
					Node cdata = child.getFirstChild();
					if (cdata != null) {
						char[] enc = cdata.getNodeValue().trim().toCharArray();
						byte[] dec = Base64.decode(enc);
						ByteArrayInputStream bais = new ByteArrayInputStream(
								dec);
						InputStream is;

						String comp = getAttributeValue(child, "compression");

						if (comp != null && "gzip".equalsIgnoreCase(comp)) {
							is = new GZIPInputStream(bais);
						} else {
							is = bais;
						}

						byte[] raw = layer.exposeRaw();
						int offset = 0;

						while (offset != raw.length) {
							offset += is.read(raw, offset, raw.length - offset);
						}
					}
				}
			}
		}

		return layer;
	}

	private void buildMap(Document doc) throws Exception {
		Node mapNode;

		mapNode = doc.getDocumentElement();

		if (!"map".equals(mapNode.getNodeName())) {
			throw new Exception("Not a valid tmx map file.");
		}

		// Get the map dimensions and create the map
		int mapWidth = getAttribute(mapNode, "width", 0);
		int mapHeight = getAttribute(mapNode, "height", 0);

		if (mapWidth > 0 && mapHeight > 0) {
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

	private StendhalMapStructure unmarshal(InputStream in) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc;
		try {
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setExpandEntityReferences(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(in, xmlPath);
		} catch (SAXException e) {
			e.printStackTrace();
			throw new Exception("Error while parsing map file: " + e.toString());
		}

		buildMap(doc);
		return stendhalMap;
	}

	public StendhalMapStructure readMap(String filename) throws Exception {
		xmlPath = filename.substring(0,
				filename.lastIndexOf(File.separatorChar) + 1);

		InputStream is = getClass().getClassLoader().getResourceAsStream(
				filename);

		if (is == null) {
			String xmlFile = makeUrl(filename);
			// xmlPath = makeUrl(xmlPath);

			URL url = new URL(xmlFile);
			is = url.openStream();
		}

		if (is == null) {
			return null;
		}

		// Wrap with GZIP decoder for .tmx.gz files
		if (filename.endsWith(".gz")) {
			is = new GZIPInputStream(is);
		}

		StendhalMapStructure unmarshalledMap = unmarshal(is);
		unmarshalledMap.setFilename(filename);

		return unmarshalledMap;
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Test: loading map");

		StendhalMapStructure map = null;
		/*
		 * long start=System.currentTimeMillis(); for(int i=0;i<90;i++) {
		 * map=new
		 * ServerTMXLoader().readMap("D:/Desarrollo/stendhal/tiled/interiors/abstract/afterlife.tmx");
		 * map=new ServerTMXLoader().readMap("D:/Desarrollo/stendhal/tiled/Level
		 * 0/ados/city_n.tmx"); map=new
		 * ServerTMXLoader().readMap("D:/Desarrollo/stendhal/tiled/Level
		 * 0/semos/city.tmx"); map=new
		 * ServerTMXLoader().readMap("D:/Desarrollo/stendhal/tiled/Level
		 * 0/nalwor/city.tmx"); map=new
		 * ServerTMXLoader().readMap("D:/Desarrollo/stendhal/tiled/Level
		 * 0/orril/castle.tmx"); }
		 * 
		 * System.out.println("Time ellapsed (ms):
		 * "+(System.currentTimeMillis()-start)); /
		 */
		map = new ServerTMXLoader().readMap("D:/Desarrollo/stendhal/tiled/Level 0/semos/village_w.tmx");
		map.build();
		System.out.printf("MAP W: %d H:%d\n", map.width, map.height);
		List<TileSetDefinition> tilesets = map.getTilesets();
		for (TileSetDefinition set : tilesets) {
			System.out.printf("TILESET firstGID: '%d' name: '%s'\n",
					set.getFirstGid(), set.getSource());
		}

		List<LayerDefinition> layers = map.getLayers();
		for (LayerDefinition layer : layers) {
			System.out.printf("LAYER name: %s\n", layer.getName());
			int w = layer.getWidth();
			int h = layer.getHeight();

			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					int gid = layer.getTileAt(x, y);
					System.out.print(gid + ((x == w - 1) ? "" : ","));
				}
				System.out.println();
			}
		}

	}

	public static StendhalMapStructure load(String filename) throws Exception {
		return new ServerTMXLoader().readMap(filename);
	}
}
