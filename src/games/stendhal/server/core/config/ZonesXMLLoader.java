/*
 * @(#) src/games/stendhal/server/config/ZonesXMLLoader.java
 *
 * $Id$
 */

package games.stendhal.server.core.config;

//
//

import games.stendhal.server.core.config.zone.ConfiguratorXMLReader;
import games.stendhal.server.core.config.zone.EntitySetupXMLReader;
import games.stendhal.server.core.config.zone.PortalSetupXMLReader;
import games.stendhal.server.core.config.zone.RegionNameSubstitutionHelper;
import games.stendhal.server.core.config.zone.SetupDescriptor;
import games.stendhal.server.core.config.zone.SetupXMLReader;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.tools.tiled.LayerDefinition;
import games.stendhal.tools.tiled.ServerTMXLoader;
import games.stendhal.tools.tiled.StendhalMapStructure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Load and configure zones via an XML configuration file.
 */
public class ZonesXMLLoader {
	/**
	 * Logger.
	 */
	private static final Logger logger = Logger.getLogger(ZonesXMLLoader.class);

	/**
	 * The ConfiguratorDescriptor XML reader.
	 */
	protected static final SetupXMLReader configuratorReader = new ConfiguratorXMLReader();

	/**
	 * The EntitySetupDescriptor XML reader.
	 */
	protected static final SetupXMLReader entitySetupReader = new EntitySetupXMLReader();

	/**
	 * The PortalSetupDescriptor XML reader.
	 */
	protected static final SetupXMLReader portalSetupReader = new PortalSetupXMLReader();

	/**
	 * The zone group file.
	 */
	protected URI uri;
	
	/**
	 * the region this zone group file is considered for
	 */
	private final String region;

	/**
	 * Create an xml based loader of zones.
	 * @param uri the zone group file
	 */
	public ZonesXMLLoader(final URI uri) {
		this.uri = uri;
		String path = this.uri.getPath();
		String xmlName = path.substring(path.lastIndexOf("/")+1);
		region = xmlName.substring(0, xmlName.indexOf("."));
	}

	//
	// ZonesXMLLoader
	//

	/**
	 * Load a group of zones into a world.
	 * 
	 * @throws SAXException
	 *             If a SAX error occurred.
	 * @throws IOException
	 *             If an I/O error occurred.
	 * @throws FileNotFoundException
	 *             If the resource was not found.
	 */
	public void load() throws SAXException, IOException {
		final InputStream in = getClass().getResourceAsStream(uri.getPath());

		if (in == null) {
			throw new FileNotFoundException("Cannot find resource: " + uri);
		}

		try {
			load(in);
		} finally {
			in.close();
		}
	}

	/**
	 * Loads a group of zones into a world using a config file.
	 * 
	 * @param in
	 *            The config file stream.
	 * 
	 * @throws SAXException
	 *             If a SAX error occurred.
	 * @throws IOException
	 *             If an I/O error occurred.
	 */
	protected void load(final InputStream in) throws SAXException, IOException {
		
		final Document doc = XMLUtil.parse(in);

		/*
		 * Load each zone
		 */
		for (final Element element : XMLUtil.getElements(doc.getDocumentElement(),
				"zone")) {
			final ZoneDesc zdesc = readZone(element);

			if (zdesc == null) {
				continue;
			}

			final String name = zdesc.getName();

			logger.info("Loading zone: " + name);

			try {
				final StendhalMapStructure zonedata = ServerTMXLoader.load(StendhalRPWorld.MAPS_FOLDER
						+ zdesc.getFile());

				if (verifyMap(zdesc, zonedata)) {
					final StendhalRPZone zone = load(zdesc, zonedata);

					/*
					 * Setup Descriptors
					 */
					final Iterator<SetupDescriptor> diter = zdesc.getDescriptors();

					while (diter.hasNext()) {
						diter.next().setup(zone);
					}
				}
			} catch (final Exception ex) {
				logger.error("Error loading zone: " + name, ex);
			}
		}
	}

	private static final String[] REQUIRED_LAYERS = { "0_floor", "1_terrain",
			"2_object", "3_roof", "objects", "collision", "protection" };

	private boolean verifyMap(final ZoneDesc zdesc, final StendhalMapStructure zonedata) {
		for (final String layer : REQUIRED_LAYERS) {
			if (!zonedata.hasLayer(layer)) {
				logger.error("Required layer " + layer + " missing in zone "
						+ zdesc.getFile());
				return false;
			}
		}
		return true;
	}

