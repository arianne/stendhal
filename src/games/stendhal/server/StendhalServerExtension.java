//* $Id$ */

/** StendhalServer Extension is copyright of Jo Seiler, 2006
 *  @author intensifly
 * The StendhalServerExtension is a base class for plugins that add
 * functions to the server.
 */
package games.stendhal.server;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

public abstract class StendhalServerExtension extends ActionListener {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(StendhalServerExtension.class);

	/** lists the instances of the loaded extensions */
	private static Map<String, StendhalServerExtension> loadedInstances = new HashMap<String, StendhalServerExtension>();

	public StendhalServerExtension() {
	}

	public abstract void init();

	public synchronized boolean perform(String name) {
		return (false);
	}

	public String getMessage(String name) {
		return (null);
	}

	@Override
	public void onAction(Player player, RPAction action) {
		return;
	}

	public static StendhalServerExtension getInstance(String name) {
		try {
			Class extensionClass = Class.forName(name);

			if (!StendhalServerExtension.class.isAssignableFrom(extensionClass)) {
				logger.debug("Class is no instance of StendhalServerExtension.");
				return null;
			}

			logger.info("Loading ServerExtension: " + name);
			java.lang.reflect.Constructor constr = extensionClass.getConstructor();

			// simply create a new instance. The constructor creates all
			// additionally objects
			StendhalServerExtension instance = (StendhalServerExtension) constr.newInstance();
			// store it in the hashmap for later reference
			loadedInstances.put(name, instance);
			return instance;
		} catch (Exception e) {
			logger.warn("StendhalServerExtension " + name + " loading failed.", e);
			return null;
		}
	}

}
