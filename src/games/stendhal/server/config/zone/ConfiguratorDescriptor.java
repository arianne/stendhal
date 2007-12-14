/*
 * @(#) src/games/stendhal/server/config/zone/ConfiguratorDescriptor.java
 *
 * $Id$
 */

package games.stendhal.server.config.zone;

//
//

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;

import org.apache.log4j.Logger;

/**
 * A zone configurator [setup] descriptor.
 */
public class ConfiguratorDescriptor extends SetupDescriptor {
	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(ConfiguratorDescriptor.class);

	/**
	 * The class name of the configurator.
	 */
	protected String className;

	/**
	 * Create a zone configurator descriptor.
	 * 
	 * @param className
	 *            The class name of the configurator.
	 */
	public ConfiguratorDescriptor(final String className) {
		this.className = className;
	}

	//
	// ConfiguratorDescriptor
	//

	/**
	 * Get the class name of the configurator.
	 * 
	 * @return The class name.
	 */
	public String getClassName() {
		return className;
	}

	//
	// SetupDescriptor
	//

	/**
	 * Do appropriate zone setup.
	 * 
	 * @param zone
	 *            The zone.
	 */
	@Override
	public void setup(final StendhalRPZone zone) {
		Class<?> clazz;
		Object obj;

		String classNameTemp = getClassName();

		/*
		 * Load class
		 */
		try {
			clazz = Class.forName(classNameTemp);
		} catch (ClassNotFoundException ex) {
			logger.error("Unable to find zone configurator: " + classNameTemp);

			return;
		}

		/*
		 * Create instance
		 */
		try {
			obj = clazz.newInstance();
		} catch (InstantiationException ex) {
			logger.error("Error creating zone configurator: " + classNameTemp,
					ex);

			return;
		} catch (IllegalAccessException ex) {
			logger.error("Error accessing zone configurator: " + classNameTemp,
					ex);

			return;
		}

		/*
		 * Apply class
		 */
		if (obj instanceof ZoneConfigurator) {
			logger.info("Configuring zone [" + zone.getName() + "] with: "
					+ classNameTemp);

			((ZoneConfigurator) obj).configureZone(zone, getParameters());
		} else {
			logger.warn("Unsupported zone configurator: " + classNameTemp);
		}
	}
}