	/**
	 * Load zone data and create a new zone from it. Most of this should be moved
	 * directly into ZoneXMLLoader.
	 * @param desc the zone's descriptor 
	 * @param zonedata to be loaded
	 * @return the created zone
	 * @throws SAXException if any xml parsing error happened
	 * @throws IOException if any IO error happened
	 * 
	 * 
	 */
	protected StendhalRPZone load(final ZoneDesc desc, final StendhalMapStructure zonedata)
			throws SAXException, IOException {
		final String name = desc.getName();
		
		final StendhalRPZone zone;
		if (desc.getImplementation() == null) {
			zone = new StendhalRPZone(name);
		} else {
			zone = createZone(desc, name);
		}

		zone.addTilesets(name + ".tilesets", zonedata.getTilesets());
		zone.addLayer(name + ".0_floor", zonedata.getLayer("0_floor"));
		zone.addLayer(name + ".1_terrain", zonedata.getLayer("1_terrain"));
		zone.addLayer(name + ".2_object", zonedata.getLayer("2_object"));
		zone.addLayer(name + ".3_roof", zonedata.getLayer("3_roof"));

		final LayerDefinition layer = zonedata.getLayer("4_roof_add");

		if (layer != null) {
			zone.addLayer(name + ".4_roof_add", layer);
		}

		zone.addCollisionLayer(name + ".collision",
				zonedata.getLayer("collision"));
		zone.addProtectionLayer(name + ".protection",
				zonedata.getLayer("protection"));

		if (desc.isInterior()) {
			zone.setPosition();
		} else {
			zone.setPosition(desc.getLevel(), desc.getX(), desc.getY());
		}

		SingletonRepository.getRPWorld().addRPZone(desc.getRegion(), zone);

		try {
			zone.onInit();
		} catch (final Exception e) {
			logger.error(e, e);
		}
		
		zone.populate(zonedata.getLayer("objects"));

		return zone;
	}

	@SuppressWarnings("unchecked")
	private StendhalRPZone createZone(final ZoneDesc desc, final String name)  {
		try {
			Class<StendhalRPZone> zoneclass = (Class<StendhalRPZone>) Class.forName(desc.getImplementation());
			Constructor<StendhalRPZone> constr = zoneclass.getConstructor(String.class);
			return constr.newInstance(name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return new StendhalRPZone(name);
	}

	public ZoneDesc readZone(final Element element) {
		if (!element.hasAttribute("name")) {
			logger.error("Unnamed zone");
			return null;
		}

		final String name = element.getAttribute("name");

		if (!element.hasAttribute("file")) {
			logger.error("Zone [" + name + "] without 'file' attribute");
			return null;
		}

		final String file = element.getAttribute("file");
		String region = this.region; 

		int level;
		int x;
		int y;

		/**
		 * Interior zones don't have levels (why not?)
		 */
		if (element.hasAttribute("level")) {
			String s = element.getAttribute("level");

			try {
				level = Integer.parseInt(s);
			} catch (final NumberFormatException ex) {
				logger.error("Zone [" + name + "] has invalid level: " + s);
				return null;
			}

			if (!element.hasAttribute("x")) {
				logger.error("Zone [" + name + "] without x coordinate");
				return null;
			} else {
				s = element.getAttribute("x");

				try {
					x = Integer.parseInt(s);
				} catch (final NumberFormatException ex) {
					logger.error("Zone [" + name
							+ "] has invalid x coordinate: " + s);
					return null;
				}
			}

			if (!element.hasAttribute("y")) {
				logger.error("Zone [" + name + "] without y coordinate");
				return null;
			} else {
				s = element.getAttribute("y");

				try {
					y = Integer.parseInt(s);
				} catch (final NumberFormatException ex) {
					logger.error("Zone [" + name
							+ "] has invalid y coordinate: " + s);
					return null;
				}
			}
		} else {
			level = ZoneDesc.UNSET;
			x = ZoneDesc.UNSET;
			y = ZoneDesc.UNSET;
		}
		region = parseRegionFromZone(name);

		final ZoneDesc desc = new ZoneDesc(name, file, region, level, x, y);

		/*
		 * Title element
		 */
		final List<Element> list = XMLUtil.getElements(element, "title");

		if (!list.isEmpty()) {
			if (list.size() > 1) {
				logger.error("Zone [" + name + "] has multiple title elements");
			}

			desc.setTitle(XMLUtil.getText(list.get(0)).trim());
		}
		
		
		

		/*
		 * Setup elements
		 */
		for (final Element child : XMLUtil.getElements(element)) {
			final String tag = child.getTagName();

			SetupDescriptor setupDesc = null;

			if (tag.equals("configurator")) {
				setupDesc = configuratorReader.read(child);
			} else if (tag.equals("implementation")) {
				desc.setImplementation(child.getAttribute("class-name"));
				
			} else if (tag.equals("entity")) {
				setupDesc = entitySetupReader.read(child);
			} else if (tag.equals("portal")) {
				setupDesc = portalSetupReader.read(child);
			} else if (tag.equals("title")) {
				// Ignore
				continue;
			} else {
				logger.warn("Zone [" + name + "] has unknown element: " + tag);
				continue;
			}

			if (setupDesc != null) {
				desc.addDescriptor(setupDesc);
			}
		}

		
		return desc;
	}

	//
	//

	/**
	 * Extracts the region out of the given zone name
	 * Basic zone name convention:
	 * exteriors:
	 * lvl_region_zonename_orientation (i.e. 0 or n1)
	 * interiors:
	 * int_region_zonename_number (number for houses)
	 * @param name the name of the zone to parse
	 */
	private String parseRegionFromZone(String name) {
		String[] split = name.split("_");
		if(split != null) {
			// standard exterior and interior zones have more than 3 parts 
			if (split.length > 1) {
				return RegionNameSubstitutionHelper.get().replaceRegionName(split[1]);
			}
		}
		//each zone that shouldn't be accounted as a region would be considered as "no region"
		return RegionNameSubstitutionHelper.get().getDefaultRegion();
	}

	/*
	 *  THIS REQUIRES StendhalRPWorld SETUP (i.e. marauroa.ini) XXX
	 */
	public static void main(final String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Usage: java " + ZonesXMLLoader.class.getName()
					+ " <filename>");
			System.exit(1);
		}

		final ZonesXMLLoader loader = new ZonesXMLLoader(new URI(args[0]));

		try {
			loader.load();
		} catch (final org.xml.sax.SAXParseException ex) {
			System.err.print("Source " + args[0] + ":" + ex.getLineNumber()
					+ "<" + ex.getColumnNumber() + ">");

			throw ex;
		}
	}

