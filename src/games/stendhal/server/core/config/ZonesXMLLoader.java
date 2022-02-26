/*
 * @(#) src/games/stendhal/server/config/ZonesXMLLoader.java
 *
 * $Id$
 */

package games.stendhal.server.core.config;

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

//
//

import games.stendhal.common.tiled.LayerDefinition;
import games.stendhal.common.tiled.StendhalMapStructure;
import games.stendhal.server.core.config.zone.AttributesXMLReader;
import games.stendhal.server.core.config.zone.ConfiguratorXMLReader;
import games.stendhal.server.core.config.zone.EntitySetupXMLReader;
import games.stendhal.server.core.config.zone.PortalSetupXMLReader;
import games.stendhal.server.core.config.zone.RegionNameSubstitutionHelper;
import games.stendhal.server.core.config.zone.SetupDescriptor;
import games.stendhal.server.core.config.zone.SetupXMLReader;
import games.stendhal.server.core.config.zone.TMXLoader;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;

/**
 * Load and configure zones via an XML configuration file.
 */
public final class ZonesXMLLoader {

	/**
	 * Logger.
	 */
	private static final Logger logger = Logger.getLogger(ZonesXMLLoader.class);

	/** Zone attributes reader. */
	private static final SetupXMLReader attributesReader = new AttributesXMLReader();
	/**
	 * The ConfiguratorDescriptor XML reader.
	 */
	private static final SetupXMLReader configuratorReader = new ConfiguratorXMLReader();

	/**
	 * The EntitySetupDescriptor XML reader.
	 */
	private static final SetupXMLReader entitySetupReader = new EntitySetupXMLReader();

	/**
	 * The PortalSetupDescriptor XML reader.
	 */
	private static final SetupXMLReader portalSetupReader = new PortalSetupXMLReader();

	/**
	 * The zone group file.
	 */
	private final URI uri;

	/**
	 * Create an XML based loader of zones.
	 * @param uri the zone group file
	 */
	public ZonesXMLLoader(final URI uri) {
		this.uri = uri;
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
		final InputStream in = ZonesXMLLoader.class.getResourceAsStream(uri.getPath());

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

		// just to speed up starting of the server in while developing
		// add -Dstendhal.zone.regex=".*semos.*" (for example) to your server start script just after the "java "
		// or for multiple regions: -Dstendhal.zone.regex=".*semos.*|.*fado.*"
		// it's a good idea to keep semos loaded as that's a default place to put the character
		// if there is a problem with the zone
		final String regex = System.getProperty("stendhal.zone.regex", ".*");

		/*
		 * Load each zone
		 */
		for (final Element element : XMLUtil.getElements(doc.getDocumentElement(), "zone")) {
			final ZoneDesc zdesc = readZone(element);

			if (zdesc == null) {
				continue;
			}

			final String name = zdesc.getName();
			if (!name.matches(regex) && !name.equals("int_semos_townhall") && !name.equals("int_semos_guard_house")) {
				continue;
			}

			logger.info("Loading zone: " + name);

			try {
				final StendhalMapStructure zonedata = TMXLoader.load(StendhalRPWorld.MAPS_FOLDER
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
					// Zone configurators can add creatures, so this should be
					// done after them
					zone.calculateDangerLevel();
				}
			} catch (final Exception ex) {
				logger.error("Error loading zone: " + name, ex);
			}
		}
	}

	private static final String[] REQUIRED_LAYERS = { "0_floor", "1_terrain",
			"2_object", "objects", "collision", "protection" };

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

		// Roof layers are optional
		loadOptionalLayer(zone, zonedata, "3_roof");
		loadOptionalLayer(zone, zonedata, "4_roof_add");
		// Effect layers are optional too
		loadOptionalLayer(zone, zonedata, "blend_ground");
		loadOptionalLayer(zone, zonedata, "blend_roof");

		zone.addCollisionLayer(name + ".collision",
				zonedata.getLayer("collision"));
		zone.addProtectionLayer(name + ".protection",
				zonedata.getLayer("protection"));

