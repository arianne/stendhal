package games.stendhal.tools.tiled;

import java.awt.Color;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import tiled.core.AnimatedTile;
import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.MapObject;
import tiled.core.ObjectGroup;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.core.TileSet;
import tiled.io.ImageHelper;
import tiled.io.MapReader;
import tiled.io.PluginLogger;
import tiled.mapeditor.util.cutter.BasicTileCutter;
import tiled.util.Base64;
import tiled.util.Util;


/**
 * Loads a TMX file to server so it can understand:
 * a) The objects layer
 * b) The collision layer
 * c) The protection layer.
 * d) All the layers that are sent to client
 * e) The tileset data that is also transfered to client
 * f) A preview of the zone for the minimap.
 * 
 * Client would get the layers plus the tileset info.
 * 
 * @author miguel
 *
 */
public class ServerTMXLoader implements MapReader {
	private Map map;
	private String xmlPath;
	private PluginLogger logger;

	public ServerTMXLoader() {
		logger = new PluginLogger();
	}

	private static String makeUrl(String filename) throws MalformedURLException {
		final String url;
		if (filename.indexOf("://") > 0 || filename.startsWith("file:")) {
			url = filename;
		} else {
			url = (new File(filename)).toURL().toString();
		}
		return url;
	}

	private static int reflectFindMethodByName(Class c, String methodName) {
		Method[] methods = c.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equalsIgnoreCase(methodName)) {
				return i;
			}
		}
		return -1;
	}

	private void reflectInvokeMethod(Object invokeVictim, Method method,
			String[] args) throws InvocationTargetException, Exception
			{
		Class[] parameterTypes = method.getParameterTypes();
		Object[] conformingArguments = new Object[parameterTypes.length];

		if (args.length < parameterTypes.length) {
			throw new Exception("Insufficient arguments were supplied");
		}

		for (int i = 0; i < parameterTypes.length; i++) {
			if ("int".equalsIgnoreCase(parameterTypes[i].getName())) {
				conformingArguments[i] = new Integer(args[i]);
			} else if ("float".equalsIgnoreCase(parameterTypes[i].getName())) {
				conformingArguments[i] = new Float(args[i]);
			} else if (parameterTypes[i].getName().endsWith("String")) {
				conformingArguments[i] = args[i];
			} else if ("boolean".equalsIgnoreCase(parameterTypes[i].getName())) {
				conformingArguments[i] = Boolean.valueOf(args[i]);
			} else {
				logger.debug("Unsupported argument type " +
						parameterTypes[i].getName() +
				", defaulting to java.lang.String");
				conformingArguments[i] = args[i];
			}
		}

		method.invoke(invokeVictim,conformingArguments);
			}

	private void setOrientation(String o) {
		if ("isometric".equalsIgnoreCase(o)) {
			map.setOrientation(Map.MDO_ISO);
		} else if ("orthogonal".equalsIgnoreCase(o)) {
			map.setOrientation(Map.MDO_ORTHO);
		} else if ("hexagonal".equalsIgnoreCase(o)) {
			map.setOrientation(Map.MDO_HEX);
		} else if ("oblique".equalsIgnoreCase(o)) {
			map.setOrientation(Map.MDO_OBLIQUE);
		} else if ("shifted".equalsIgnoreCase(o)) {
			map.setOrientation(Map.MDO_SHIFTED);
		} else {
			logger.warn("Unknown orientation '" + o + "'");
		}
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

	private Object unmarshalClass(Class reflector, Node node)
	throws InstantiationException, IllegalAccessException,
	InvocationTargetException {
		Constructor cons = null;
		try {
			cons = reflector.getConstructor(null);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			return null;
		}
		Object o = cons.newInstance(null);
		Node n;

		Method[] methods = reflector.getMethods();
		NamedNodeMap nnm = node.getAttributes();

		if (nnm != null) {
			for (int i = 0; i < nnm.getLength(); i++) {
				n = nnm.item(i);

				try {
					int j = reflectFindMethodByName(reflector,
							"set" + n.getNodeName());
					if (j >= 0) {
						reflectInvokeMethod(o,methods[j],
								new String [] {n.getNodeValue()});
					} else {
						logger.warn("Unsupported attribute '" +
								n.getNodeName() +
								"' on <" + node.getNodeName() + "> tag");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return o;
	}

	private Image unmarshalImage(Node t, String baseDir)
	throws MalformedURLException, IOException
	{
		Image img = null;

		String source = getAttributeValue(t, "source");

		if (source != null) {
			if (Util.checkRoot(source)) {
				source = makeUrl(source);
			} else {
				source = makeUrl(baseDir + source);
			}
			img = ImageIO.read(new URL(source));
			// todo: check whether external images would also be faster drawn
			// todo: from a scaled instance, see below
		} else {
			NodeList nl = t.getChildNodes();

			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if ("data".equals(n.getNodeName())) {
					Node cdata = n.getFirstChild();
					if (cdata == null) {
						logger.warn("image <data> tag enclosed no " +
						"data. (empty data tag)");
					} else {
						String sdata = cdata.getNodeValue();
						char[] charArray = sdata.trim().toCharArray();
						byte[] imageData = Base64.decode(charArray);
						img = ImageHelper.bytesToImage(imageData);

						// Deriving a scaled instance, even if it has the same
						// size, somehow makes drawing of the tiles a lot
						// faster on various systems (seen on Linux, Windows
						// and MacOS X).
						img = img.getScaledInstance(
								img.getWidth(null), img.getHeight(null),
								Image.SCALE_FAST);
					}
					break;
				}
			}
		}

		/*
	        if (getAttributeValue(t, "set") != null) {
	            TileSet ts = (TileSet)map.getTilesets().get(
	                    Integer.parseInt(getAttributeValue(t, "set")));
	            if (ts != null) {
	                ts.addImage(img);
	            }
	        }
		 */

		return img;
	}

	private TileSet unmarshalTilesetFile(InputStream in, String filename)
	throws Exception
	{
		TileSet set = null;
		Node tsNode;
		Document tsDoc = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			//builder.setErrorHandler(new XMLErrorHandler());
			tsDoc = builder.parse(in, ".");

			String xmlPathSave = xmlPath;
			if (filename.indexOf(File.separatorChar) >= 0) {
				xmlPath = filename.substring(0,
						filename.lastIndexOf(File.separatorChar) + 1);
			}

			NodeList tsNodeList = tsDoc.getElementsByTagName("tileset");

			for (int itr = 0; (tsNode = tsNodeList.item(itr)) != null; itr++) {
				set = unmarshalTileset(tsNode);
				if (set.getSource() != null) {
					logger.warn("Recursive external Tilesets are not supported.");
				}
				set.setSource(filename);
				// NOTE: This is a deliberate break. multiple tilesets per TSX are
				// not supported yet (maybe never)...
				break;
			}

			xmlPath = xmlPathSave;
		} catch (SAXException e) {
			logger.error("Failed while loading "+filename+": "+e.getMessage());
			//e.printStackTrace();
		}

		return set;
	}

	private TileSet unmarshalTileset(Node t) throws Exception {
		String source = getAttributeValue(t, "source");
		String basedir = getAttributeValue(t, "basedir");
		int firstGid = getAttribute(t, "firstgid", 1);

		String tilesetBaseDir = xmlPath;

		if (basedir != null) {
			tilesetBaseDir = basedir; //makeUrl(basedir);
		}

		if (source != null) {
			String filename = tilesetBaseDir + source;
			//if (Util.checkRoot(source)) {
			//    filename = makeUrl(source);
			//}

			TileSet ext = null;

			try {
				//just a little check for tricky people...
				String extention = source.substring(source.lastIndexOf('.') + 1);
				if (!"tsx".equals(extention.toLowerCase())) {
					logger.warn("tileset files should end in .tsx! ("+source+")");
				}

				InputStream in = new URL(makeUrl(filename)).openStream();
				ext = unmarshalTilesetFile(in, filename);
			} catch (FileNotFoundException fnf) {
				logger.error("Could not find external tileset file " +
						filename);
			}

			if (ext == null) {
				logger.error("tileset "+source+" was not loaded correctly!");
				ext = new TileSet();
			}

			ext.setFirstGid(firstGid);
			return ext;
		}
		else {
			int tileWidth = getAttribute(t, "tilewidth", map != null ? map.getTileWidth() : 0);
			int tileHeight = getAttribute(t, "tileheight", map != null ? map.getTileHeight() : 0);
			int tileSpacing = getAttribute(t, "spacing", 0);

			TileSet set = new TileSet();

			set.setName(getAttributeValue(t, "name"));
			set.setBaseDir(basedir);
			set.setFirstGid(firstGid);

			boolean hasTilesetImage = false;
			NodeList children = t.getChildNodes();

			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);

				if (child.getNodeName().equalsIgnoreCase("image")) {
					if (hasTilesetImage) {
						logger.warn("Ignoring illegal image element after tileset image.");
						continue;
					}

					String imgSource = getAttributeValue(child, "source");
					String id = getAttributeValue(child, "id");
					String transStr = getAttributeValue(child, "trans");

					if (imgSource != null && id == null) {
						// Not a shared image, but an entire set in one image
						// file. There should be only one image element in this
						// case.
						hasTilesetImage = true;

						// FIXME: importTileBitmap does not fully support URLs
						String sourcePath = imgSource;
						if (!Util.checkRoot(imgSource)) {
							sourcePath = tilesetBaseDir + imgSource;
						}

						logger.info("Importing " + sourcePath + "...");

						if (transStr != null) {
							int colorInt = Integer.parseInt(transStr, 16);
							Color color = new Color(colorInt);
							set.setTransparentColor(color);
						}

						set.setSource(imgSource);
						set.importTileBitmap(sourcePath, new BasicTileCutter(
								tileWidth, tileHeight, tileSpacing, 0));
					} else {
						Image image = unmarshalImage(child, tilesetBaseDir);
						String idValue = getAttributeValue(child, "id");
						int imageId = Integer.parseInt(idValue);
						set.addImage(image, imageId);
					}
				}
				else if (child.getNodeName().equalsIgnoreCase("tile")) {
					Tile tile = unmarshalTile(set, child, tilesetBaseDir);
					if (!hasTilesetImage || tile.getId() >= set.getMaxTileId()) {
						set.addTile(tile);
					} else {
						Tile myTile = set.getTile(tile.getId());
						myTile.setProperties(tile.getProperties());
						//TODO: there is the possibility here of overlaying images,
						//      which some people may want
					}
				}
			}

			return set;
		}
	}

	private MapObject unmarshalObject(Node t) throws Exception {
		MapObject obj = null;
		try {
			obj = (MapObject)unmarshalClass(MapObject.class, t);
		} catch (Exception e) {
			e.printStackTrace();
			return obj;
		}

		readProperties(t.getChildNodes(), obj.getProperties());
		return obj;
	}

	/**
	 * Reads properties from amongst the given children. When a "properties"
	 * element is encountered, it recursively calls itself with the children
	 * of this node. This function ensures backward compatibility with tmx
	 * version 0.99a.
	 *
	 * @param children the children amongst which to find properties
	 * @param props    the properties object to set the properties of
	 */
	private static void readProperties(NodeList children, Properties props) {
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if ("property".equalsIgnoreCase(child.getNodeName())) {
				props.setProperty(
						getAttributeValue(child, "name"),
						getAttributeValue(child, "value"));
			}
			else if ("properties".equals(child.getNodeName())) {
				readProperties(child.getChildNodes(), props);
			}
		}
	}

	private Tile unmarshalTile(TileSet set, Node t, String baseDir)
	throws Exception
	{
		Tile tile = null;
		NodeList children = t.getChildNodes();
		boolean isAnimated = false;

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if ("animation".equalsIgnoreCase(child.getNodeName())) {
				isAnimated = true;
				break;
			}
		}

		try {
			if (isAnimated) {
				tile = (Tile)unmarshalClass(AnimatedTile.class, t);
			} else {
				tile = (Tile)unmarshalClass(Tile.class, t);
			}
		} catch (Exception e) {
			logger.error("failed creating tile: "+e.getLocalizedMessage());
			//e.printStackTrace();
			return tile;
		}

		tile.setTileSet(set);

		readProperties(children, tile.getProperties());

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if ("image".equalsIgnoreCase(child.getNodeName())) {
				int id = getAttribute(child, "id", -1);
				Image img = unmarshalImage(child, baseDir);
				if (id < 0) {
					id = set.addImage(img);
				}
				tile.setImage(id);
			} else if ("animation".equalsIgnoreCase(child.getNodeName())) {
				// TODO: fill this in once XMLMapWriter is complete
			}
		}

		return tile;
	}

	private MapLayer unmarshalObjectGroup(Node t) throws Exception {
		ObjectGroup og = null;
		try {
			og = (ObjectGroup)unmarshalClass(ObjectGroup.class, t);
		} catch (Exception e) {
			e.printStackTrace();
			return og;
		}

		//Read all objects from the group, "...and in the darkness bind them."
		NodeList children = t.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if ("object".equalsIgnoreCase(child.getNodeName())) {
				og.bindObject(unmarshalObject(child));
			}
		}

		return og;
	}

	/**
	 * Loads a map layer from a layer node.
	 */
	private MapLayer readLayer(Node t) throws Exception {
		int layerWidth = getAttribute(t, "width", map.getWidth());
		int layerHeight = getAttribute(t, "height", map.getHeight());

		TileLayer ml = new TileLayer(layerWidth, layerHeight);

		int offsetX = getAttribute(t, "x", 0);
		int offsetY = getAttribute(t, "y", 0);
		int visible = getAttribute(t, "visible", 1);
		String opacity = getAttributeValue(t, "opacity");

		ml.setOffset(offsetX, offsetY);
		ml.setName(getAttributeValue(t, "name"));

		if (opacity != null) {
			ml.setOpacity(Float.parseFloat(opacity));
		}

		readProperties(t.getChildNodes(), ml.getProperties());

		for (Node child = t.getFirstChild(); child != null;
		child = child.getNextSibling())
		{
			if ("data".equalsIgnoreCase(child.getNodeName())) {
				String encoding = getAttributeValue(child, "encoding");

				if (encoding != null && "base64".equalsIgnoreCase(encoding)) {
					Node cdata = child.getFirstChild();
					if (cdata == null) {
						logger.warn("layer <data> tag enclosed no data. (empty data tag)");
					} else {
						char[] enc = cdata.getNodeValue().trim().toCharArray();
						byte[] dec = Base64.decode(enc);
						ByteArrayInputStream bais = new ByteArrayInputStream(dec);
						InputStream is;

						String comp = getAttributeValue(child, "compression");

						if (comp != null && "gzip".equalsIgnoreCase(comp)) {
							is = new GZIPInputStream(bais);
						} else {
							is = bais;
						}

						for (int y = 0; y < ml.getHeight(); y++) {
							for (int x = 0; x < ml.getWidth(); x++) {
								int tileId = 0;
								tileId |= is.read();
								tileId |= is.read() <<  8;
								tileId |= is.read() << 16;
								tileId |= is.read() << 24;

								TileSet ts = map.findTileSetForTileGID(tileId);
								if (ts != null) {
									ml.setTileAt(x, y,
											ts.getTile(tileId - ts.getFirstGid()));
								} else {
									ml.setTileAt(x, y, null);
								}
							}
						}
					}
				} else {
					int x = 0, y = 0;
					for (Node dataChild = child.getFirstChild();
					dataChild != null;
					dataChild = dataChild.getNextSibling())
					{
						if ("tile".equalsIgnoreCase(dataChild.getNodeName())) {
							int tileId = getAttribute(dataChild, "gid", -1);
							TileSet ts = map.findTileSetForTileGID(tileId);
							if (ts != null) {
								ml.setTileAt(x, y,
										ts.getTile(tileId - ts.getFirstGid()));
							} else {
								ml.setTileAt(x, y, null);
							}

							x++;
							if (x == ml.getWidth()) {
								x = 0; y++;
							}
							if (y == ml.getHeight()) { break; }
						}
					}
				}
			}
		}

		// Invisible layers are automatically locked, so it is important to
		// set the layer to potentially invisible _after_ the layer data is
		// loaded.
		// todo: Shouldn't this be just a user interface feature, rather than
		// todo: something to keep in mind at this level?
		ml.setVisible(visible == 1);

		return ml;
	}

	private void buildMap(Document doc) throws Exception {
		Node item, mapNode;

		mapNode = doc.getDocumentElement();

		if (!"map".equals(mapNode.getNodeName())) {
			throw new Exception("Not a valid tmx map file.");
		}

		// Get the map dimensions and create the map
		int mapWidth = getAttribute(mapNode, "width", 0);
		int mapHeight = getAttribute(mapNode, "height", 0);

		if (mapWidth > 0 && mapHeight > 0) {
			map = new Map(mapWidth, mapHeight);
		} else {
			// Maybe this map is still using the dimensions element
			NodeList l = doc.getElementsByTagName("dimensions");
			for (int i = 0; (item = l.item(i)) != null; i++) {
				if (item.getParentNode() == mapNode) {
					mapWidth = getAttribute(item, "width", 0);
					mapHeight = getAttribute(item, "height", 0);

					if (mapWidth > 0 && mapHeight > 0) {
						map = new Map(mapWidth, mapHeight);
					}
				}
			}
		}

		if (map == null) {
			throw new Exception("Couldn't locate map dimensions.");
		}

		// Load other map attributes
		String orientation = getAttributeValue(mapNode, "orientation");
		int tileWidth = getAttribute(mapNode, "tilewidth", 0);
		int tileHeight = getAttribute(mapNode, "tileheight", 0);

		if (tileWidth > 0) {
			map.setTileWidth(tileWidth);
		}
		if (tileHeight > 0) {
			map.setTileHeight(tileHeight);
		}

		if (orientation != null) {
			setOrientation(orientation);
		} else {
			setOrientation("orthogonal");
		}

		readProperties(mapNode.getChildNodes(), map.getProperties());

		// Load the tilesets, properties, layers and objectgroups
		for (Node sibs = mapNode.getFirstChild(); sibs != null;
		sibs = sibs.getNextSibling())
		{
			if ("tileset".equals(sibs.getNodeName())) {
				map.addTileset(unmarshalTileset(sibs));
			}
			else if ("layer".equals(sibs.getNodeName())) {
				MapLayer layer = readLayer(sibs);
				if (layer != null) {
					map.addLayer(layer);
				}
			}
			else if ("objectgroup".equals(sibs.getNodeName())) {
				MapLayer layer = unmarshalObjectGroup(sibs);
				if (layer != null) {
					map.addLayer(layer);
				}
			}
		}
	}

	private Map unmarshal(InputStream in) throws IOException, Exception {
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
			throw new Exception("Error while parsing map file: " +
					e.toString());
		}

		buildMap(doc);
		return map;
	}


	// MapReader interface

	public Map readMap(String filename) throws Exception {
		xmlPath = filename.substring(0,
				filename.lastIndexOf(File.separatorChar) + 1);

		String xmlFile = makeUrl(filename);
		//xmlPath = makeUrl(xmlPath);

		URL url = new URL(xmlFile);
		InputStream is = url.openStream();

		// Wrap with GZIP decoder for .tmx.gz files
		if (filename.endsWith(".gz")) {
			is = new GZIPInputStream(is);
		}

		Map unmarshalledMap = unmarshal(is);
		unmarshalledMap.setFilename(filename);

		return unmarshalledMap;
	}

	public Map readMap(InputStream in) throws Exception {
		xmlPath = makeUrl(".");

		Map unmarshalledMap = unmarshal(in);

		//unmarshalledMap.setFilename(xmlFile)
		//
		return unmarshalledMap;
	}

	public TileSet readTileset(String filename) throws Exception {
		String xmlFile = filename;

		xmlPath = filename.substring(0,
				filename.lastIndexOf(File.separatorChar) + 1);

		xmlFile = makeUrl(xmlFile);
		xmlPath = makeUrl(xmlPath);

		URL url = new URL(xmlFile);
		return unmarshalTilesetFile(url.openStream(), filename);
	}

	public TileSet readTileset(InputStream in) throws Exception {
		// TODO: The MapReader interface should be changed...
		return unmarshalTilesetFile(in, ".");
	}

	/**
	 * @see tiled.io.PluggableMapIO#getFilter()
	 */
	public String getFilter() throws Exception {
		return "*.tmx,*.tmx.gz,*.tsx";
	}

	public String getPluginPackage() {
		return "Tiled internal TMX reader/writer";
	}

	/**
	 * @see tiled.io.PluggableMapIO#getDescription()
	 */
	public String getDescription() {
		return "This is the core Tiled TMX format reader\n" +
		"\n" +
		"Tiled Map Editor, (c) 2004-2006\n" +
		"Adam Turk\n" +
		"Bjorn Lindeijer";
	}

	public String getName() {
		return "Default Tiled XML (TMX) map reader";
	}

	public boolean accept(File pathname) {
		try {
			String path = pathname.getCanonicalPath();
			if (path.endsWith(".tmx") || path.endsWith(".tsx") ||
					path.endsWith(".tmx.gz")) {
				return true;
			}
		} catch (IOException e) {}
		return false;
	}

	public void setLogger(PluginLogger logger) {
		this.logger = logger;
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Test: loading map");
		long start=System.currentTimeMillis();
		Map map=new ServerTMXLoader().readMap("D:/Desarrollo/stendhal/tiled/interiors/abstract/afterlife.tmx");
		System.out.println("Time ellapsed (ms): "+(System.currentTimeMillis()-start));

		System.out.printf("MAP W: %d H:%d\n", map.getWidth(), map.getHeight());
		Vector<TileSet> tilesets=map.getTilesets();
		for(TileSet set: tilesets) {
			System.out.printf("TILESET firstGID: %d name: %s\n", set.getFirstGid(), set.getSource());
		}

		ListIterator<MapLayer> it=map.getLayers();
		while(it.hasNext()) {
			TileLayer layer=(TileLayer) it.next();
			System.out.printf("LAYER name: %s\n", layer.getName());
			int w=layer.getWidth();
			int h=layer.getHeight();
			for(int i=0;i<w;i++) {
				for(int j=0;j<h;j++) {
					Tile tile=layer.getTileAt(i, j);
					int gid = 0;

					if (tile != null) {
						gid = tile.getGid();
					}

					System.out.print(gid + ((j == layer.getWidth() - 1) ? "" : ","));
				}
			System.out.println();
			}
		}
	}
}

