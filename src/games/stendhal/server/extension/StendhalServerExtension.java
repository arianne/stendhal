//* $Id$ */

/** StendhalServer Extension is copyright of Jo Seiler, 2006
 *  @author intensifly
 * The StendhalServerExtension is a base class for plugins that add
 * functions to the server.
 */
package games.stendhal.server.extension;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public abstract class StendhalServerExtension {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalServerExtension.class);

	/** Lists the instances of the loaded extensions. */
	private static Map<String, StendhalServerExtension> loadedInstances = new HashMap<String, StendhalServerExtension>();

	public abstract void init();

	public synchronized boolean perform(final String name) {
		return (false);
	}

	public String getMessage(final String name) {
		return (null);
	}

	public static StendhalServerExtension getInstance(final String name) {
		try {
			final Class< ? > extensionClass = Class.forName(name);

			if (!StendhalServerExtension.class.isAssignableFrom(extensionClass)) {
				logger.debug("Class is no instance of StendhalServerExtension.");
				return null;
			}

			logger.info("Loading ServerExtension: " + name);
			final java.lang.reflect.Constructor< ? > constr = extensionClass.getConstructor();

			// simply create a new instance. The constructor creates all
			// additionally objects
			final StendhalServerExtension instance = (StendhalServerExtension) constr.newInstance();
			// store it in the hashmap for later reference
			loadedInstances.put(name, instance);
			return instance;
		} catch (final Exception e) {
			logger.warn("StendhalServerExtension " + name + " loading failed.",
					e);
			return null;
		}
	}

}
