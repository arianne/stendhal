/***************************************************************************
 *                   (C) Copyright 2006-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.extension;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * The StendhalServerExtension is a base class for plugins that add
 * functions to the server.
 */
public abstract class StendhalServerExtension implements StendhalServerExtensionIface {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalServerExtension.class);

	/** Lists the instances of the loaded extensions. */
	private static Map<String, StendhalServerExtensionIface> loadedInstances = new HashMap<String, StendhalServerExtensionIface>();

	/**
	 * init the extension
	 */
	@Override
	public abstract void init();

	/**
	 * @param name
	 * @return <code>true</code> on success, otherwise <code>false>/code>
	 */
	public synchronized boolean perform(final String name) {
		return (false);
	}

	/**
	 * @param name
	 * @return message
	 */
	public String getMessage(final String name) {
		return (null);
	}

	/**
	 * gets an stendhal extension instance
	 *
	 * @param name name of the extension class
	 * @return StendhalServerExtension
	 */
	public static StendhalServerExtensionIface getInstance(final String name) {
		try {
			final Class<? extends StendhalServerExtensionIface> extensionClass = Class.forName(name).asSubclass(StendhalServerExtensionIface.class);

			if (!StendhalServerExtensionIface.class.isAssignableFrom(extensionClass)) {
				logger.debug("Class is no instance of StendhalServerExtension.");
				return null;
			}

			logger.info("Loading ServerExtension: " + name);
			final java.lang.reflect.Constructor<? extends StendhalServerExtensionIface> constr = extensionClass.getConstructor();

			// simply create a new instance. The constructor creates all
			// additionally objects
			final StendhalServerExtensionIface instance = constr.newInstance();
			// store it in the hashmap for later reference
			loadedInstances.put(name, instance);
			return instance;
		} catch (final Exception e) {
			logger.warn("StendhalServerExtension " + name + " loading failed.",	e);
			return null;
		}
	}

}
