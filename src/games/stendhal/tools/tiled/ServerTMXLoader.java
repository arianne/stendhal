package games.stendhal.tools.tiled;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
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

import tiled.core.Map;
import tiled.io.PluginLogger;
import tiled.util.Base64;


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
public class ServerTMXLoader {
	static class TileSetDef {
		String name;
		String source;
		int gid;			
	}
	
	static class LayerDef {
		int width;
		int height;
		
		String name;
		int[] data;

		public LayerDef(int layerWidth, int layerHeight) {
	        data=new int[layerWidth*layerHeight];
	        width=layerWidth;
	        height=layerHeight;
        }
		
		public int[] expose() {
			return data;
		}
		
		public void set(int x, int y, int tileId) {
			data[y*width+x]=tileId;
        }

		public int getTileAt(int x, int y) {
			return data[y*width+x];
        }
	}
	
	/**
	 * This is the format that our client uses.
	 * 
	 * @author miguel
	 *
	 */
	static class StendhalMapFormat {
		String filename;
		int width;
		int height;
		List<TileSetDef> tilesets;
		List<LayerDef> layers;		
		
		public StendhalMapFormat(int w, int h){
			width=w;
			height=h;
			tilesets=new LinkedList<TileSetDef>();
			layers=new LinkedList<LayerDef>();
		}

		public void addTileset(TileSetDef set) {
			tilesets.add(set);	        
        }

		public void addLayer(LayerDef layer) {
			layers.add(layer);
        }

		public void setFilename(String filename) {
	        this.filename=filename;	        
        }

		public List<TileSetDef> getTilesets() {
	        return tilesets;
        }

		public List<LayerDef> getLayers() {
	        return layers;
        }
	}
	
	private StendhalMapFormat stendhalMap;
	
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

	private TileSetDef unmarshalTileset(Node t) throws Exception {
		int firstGid = getAttribute(t, "firstgid", 1);

		TileSetDef set = new TileSetDef();

		set.name=getAttributeValue(t, "name");
		set.gid=firstGid;

		boolean hasTilesetImage = false;
		NodeList children = t.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);

			if (child.getNodeName().equalsIgnoreCase("image")) {
				if (hasTilesetImage) {
					logger.warn("Ignoring illegal image element after tileset image.");
					continue;
				}

				set.source = getAttributeValue(child, "source");
			}
		}

		return set;
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

	/**
	 * Loads a map layer from a layer node.
	 */
	private LayerDef readLayer(Node t) throws Exception {
		int layerWidth = getAttribute(t, "width", stendhalMap.width);
		int layerHeight = getAttribute(t, "height", stendhalMap.height);

		LayerDef layer=new LayerDef(layerWidth, layerHeight);

		int offsetX = getAttribute(t, "x", 0);
		int offsetY = getAttribute(t, "y", 0);

		if(offsetX!=0 || offsetY!=0) {
			System.err.println("Severe error: maps has offset displacement");
		}
		
		layer.name=getAttributeValue(t, "name");

		// XXX: Ignored by now.
		//readProperties(t.getChildNodes(), ml.getProperties());

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

						byte[] raw=new byte[4];
						int[] data=layer.expose();

						for (int y = 0; y < layer.height; y++) {
							for (int x = 0; x < layer.width; x++) {
								is.read(raw);								
								
								int tileId = 0;
								tileId |= ((int)raw[0]& 0xFF);
								tileId |= ((int)raw[1]& 0xFF) <<  8;
								tileId |= ((int)raw[2]& 0xFF) << 16;
								tileId |= ((int)raw[3]& 0xFF) << 24;
								
								data[x+y*layer.width]=tileId;
							}
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
			stendhalMap= new StendhalMapFormat(mapWidth, mapHeight);
		}
		
		if (stendhalMap == null) {
			throw new Exception("Couldn't locate map dimensions.");
		}

		// Load the tilesets, properties, layers and objectgroups
		for (Node sibs = mapNode.getFirstChild(); sibs != null; sibs = sibs.getNextSibling())
		{
			if ("tileset".equals(sibs.getNodeName())) {
				stendhalMap.addTileset(unmarshalTileset(sibs));
			}
			else if ("layer".equals(sibs.getNodeName())) {
				stendhalMap.addLayer(readLayer(sibs));
			}
		}
	}

	private StendhalMapFormat unmarshal(InputStream in) throws IOException, Exception {
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
		return stendhalMap;
	}


	// MapReader interface

	public StendhalMapFormat readMap(String filename) throws Exception {
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

		StendhalMapFormat unmarshalledMap = unmarshal(is);
		unmarshalledMap.setFilename(filename);

		return unmarshalledMap;
	}

	public void setLogger(PluginLogger logger) {
		this.logger = logger;
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Test: loading map");
		long start=System.currentTimeMillis();
		StendhalMapFormat map=new ServerTMXLoader().readMap("D:/Desarrollo/stendhal/tiled/interiors/abstract/afterlife.tmx");
		map=new ServerTMXLoader().readMap("D:/Desarrollo/stendhal/tiled/Level 0/ados/city_n.tmx");
		map=new ServerTMXLoader().readMap("D:/Desarrollo/stendhal/tiled/Level 0/ados/swamp.tmx");
		System.out.println("Time ellapsed (ms): "+(System.currentTimeMillis()-start));
/*		
		System.out.printf("MAP W: %d H:%d\n", map.width, map.height);
		List<TileSetDef> tilesets=map.getTilesets();
		for(TileSetDef set: tilesets) {
			System.out.printf("TILESET firstGID: %d name: %s\n", set.gid, set.source);
		}

		List<LayerDef> layers=map.getLayers();
		for(LayerDef layer: layers) {			
			System.out.printf("LAYER name: %s\n", layer.name);
			int w=layer.width;
			int h=layer.height;
			
			for(int y=0;y<h;y++) {
				for(int x=0;x<w;x++) {
					int gid=layer.getTileAt(x, y);
					System.out.print(gid + ((x == layer.width - 1) ? "" : ","));
				}
			System.out.println();
			}
		}
*/
	}
}