		if (desc.isInterior()) {
			zone.setPosition();
		} else {
			zone.setPosition(desc.getLevel(), desc.getX(), desc.getY());
		}

		zone.setPublicAccessible(desc.accessible);

		final String az = desc.getAssociatedZones();
		if (az != null) {
			zone.setAssociatedZones(az);
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

	/**
	 * Load an optional layer, if present, to a zone.
	 *
	 * @param zone
	 * @param zonedata
	 * @param layerName
	 * @throws IOException
	 */
	private void loadOptionalLayer(StendhalRPZone zone,
			StendhalMapStructure zonedata, String layerName) throws IOException {
		LayerDefinition layer = zonedata.getLayer(layerName);
		if (layer != null) {
			zone.addLayer(zone.getName() + "." + layerName, layer);
		}
	}

	@SuppressWarnings("unchecked")
	private StendhalRPZone createZone(final ZoneDesc desc, final String name)  {
		try {
			Class<StendhalRPZone> zoneclass = (Class<StendhalRPZone>) Class.forName(desc.getImplementation());
			Constructor<StendhalRPZone> constr = zoneclass.getConstructor(String.class);
			return constr.newInstance(name);
		} catch (ClassNotFoundException e) {
			logger.error(e, e);
		} catch (SecurityException e) {
			logger.error(e, e);
		} catch (NoSuchMethodException e) {
			logger.error(e, e);
		} catch (IllegalArgumentException e) {
			logger.error(e, e);
		} catch (InstantiationException e) {
			logger.error(e, e);
		} catch (IllegalAccessException e) {
			logger.error(e, e);
		} catch (InvocationTargetException e) {
			logger.error(e, e);
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

		String file = element.getAttribute("file");

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

		String region = parseRegionFromZone(name);

		boolean accessible = true;
		if (element.hasAttribute("accessible")) {
			accessible = Boolean.parseBoolean(element.getAttribute("accessible"));
		}
		final ZoneDesc desc = new ZoneDesc(name, file, region, level, x, y, accessible );

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

			if (!XMLUtil.checkCondition(child.getAttribute("condition"))) {
				continue;
			}

			SetupDescriptor setupDesc = null;

			if (tag.equals("attributes")) {
				setupDesc = attributesReader.read(child);
				if (setupDesc.getParameters().get("file") != null) {
					desc.file = setupDesc.getParameters().get("file");
				}
			} else if (tag.equals("configurator")) {
				setupDesc = configuratorReader.read(child);
			} else if (tag.equals("implementation")) {
				desc.setImplementation(child.getAttribute("class-name"));

			} else if (tag.equals("entity")) {
				setupDesc = entitySetupReader.read(child);
			} else if (tag.equals("portal")) {
				setupDesc = portalSetupReader.read(child);
			} else if (tag.equals("title") || tag.equals("point-of-interest")) {
				// Ignore
				continue;
			} else if (tag.equals("associated")) {
				desc.setAssociatedZones(child.getAttribute("zones"));
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
	 *
	 * @return region name
	 */
	private String parseRegionFromZone(String name) {
		String[] split = name.split("_");
		// standard exterior and interior zones have more than 3 parts
		if (split.length > 1) {
			return RegionNameSubstitutionHelper.get().replaceRegionName(split[1]);
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

		protected String name;

		protected String file;

		protected String title;

		protected String region;

		protected int level;

		protected int x;

		protected int y;

		protected ArrayList<SetupDescriptor> descriptors;

		private String implementation;

		private final boolean accessible;

		private String associatedZones;

		public ZoneDesc(final String name, final String file, final String region, final int level, final int x, final int y, final boolean accessible) {
			this.name = name;
			this.file = file;
			this.level = level;
			this.x = x;
			this.y = y;
			this.accessible = accessible;
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

		public boolean isAccessible() {
			return accessible;
		}

		public void setAssociatedZones(final String zones) {
			associatedZones = zones;
		}

		public String getAssociatedZones() {
			return associatedZones;
		}
	}
}