	//
	//

	/**
	 * A zone descriptor.
	 */
	protected static class ZoneDesc {
		public static final int UNSET = Integer.MIN_VALUE;

		protected String zoneClassName;
		
		protected String name;

		protected String file;

		protected String title;
		
		protected String region;

		protected int level;

		protected int x;

		protected int y;

		protected ArrayList<SetupDescriptor> descriptors;

		private String implementation;

		public ZoneDesc(final String name, final String file, final String region, final int level, final int x, final int y) {
			this.name = name;
			this.file = file;
			this.level = level;
			this.x = x;
			this.y = y;
			this.region = region;

			descriptors = new ArrayList<SetupDescriptor>();
		}

		public void setImplementation(final String imclass) {
			implementation = imclass;
		}

		//
		// ZoneDesc
		//

		public String getImplementation() {
			return implementation;
		}

		/**
		 * Add a setup descriptor.
		 * @param desc 
		 * 
		 */
		public void addDescriptor(final SetupDescriptor desc) {
			descriptors.add(desc);
		}

		/**
		 * Get the zone file.
		 * @return the file name
		 * 
		 */
		public String getFile() {
			return file;
		}

		/**
		 * Get the level.
		 * @return the level of the zone
		 * 
		 */
		public int getLevel() {
			return level;
		}

		/**
		 * Get the zone name.
		 * @return the name of the zone
		 * 
		 */
		public String getName() {
			return name;
		}

		/**
		 * Get an iterator of setup descriptors.
		 * @return an iterator over the descriptors
		 * 
		 */
		public Iterator<SetupDescriptor> getDescriptors() {
			return descriptors.iterator();
		}

		/**
		 * Gets the zone's title.
		 * @return the zone's title
		 * 
		 */
		public String getTitle() {
			return title;
		}
		
		/**
		 * @return the zone's region
		 */
		public String getRegion() {
			return region;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public boolean isInterior() {
			return (getLevel() == UNSET);
		}

		/**
		 * Sets the zone title.
		 * @param title of the zone
		 * 
		 */
		public void setTitle(final String title) {
			this.title = title;
		}
	}
}
