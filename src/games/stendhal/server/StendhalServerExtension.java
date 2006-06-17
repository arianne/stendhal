//* $Id$ */

/** StendhalHttpServer Extension is copyright of Jo Seiler, 2006
 *  @author intensifly
 * The StendhalServerExtension is a base class for plugins that add
 * functions to the server.
*/
package games.stendhal.server;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.Player;
import marauroa.common.game.RPAction;
import marauroa.server.game.RPWorld;
import marauroa.common.Log4J;
import org.apache.log4j.Logger;
import java.util.HashMap;
import java.util.Map;

public abstract class StendhalServerExtension extends ActionListener {
	/** our connection points to the game objects * */
	static StendhalRPRuleProcessor rules;

	static protected StendhalRPWorld world;

	/** the logger instance. */
	protected static final Logger logger = Log4J
			.getLogger(StendhalServerExtension.class);

	/** lists the instances of the loaded extensions */
	private static Map<String, StendhalServerExtension> loadedInstances = new HashMap<String, StendhalServerExtension>();

	public StendhalServerExtension(StendhalRPRuleProcessor rules,
			StendhalRPWorld world) {
		StendhalServerExtension.rules = rules;
		StendhalServerExtension.world = world;
	}

	public abstract void init();

	public synchronized boolean perform(String name) {
		return (false);
	}

	public String getMessage(String name) {
		return (null);
	}

	@Override
	public void onAction(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		return;
	}

	public static StendhalServerExtension getInstance(String name) {
		return (loadedInstances.get(name));
	}

	public static StendhalServerExtension getInstance(String name,
			StendhalRPRuleProcessor pRules, StendhalRPWorld pWorld) {
		try {
			Class extensionClass = Class.forName(name);

			if (!StendhalServerExtension.class.isAssignableFrom(extensionClass)) {
				logger.debug("Class is no instance of StendhalServerExtension.");
				return null;
			}

			logger.info("Loading ServerExtension: " + name);
			java.lang.reflect.Constructor constr = extensionClass
					.getConstructor(StendhalRPRuleProcessor.class,
							StendhalRPWorld.class);

			// simply create a new instance. The constructor creates all
			// additionally objects
			StendhalServerExtension instance = (StendhalServerExtension) constr
					.newInstance(pRules, pWorld);
			// store it in the hashmap for later reference
			loadedInstances.put(name, instance);
			return instance;
		} catch (Exception e) {
			logger.warn("StendhalServerExtension " + name + " loading failed.",
					e);
			return null;
		}
	}

}
