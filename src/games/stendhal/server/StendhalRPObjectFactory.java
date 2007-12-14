package games.stendhal.server;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.server.game.rp.RPObjectFactory;

import org.apache.log4j.Logger;

/**
 * creates concrete objects of Stendhal classes
 * 
 * @author hendrik
 */
public class StendhalRPObjectFactory extends RPObjectFactory {
	private static Logger logger = Logger.getLogger(StendhalRPObjectFactory.class);

	@Override
	public RPObject transform(RPObject object) {
		RPClass clazz = object.getRPClass();
		if (clazz == null) {
			logger.error("Cannot create concrete object for " + object
					+ " because it does not have an RPClass.");
			return super.transform(object);
		}
		String name = clazz.getName();

		// TODO: add factory here

		return super.transform(object);
	}

}
